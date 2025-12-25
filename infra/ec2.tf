resource "tls_private_key" "pk" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

resource "aws_key_pair" "franchise_key" {
  key_name   = var.key_name
  public_key = tls_private_key.pk.public_key_openssh
}

resource "local_file" "ssh_key" {
  filename        = "${path.module}/${var.key_name}.pem"
  content         = tls_private_key.pk.private_key_pem
  file_permission = "0400"
}

resource "aws_instance" "franchise_api" {
  ami                         = "ami-0fc5d935ebf8bc3bc" # Ubuntu 22.04 LTS (us-east-1)
  instance_type               = "t3.micro"
  key_name                    = aws_key_pair.franchise_key.key_name
  availability_zone           = "us-east-1c" # Match RDS AZ
  user_data_replace_on_change = true

  vpc_security_group_ids = [aws_security_group.franchise_sg.id]
  iam_instance_profile   = aws_iam_instance_profile.franchise_profile.name

  user_data = <<-EOF
    #!/bin/bash
    set -e
    
    # Update package list
    apt-get update -y
    
    # Install Docker
    apt-get install -y docker.io
    systemctl start docker
    systemctl enable docker
    
    # Install AWS CLI
    apt-get install -y awscli
    
    # Add ubuntu user to docker group
    usermod -aG docker ubuntu
    
    # Extract registry from ecr_image and login
    REGISTRY=$(echo "${var.ecr_image}" | cut -d'/' -f1)
    aws ecr get-login-password --region us-east-1 \
    | docker login --username AWS --password-stdin "$REGISTRY"

    # Pull and run container
    docker pull "${var.ecr_image}"
    docker run -d --name franchise-api -p 8080:8080 \
      --restart unless-stopped \
      -e RDS_HOSTNAME="${var.rds_hostname}" \
      -e RDS_PORT="${var.rds_port}" \
      -e RDS_DB_NAME="${var.rds_database}" \
      -e RDS_DB_USERNAME="${var.rds_username}" \
      -e RDS_DB_PASSWORD="${var.rds_password}" \
      "${var.ecr_image}"
  EOF

  tags = {
    Name = "franchise-api"
  }
}
