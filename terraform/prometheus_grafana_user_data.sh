#!/bin/bash

# Docker 설치
yum update -y
amazon-linux-extras install docker -y
systemctl start docker
systemctl enable docker
usermod -a -G docker ec2-user

# Prometheus 설정 파일 생성
cat <<EOF > /tmp/prometheus.yml
global:
  scrape_interval: 5s
  evaluation_interval: 5s

scrape_configs:
- job_name: 'prometheus'
  static_configs:
  - targets: ['localhost:9090']
  
- job_name: 'spring-app'
  metrics_path: '/actuator/prometheus'
  static_configs:
  - targets: ['${spring_ip}:9000']
EOF

# Docker 네트워크 생성
docker network create monitoring-network

# Prometheus 컨테이너 실행
docker run -d --name prometheus --network monitoring-network -p 9090:9090 \
  -v /tmp/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus:latest \
  --config.file=/etc/prometheus/prometheus.yml \
  --web.enable-remote-write-receiver \
  --storage.tsdb.path=/prometheus

# Grafana 컨테이너 실행
docker run -d --name grafana --network monitoring-network -p 3000:3000 \
  -e GF_SECURITY_ADMIN_USER=admin \
  -e GF_SECURITY_ADMIN_PASSWORD=admin \
  -e GF_AUTH_ANONYMOUS_ENABLED=true \
  -e GF_AUTH_ANONYMOUS_ORG_ROLE=Admin \
  grafana/grafana:latest