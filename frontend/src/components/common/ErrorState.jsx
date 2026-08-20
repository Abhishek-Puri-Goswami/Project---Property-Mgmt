export default function ErrorState({ message = 'Something went wrong.' }) {
  return (
    <div role="alert" style={{ padding: 'var(--space-lg)', textAlign: 'center', color: 'var(--color-error)' }}>
      {message}
    </div>
  )
}
