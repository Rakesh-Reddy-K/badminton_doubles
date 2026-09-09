import { useState, useEffect, useCallback } from 'react';
import { getRankings } from '../api/api';
import Loading from '../components/Loading';
import ErrorState from '../components/ErrorState';

export default function PlayerRanking() {
  const [rankings, setRankings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [minMatches, setMinMatches] = useState(0);

  const loadRankings = useCallback(() => {
    setLoading(true);
    setError(null);
    getRankings(minMatches)
      .then(res => setRankings(res.data))
      .catch(err => setError(err.response?.data?.message || 'Failed to load rankings'))
      .finally(() => setLoading(false));
  }, [minMatches]);

  useEffect(() => { loadRankings(); }, [loadRankings]);

  return (
    <div>
      <div className="page-header">
        <h1>Player Ranking</h1>
        <p>Players ranked by win percentage</p>
      </div>

      <div className="toolbar">
        <label style={{ fontSize: 14, fontWeight: 600 }}>Minimum matches:</label>
        <input type="number" className="form-control" min="0" value={minMatches} onChange={e => setMinMatches(Number(e.target.value))} style={{ maxWidth: 80 }} />
      </div>

      {loading ? <Loading /> : error ? <ErrorState message={error} onRetry={loadRankings} /> : rankings.length === 0 ? (
        <div className="empty-state">
          <div className="icon">🏆</div>
          <h3>No rankings available</h3>
          <p>Lower the minimum matches filter or play more matches</p>
        </div>
      ) : (
        <div className="card">
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>Rank</th>
                  <th>Player</th>
                  <th>Matches</th>
                  <th>Wins</th>
                  <th>Losses</th>
                  <th>Win %</th>
                  <th>Win Rate</th>
                </tr>
              </thead>
              <tbody>
                {rankings.map((p, i) => (
                  <tr key={p.playerId}>
                    <td>
                      <div style={{ width: 32, height: 32, borderRadius: 'var(--radius-full)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 16, fontWeight: 800, background: i === 0 ? '#fef3c7' : i === 1 ? '#f1f5f9' : i === 2 ? '#fef3c7' : 'transparent', color: i === 0 ? '#b45309' : i === 1 ? '#475569' : i === 2 ? '#92400e' : 'var(--text)' }}>
                        {i === 0 ? '🥇' : i === 1 ? '🥈' : i === 2 ? '🥉' : i + 1}
                      </div>
                    </td>
                    <td style={{ fontWeight: 700 }}>{p.playerName}</td>
                    <td>{p.matchesPlayed}</td>
                    <td style={{ color: 'var(--success)', fontWeight: 600 }}>{p.wins}</td>
                    <td style={{ color: 'var(--danger)', fontWeight: 600 }}>{p.losses}</td>
                    <td style={{ fontWeight: 800 }}>{p.winPercentage}%</td>
                    <td>
                      <div className="win-rate-bar">
                        <div className="win-rate-bar-fill" style={{ width: `${p.winPercentage}%` }}></div>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
