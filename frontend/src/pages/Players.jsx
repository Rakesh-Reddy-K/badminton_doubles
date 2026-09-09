import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPlayers, createPlayer, updatePlayer, updatePlayerStatus, deletePlayer } from '../api/api';
import ConfirmDialog from '../components/ConfirmDialog';
import Loading from '../components/Loading';

function PlayerAvatar({ name }) {
  const initials = name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase();
  const colors = ['#14b8a6', '#22c55e', '#06b6d4', '#10b981', '#f59e0b', '#64748b', '#0d9488'];
  const colorIndex = name.charCodeAt(0) % colors.length;
  return (
    <div style={{
      width: 42, height: 42, borderRadius: 'var(--radius-full)',
      background: `linear-gradient(135deg, ${colors[colorIndex]}, ${colors[(colorIndex + 1) % colors.length]})`,
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      color: 'white', fontWeight: 800, fontSize: 14, flexShrink: 0,
    }}>
      {initials}
    </div>
  );
}

export default function Players() {
  const navigate = useNavigate();
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingPlayer, setEditingPlayer] = useState(null);
  const [formData, setFormData] = useState({ name: '', phone: '', email: '' });
  const [error, setError] = useState(null);
  const [confirmDialog, setConfirmDialog] = useState({ open: false, player: null });

  const loadPlayers = () => {
    setLoading(true);
    getPlayers(search || undefined)
      .then(res => setPlayers(res.data))
      .catch(err => setError(err.response?.data?.message || 'Failed to load players'))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadPlayers(); }, [search]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setError(null);
    const action = editingPlayer
      ? updatePlayer(editingPlayer.id, formData)
      : createPlayer(formData);
    action
      .then(() => { setShowForm(false); setEditingPlayer(null); setFormData({ name: '', phone: '', email: '' }); loadPlayers(); })
      .catch(err => setError(err.response?.data?.message || 'Failed to save player'));
  };

  const handleEdit = (player) => {
    setEditingPlayer(player);
    setFormData({ name: player.name, phone: player.phone || '', email: player.email || '' });
    setShowForm(true);
  };

  const handleToggleStatus = (player) => {
    updatePlayerStatus(player.id, !player.active)
      .then(() => loadPlayers())
      .catch(err => setError(err.response?.data?.message || 'Failed to update status'));
  };

  const handleDelete = () => {
    if (!confirmDialog.player) return;
    deletePlayer(confirmDialog.player.id)
      .then(() => { setConfirmDialog({ open: false, player: null }); loadPlayers(); })
      .catch(err => {
        setError(err.response?.data?.message || 'Failed to delete player');
        setConfirmDialog({ open: false, player: null });
      });
  };

  return (
    <div>
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 12 }}>
        <div>
          <h1>Players</h1>
          <p>Manage your team roster</p>
        </div>
        <button className="btn btn-primary" onClick={() => { setEditingPlayer(null); setFormData({ name: '', phone: '', email: '' }); setShowForm(true); }}>
          + Add Player
        </button>
      </div>

      {error && <div className="error-toast">{error}</div>}

      {showForm && (
        <div className="dialog-overlay" onClick={() => { setShowForm(false); setEditingPlayer(null); }}>
          <div className="dialog" onClick={e => e.stopPropagation()}>
            <h3>{editingPlayer ? '✏️ Edit Player' : '➕ Add New Player'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Name *</label>
                <input className="form-control" placeholder="Enter player name" value={formData.name} onChange={e => setFormData({...formData, name: e.target.value})} required maxLength={100} />
              </div>
              <div className="form-group">
                <label>Phone</label>
                <input className="form-control" placeholder="Enter phone number" value={formData.phone} onChange={e => setFormData({...formData, phone: e.target.value})} maxLength={20} />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input className="form-control" type="email" placeholder="Enter email address" value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} maxLength={255} />
              </div>
              <div className="dialog-actions">
                <button type="button" className="btn btn-outline" onClick={() => { setShowForm(false); setEditingPlayer(null); }}>Cancel</button>
                <button type="submit" className="btn btn-success">{editingPlayer ? '✓ Update' : '✓ Save'} Player</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <div className="toolbar">
        <input className="form-control" placeholder="Search players..." value={search} onChange={e => setSearch(e.target.value)} />
      </div>

      {loading ? <Loading /> : players.length === 0 ? (
        <div className="empty-state">
          <div className="icon">👥</div>
          <h3>No players found</h3>
          <p>Add your first player to get started</p>
        </div>
      ) : (
        <div className="player-list">
          {players.map(p => (
            <div key={p.id} className="player-card">
              <div style={{ display: 'flex', gap: 14, alignItems: 'center', cursor: 'pointer' }} onClick={() => navigate(`/players/${p.id}`)}>
                <PlayerAvatar name={p.name} />
                <div className="player-card-info">
                  <h3>{p.name} <span className={`badge ${p.active ? 'badge-success' : 'badge-danger'}`}>{p.active ? 'Active' : 'Inactive'}</span></h3>
                  <p>{p.phone || 'No phone'} {p.email ? `• ${p.email}` : ''}</p>
                </div>
              </div>
              <div className="player-card-actions">
                <button className="btn btn-sm btn-outline" onClick={(e) => { e.stopPropagation(); handleEdit(p); }}>✏️ Edit</button>
                <button className="btn btn-sm btn-outline" onClick={(e) => { e.stopPropagation(); handleToggleStatus(p); }}>{p.active ? '🔴 Deactivate' : '🟢 Activate'}</button>
                <button className="btn btn-sm btn-danger" onClick={(e) => { e.stopPropagation(); setConfirmDialog({ open: true, player: p }); }}>🗑️</button>
              </div>
            </div>
          ))}
        </div>
      )}

      <ConfirmDialog
        open={confirmDialog.open}
        title="Delete Player"
        message={`Are you sure you want to delete "${confirmDialog.player?.name}"? This action cannot be undone.`}
        onConfirm={handleDelete}
        onCancel={() => setConfirmDialog({ open: false, player: null })}
      />
    </div>
  );
}

