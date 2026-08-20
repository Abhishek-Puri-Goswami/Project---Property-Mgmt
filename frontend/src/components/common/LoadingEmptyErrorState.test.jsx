import { describe, it, expect } from 'vitest'
import { render, screen } from '@testing-library/react'
import LoadingState from './LoadingState.jsx'
import EmptyState from './EmptyState.jsx'
import ErrorState from './ErrorState.jsx'

describe('LoadingState', () => {
  it('renders the default loading message', () => {
    render(<LoadingState />)
    expect(screen.getByText('Loading...')).toBeInTheDocument()
  })
})

describe('EmptyState', () => {
  it('renders a custom empty message', () => {
    render(<EmptyState message="No properties found." />)
    expect(screen.getByText('No properties found.')).toBeInTheDocument()
  })
})

describe('ErrorState', () => {
  it('renders a custom error message', () => {
    render(<ErrorState message="Unable to load properties." />)
    expect(screen.getByText('Unable to load properties.')).toBeInTheDocument()
  })
})
