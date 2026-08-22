import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen } from '@testing-library/react'
import * as adminApi from '../api/adminApi.js'
import UsersPage from './UsersPage.jsx'
import AgentsPage from './AgentsPage.jsx'

vi.mock('../api/adminApi.js')

describe('UsersPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('renders all users when no roleFilter is given', async () => {
    adminApi.listUsers.mockResolvedValue([
      { id: 1, email: 'buyer@example.com', role: 'BUYER', createdAt: '2026-08-21T00:00:00' },
      { id: 2, email: 'agent@example.com', role: 'AGENT', createdAt: '2026-08-21T00:00:00' },
    ])

    render(<UsersPage />)

    expect(await screen.findByText('buyer@example.com')).toBeInTheDocument()
    expect(screen.getByText('agent@example.com')).toBeInTheDocument()
  })

  it('shows an empty state when there are no users', async () => {
    adminApi.listUsers.mockResolvedValue([])

    render(<UsersPage />)

    expect(await screen.findByText('No users found.')).toBeInTheDocument()
  })
})

describe('AgentsPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('filters to only AGENT role users', async () => {
    adminApi.listUsers.mockResolvedValue([
      { id: 1, email: 'buyer@example.com', role: 'BUYER', createdAt: '2026-08-21T00:00:00' },
      { id: 2, email: 'agent@example.com', role: 'AGENT', createdAt: '2026-08-21T00:00:00' },
    ])

    render(<AgentsPage />)

    expect(await screen.findByText('agent@example.com')).toBeInTheDocument()
    expect(screen.queryByText('buyer@example.com')).not.toBeInTheDocument()
  })
})
