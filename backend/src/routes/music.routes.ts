import { Router } from 'express';

import { upload } from '../storage';
import { authenticateToken } from '../middleware/auth.middleware';
import { MusicController } from '../controllers/music.controller';

const router = Router();
const musicController = new MusicController();

router.post(
  '/search-artists',
  musicController.searchForArtists.bind(musicController)
);

export default router;