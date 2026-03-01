output "spring_app_private_ip" {
  description = "Spring 애플리케이션 EC2 인스턴스의 사설 IP"
  value       = aws_instance.spring_app.private_ip
}

output "spring_app_public_ip" {
  description = "Spring 애플리케이션 EC2 인스턴스의 공인 IP"
  value       = aws_instance.spring_app.public_ip
}

output "k6_load_generator_private_ip" {
  description = "k6 부하 생성기 EC2 인스턴스의 사설 IP"
  value       = aws_instance.k6_load_generator.private_ip
}

output "k6_load_generator_public_ip" {
  description = "k6 부하 생성기 EC2 인스턴스의 공인 IP"
  value       = aws_instance.k6_load_generator.public_ip
}

output "prometheus_grafana_private_ip" {
  description = "Prometheus, Grafana EC2 인스턴스의 사설 IP"
  value       = aws_instance.prometheus_grafana.private_ip
}

output "prometheus_grafana_public_ip" {
  description = "Prometheus, Grafana EC2 인스턴스의 공인 IP"
  value       = aws_instance.prometheus_grafana.public_ip
}

output "rds_endpoint" {
  description = "RDS 엔드포인트"
  value       = aws_db_instance.mysql.endpoint
}