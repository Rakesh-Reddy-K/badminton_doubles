export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null;

  const pages = [];
  const start = Math.max(0, page - 2);
  const end = Math.min(totalPages - 1, page + 2);
  for (let i = start; i <= end; i++) pages.push(i);

  return (
    <div className="pagination">
      <button disabled={page === 0} onClick={() => onPageChange(0)}>«</button>
      <button disabled={page === 0} onClick={() => onPageChange(page - 1)}>‹</button>
      {pages.map(p => (
        <button key={p} className={p === page ? 'active' : ''} onClick={() => onPageChange(p)}>
          {p + 1}
        </button>
      ))}
      <button disabled={page >= totalPages - 1} onClick={() => onPageChange(page + 1)}>›</button>
      <button disabled={page >= totalPages - 1} onClick={() => onPageChange(totalPages - 1)}>»</button>
    </div>
  );
}
