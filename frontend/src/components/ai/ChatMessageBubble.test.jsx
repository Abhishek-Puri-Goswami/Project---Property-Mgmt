import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import ChatMessageBubble from './ChatMessageBubble.jsx'

describe('ChatMessageBubble', () => {
  it('renders the message content', () => {
    render(<ChatMessageBubble role="USER" content="Find me a 2 BHK in Pune" />)
    expect(screen.getByText('Find me a 2 BHK in Pune')).toBeInTheDocument()
  })

  it('renders an assistant message', () => {
    render(<ChatMessageBubble role="ASSISTANT" content="I found 3 matching properties." />)
    expect(screen.getByText('I found 3 matching properties.')).toBeInTheDocument()
  })
})
