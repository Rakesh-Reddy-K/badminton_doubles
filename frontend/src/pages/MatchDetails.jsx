import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getMatch, deleteMatch } from '../api/api';
import { formatDateTime, getPlayerNames } from '../utils/format';
import ConfirmDialog from '../components/ConfirmDialog';
import Loading from '../components/Loading';

export default function MatchDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [match, setMatch] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showDelete, setShowDelete] = useState(false);

  useEffect(() => {
    getMatch(id)
      .then(res => setMatch(res.data))
      .catch(() => navigate('/matches'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleDelete = () => {
    deleteMatch(id).then(() => navigate('/matches'));
  };

  if (loading) return <Loading />;
  if (!match) return null;

  const isWinnerA = match.winnerSide === 'A';

  return (
    <div className="match-detail">
      <div className="page-header" style={{ textAlign: 'center' }}>
        <h1>🏸 Match #{match.id}</h1>
        <p>{formatDateTime(match.playedAt)}</p>
      </div>

      <div className="match-detail-sides">
        <div className={`side-card ${isWinnerA ? 'winner' : ''}`}>
          <div className="side-card-title">Side A</div>
          {match.sideAPlayers.map(p => (
            <div key={p.id} className="side-card-player">{p.name}</div>
          ))}
          <div className="side-card-score">{match.sideAScore}</div>
        </div>
        <div className="match-detail-vs">VS</div>
        <div className={`side-card ${!isWinnerA ? 'winner' : ''}`}>
          <div className="side-card-title">Side B</div>
          {match.sideBPlayers.map(p => (
            <div key={p.id} className="side-card-player">{p.name}</div>
          ))}
          <div className="side-card-score">{match.sideBScore}</div>
        </div>
      </div>

      <div className="match-detail-winner">
        <div className="trophy">🏆</div>
        <div className="winner-text">
          Winner: {isWinnerA ? getPlayerNames(match.sideAPlayers) : getPlayerNames(match.sideBPlayers)}
        </div>
      </div>

      {match.notes && (
        <div className="match-detail-notes">
          <strong>Notes:</strong> {match.notes}
        </div>
      )}

      <div className="match-detail-actions" style={{ display: 'flex', gap: 12, marginTop: 24, justifyContent: 'center' }}>
        <button className="btn btn-outline" onClick={() => navigate('/matches')}>← Back to History</button>
        <button className="btn btn-danger" onClick={() => setShowDelete(true)}>Delete Match</button>
      </div>

      <ConfirmDialog
        open={showDelete}
        title="Delete Match"
        message="Are you sure you want to delete this match? This action cannot be undone."
        onConfirm={handleDelete}
        onCancel={() => setShowDelete(false)}
      />
    </div>
  );
}
