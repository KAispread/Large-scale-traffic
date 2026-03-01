#!/bin/bash

# ECR 레포지토리 이름
ECR_REPOSITORY="kaispread/kuke-board"

# AWS 리전
AWS_REGION="ap-northeast-2"

# Docker 이미지 태그
IMAGE_TAG="latest"

# Gradle 빌드
../../gradlew :service:article:build -x test

# ECR 로그인
aws ecr get-login-password --region $AWS_REGION | docker login --username AWS --password-stdin $(aws sts get-caller-identity --query Account --output text).dkr.ecr.ap-northeast-2.amazonaws.com/$ECR_REPOSITORY

# Docker 이미지 빌드
docker build -t $ECR_REPOSITORY .

# Docker 이미지 태그
docker tag $ECR_REPOSITORY:$IMAGE_TAG $(aws sts get-caller-identity --query Account --output text).dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG

# Docker 이미지 푸시
docker push $(aws sts get-caller-identity --query Account --output text).dkr.ecr.$AWS_REGION.amazonaws.com/$ECR_REPOSITORY:$IMAGE_TAG