import { useState } from 'react'
import Input from '../common/Input.jsx'
import Button from '../common/Button.jsx'

export default function ChatInput({ onSend, loading }) {
  const [message, setMessage] = useState('')

  function handleSubmit(event) {
    event.preventDefault()
    const trimmed = message.trim()
    if (!trimmed || loading) {
      return
    }
    onSend(trimmed)
    setMessage('')
  }

  return (
    <form onSubmit={handleSubmit} style={{ display: 'flex', gap: 'var(--space-sm)', alignItems: 'flex-start' }}>
      <div style={{ flex: 1 }}>
        <Input
          name="message"
          placeholder="Ask about properties, localities, or your budget..."
          value={message}
          onChange={(event) => setMessage(event.target.value)}
        />
      </div>
      <Button type="submit" disabled={loading}>{loading ? 'Sending...' : 'Send'}</Button>
    </form>
  )
}
