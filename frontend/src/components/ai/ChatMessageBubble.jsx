export default function ChatMessageBubble({ role, content }) {
  const isUser = role === 'USER' || role === 'user'

  return (
    <div style={{ display: 'flex', justifyContent: isUser ? 'flex-end' : 'flex-start', marginBottom: 'var(--space-sm)' }}>
      <div
        className="clay"
        style={{
          maxWidth: '75%',
          padding: '10px 16px',
          background: isUser ? 'var(--color-primary)' : 'var(--color-surface)',
          color: isUser ? '#ffffff' : 'var(--color-text)',
          whiteSpace: 'pre-wrap',
        }}
      >
        {content}
      </div>
    </div>
  )
}
