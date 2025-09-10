import { Express } from 'express';

export type ArtistRequest = {
  artist: string;
};

export type ArtistResponse = {
  id: string;
  name: string;
  genres: string[];
};

export type MusicRequest = {
    genre: string;
}

export type MusicResponse = {
    url: string;
}