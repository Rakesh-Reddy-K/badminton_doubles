import { useState, useEffect } from 'react';
import { getPairStats } from '../api/api';
import Loading from '../components/Loading';

export default function PairStatistics() {
  const [pairs, setPairs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [minMatches, setMinMatches] = useState(0);

  useEffect(() => {
    setLoading(true);
    getPairStats(minMatches)
      .then(res => setPairs(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [minMatches]);

  return (
    <div>
      <div className="page-header">
        <h1>Pair Statistics</h1>
        <p>Doubles pair performance</p>
      </div>

      <div className="toolbar">
        <label style={{ fontSize: 14, fontWeight: 600 }}>Minimum matches:</label>
        <input type="number" className="form-control" min="0" value={minMatches} onChange={e => setMinMatches(Number(e.target.value))} style={{ maxWidth: 80 }} />
      </div>

      {loading ? <Loading /> : pairs.length === 0 ? (
        <div className="empty-state">
          <div className="icon">🤝</div>
          <h3>No pair data available</h3>
          <p>Lower the minimum matches filter or play more matches</p>
        </div>
      ) : (
        <div className="player-list">
          {pairs.map((ps, i) => (
            <div key={`${ps.player1Id}-${ps.player2Id}`} className="card">
              <div className="card-header">
                <span style={{ fontWeight: 700 }}>🤝 #{i + 1} {ps.player1Name} & {ps.player2Name}</span>
                <span className="badge badge-success">{ps.winPercentage}%</span>
              </div>
              <div className="card-body">
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 12, textAlign: 'center' }}>
                  <div>
                    <div style={{ fontSize: 10, fontWeight: 700, color: 'var(--text-secondary)', marginBottom: 4, textTransform: 'uppercase', letterSpacing: '0.08em' }}>Matches</div>
                    <div style={{ fontSize: 22, fontWeight: 800 }}>{ps.matchesPlayed}</div>
                  </div>
                  <div>
                    <div style={{ fontSize: 10, fontWeight: 700, color: 'var(--text-secondary)', marginBottom: 4, textTransform: 'uppercase', letterSpacing: '0.08em' }}>Wins</div>
                    <div style={{ fontSize: 22, fontWeight: 800, color: 'var(--success)' }}>{ps.wins}</div>
                  </div>
                  <div>
                    <div style={{ fontSize: 10, fontWeight: 700, color: 'var(--text-secondary)', marginBottom: 4, textTransform: 'uppercase', letterSpacing: '0.08em' }}>Losses</div>
                    <div style={{ fontSize: 22, fontWeight: 800, color: 'var(--danger)' }}>{ps.losses}</div>
                  </div>
                </div>
                <div style={{ marginTop: 16 }}>
                  <div className="win-rate-bar" style={{ width: '100%', height: 8 }}>
                    <div className="win-rate-bar-fill" style={{ width: `${ps.winPercentage}%` }}></div>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
