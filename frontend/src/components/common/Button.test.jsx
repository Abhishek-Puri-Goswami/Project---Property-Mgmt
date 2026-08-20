import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import Button from './Button.jsx'

describe('Button', () => {
  it('renders children and fires onClick', () => {
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
