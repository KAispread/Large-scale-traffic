provider "aws" {
  region = var.aws_region
}

data "aws_caller_identity" "current" {}

resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = {
    Name = "kuke-board-mine-vpc"
  }
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.public_subnet_cidr
  availability_zone       = var.availability_zone
  map_public_ip_on_launch = true

  tags = {
    Name = "kuke-board-mine-public-subnet"
  }
}

resource "aws_subnet" "private" {
  count             = 2
  vpc_id            = aws_vpc.main.id
  cidr_block        = count.index == 0 ? var.private_subnet_cidr : cidrsubnet(var.vpc_cidr, 8, 3 + count.index)
  availability_zone = count.index == 0 ? "${var.aws_region}a" : "${var.aws_region}b"

  tags = {
    Name = count.index == 0 ? "kuke-board-mine-private-subnet" : "kuke-board-mine-private-subnet-2"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "kuke-board-mine-igw"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }

  tags = {
    Name = "kuke-board-mine-public-rt"
  }
}

resource "aws_route_table_association" "public" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

resource "aws_security_group" "spring_app" {
  name_prefix = "spring-app-sg-"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 9000
    to_port     = 9000
    protocol    = "tcp"
    cidr_blocks = [var.vpc_cidr]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "rds" {
  name_prefix = "rds-sg-"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.spring_app.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "k6_load_generator" {
  name_prefix = "k6-load-generator-sg-"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "prometheus_grafana" {
  name_prefix = "prometheus-grafana-sg-"
  vpc_id      = aws_vpc.main.id

  ingress {
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_ssm_parameter" "spring_app_ip" {
  name  = "/kuke-board/spring-app-ip"
  type  = "String"
  value = aws_instance.spring_app.private_ip

  tags = {
    Name = "kuke-board-spring-app-ip"
  }
}

resource "aws_db_instance" "mysql" {
  identifier             = "kuke-board-mine-mysql"
  db_name                = "article"
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = "db.t3.micro"
  username               = "root"
  password               = "root1234"
  parameter_group_name   = "default.mysql8.0"
  allocated_storage      = 20
  storage_type           = "gp2"
  publicly_accessible    = false
  vpc_security_group_ids = [aws_security_group.rds.id]
  db_subnet_group_name   = aws_db_subnet_group.main.name

  skip_final_snapshot = true
}

resource "aws_db_subnet_group" "main" {
  name       = "kuke-board-mine-db-subnet-group"
  subnet_ids = aws_subnet.private[*].id  # 두 개의 서브넷 ID를 모두 포함

  tags = {
    Name = "kuke-board-mine-db-subnet-group"
  }
}

resource "aws_iam_instance_profile" "ssm_profile" {
  name = "kuke-board-mine-ssm-profile"
  role = aws_iam_role.ssm_role.name
}

resource "aws_iam_role" "ssm_role" {
  name = "kuke-board-mine-ssm-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_policy" {
  role       = aws_iam_role.ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_instance" "spring_app" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.private[0].id
  vpc_security_group_ids = [aws_security_group.spring_app.id]
  iam_instance_profile   = aws_iam_instance_profile.ssm_profile.name

  user_data = base64encode(templatefile("${path.module}/user_data_spring_app.sh", {
    db_endpoint = aws_db_instance.mysql.endpoint
    aws_region  = var.aws_region
    account_id  = data.aws_caller_identity.current.account_id
  }))

  tags = {
    Name = "kuke-board-mine-spring-app"
  }
}

resource "aws_instance" "k6_load_generator" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.k6_load_generator.id]
  iam_instance_profile   = aws_iam_instance_profile.ssm_profile.name

  tags = {
    Name = "kuke-board-mine-k6-load-generator"
  }
}

resource "aws_instance" "prometheus_grafana" {
  ami                    = var.ami_id
  instance_type          = var.instance_type
  subnet_id              = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.prometheus_grafana.id]
  iam_instance_profile   = aws_iam_instance_profile.ssm_profile.name

  user_data = base64encode(templatefile("${path.module}/prometheus_grafana_user_data.sh", {
    aws_region = var.aws_region
    account_id = data.aws_caller_identity.current.account_id
    spring_ip = aws_ssm_parameter.spring_app_ip.value
  }))

  depends_on = [
    aws_ssm_parameter.spring_app_ip
  ]

  tags = {
    Name = "kuke-board-mine-prometheus-grafana"
  }
}