import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { AuthProvider, STORAGE_KEY } from '../context/AuthContext.jsx'
import { ToastProvider } from '../context/ToastContext.jsx'
import { ComparisonProvider } from '../context/ComparisonContext.jsx'
import * as propertyApi from '../api/propertyApi.js'
import PropertySearchPage from './PropertySearchPage.jsx'

vi.mock('../api/propertyApi.js')

function renderPage() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify({ user: { id: 5, email: 'buyer@example.com', role: 'BUYER' }, token: 'jwt-token' }))
  return render(
    <AuthProvider>
      <ToastProvider>
        <ComparisonProvider>
          <MemoryRouter>
            <PropertySearchPage />
          </MemoryRouter>
        </ComparisonProvider>
      </ToastProvider>
    </AuthProvider>
  )
}

describe('PropertySearchPage', () => {
  beforeEach(() => {
    localStorage.clear()
    vi.clearAllMocks()
    propertyApi.listFavorites.mockResolvedValue([])
  })

  it('renders results on successful search', async () => {
    propertyApi.search.mockResolvedValue([
      { id: 1, title: '2BHK in Hinjewadi', city: 'Pune', price: 7200000, bhk: 2, area: 1150, propertyType: 'APARTMENT' },
    ])
    renderPage()

    expect(await screen.findByText('2BHK in Hinjewadi')).toBeInTheDocument()
  })

  it('shows an empty state when no results match', async () => {
    propertyApi.search.mockResolvedValue([])
    renderPage()

    expect(await screen.findByText('No properties match your search.')).toBeInTheDocument()
  })

  it('shows an error state when the search fails', async () => {
    propertyApi.search.mockRejectedValue(new Error('Server unavailable'))
    renderPage()

    expect(await screen.findByText('Unable to load properties.')).toBeInTheDocument()
  })

  it('re-runs search with filters on submit', async () => {
    propertyApi.search.mockResolvedValue([])
    renderPage()
    await waitFor(() => expect(propertyApi.search).toHaveBeenCalledOnce())
  })
})
