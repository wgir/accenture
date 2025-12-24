variable "key_name" {
  description = "EC2 key pair name"
}

variable "ecr_image" {
  description = "ECR image URL"
}

variable "rds_hostname" {
  description = "RDS database hostname"
}

variable "rds_port" {
  description = "RDS database port"
}

variable "rds_database" {
  description = "RDS database name"
}

variable "rds_username" {
  description = "RDS database username"
  sensitive   = true
}

variable "rds_password" {
  description = "RDS database password"
  sensitive   = true
}

variable "rds_security_group_id" {
  description = "The ID of the existing RDS security group"
}