import { useState, useEffect } from 'react';
import { Routes, Route, NavLink, useLocation } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import Players from './pages/Players';
import PlayerDetails from './pages/PlayerDetails';
import CreateMatch from './pages/CreateMatch';
import MatchHistory from './pages/MatchHistory';
import MatchDetails from './pages/MatchDetails';
import PlayerRanking from './pages/PlayerRanking';
import PairStatistics from './pages/PairStatistics';

function HamburgerIcon({ open }) {
  return (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      {open ? (
        <>
          <line x1="18" y1="6" x2="6" y2="18" />
          <line x1="6" y1="6" x2="18" y2="18" />
        </>
      ) : (
        <>
          <line x1="3" y1="6" x2="21" y2="6" />
          <line x1="3" y1="12" x2="21" y2="12" />
          <line x1="3" y1="18" x2="21" y2="18" />
        </>
      )}
    </svg>
  );
}

function App() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();

  // Close sidebar on route change (mobile)
  useEffect(() => {
    setSidebarOpen(false);
  }, [location.pathname]);

  return (
    <div className="app-layout">
      {/* Mobile hamburger */}
      <button
        className="hamburger-btn"
        onClick={() => setSidebarOpen(!sidebarOpen)}
        aria-label="Toggle menu"
      >
        <HamburgerIcon open={sidebarOpen} />
      </button>

      {/* Mobile overlay */}
      <div
        className={`sidebar-overlay ${sidebarOpen ? 'visible' : ''}`}
        onClick={() => setSidebarOpen(false)}
      />

      <nav className={`sidebar ${sidebarOpen ? 'open' : ''}`}>
        <div className="sidebar-header">
          <h2>🏸 <span>BADMINTON</span></h2>
        </div>
        <div className="sidebar-nav">
          <NavLink to="/" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`} end>
            <span className="nav-icon">📊</span> Dashboard
          </NavLink>

          <div className="nav-section">Matches</div>
          <NavLink to="/matches" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <span className="nav-icon">📋</span> Match History
          </NavLink>
          <NavLink to="/matches/new" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <span className="nav-icon">➕</span> Add Match
          </NavLink>

          <div className="nav-section">Players</div>
          <NavLink to="/players" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <span className="nav-icon">👥</span> Players
          </NavLink>

          <div className="nav-section">Statistics</div>
          <NavLink to="/rankings" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <span className="nav-icon">🏆</span> Player Ranking
          </NavLink>
          <NavLink to="/pairs" className={({ isActive }) => `nav-link ${isActive ? 'active' : ''}`}>
            <span className="nav-icon">🤝</span> Pair Statistics
          </NavLink>
        </div>
      </nav>

      <main className="main-content">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/players" element={<Players />} />
          <Route path="/players/:id" element={<PlayerDetails />} />
          <Route path="/matches" element={<MatchHistory />} />
          <Route path="/matches/new" element={<CreateMatch />} />
          <Route path="/matches/:id" element={<MatchDetails />} />
          <Route path="/rankings" element={<PlayerRanking />} />
          <Route path="/pairs" element={<PairStatistics />} />
        </Routes>
      </main>
    </div>
  );
}

export default App;
