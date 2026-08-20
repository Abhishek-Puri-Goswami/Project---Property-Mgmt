import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import Modal from './Modal.jsx'

describe('Modal', () => {
  it('renders nothing when closed', () => {
    render(<Modal open={false} title="Test" onClose={vi.fn()}>Body</Modal>)
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
  })

  it('renders content and fires onClose when open', () => {
    const onClose = vi.fn()
    render(<Modal open title="Test Modal" onClose={onClose}>Body content</Modal>)

    expect(screen.getByText('Test Modal')).toBeInTheDocument()
    expect(screen.getByText('Body content')).toBeInTheDocument()

    fireEvent.click(screen.getByLabelText('Close'))
    expect(onClose).toHaveBeenCalledOnce()
  })
})
