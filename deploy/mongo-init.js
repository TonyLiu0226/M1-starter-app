// MongoDB initialization script
// This script creates an application user with proper permissions

const dbName = process.env.MONGO_INITDB_DATABASE || 'yourapp';
const appPassword = process.env.MONGO_APP_PASSWORD || 'changeme';

// Switch to the application database
db = db.getSiblingDB(dbName);

// Create application user
db.createUser({
  user: 'appuser',
  pwd: appPassword,
  roles: [
    {
      role: 'readWrite',
      db: dbName
    }
  ]
});

print('Database and user created successfully!'); 