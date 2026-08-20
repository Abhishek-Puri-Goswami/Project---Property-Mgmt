import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { AuthProvider, STORAGE_KEY } from '../context/AuthContext.jsx'
import * as propertyApi from '../api/propertyApi.js'
import VisitsPage from './VisitsPage.jsx'

vi.mock('../api/propertyApi.js')

function renderPage(user) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ user, token: 'jwt-token' }))
  return render(
    <AuthProvider>
      <VisitsPage />
    </AuthProvider>
  )
}

describe('VisitsPage', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
  })

  it('fetches by userId for a BUYER', async () => {
    propertyApi.listVisitsByUser.mockResolvedValue([])
    renderPage({ id: 5, email: 'buyer@example.com', role: 'BUYER' })

    await waitFor(() => expect(propertyApi.listVisitsByUser).toHaveBeenCalledWith(5))
    expect(propertyApi.listVisitsByAgent).not.toHaveBeenCalled()
  })

  it('fetches by agentId for an AGENT', async () => {
    propertyApi.listVisitsByAgent.mockResolvedValue([])
    renderPage({ id: 9, email: 'agent@example.com', role: 'AGENT' })

    await waitFor(() => expect(propertyApi.listVisitsByAgent).toHaveBeenCalledWith(9))
    expect(propertyApi.listVisitsByUser).not.toHaveBeenCalled()
  })

  it('renders the visit list on success', async () => {
    propertyApi.listVisitsByUser.mockResolvedValue([
      { id: 1, propertyTitle: '2BHK in Hinjewadi', scheduledAt: '2026-08-23T10:00:00', status: 'PENDING', notes: null },
    ])
    renderPage({ id: 5, email: 'buyer@example.com', role: 'BUYER' })

    expect(await screen.findByText('2BHK in Hinjewadi')).toBeInTheDocument()
  })
})
