export default function ErrorState({ message, onRetry }) {
  return (
    <div className="error-state">
      <div className="error-state-icon">⚠️</div>
      <h3>Something went wrong</h3>
      <p>{message || 'The server might be waking up. Please wait a moment and try again.'}</p>
      {onRetry && (
        <button className="btn btn-primary" onClick={onRetry} style={{ marginTop: 16 }}>
          🔄 Retry
        </button>
      )}
    </div>
  );
}