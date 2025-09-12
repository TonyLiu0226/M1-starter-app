#!/bin/bash

# DigitalOcean Droplet Setup Script for Node.js + MongoDB Backend
# Run this script on your fresh Ubuntu/Debian droplet

set -e

echo "🚀 Starting DigitalOcean server setup..."

# Update system packages
echo "📦 Updating system packages..."
sudo apt update && sudo apt upgrade -y

# Install essential packages
echo "🔧 Installing essential packages..."
sudo apt install -y curl wget git ufw fail2ban htop nano unzip software-properties-common apt-transport-https ca-certificates gnupg lsb-release

# Install Docker
echo "🐳 Installing Docker..."
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# Add current user to docker group
sudo usermod -aG docker $USER

# Install Docker Compose (standalone)
echo "🐙 Installing Docker Compose..."
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Install Nginx
echo "🌐 Installing Nginx..."
sudo apt install -y nginx

# Install Certbot for SSL certificates
echo "🔒 Installing Certbot..."
sudo apt install -y certbot python3-certbot-nginx

# Configure UFW Firewall
echo "🔥 Configuring firewall..."
sudo ufw default deny incoming
sudo ufw default allow outgoing
sudo ufw allow ssh
sudo ufw allow 'Nginx Full'
sudo ufw --force enable

# Configure fail2ban
echo "🛡️ Configuring fail2ban..."
sudo systemctl enable fail2ban
sudo systemctl start fail2ban

# Create application directory with proper permissions
echo "📁 Creating application directory..."
sudo mkdir -p /opt/app
sudo chown $USER:$USER /opt/app
sudo chmod 755 /opt/app

# Create uploads directory with proper permissions
sudo mkdir -p /opt/app/uploads
sudo chown -R www-data:www-data /opt/app/uploads
sudo chmod -R 755 /opt/app/uploads

# Enable and start services
echo "⚙️ Enabling services..."
sudo systemctl enable docker
sudo systemctl enable nginx
sudo systemctl start docker
sudo systemctl start nginx

# Create swapfile (recommended for small droplets)
echo "💾 Creating swap file..."
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# Install Node.js (for health checks and utilities)
echo "📗 Installing Node.js..."
curl -fsSL https://deb.nodesource.com/setup_18.x | sudo -E bash -
sudo apt install -y nodejs

echo "✅ Server setup completed!"
echo ""
echo "📋 Next steps:"
echo "1. Log out and log back in to apply Docker group membership"
echo "2. Change to /opt/app directory: cd /opt/app"
echo "3. Clone your repository: git clone https://github.com/yourusername/yourrepo.git ."
echo "4. Configure your .env file: cd deploy && cp env.example .env && nano .env"
echo "5. Run deployment: chmod +x deploy.sh && ./deploy.sh"
echo "6. Configure your domain and SSL with Certbot (optional)"
echo ""
echo "🔧 Useful commands:"
echo "  - Check Docker: docker --version"
echo "  - Check services: sudo systemctl status nginx docker"
echo "  - View logs: sudo journalctl -u nginx -f" 