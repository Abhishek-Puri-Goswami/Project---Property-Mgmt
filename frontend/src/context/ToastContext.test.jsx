import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { render, screen, fireEvent, act } from '@testing-library/react'
import { ToastProvider, useToast, TOAST_TIMEOUT_MS } from './ToastContext.jsx'

function Probe() {
  const { toasts, showToast } = useToast()
  return (
    <div>
      <button onClick={() => showToast('Saved successfully', 'success')}>show</button>
      <span data-testid="count">{toasts.length}</span>
    </div>
  )
}

describe('ToastContext', () => {
  beforeEach(() => {
    vi.useFakeTimers()
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  it('showToast adds a toast that auto-removes after the timeout', () => {
    render(
      <ToastProvider>
        <Probe />
      </ToastProvider>
    )

    act(() => {
      fireEvent.click(screen.getByText('show'))
    })
    expect(screen.getByTestId('count').textContent).toBe('1')

    act(() => {
      vi.advanceTimersByTime(TOAST_TIMEOUT_MS)
    })
    expect(screen.getByTestId('count').textContent).toBe('0')
  })
})
