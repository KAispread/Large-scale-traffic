#!/bin/bash

# Docker 설치
yum update -y
amazon-linux-extras install docker -y
systemctl start docker
systemctl enable docker
usermod -a -G docker ec2-user

# ECR 로그인
aws ecr get-login-password --region ${aws_region} | docker login --username AWS --password-stdin ${account_id}.dkr.ecr.${aws_region}.amazonaws.com

# Docker 이미지 풀
docker pull ${account_id}.dkr.ecr.${aws_region}.amazonaws.com/kaispread/kuke-board:latest

# Docker 컨테이너 실행
docker run -d -p 9000:9000 -e SPRING_DATASOURCE_URL=jdbc:mysql://${db_endpoint}/article -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=root1234 ${account_id}.dkr.ecr.${aws_region}.amazonaws.com/kaispread/kuke-board:latest