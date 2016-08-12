#!/bin/bash

# settings
[ -z "${CURRENT_DIR}" ] && CURRENT_DIR="$0"
if [ ! -e "$CURRENT_DIR" ]; then
  export CURRENT_DIR=$(pwd)
else
  [ ! -d "$CURRENT_DIR" ] && CURRENT_DIR=$(dirname $CURRENT_DIR)
  export CURRENT_DIR=$(cd $CURRENT_DIR; pwd)
fi
WS_TOP=${CURRENT_DIR}
EXTRA_PATCHES_DIR=${CURRENT_DIR}/extra-patches

for file in $(find ${EXTRA_PATCHES_DIR}/ -name "*.patch"); do
   dir=$(dirname $file | sed "s|${EXTRA_PATCHES_DIR}/||")
   patchfile=$(basename $file)
   (cd ${WS_TOP}/${dir} && patch -p1 < "${EXTRA_PATCHES_DIR}/${dir}/$patchfile")
done