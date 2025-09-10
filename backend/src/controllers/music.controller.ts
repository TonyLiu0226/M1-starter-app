import { NextFunction, Request, Response } from 'express';

import logger from '../utils/logger.util';
import { MediaService } from '../services/media.service';
import { ArtistRequest, ArtistResponse } from '../types/music.types.js';
import { sanitizeInput } from '../utils/sanitizeInput.util';
import axios from 'axios';

//generate music using suno api
export class MusicController {
  
  private async getSpotifyAccessToken(): Promise<string> {
    const clientId = process.env.SPOTIFY_CLIENT_ID;
    const clientSecret = process.env.SPOTIFY_CLIENT_SECRET;
    
    if (!clientId || !clientSecret) {
      throw new Error('Spotify credentials not configured');
    }

    //must auth into spotify first before calling the API
    const response = await axios.post('https://accounts.spotify.com/api/token', 
      'grant_type=client_credentials',
      {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Authorization': `Basic ${Buffer.from(`${clientId}:${clientSecret}`).toString('base64')}`
        }
      }
    );
    
    return response.data.access_token;
  }

  async searchForArtists(
    req: Request<unknown, unknown, ArtistRequest>,
    res: Response<ArtistResponse>,
    next: NextFunction
  ) {
    try {
      // Get Spotify access token
      const accessToken = await this.getSpotifyAccessToken();
      
      //call spotify external API
      const apiResponse = await axios.get(`https://api.spotify.com/v1/search?q=${req.body.artist}&type=artist`, {
        headers: {
          Accept: 'application/json',
          Authorization: `Bearer ${accessToken}`,
          'Content-Type': 'application/json',
        }
      });
      
      if (apiResponse.status === 200 && apiResponse.data.artists?.items?.length > 0) {
        const firstArtist = apiResponse.data.artists.items[0];
        (res as Response).json({
          id: firstArtist.id,
          name: firstArtist.name
        });
        //404 error for no artists found, or status code other than 200 in initial response
      } else {
        return (res as Response).status(404).json({
          message: 'No artists found',
        });
      }
    } catch (error) {
      return (res as Response).status(500).json({
        message: 'Error getting artists',
        error: axios.isAxiosError(error) ? error.response?.data : 'Unknown error'
      });
    }
  }
}