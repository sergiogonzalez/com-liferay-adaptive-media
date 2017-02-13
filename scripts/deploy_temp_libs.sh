#! /bin/sh

set -e

TEMP_LIBS='adaptive-media-image-jax-rs-test/libs/*'

for m in ${TEMP_LIBS}; do
       (cp ${m} ../bundles/deploy)
done
