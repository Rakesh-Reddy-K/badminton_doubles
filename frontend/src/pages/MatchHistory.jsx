import { useState, useEffect } from 'react';
import { getMatches, getDayStatistics } from '../api/api';
import MatchCard from '../components/MatchCard';
import Loading from '../components/Loading';
import Pagination from '../components/Pagination';

function DayStatsGrid({ dayStats }) {
  if (!dayStats) return null;
  const topPlayer = dayStats.topPlayers && dayStats.topPlayers.length > 0;
  const mostActive = dayStats.mostActivePlayers && dayStats.mostActivePlayers.length > 0;
  return (
    <div className="stat-grid">
      <div className="stat-card">
        <div className="stat-card-label">Matches Played</div>
        <div className="stat-card-value">{dayStats.totalMatches}</div>
      </div>
      <div className="stat-card">
        <div className="stat-card-label">Players Active</div>
        <div className="stat-card-value">{dayStats.totalPlayers}</div>
      </div>
      <div className="stat-card">
        <div className="stat-card-label">Most Active</div>
        <div className="stat-card-value stat-card-value-lg" title={mostActive ? dayStats.mostActivePlayers[0].playerName : 'No matches'}>
          {mostActive ? dayStats.mostActivePlayers[0].playerName : 'N/A'}
        </div>
      </div>
      <div className="stat-card">
        <div className="stat-card-label">Top Win Rate</div>
        <div className="stat-card-value">
          {topPlayer ? `${dayStats.topPlayers[0].winPercentage}%` : 'N/A'}
        </div>
      </div>
    </div>
  );
}

export default function MatchHistory() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState('newest');
  const [filterDate, setFilterDate] = useState('');
  const [dayStats, setDayStats] = useState(null);
  const size = 10;

  useEffect(() => {
    setLoading(true);
    const params = { page, size, sort };
    if (filterDate) {
      params.from = filterDate;
      params.to = filterDate;
    }
    getMatches(params)
      .then(res => setData(res.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, [page, sort, filterDate]);

  useEffect(() => {
    if (filterDate) {
      getDayStatistics(filterDate)
        .then(res => setDayStats(res.data))
        .catch(() => setDayStats(null));
    } else {
      setDayStats(null);
    }
  }, [filterDate]);

  return (
    <div>
      <div className="page-header">
        <h1>Match History</h1>
        <p>All recorded matches</p>
      </div>

      <div className="toolbar">
        <select className="form-control" value={sort} onChange={e => { setSort(e.target.value); setPage(0); }} style={{ maxWidth: 160 }}>
          <option value="newest">Newest First</option>
          <option value="oldest">Oldest First</option>
        </select>
        <div className="date-filter-group">
          <span className="date-filter-icon">📅</span>
          <input
            type="date"
            className="form-control"
            value={filterDate}
            onChange={e => { setFilterDate(e.target.value); setPage(0); }}
          />
          {filterDate && (
            <button className="btn btn-sm btn-outline date-clear-btn" onClick={() => { setFilterDate(''); setPage(0); }}>
              ✕ Clear
            </button>
          )}
        </div>
      </div>

      {filterDate ? (
        <>
          <div className="day-dashboard-header">
            <div>
              <h2>📅 Day Dashboard</h2>
              <p>Statistics for <strong>{filterDate}</strong></p>
            </div>
          </div>
          <DayStatsGrid dayStats={dayStats} />

          <div className="day-dashboard-body">
            <div>
              <div className="card">
                <div className="card-header">
                  <span>📋 Day's Matches</span>
                </div>
                <div className="card-body">
                  {loading ? <Loading /> : !data || data.content.length === 0 ? (
                    <div className="empty-state" style={{ padding: '32px' }}>
                      <div className="icon">📋</div>
                      <h3>No matches on this date</h3>
                      <p>Try selecting a different date</p>
                    </div>
                  ) : (
                    <>
                      {data.content.map(m => <MatchCard key={m.id} match={m} />)}
                      <Pagination page={data.page} totalPages={data.totalPages} onPageChange={setPage} />
                    </>
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
                  {dayStats && dayStats.mostActivePlayers && dayStats.mostActivePlayers.length > 0 ? (
                    dayStats.mostActivePlayers.map((p, i) => (
                      <div key={p.playerId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 0', borderBottom: i < dayStats.mostActivePlayers.length - 1 ? '1px solid var(--border-light)' : 'none' }}>
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
                  {dayStats && dayStats.topPlayers && dayStats.topPlayers.length > 0 ? (
                    dayStats.topPlayers.map((p, i) => (
                      <div key={p.playerId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '10px 0', borderBottom: i < dayStats.topPlayers.length - 1 ? '1px solid var(--border-light)' : 'none' }}>
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
        </>
      ) : (
        <>
          {loading ? <Loading /> : !data || data.content.length === 0 ? (
            <div className="empty-state">
              <div className="icon">📋</div>
              <h3>No matches found</h3>
              <p>Start recording matches to see them here</p>
            </div>
          ) : (
            <>
              {data.content.map(m => <MatchCard key={m.id} match={m} />)}
              <Pagination page={data.page} totalPages={data.totalPages} onPageChange={setPage} />
              <p style={{ textAlign: 'center', marginTop: 12, fontSize: 13, color: 'var(--text-light)' }}>
                Showing {data.content.length} of {data.totalElements} matches
              </p>
            </>
          )}
        </>
      )}
    </div>
  );
}
