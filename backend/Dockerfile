# Use official Node.js runtime as base image
FROM node:18-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Copy package files
COPY package*.json ./

# Install ALL dependencies (including dev dependencies for building)
RUN npm install

# Copy TypeScript configuration
COPY tsconfig.json ./

# Copy source code
COPY src/ ./src/

# Build TypeScript to JavaScript
RUN npm run build

# Create uploads directory structure with proper permissions BEFORE switching users
RUN mkdir -p uploads/images && chown -R node:node uploads && chmod -R 755 uploads

# Remove dev dependencies after building to reduce image size
RUN npm prune --production

# Create non-root user for security
USER node

# Expose port
EXPOSE 3000

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD node healthcheck.js

# Start application
CMD ["npm", "start"] 