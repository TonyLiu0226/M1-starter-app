#!/bin/bash

# Deployment script for DigitalOcean
# This script handles the complete deployment process

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
DEPLOY_DIR="$HOME/myapp"
BACKUP_DIR="$HOME/myapp/backups"
COMPOSE_FILE="docker-compose.yml"

echo -e "${GREEN}🚀 Starting deployment process...${NC}"

# Check if running as root (not recommended)

# Check if .env file exists
if [ ! -f ".env" ]; then
    echo -e "${RED}❌ .env file not found! Please create one based on env.example${NC}"
    exit 1
fi

# Check if Docker is running
if ! docker info >/dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker first.${NC}"
    exit 1
fi

# Create backup directory
mkdir -p $BACKUP_DIR

# Function to backup database
backup_database() {
    echo -e "${YELLOW}📦 Creating database backup...${NC}"
    BACKUP_FILE="$BACKUP_DIR/mongodb-backup-$(date +%Y%m%d-%H%M%S).tar.gz"
    
    # Check if MongoDB container is running
    if docker-compose ps mongodb | grep -q "Up"; then
        docker-compose exec -T mongodb mongodump --archive | gzip > $BACKUP_FILE
        echo -e "${GREEN}✅ Database backup created: $BACKUP_FILE${NC}"
    else
        echo -e "${YELLOW}⚠️ MongoDB container not running, skipping backup${NC}"
    fi
}

# Function to deploy application
deploy_app() {
    echo -e "${YELLOW}🏗️ Building and deploying application...${NC}"
    
    # Pull latest images
    docker-compose pull
    
    # Build backend image
    docker-compose build --no-cache backend
    
    # Stop existing containers
    docker-compose down
    
    # Start services
    docker-compose up -d
    
    # Wait for services to be healthy
    echo -e "${YELLOW}⏳ Waiting for services to be healthy...${NC}"
    sleep 30
    
    # Check if services are running
    if docker-compose ps | grep -q "Up"; then
        echo -e "${GREEN}✅ Services are running${NC}"
    else
        echo -e "${RED}❌ Some services failed to start${NC}"
        docker-compose logs
        exit 1
    fi
}

# Function to test deployment
test_deployment() {
    echo -e "${YELLOW}🧪 Testing deployment...${NC}"
    
    # Test backend health
    if curl -f -s http://localhost:3000/api/health >/dev/null; then
        echo -e "${GREEN}✅ Backend health check passed${NC}"
    else
        echo -e "${RED}❌ Backend health check failed${NC}"
        docker-compose logs backend
        exit 1
    fi
    
    # Test MongoDB connection
    if docker-compose exec -T mongodb mongo --eval "db.adminCommand('ping')" >/dev/null 2>&1; then
        echo -e "${GREEN}✅ MongoDB connection test passed${NC}"
    else
        echo -e "${RED}❌ MongoDB connection test failed${NC}"
        docker-compose logs mongodb
        exit 1
    fi
}

# Function to cleanup old backups (keep last 7 days)
cleanup_backups() {
    echo -e "${YELLOW}🧹 Cleaning up old backups...${NC}"
    find $BACKUP_DIR -name "mongodb-backup-*.tar.gz" -mtime +7 -delete
    echo -e "${GREEN}✅ Old backups cleaned up${NC}"
}

# Main deployment process
main() {
    echo -e "${GREEN}📍 Deploying to: $DEPLOY_DIR${NC}"
    
    # Change to deployment directory
    cd $DEPLOY_DIR/deploy
    
    # Create backup (if existing deployment)
    backup_database
    
    # Deploy application
    deploy_app
    
    # Test deployment
    test_deployment
    
    # Cleanup old backups
    cleanup_backups
    
    echo -e "${GREEN}🎉 Deployment completed successfully!${NC}"
    echo -e "${GREEN}📊 Service status:${NC}"
    docker-compose ps
    
    echo -e "${YELLOW}📋 Useful commands:${NC}"
    echo -e "  View logs: docker-compose logs -f"
    echo -e "  Restart services: docker-compose restart"
    echo -e "  Stop services: docker-compose down"
    echo -e "  Update services: ./deploy.sh"
}

# Run main function
main "$@" 