import { NextFunction, Request, Response } from 'express';

import logger from '../utils/logger.util';
import { MediaService } from '../services/media.service';
import { ArtistRequest, ArtistResponse, MusicRequest, MusicResponse } from '../types/music.types.js';
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
        const firstArtist = apiResponse.data.artists.items[0]; //get first artist from response, typically is the most relevant one
        console.log(firstArtist);
        console.log(firstArtist.genres);
        
        if (!firstArtist.genres) {
          (res as Response).json({
            id: firstArtist.id,
            name: firstArtist.name,
            genres: [firstArtist.name] //in this case, just use the name as the search parameter in the next step
          });
        } else {
          (res as Response).json({
            id: firstArtist.id,
            name: firstArtist.name,
            genres: firstArtist.genres
          });
        }
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

    async downloadTrack(
    req: Request<unknown, unknown, MusicRequest>,
    res: Response<MusicResponse>,
    next: NextFunction
  ) {
    try {
      const serverList = await axios.get('https://api.audius.co', {
        headers: {
          'Content-Type': 'application/json',
        }
      });
      
      if (serverList.status !== 200 || !serverList.data.data.length) {
        return (res as Response).status(404).json({
          message: 'No servers found',
        });
      }

      const servers = serverList.data.data;
      console.log(`Found ${servers.length} servers to try`);
      
      // Try each server until we find tracks
      for (let i = 0; i < servers.length; i++) {
        const server = servers[i];
        try {
          const trackURL = `${server}/v1/tracks/search?query=${req.body.genre}&only_downloadable=true&has_downloads=true`;
          console.log(`Requesting: ${trackURL}`);
          
          const trackResponse = await axios.get(trackURL, {
            headers: {
              'Content-Type': 'application/json',
            }
          });
          // Check if this server returned tracks
          if (trackResponse.data.data && trackResponse.data.data.length > 0) {
            const tracks = trackResponse.data.data;
            
            //generate random index order to download tracks in
            let order = [];
            for (let k = 0; k < tracks.length; k++) {
              order.push(k);
            }
            order = order.sort(() => Math.random() - 0.5);
          
            //try every track in order
            for (let j = 0; j < order.length; j++) {
                const randomTrack = tracks[order[j]];
                if (!randomTrack || !randomTrack.id) {
                  console.log(`Invalid track data on server ${i + 1}, trying next server...`);
                  continue;
                }
                //sanity check if track is downloadable first. If not downloadable, try another track
                const downloadableResponse = await axios.get(`${server}/v1/tracks/${randomTrack.id}`, {
                  headers: {
                    'Content-Type': 'application/json',
                  }
                });
                if (downloadableResponse.data.data.is_downloadable) {
                  try {
                    const downloadURL = await axios.get(`${server}/v1/tracks/${randomTrack.id}/download`, {
                      headers: {
                        'Content-Type': 'application/json',
                      }
                    });
                    if (downloadURL.status === 200) {
                      // Return just the URL string, not the axios response object
                      const actualDownloadURL = `${server}/v1/tracks/${randomTrack.id}/download`;
                      return res.status(200).json({
                        url: actualDownloadURL
                      });
                    }
                  } catch (downloadError) {
                    console.error(`Error with download URL for track ${randomTrack.id}:`, downloadError);
                    continue;
                  }  
                }
              }
            }
        } catch (serverError) {
          //at this stage, can try another server
          console.error(`Error with server ${i + 1} (${server}):`, serverError);
          continue;
        }
        //if no tracks found on server, try another server
        console.log(`No tracks found on server ${i + 1}, trying next server...`);
      }
      
      // If we get here, no server had tracks
      return (res as Response).status(404).json({
        message: 'No tracks found for the specified genre on any server',
      });
      
    //ran into fatal exception
    } catch(error) {
      return (res as Response).status(500).json({
        message: 'Error downloading track',
        error: axios.isAxiosError(error) ? error.response?.data : 'Unknown error'
      });
    }
  }

}