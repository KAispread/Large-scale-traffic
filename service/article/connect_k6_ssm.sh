#!/bin/bash

# k6 EC2 인스턴스에 SSM을 통해 접속하는 스크립트

# AWS 리전 설정 (Terraform 변수에 따라 조정 가능)
AWS_REGION="ap-northeast-2"

# k6 EC2 인스턴스 ID 가져오기 (인스턴스 이름 기준)
INSTANCE_ID=$(aws ssm describe-instance-information --region $AWS_REGION --query 'InstanceInformationList[?contains(ComputerName, `kuke-board-mine-k6-load-generator`)].InstanceId' --output text)

if [ -z "$INSTANCE_ID" ]; then
  echo "k6 부하 생성기 인스턴스를 찾을 수 없습니다."
  exit 1
fi

echo "k6 인스턴스 ID: $INSTANCE_ID"

# SSM 세션 시작
aws ssm start-session --region $AWS_REGION --target $INSTANCE_ID