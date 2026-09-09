import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getPlayers, createMatch } from '../api/api';
import Loading from '../components/Loading';
import ErrorState from '../components/ErrorState';

export default function CreateMatch() {
  const navigate = useNavigate();
  const [players, setPlayers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState(null);
  const [form, setForm] = useState({
    sideAPlayer1Id: '', sideAPlayer2Id: '',
    sideBPlayer1Id: '', sideBPlayer2Id: '',
    sideAScore: '', sideBScore: '',
    playedAt: new Date().toISOString().slice(0, 16),
    notes: '',
  });

  useEffect(() => {
    getPlayers()
      .then(res => setPlayers(res.data.filter(p => p.active)))
      .catch(err => setError(err.response?.data?.message || 'Failed to load players'))
      .finally(() => setLoading(false));
  }, []);

  const handleChange = (field, value) => setForm(prev => ({ ...prev, [field]: value }));
  const selectedIds = [form.sideAPlayer1Id, form.sideAPlayer2Id, form.sideBPlayer1Id, form.sideBPlayer2Id].filter(Boolean).map(Number);

  const filterPlayers = (excludeId) => players.filter(p => !selectedIds.includes(p.id) || p.id === Number(excludeId));

  const handleSubmit = (e) => {
    e.preventDefault();
    setFormError(null);
    setSaving(true);
    createMatch({
      ...form,
      sideAPlayer1Id: Number(form.sideAPlayer1Id), sideAPlayer2Id: Number(form.sideAPlayer2Id),
      sideBPlayer1Id: Number(form.sideBPlayer1Id), sideBPlayer2Id: Number(form.sideBPlayer2Id),
      sideAScore: Number(form.sideAScore), sideBScore: Number(form.sideBScore),
      playedAt: new Date(form.playedAt).toISOString(),
    })
      .then(res => navigate(`/matches/${res.data.id}`))
      .catch(err => setFormError(err.response?.data?.message || 'Failed to create match'))
      .finally(() => setSaving(false));
  };

  if (loading) return <Loading message="Loading players..." />;
  if (error) return <ErrorState message={error} />;

  return (
    <div className="match-detail">
      <div className="page-header">
        <h1>Create Match</h1>
        <p>Record a new doubles match</p>
      </div>
      {formError && <div className="error-toast">{formError}</div>}
      <div className="card">
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="form-section">
              <div className="form-section-title">Date & Time</div>
              <div className="form-group">
                <input type="datetime-local" className="form-control" value={form.playedAt} onChange={e => handleChange('playedAt', e.target.value)} required />
              </div>
            </div>
            <div className="form-section">
              <div className="form-section-title">SIDE A</div>
              <div className="form-row">
                <div className="form-group"><label>Player 1</label>
                  <select className="form-control" value={form.sideAPlayer1Id} onChange={e => handleChange('sideAPlayer1Id', e.target.value)} required>
                    <option value="">Select Player</option>
                    {filterPlayers(form.sideAPlayer1Id).map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                  </select>
                </div>
                <div className="form-group"><label>Player 2</label>
                  <select className="form-control" value={form.sideAPlayer2Id} onChange={e => handleChange('sideAPlayer2Id', e.target.value)} required>
                    <option value="">Select Player</option>
                    {filterPlayers(form.sideAPlayer2Id).map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                  </select>
                </div>
              </div>
              <div className="form-group"><label>Score</label>
                <input type="number" className="form-control" min="0" value={form.sideAScore} onChange={e => handleChange('sideAScore', e.target.value)} required style={{maxWidth:120}} />
              </div>
            </div>
            <div className="form-section">
              <div className="form-section-title">SIDE B</div>
              <div className="form-row">
                <div className="form-group"><label>Player 1</label>
                  <select className="form-control" value={form.sideBPlayer1Id} onChange={e => handleChange('sideBPlayer1Id', e.target.value)} required>
                    <option value="">Select Player</option>
                    {filterPlayers(form.sideBPlayer1Id).map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                  </select>
                </div>
                <div className="form-group"><label>Player 2</label>
                  <select className="form-control" value={form.sideBPlayer2Id} onChange={e => handleChange('sideBPlayer2Id', e.target.value)} required>
                    <option value="">Select Player</option>
                    {filterPlayers(form.sideBPlayer2Id).map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                  </select>
                </div>
              </div>
              <div className="form-group"><label>Score</label>
                <input type="number" className="form-control" min="0" value={form.sideBScore} onChange={e => handleChange('sideBScore', e.target.value)} required style={{maxWidth:120}} />
              </div>
            </div>
            <div className="form-section">
              <div className="form-group"><label>Notes (optional)</label>
                <textarea className="form-control" rows="3" value={form.notes} onChange={e => handleChange('notes', e.target.value)} />
              </div>
            </div>
            <button type="submit" className="btn btn-success" disabled={saving}>{saving ? '⏳ Saving...' : '💾 Save Match'}</button>
          </form>
        </div>
      </div>
    </div>
  );
}
