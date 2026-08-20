import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import ChatInput from './ChatInput.jsx'

describe('ChatInput', () => {
  it('sends the trimmed message and clears the input', () => {
    const onSend = vi.fn()
    render(<ChatInput onSend={onSend} loading={false} />)

    const input = screen.getByPlaceholderText('Ask about properties, localities, or your budget...')
    fireEvent.change(input, { target: { value: '  2BHK in Pune  ' } })
    fireEvent.click(screen.getByText('Send'))

    expect(onSend).toHaveBeenCalledWith('2BHK in Pune')
    expect(input.value).toBe('')
  })

  it('does not send an empty message', () => {
    const onSend = vi.fn()
    render(<ChatInput onSend={onSend} loading={false} />)

    fireEvent.click(screen.getByText('Send'))

    expect(onSend).not.toHaveBeenCalled()
  })

  it('disables the send button while loading', () => {
    render(<ChatInput onSend={vi.fn()} loading />)
    expect(screen.getByText('Sending...')).toBeDisabled()
  })
})
