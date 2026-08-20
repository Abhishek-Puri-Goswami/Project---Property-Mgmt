import { describe, it, expect, beforeEach } from 'vitest'
import { render, screen, fireEvent } from '@testing-library/react'
import { ComparisonProvider, useComparison, STORAGE_KEY } from './ComparisonContext.jsx'

function Probe() {
  const { ids, toggle, clear, isFull } = useComparison()
  return (
    <div>
      <span data-testid="count">{ids.length}</span>
      <span data-testid="full">{isFull ? 'full' : 'not-full'}</span>
      {[1, 2, 3, 4, 5].map((id) => (
        <button key={id} onClick={() => toggle(id)}>toggle-{id}</button>
      ))}
      <button onClick={clear}>clear</button>
    </div>
  )
}

describe('ComparisonContext', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('adds and removes ids, persisting to localStorage', () => {
    render(<ComparisonProvider><Probe /></ComparisonProvider>)

    fireEvent.click(screen.getByText('toggle-1'))
    expect(screen.getByTestId('count').textContent).toBe('1')
    expect(JSON.parse(localStorage.getItem(STORAGE_KEY))).toEqual([1])

    fireEvent.click(screen.getByText('toggle-1'))
    expect(screen.getByTestId('count').textContent).toBe('0')
  })

  it('caps selection at 4 items', () => {
    render(<ComparisonProvider><Probe /></ComparisonProvider>)

    fireEvent.click(screen.getByText('toggle-1'))
    fireEvent.click(screen.getByText('toggle-2'))
    fireEvent.click(screen.getByText('toggle-3'))
    fireEvent.click(screen.getByText('toggle-4'))
    expect(screen.getByTestId('full').textContent).toBe('full')

    fireEvent.click(screen.getByText('toggle-5'))
    expect(screen.getByTestId('count').textContent).toBe('4')
  })

  it('clear empties the selection', () => {
    render(<ComparisonProvider><Probe /></ComparisonProvider>)

    fireEvent.click(screen.getByText('toggle-1'))
    fireEvent.click(screen.getByText('clear'))
    expect(screen.getByTestId('count').textContent).toBe('0')
  })
})
