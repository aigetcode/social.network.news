#!/usr/bin/env bash

set -e

#script directory
BUILD_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_VERSION=$(grep -oP 'version = "\K[^"\047]+(?=["\047])' $BUILD_DIR/../build.gradle.kts)
PROJECT_NAME=$(grep -oP 'projectName = "\K[^"\047]+(?=["\047])' $BUILD_DIR/../build.gradle.kts)

echo "Building image: social-$PROJECT_NAME:$PROJECT_VERSION"


cp "$BUILD_DIR/../build/libs/$PROJECT_NAME-$PROJECT_VERSION.jar" "$BUILD_DIR/$PROJECT_NAME.jar"
cd docker

docker build . --tag "ghcr.io/aigetcode/social-$PROJECT_NAME:$PROJECT_VERSION"
echo "Built docker image"

docker push "ghcr.io/aigetcode/social-$PROJECT_NAME:$PROJECT_VERSION"
echo "Pushed docker image"

echo "Built image: social-$PROJECT_NAME:$PROJECT_VERSION"
