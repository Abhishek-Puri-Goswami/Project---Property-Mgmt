import { describe, it, expect, vi } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import ScheduleVisitModal from './ScheduleVisitModal.jsx'

describe('ScheduleVisitModal', () => {
  it('rejects a past date', () => {
    const onSubmit = vi.fn()
    render(<ScheduleVisitModal open onClose={vi.fn()} onSubmit={onSubmit} />)

    fireEvent.change(screen.getByLabelText('Date and Time'), { target: { value: '2020-01-01T10:00' } })
    fireEvent.click(screen.getByText('Schedule'))

    expect(screen.getByText('Scheduled time must be in the future')).toBeInTheDocument()
    expect(onSubmit).not.toHaveBeenCalled()
  })

  it('submits a valid future date', () => {
    const onSubmit = vi.fn()
    render(<ScheduleVisitModal open onClose={vi.fn()} onSubmit={onSubmit} />)

    const future = new Date(Date.now() + 86400000).toISOString().slice(0, 16)
    fireEvent.change(screen.getByLabelText('Date and Time'), { target: { value: future } })
    fireEvent.click(screen.getByText('Schedule'))

    expect(onSubmit).toHaveBeenCalledOnce()
  })
})
