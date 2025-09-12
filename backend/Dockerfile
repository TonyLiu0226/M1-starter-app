# Use official Node.js runtime as base image
FROM node:18-alpine

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy TypeScript configuration
COPY tsconfig.json ./

# Install TypeScript globally for building
RUN npm install -g typescript

# Copy source code
COPY src/ ./src/

# Build TypeScript to JavaScript
RUN npm run build

# Remove TypeScript and dev dependencies to reduce image size
RUN npm prune --production

# Create uploads directory with proper permissions
RUN mkdir -p uploads && chown -R node:node uploads

# Create non-root user for security
USER node

# Expose port
EXPOSE 3000

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD node healthcheck.js

# Start application
CMD ["npm", "start"] 