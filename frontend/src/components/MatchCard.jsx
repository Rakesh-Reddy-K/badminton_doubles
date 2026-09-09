import { useNavigate } from 'react-router-dom';
import { formatShortDate, getPlayerNames } from '../utils/format';

export default function MatchCard({ match }) {
  const navigate = useNavigate();
  const isWinnerA = match.winnerSide === 'A';

  return (
    <div className="match-card" onClick={() => navigate(`/matches/${match.id}`)}>
      <div className="match-card-header">
        <span className="match-card-id">Match #{match.id}</span>
        <span className="match-card-date">{formatShortDate(match.playedAt)}</span>
      </div>
      <div className="match-card-sides">
        <div className={`match-side ${isWinnerA ? 'winner' : 'loser'}`}>
          <div className="match-side-players">{getPlayerNames(match.sideAPlayers)}</div>
          <div className="match-side-score">{match.sideAScore}</div>
        </div>
        <div className="match-vs">VS</div>
        <div className={`match-side ${!isWinnerA ? 'winner' : 'loser'}`}>
          <div className="match-side-players">{getPlayerNames(match.sideBPlayers)}</div>
          <div className="match-side-score">{match.sideBScore}</div>
        </div>
      </div>
      <div className="match-card-winner">
        🏆 Winner: {isWinnerA ? getPlayerNames(match.sideAPlayers) : getPlayerNames(match.sideBPlayers)}
      </div>
    </div>
  );
}
