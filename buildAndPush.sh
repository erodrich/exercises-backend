#!/bin/bash
# Build and Push Docker Image to Docker Hub
# Usage: ./buildAndPush.sh [version]

set -e

# Configuration
DOCKER_USERNAME="${DOCKER_USERNAME:-}"  # Set via environment or prompt
IMAGE_NAME="exercises-backend"
VERSION="${1:-latest}"
FULL_IMAGE_TAG="${DOCKER_USERNAME}/${IMAGE_NAME}:${VERSION}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "🐳 Exercise Backend - Build and Push to Docker Hub"
echo "=================================================="
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Error: Docker is not running${NC}"
    echo "Please start Docker and try again"
    exit 1
fi

# Check if DOCKER_USERNAME is set
if [ -z "$DOCKER_USERNAME" ]; then
    echo -e "${YELLOW}⚠️  DOCKER_USERNAME not set${NC}"
    read -p "Enter your Docker Hub username: " DOCKER_USERNAME
    
    if [ -z "$DOCKER_USERNAME" ]; then
        echo -e "${RED}❌ Error: Docker Hub username is required${NC}"
        exit 1
    fi
    
    FULL_IMAGE_TAG="${DOCKER_USERNAME}/${IMAGE_NAME}:${VERSION}"
fi

echo "📦 Building Docker image..."
echo "   Image: ${FULL_IMAGE_TAG}"
echo "   Version: ${VERSION}"
echo ""

# Build the image
if docker build -t ${FULL_IMAGE_TAG} . ; then
    echo -e "${GREEN}✅ Docker image built successfully${NC}"
else
    echo -e "${RED}❌ Error: Docker build failed${NC}"
    exit 1
fi

# Also tag as latest if version is not 'latest'
if [ "$VERSION" != "latest" ]; then
    LATEST_TAG="${DOCKER_USERNAME}/${IMAGE_NAME}:latest"
    docker tag ${FULL_IMAGE_TAG} ${LATEST_TAG}
    echo -e "${GREEN}✅ Tagged as latest: ${LATEST_TAG}${NC}"
fi

echo ""
echo "📊 Image Details:"
docker images ${DOCKER_USERNAME}/${IMAGE_NAME} --format "table {{.Repository}}\t{{.Tag}}\t{{.Size}}\t{{.CreatedAt}}"

echo ""
read -p "Push to Docker Hub? (y/N) " -n 1 -r
echo

if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo ""
    echo "🔐 Logging in to Docker Hub..."
    
    if docker login; then
        echo -e "${GREEN}✅ Login successful${NC}"
        echo ""
        echo "📤 Pushing image to Docker Hub..."
        
        if docker push ${FULL_IMAGE_TAG}; then
            echo -e "${GREEN}✅ Successfully pushed: ${FULL_IMAGE_TAG}${NC}"
            
            # Push latest tag if it exists
            if [ "$VERSION" != "latest" ]; then
                if docker push ${LATEST_TAG}; then
                    echo -e "${GREEN}✅ Successfully pushed: ${LATEST_TAG}${NC}"
                fi
            fi
            
            echo ""
            echo "🎉 Build and push completed successfully!"
            echo ""
            echo "📝 Image available at:"
            echo "   https://hub.docker.com/r/${DOCKER_USERNAME}/${IMAGE_NAME}"
            echo ""
            echo "🚀 Pull command:"
            echo "   docker pull ${FULL_IMAGE_TAG}"
        else
            echo -e "${RED}❌ Error: Push failed${NC}"
            exit 1
        fi
    else
        echo -e "${RED}❌ Error: Docker login failed${NC}"
        exit 1
    fi
else
    echo ""
    echo "ℹ️  Push cancelled"
    echo ""
    echo "To push manually later:"
    echo "   docker login"
    echo "   docker push ${FULL_IMAGE_TAG}"
fi

echo ""
echo "✨ Done!"
