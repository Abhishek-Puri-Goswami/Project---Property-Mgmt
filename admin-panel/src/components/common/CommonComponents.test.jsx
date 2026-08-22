import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import Button from './Button.jsx'
import Modal from './Modal.jsx'
import LoadingState from './LoadingState.jsx'
import EmptyState from './EmptyState.jsx'
import ErrorState from './ErrorState.jsx'

describe('Button', () => {
  it('fires onClick when enabled', () => {
    const onClick = vi.fn()
    render(<Button onClick={onClick}>Save</Button>)
    fireEvent.click(screen.getByText('Save'))
    expect(onClick).toHaveBeenCalledOnce()
  })

  it('does not fire onClick when disabled', () => {
    const onClick = vi.fn()
    render(<Button onClick={onClick} disabled>Save</Button>)
    fireEvent.click(screen.getByText('Save'))
    expect(onClick).not.toHaveBeenCalled()
  })
})

describe('Modal', () => {
  it('renders nothing when closed', () => {
    render(<Modal open={false} title="Test" onClose={vi.fn()}>Body</Modal>)
    expect(screen.queryByRole('dialog')).not.toBeInTheDocument()
  })

  it('renders content and fires onClose when open', () => {
    const onClose = vi.fn()
    render(<Modal open title="Test Modal" onClose={onClose}>Body content</Modal>)
    expect(screen.getByText('Test Modal')).toBeInTheDocument()
    fireEvent.click(screen.getByLabelText('Close'))
    expect(onClose).toHaveBeenCalledOnce()
  })
})

describe('LoadingState / EmptyState / ErrorState', () => {
  it('render their messages', () => {
    render(<LoadingState />)
    expect(screen.getByText('Loading...')).toBeInTheDocument()
    render(<EmptyState message="No data." />)
    expect(screen.getByText('No data.')).toBeInTheDocument()
    render(<ErrorState message="Failed." />)
    expect(screen.getByText('Failed.')).toBeInTheDocument()
  })
})
