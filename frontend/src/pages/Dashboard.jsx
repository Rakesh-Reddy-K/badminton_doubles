import { useState, useEffect, useCallback } from 'react';
import { getDashboard } from '../api/api';
import MatchCard from '../components/MatchCard';
import Loading from '../components/Loading';
import ErrorState from '../components/ErrorState';

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadData = useCallback(() => {
    setLoading(true);
    setError(null);
    getDashboard()
      .then(res => setData(res.data))
      .catch(err => setError(err.response?.data?.message || 'Failed to load dashboard'))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => { loadData(); }, [loadData]);

  if (loading) return <Loading message="Loading dashboard..." />;
  if (error) return <ErrorState message={error} onRetry={loadData} />;
  if (!data) return null;

  return (
    <div>
      <div className="page-header">
        <h1>Dashboard</h1>
        <p>Welcome back! Here's your badminton overview</p>
      </div>

      <div className="stat-grid">
        <div className="stat-card">
          <div className="stat-card-label">Total Matches</div>
          <div className="stat-card-value">{data.totalMatches}</div>
        </div>
        <div className="stat-card">
          <div className="stat-card-label">Total Players</div>
          <div className="stat-card-value">{data.totalPlayers}</div>
        </div>
        <div className="stat-card">
          <div className="stat-card-label">Today's Matches</div>
          <div className="stat-card-value">{data.matchesToday}</div>
        </div>
        <div className="stat-card">
          <div className="stat-card-label">Top Win Rate</div>
          <div className="stat-card-value">
            {data.topPlayers && data.topPlayers.length > 0
              ? `${data.topPlayers[0].winPercentage}%`
              : 'N/A'}
          </div>
        </div>
      </div>

      <div className="dashboard-grid" style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '24px' }}>
        <div>
          <div className="card">
            <div className="card-header">
              <span>📋 Recent Matches</span>
            </div>
            <div className="card-body">
              {data.recentMatches && data.recentMatches.length > 0 ? (
                data.recentMatches.map(m => <MatchCard key={m.id} match={m} />)
              ) : (
                <div className="empty-state">
                  <div className="icon">🏸</div>
                  <h3>No matches played yet</h3>
                  <p>Record your first match to get started!</p>
                </div>
              )}
            </div>
          </div>
        </div>
        <div>
          <div className="card" style={{ marginBottom: '16px' }}>
            <div className="card-header">
              <span>🔥 Most Active Players</span>
            </div>
            <div className="card-body">
              {data.mostActivePlayers && data.mostActivePlayers.length > 0 ? (
                data.mostActivePlayers.map((p, i) => (
                  <div key={p.playerId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 0', borderBottom: i < data.mostActivePlayers.length - 1 ? '1px solid var(--border-light)' : 'none' }}>
                    <span style={{ fontWeight: 600, fontSize: '14px' }}>{p.playerName}</span>
                    <span style={{ color: 'var(--text-secondary)', fontSize: '13px', fontWeight: 500 }}>{p.matchesPlayed} matches</span>
                  </div>
                ))
              ) : (
                <div className="empty-state" style={{ padding: '24px' }}>
                  <p>No data</p>
                </div>
              )}
            </div>
          </div>
          <div className="card">
            <div className="card-header">
              <span>⭐ Top Players</span>
            </div>
            <div className="card-body">
              {data.topPlayers && data.topPlayers.length > 0 ? (
                data.topPlayers.map((p, i) => (
                  <div key={p.playerId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 0', borderBottom: i < data.topPlayers.length - 1 ? '1px solid var(--border-light)' : 'none' }}>
                    <span style={{ fontWeight: 600, fontSize: '14px' }}>{p.playerName}</span>
                    <span className="badge badge-success">{p.winPercentage}%</span>
                  </div>
                ))
              ) : (
                <div className="empty-state" style={{ padding: '24px' }}>
                  <p>No data</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
