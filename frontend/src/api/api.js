import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE_URL || '';

const MAX_RETRIES = 3;
const RETRY_DELAY = 3000; // 3 seconds between retries

// Simple retry logic for cold-start / 502 / 503 errors
async function requestWithRetry(config, retries = MAX_RETRIES) {
  for (let attempt = 0; attempt <= retries; attempt++) {
    try {
      return await api(config);
    } catch (err) {
      const status = err.response?.status;
      const isRetryable = !status || status === 502 || status === 503 || status === 504;

      if (isRetryable && attempt < retries) {
        // Wait before retrying, increasing delay each time
        await new Promise(r => setTimeout(r, RETRY_DELAY * (attempt + 1)));
        continue;
      }
      throw err;
    }
  }
}

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

// Players
export const getPlayers = (search) => requestWithRetry({ url: '/api/players', params: { search } });
export const getPlayer = (id) => requestWithRetry({ url: `/api/players/${id}` });
export const createPlayer = (data) => api.post('/api/players', data);
export const updatePlayer = (id, data) => api.put(`/api/players/${id}`, data);
export const updatePlayerStatus = (id, active) => api.patch(`/api/players/${id}/status`, { active });
export const deletePlayer = (id) => api.delete(`/api/players/${id}`);

// Matches
export const getMatches = (params) => requestWithRetry({ url: '/api/matches', params });
export const getMatch = (id) => requestWithRetry({ url: `/api/matches/${id}` });
export const createMatch = (data) => api.post('/api/matches', data);
export const updateMatch = (id, data) => api.put(`/api/matches/${id}`, data);
export const deleteMatch = (id) => api.delete(`/api/matches/${id}`);

// Statistics
export const getDashboard = () => requestWithRetry({ url: '/api/statistics/dashboard' });
export const getDayStatistics = (date) => requestWithRetry({ url: '/api/statistics/day', params: { date } });
export const getPlayerStats = () => requestWithRetry({ url: '/api/statistics/players' });
export const getPlayerStat = (id) => requestWithRetry({ url: `/api/statistics/players/${id}` });
export const getRankings = (minMatches = 0) => requestWithRetry({ url: '/api/statistics/players/ranking', params: { minMatches } });
export const getPairStats = (minMatches = 0) => requestWithRetry({ url: '/api/statistics/pairs', params: { minMatches } });

export default api;
