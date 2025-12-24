output "instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.franchise_api.id
}

output "public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.franchise_api.public_ip
}

output "ssh_command" {
  description = "SSH command to connect to the instance"
  value       = "ssh -i ${var.key_name}.pem ubuntu@${aws_instance.franchise_api.public_ip}"
}

output "app_url" {
  description = "Application URL"
  value       = "http://${aws_instance.franchise_api.public_ip}:8080"
}

output "check_logs_command" {
  description = "AWS CLI command to check user data logs"
  value       = "aws ec2 get-console-output --instance-id ${aws_instance.franchise_api.id} --region us-east-1"
}
