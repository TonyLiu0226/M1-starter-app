import { Router } from 'express';

import { authenticateToken } from './middleware/auth.middleware';
import authRoutes from './routes/auth.routes';
import hobbiesRoutes from './routes/hobbies.routes';
import mediaRoutes from './routes/media.routes';
import musicRoutes from './routes/music.routes';
import usersRoutes from './routes/user.routes';

const router = Router();

// Health check endpoint for monitoring
router.get('/health', (req, res) => {
  res.status(200).json({
    status: 'OK',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
    environment: process.env.NODE_ENV || 'development'
  });
});

router.use('/auth', authRoutes);

router.use('/music', musicRoutes);

router.use('/hobbies', authenticateToken, hobbiesRoutes);

router.use('/user', authenticateToken, usersRoutes);

router.use('/media', authenticateToken, mediaRoutes);

export default router;
