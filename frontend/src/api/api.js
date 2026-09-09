import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE_URL || '';

const api = axios.create({
  baseURL: API_BASE,
  headers: { 'Content-Type': 'application/json' },
});

// Players
export const getPlayers = (search) => api.get('/api/players', { params: { search } });
export const getPlayer = (id) => api.get(`/api/players/${id}`);
export const createPlayer = (data) => api.post('/api/players', data);
export const updatePlayer = (id, data) => api.put(`/api/players/${id}`, data);
export const updatePlayerStatus = (id, active) => api.patch(`/api/players/${id}/status`, { active });
export const deletePlayer = (id) => api.delete(`/api/players/${id}`);

// Matches
export const getMatches = (params) => api.get('/api/matches', { params });
export const getMatch = (id) => api.get(`/api/matches/${id}`);
export const createMatch = (data) => api.post('/api/matches', data);
export const updateMatch = (id, data) => api.put(`/api/matches/${id}`, data);
export const deleteMatch = (id) => api.delete(`/api/matches/${id}`);

// Statistics
export const getDashboard = () => api.get('/api/statistics/dashboard');
export const getDayStatistics = (date) => api.get('/api/statistics/day', { params: { date } });
export const getPlayerStats = () => api.get('/api/statistics/players');
export const getPlayerStat = (id) => api.get(`/api/statistics/players/${id}`);
export const getRankings = (minMatches = 0) => api.get('/api/statistics/players/ranking', { params: { minMatches } });
export const getPairStats = (minMatches = 0) => api.get('/api/statistics/pairs', { params: { minMatches } });

export default api;
