/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef ART_RUNTIME_GC_COLLECTOR_SEMI_SPACE_INL_H_
#define ART_RUNTIME_GC_COLLECTOR_SEMI_SPACE_INL_H_

#include "semi_space.h"

#include "gc/accounting/heap_bitmap.h"
#include "mirror/object-inl.h"

namespace art {
namespace gc {
namespace collector {

// Reuse TLAB as PLAB since this is for STW GC.
static constexpr bool kUsePlab = kUseTlab;
static constexpr size_t kDefaultPLABSize = 64 * KB;

class BitmapSetSlowPathVisitor {
 public:
  explicit BitmapSetSlowPathVisitor(SemiSpace* semi_space) : semi_space_(semi_space) {
  }

  void operator()(const mirror::Object* obj) const {
    CHECK(!semi_space_->to_space_->HasAddress(obj)) << "Marking " << obj << " in to_space_";
    // Marking a large object, make sure its aligned as a sanity check.
    CHECK(IsAligned<kPageSize>(obj));
  }

 private:
  SemiSpace* const semi_space_;
};

inline mirror::Object* SemiSpace::GetForwardingAddressInFromSpace(mirror::Object* obj) const {
  DCHECK(from_space_->HasAddress(obj));
  LockWord lock_word = obj->GetLockWord(false);
  if (lock_word.GetState() != LockWord::kForwardingAddress) {
    return nullptr;
  }
  return reinterpret_cast<mirror::Object*>(lock_word.ForwardingAddress());
}

// Used to mark and copy objects. Any newly-marked objects who are in the from space Get moved to
// the to-space and have their forward address updated. Objects which have been newly marked are
// pushed on the mark stack.
template<bool kPoisonReferences>
inline void SemiSpace::MarkObject(
    mirror::ObjectReference<kPoisonReferences, mirror::Object>* obj_ptr) {
  mirror::Object* obj = obj_ptr->AsMirrorPtr();
  if (obj == nullptr) {
    return;
  }
  if (from_space_->HasAddress(obj)) {
    mirror::Object* forward_address = GetForwardingAddressInFromSpace(obj);
    // If the object has already been moved, return the new forward address.
    if (UNLIKELY(forward_address == nullptr)) {
      forward_address = MarkNonForwardedObject(obj);
      DCHECK(forward_address != nullptr);
      // Make sure to only update the forwarding address AFTER you copy the object so that the
      // monitor word doesn't Get stomped over.
      obj->SetLockWord(
          LockWord::FromForwardingAddress(reinterpret_cast<size_t>(forward_address)), false);
      // Push the object onto the mark stack for later processing.
      MarkStackPush(forward_address);
    }
    obj_ptr->Assign(forward_address);
  } else if (!collect_from_space_only_ && !immune_region_.ContainsObject(obj)) {
    BitmapSetSlowPathVisitor visitor(this);
    if (!mark_bitmap_->Set(obj, visitor)) {
      // This object was not previously marked.
      MarkStackPush(obj);
    }
  }
}

inline bool SemiSpace::IsMarked(mirror::Object* obj) {
  DCHECK(!from_space_->HasAddress(obj)) << "Scanning object " << obj << " in from space";
  if (to_space_->HasAddress(obj)) {
    // Always take to_space_ object as marked.
    return true;
  } else if (!collect_from_space_only_ && !immune_region_.ContainsObject(obj)) {
    // For GSS, obj in promoted space, test the bitmap.
    return mark_bitmap_->Test(obj);
  } else if (immune_region_.ContainsObject(obj)) {
    // Immune objects, always consider as marked.
    return true;
  }
  return false;
}

static inline size_t CopyAvoidingDirtyingPages(void* dest, const void* src, size_t size) {
  if (LIKELY(size <= static_cast<size_t>(kPageSize))) {
    // We will dirty the current page and somewhere in the middle of the next page. This means
    // that the next object copied will also dirty that page.
    // TODO: Worth considering the last object copied? We may end up dirtying one page which is
    // not necessary per GC.
    memcpy(dest, src, size);
    return 0;
  }
  size_t saved_bytes = 0;
  uint8_t* byte_dest = reinterpret_cast<uint8_t*>(dest);
  if (kIsDebugBuild) {
    for (size_t i = 0; i < size; ++i) {
      CHECK_EQ(byte_dest[i], 0U);
    }
  }
  // Process the start of the page. The page must already be dirty, don't bother with checking.
  const uint8_t* byte_src = reinterpret_cast<const uint8_t*>(src);
  const uint8_t* limit = byte_src + size;
  size_t page_remain = AlignUp(byte_dest, kPageSize) - byte_dest;
  // Copy the bytes until the start of the next page.
  memcpy(dest, src, page_remain);
  byte_src += page_remain;
  byte_dest += page_remain;
  DCHECK_ALIGNED(reinterpret_cast<uintptr_t>(byte_dest), kPageSize);
  DCHECK_ALIGNED(reinterpret_cast<uintptr_t>(byte_dest), sizeof(uintptr_t));
  DCHECK_ALIGNED(reinterpret_cast<uintptr_t>(byte_src), sizeof(uintptr_t));
  while (byte_src + kPageSize < limit) {
    bool all_zero = true;
    uintptr_t* word_dest = reinterpret_cast<uintptr_t*>(byte_dest);
    const uintptr_t* word_src = reinterpret_cast<const uintptr_t*>(byte_src);
    for (size_t i = 0; i < kPageSize / sizeof(*word_src); ++i) {
      // Assumes the destination of the copy is all zeros.
      if (word_src[i] != 0) {
        all_zero = false;
        word_dest[i] = word_src[i];
      }
    }
    if (all_zero) {
      // Avoided copying into the page since it was all zeros.
      saved_bytes += kPageSize;
    }
    byte_src += kPageSize;
    byte_dest += kPageSize;
  }
  // Handle the part of the page at the end.
  memcpy(byte_dest, byte_src, limit - byte_src);
  return saved_bytes;
}

// Alloc thect in the dest_space and try update the lockword.
// If the update fail, try roll back the space.
inline mirror::Object* SemiSpace::TryInstallForwardingAddress(mirror::Object* obj,
                                                   space::ContinuousMemMapAllocSpace* dest_space,
                                                   size_t* bytes_allocated,
                                                   bool* win) {
  const size_t object_size = obj->SizeOf();
  mirror::Object* forward_address = nullptr;
  Thread* self = Thread::Current();
  size_t dummy = 0;
  *win = false;
  if (!kUsePlab) {
    forward_address = dest_space->Alloc(self, object_size, bytes_allocated, nullptr, &dummy);
  } else {
    if (dest_space->IsBumpPointerSpace()) {
     size_t  byte_count = RoundUp(object_size, space::BumpPointerSpace::kAlignment);
     if (byte_count <= self->TlabSize()) {
       forward_address = self->AllocTlab(byte_count);
       DCHECK(forward_address != nullptr);
     } else {
       // Fail allocate in Tlab, create a new one.
       // TODO: Delete this atomic operation, it is only for statistic.
       wasted_bytes_.FetchAndAddSequentiallyConsistent(self->TlabSize());
       DCHECK_ALIGNED(byte_count, space::BumpPointerSpace::kAlignment);
       size_t new_tlab_size = byte_count + kDefaultPLABSize;
       space::BumpPointerSpace* bump_pointer_space = dest_space->AsBumpPointerSpace();
       // Try allocating a new thread local buffer, if the allocation fails the space must be
       // full so return null.
       if (!bump_pointer_space->AllocNewTlab(self, new_tlab_size)) {
         // Try alloc smaller.
         new_tlab_size -= kDefaultPLABSize / 2;
         if (!bump_pointer_space->AllocNewTlab(self, new_tlab_size)) {
           // Try just the required size.
           new_tlab_size = byte_count;
           if (!bump_pointer_space->AllocNewTlab(self, new_tlab_size)) {
             CHECK(forward_address != nullptr) << "OutOfMemory when alloc TLAB in to_space!";
           }
         }
       }
       // The allocation can't fail.
       forward_address = self->AllocTlab(byte_count);
       DCHECK(forward_address != nullptr);
     }
     *bytes_allocated = byte_count;
    } else if (dest_space->IsRosAllocSpace()) {
      size_t byte_count = RoundUp(object_size, space::BumpPointerSpace::kAlignment);
      space::RosAllocSpace* ros_space = dest_space->AsRosAllocSpace();
      forward_address = ros_space->AllocThreadLocal(self, byte_count, bytes_allocated);
      if (forward_address == nullptr) {
        forward_address = ros_space->Alloc(self, object_size, bytes_allocated, nullptr, &dummy);
      }
    } else {
      forward_address = dest_space->Alloc(self, object_size, bytes_allocated, nullptr, &dummy);
    }
  }
  if (forward_address != nullptr) {
    // Try to set lockword.
    LockWord old_lock_word = obj->GetLockWord(false);
    if (old_lock_word.GetState() == LockWord::kForwardingAddress) {
      // Object has been copied by another thread, roll back.
      if (!kUsePlab) {
        // The RollBack should for all different space.
        // For Bump pointer space, need fill with dummy.
        // For ROS, need roll back.
        if (dest_space->IsBumpPointerSpace()) {
          this->FillWithDummyObject(forward_address, *bytes_allocated);
          dummy_bytes_.FetchAndAddSequentiallyConsistent(*bytes_allocated);
          dummy_objects_.FetchAndAddSequentiallyConsistent(1);
        } else {
          dest_space->Free(self, forward_address);
          *bytes_allocated -= *bytes_allocated;
        }
      } else {
        if (dest_space->IsBumpPointerSpace()) {
           // Roll back directly.
           DCHECK_ALIGNED(*bytes_allocated, space::BumpPointerSpace::kAlignment);
           self->RollBackTlab(*bytes_allocated);
        } else if (dest_space->IsRosAllocSpace()) {
          bool freed = dest_space->AsRosAllocSpace()->FreeThreadLocal(self, *bytes_allocated, forward_address);
          if (!freed) {
            // Object not in thread local run.
            size_t freed_bytes = dest_space->AsRosAllocSpace()->FreeNonThread(self, forward_address);
            DCHECK(freed_bytes == *bytes_allocated);
          }
        } else {
            size_t freed_bytes = dest_space->Free(self, forward_address);
            DCHECK(freed_bytes == *bytes_allocated);
        }
        *bytes_allocated -= *bytes_allocated;
      }
      // Fail, return new forwarding address.
      forward_address = reinterpret_cast<mirror::Object*>(old_lock_word.ForwardingAddress());
    } else {
      LockWord new_lock_word = LockWord::FromForwardingAddress(reinterpret_cast<size_t>(forward_address));
      // Try set lockword
      bool success = obj->CasLockWordWeakSequentiallyConsistent(old_lock_word, new_lock_word);

      if (!success) {
        // Object has been copied by other thread, roll back.
        if (!kUsePlab) {
          if (dest_space->IsBumpPointerSpace()) {
            this->FillWithDummyObject(forward_address, *bytes_allocated);
            dummy_bytes_.FetchAndAddSequentiallyConsistent(*bytes_allocated);
            dummy_objects_.FetchAndAddSequentiallyConsistent(1);
          } else {
            dest_space->Free(self, forward_address);
            *bytes_allocated -= *bytes_allocated;
          }
        } else {
          if (dest_space->IsBumpPointerSpace()) {
             DCHECK_ALIGNED(*bytes_allocated, space::BumpPointerSpace::kAlignment);
             self->RollBackTlab(*bytes_allocated);
          } else if (dest_space->IsRosAllocSpace()) {
            bool freed = dest_space->AsRosAllocSpace()->FreeThreadLocal(self, *bytes_allocated, forward_address);
            if (!freed) {
              size_t freed_bytes = dest_space->AsRosAllocSpace()->FreeNonThread(self, forward_address);
              DCHECK(freed_bytes == *bytes_allocated);
            }
          } else {
              size_t freed_bytes = dest_space->Free(self, forward_address);
              DCHECK(freed_bytes == *bytes_allocated);
          }
          *bytes_allocated -= *bytes_allocated;
        }

        // Other thread has updated the lock_word, return winner's forward address.
        DCHECK(obj->GetLockWord(false).GetState() == LockWord::kForwardingAddress);
        forward_address = reinterpret_cast<mirror::Object*>(obj->GetLockWord(false).ForwardingAddress());
      } else {
        // Successfully updated the lockword.
        DCHECK(reinterpret_cast<mirror::Object*>(obj->GetLockWord(false).ForwardingAddress(), forward_address));
        saved_bytes_ += (CopyAvoidingDirtyingPages(reinterpret_cast<void*>(forward_address), obj, object_size));
        // memcpy(forward_address, obj, object_size);
        // We are save to set back the lock word since this is in pause phase.
        DCHECK(!from_space_->HasAddress(forward_address));
        // TODO: Volatile?
        forward_address->SetLockWord(old_lock_word, true);
        *win = true;
        return forward_address;
      }
    }
  }
  DCHECK(*win != true);
  return forward_address;
}

template<typename MarkVisitor, typename ReferenceVisitor>
inline void SemiSpace::ScanObjectVisit(mirror::Object* obj, const MarkVisitor& visitor,
                                       const ReferenceVisitor& ref_visitor) {
  DCHECK(IsMarked(obj)) << "Scanning unmarked object " << obj << "\n" << heap_->DumpSpaces();
  obj->VisitReferences<true>(visitor, ref_visitor);
}

// Fill the given memory block with a dummy object.
// Use to fill in a copy of object that was lost in race.
inline void SemiSpace::FillWithDummyObject(mirror::Object* dummy_obj, size_t byte_size) {
  DCHECK_ALIGNED(byte_size, kObjectAlignment);
  memset(dummy_obj, 0, byte_size);
  mirror::Class* int_array_class = mirror::IntArray::GetArrayClass();
  DCHECK(int_array_class != nullptr);
  size_t component_size = int_array_class->GetComponentSize();
  DCHECK_EQ(component_size, sizeof(int32_t));
  size_t data_offset = mirror::Array::DataOffset(component_size).SizeValue();
  if (data_offset > byte_size) {
    // An int array is too big. Use java.lang.Object.
    mirror::Class* java_lang_Object = WellKnownClasses::ToClass(WellKnownClasses::java_lang_Object);
    DCHECK_EQ(byte_size, java_lang_Object->GetObjectSize());
    dummy_obj->SetClass(java_lang_Object);
    DCHECK_EQ(byte_size, dummy_obj->SizeOf());
  } else {
    // Use an int array.
    dummy_obj->SetClass(int_array_class);
    DCHECK(dummy_obj->IsArrayInstance());
    int32_t length = (byte_size - data_offset) / component_size;
    dummy_obj->AsArray()->SetLength(length);
    DCHECK_EQ(dummy_obj->AsArray()->GetLength(), length)
        << "byte_size=" << byte_size << " length=" << length
        << " component_size=" << component_size << " data_offset=" << data_offset;
    DCHECK_EQ(byte_size, dummy_obj->SizeOf())
        << "byte_size=" << byte_size << " length=" << length
        << " component_size=" << component_size << " data_offset=" << data_offset;
  }
}
}  // namespace collector
}  // namespace gc
}  // namespace art

#endif  // ART_RUNTIME_GC_COLLECTOR_SEMI_SPACE_INL_H_
