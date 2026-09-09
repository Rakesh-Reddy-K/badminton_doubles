import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getPlayer, getPlayerStat } from '../api/api';
import { formatDateTime } from '../utils/format';
import Loading from '../components/Loading';

export default function PlayerDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [player, setPlayer] = useState(null);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([getPlayer(id), getPlayerStat(id)])
      .then(([pRes, sRes]) => { setPlayer(pRes.data); setStats(sRes.data); })
      .catch(() => navigate('/players'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Loading />;
  if (!player) return null;

  const initials = player.name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
  const colors = ['#14b8a6', '#22c55e', '#06b6d4', '#10b981', '#f59e0b', '#64748b', '#0d9488'];
  const colorIndex = player.name.charCodeAt(0) % colors.length;

  return (
    <div className="match-detail">
      <div className="page-header" style={{ display: 'flex', alignItems: 'center', gap: 20 }}>
        <div style={{
          width: 56, height: 56, borderRadius: 'var(--radius-full)',
          background: `linear-gradient(135deg, ${colors[colorIndex]}, ${colors[(colorIndex + 1) % colors.length]})`,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          color: 'white', fontWeight: 800, fontSize: 20, flexShrink: 0,
        }}>
          {initials}
        </div>
        <div>
          <h1>{player.name}</h1>
          <p>{player.email || ''} {player.phone ? `• ${player.phone}` : ''}</p>
        </div>
      </div>

      {stats && (
        <div className="stat-grid">
          <div className="stat-card">
            <div className="stat-card-label">Matches Played</div>
            <div className="stat-card-value">{stats.matchesPlayed}</div>
          </div>
          <div className="stat-card">
            <div className="stat-card-label">Wins</div>
            <div className="stat-card-value" style={{ color: 'var(--success)' }}>{stats.wins}</div>
          </div>
          <div className="stat-card">
            <div className="stat-card-label">Losses</div>
            <div className="stat-card-value" style={{ color: 'var(--danger)' }}>{stats.losses}</div>
          </div>
          <div className="stat-card">
            <div className="stat-card-label">Win Rate</div>
            <div className="stat-card-value">{stats.winPercentage}%</div>
          </div>
        </div>
      )}

      <div className="card">
        <div className="card-header">
          <span>📋 Player Info</span>
          <span className={`badge ${player.active ? 'badge-success' : 'badge-danger'}`}>{player.active ? 'Active' : 'Inactive'}</span>
        </div>
        <div className="card-body">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
            <div>
              <div style={{ fontSize: 12, fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.06em', color: 'var(--text-secondary)', marginBottom: 4 }}>Created</div>
              <div style={{ fontSize: 14, fontWeight: 500 }}>{formatDateTime(player.createdAt)}</div>
            </div>
            <div>
              <div style={{ fontSize: 12, fontWeight: 700, textTransform: 'uppercase', letterSpacing: '0.06em', color: 'var(--text-secondary)', marginBottom: 4 }}>Last Updated</div>
              <div style={{ fontSize: 14, fontWeight: 500 }}>{formatDateTime(player.updatedAt)}</div>
            </div>
          </div>
        </div>
      </div>

      <div style={{ marginTop: 24 }}>
        <button className="btn btn-outline" onClick={() => navigate('/players')}>← Back to Players</button>
      </div>
    </div>
  );
}
