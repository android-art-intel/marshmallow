#!/bin/bash

# settings
[ -z "${CURRENT_DIR}" ] && CURRENT_DIR="$0"
if [ ! -e "$CURRENT_DIR" ]; then
  export CURRENT_DIR=$(pwd)
else
  [ ! -d "$CURRENT_DIR" ] && CURRENT_DIR=$(dirname $CURRENT_DIR)
  export CURRENT_DIR=$(cd $CURRENT_DIR; pwd)
fi
WS_TOP=${CURRENT_DIR}/..
WS_OUT=${WS_TOP}/out

# Create out dir
[ -d "${WS_OUT}" ] || mkdir ${WS_OUT} || exit 1

# Download repo
([ -x "${WS_OUT}/repo" ] || curl ${CURL_PROXY} https://storage.googleapis.com/git-repo-downloads/repo > ${WS_OUT}/repo) || exit 1
chmod a+x ${WS_OUT}/repo

# Initalize Android android-6.0.1_r46 workspace
(cd ${WS_TOP} && ${WS_OUT}/repo init -u https://android.googlesource.com/platform/manifest -b android-6.0.1_r46) || exit 1

# Download Android android-6.0.1_r46 sources
(cd ${WS_TOP} && ./.repo/repo/repo sync -c -j5) || exit 1

# apply extra-patches
${CURRENT_DIR}/apply_extra.sh
