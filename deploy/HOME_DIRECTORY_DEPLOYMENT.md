# Home Directory Deployment Guide

This simplified guide deploys your Node.js + MongoDB backend using your home directory instead of `/opt/app`.

## 🚀 Quick Deployment Steps

### 1. Create DigitalOcean Droplet
- Ubuntu 22.04 LTS
- At least 1GB RAM (2GB recommended)
- Add your SSH key

### 2. Setup Server (One-time)
```bash
# Connect to your droplet
ssh root@your-droplet-ip

# Run setup script (installs Docker, Nginx, etc.)
curl -O https://raw.githubusercontent.com/yourusername/yourrepo/main/deploy/setup-server.sh
chmod +x setup-server.sh
./setup-server.sh

# IMPORTANT: Log out and back in for Docker permissions
exit
ssh root@your-droplet-ip
```

### 3. Deploy Your Application
```bash
# Create app directory in home
mkdir ~/myapp
cd ~/myapp

# Clone your repository
git clone https://github.com/yourusername/yourrepo.git .

# Configure environment variables
cp deploy/env.example deploy/.env
nano deploy/.env
```

**Fill in your production values in `deploy/.env`:**
```env
PORT=3000
NODE_ENV=production
DB_NAME=yourapp
MONGO_ROOT_PASSWORD=your_secure_root_password_123
MONGO_APP_PASSWORD=your_app_password_456
JWT_SECRET=your_very_long_and_secure_jwt_secret_key_here
GOOGLE_CLIENT_ID=your_google_client_id.apps.googleusercontent.com
```

```bash
# Deploy the application
cd deploy
chmod +x deploy.sh
./deploy.sh
```

## 🎯 That's It!

Your backend will be running at:
- **Direct access**: `http://your-droplet-ip:3000/api/`
- **Health check**: `http://your-droplet-ip:3000/api/health`

## 🔧 Management Commands

```bash
# Navigate to your app
cd ~/myapp/deploy

# Check status
docker-compose ps

# View logs
docker-compose logs -f backend
docker-compose logs -f mongodb

# Restart services
docker-compose restart

# Update application
cd ~/myapp
git pull
cd deploy
./deploy.sh

# Stop services
docker-compose down
```

## 📁 Directory Structure
```
~/myapp/
├── backend/
│   ├── src/
│   ├── uploads/          # File uploads stored here
│   ├── Dockerfile        # Moved here for easier builds
│   └── package.json
├── deploy/
│   ├── docker-compose.yml
│   ├── .env             # Your production config
│   ├── deploy.sh
│   └── mongo-init.js
└── frontend/
```

## 🌐 Optional: Add Domain & SSL

If you have a domain name:

1. **Point DNS A record** to your droplet IP

2. **Update Nginx config:**
```bash
sudo nano /etc/nginx/sites-available/yourapp
# Replace 'your-domain.com' with your actual domain
# Replace '/home/your-username/' with your actual username
```

3. **Enable the site:**
```bash
sudo ln -s /etc/nginx/sites-available/yourapp /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

4. **Get SSL certificate:**
```bash
sudo certbot --nginx -d yourdomain.com
```

## 🛡️ Security Notes

- MongoDB is only accessible within Docker network
- Firewall allows only SSH, HTTP, and HTTPS
- Regular backups created during deployments
- Application runs as non-root user in containers

## 🚨 Troubleshooting

**Services won't start?**
```bash
docker-compose logs
```

**Permission issues with uploads?**
```bash
chmod -R 755 ~/myapp/backend/uploads
```

**Can't access from outside?**
```bash
sudo ufw status  # Check firewall
docker-compose ps  # Check if services are running
```

This approach is simpler, avoids permission issues, and works perfectly for most deployments! 