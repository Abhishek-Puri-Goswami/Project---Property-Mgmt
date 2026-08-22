export default function LoadingState({ message = 'Loading...' }) {
  return (
    <div role="status" style={{ padding: 'var(--space-lg)', textAlign: 'center', color: 'var(--color-text-muted)' }}>
      {message}
    </div>
  )
}
