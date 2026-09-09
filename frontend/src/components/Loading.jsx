export default function Loading({ message = 'Loading...' }) {
  return (
    <div className="loading">
      <div className="spinner"></div>
      <span style={{ fontWeight: 500 }}>{message}</span>
    </div>
  );
}
