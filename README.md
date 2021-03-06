# ART-Extension for Android Marshmallow

## ART-Extension project (Android Marshmallow)

ART-Extension is an Android Runtime (ART) extension that adds features and optimizations on top of what AOSP provides.
It has been tested and works on Intel platforms, but could easily be brought up on ARM as well. A few of the optimizations are Intel-specific, but most are generic.
Most are compiler-related, but there are a few garbage collection and runtime tweaks as well.

## Details

ART-Extension was developed and open sourced by Intel (TM) Corporation to deliver best Android UX on x86 platform.
It consists of a flexible pass compilation framework and number of optimizations.
Pass compilation framework allows to manage an order of compiler optimizations applied, supports dataflow consistency, optimizations order.

In addition to the base optimizations in the Android Open Source Project (AOSP), ART-Extension implements or introduces new use models at least for the following optimizations:

* Enhanced constant folding
* Loop formation before peeling
* Value propagation through heap
* Pure invokes analysis
* Loop peeling
* Enhanced constant folding after inlining
* Form bottom loops
* GVN after form bottom loops
* Loop formation
* Find ivs
* Trivial loop evaluator
* Non temporal move
* Constant calculation sinking
* Remove loop suspend checks
* Load hoisting and Store sinking
* Remove unused loops
* Loop full unrolling
* Dead code elimination final pass

## Usage

To start using ART-Extension one needs to follow the steps:

1. Step 1. Download AOSP Marshmallow source code. To do that execute setup.sh script. It will download the correct version of AOSP. Setup proxy settings on your system if needed.

2. Step 2. Apply patches needed for ART-Extension. To do that execute apply_extra.sh script.

3. Step 3. Build Android image using AOSP for your device/emulator:
   * See instructions at https://source.android.com/source/ (your device must be supported by AOSP build or use x86 emulator).
   * To start build with ART-Extension one needs to use VENDOR_ART_PATH=<path to art-extension> option in make, example: "make dist VENDOR_ART_PATH=art-extension"
   * ART-Extension was tested to build and boot to GUI on at least x86 emulator, to build ARM emulator one should add "WITH_DEXPREOPT=false" to the build options, besides testing showed ARM emulator was not able to boot to GUI.

4. Step 4. Flash your device (use AOSP instructions), test it, and then try.

## License

Apache License Version 2.0

See LICENSE file for details

