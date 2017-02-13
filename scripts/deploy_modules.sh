#! /bin/sh

set -e

PREFIX='adaptive-media-'
MODULES='api demo-data-creator-api demo-data-creator-impl document-library image-api image-impl image-service image-item-selector-api image-jax-rs image-js image-web web'

for m in ${MODULES}; do
	(cd ${PREFIX}${m} && ../gradlew deploy)
done