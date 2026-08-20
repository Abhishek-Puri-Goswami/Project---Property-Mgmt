export default function EmptyState({ message = 'Nothing here yet.' }) {
  return (
    <div style={{ padding: 'var(--space-lg)', textAlign: 'center', color: 'var(--color-text-muted)' }}>
      {message}
    </div>
  )
}
