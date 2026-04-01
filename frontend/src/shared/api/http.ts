import axios from 'axios';
import { env } from '@/shared/config/env';

export const API_BASE_URL = env.apiBaseUrl;

export const http = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10_000,
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
});
