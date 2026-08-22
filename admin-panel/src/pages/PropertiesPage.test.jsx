import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, fireEvent, waitFor } from '@testing-library/react'
import { ToastProvider } from '../context/ToastContext.jsx'
import * as adminApi from '../api/adminApi.js'
import PropertiesPage from './PropertiesPage.jsx'

vi.mock('../api/adminApi.js')

function renderPage() {
  return render(
    <ToastProvider>
      <PropertiesPage />
    </ToastProvider>
  )
}

const pendingProperty = { id: 1, title: '2BHK in Hinjewadi', city: 'Pune', price: 7200000, status: 'PENDING' }
const activeProperty = { id: 2, title: '3BHK in Wakad', city: 'Pune', price: 8500000, status: 'ACTIVE' }

describe('PropertiesPage', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('shows an Approve button only for PENDING properties', async () => {
    adminApi.listPropertiesAdmin.mockResolvedValue([pendingProperty, activeProperty])

    renderPage()

    await screen.findByText('2BHK in Hinjewadi')
    const approveButtons = screen.getAllByText('Approve')
    expect(approveButtons).toHaveLength(1)
  })

  it('calls approveProperty when Approve is clicked', async () => {
    adminApi.listPropertiesAdmin.mockResolvedValue([pendingProperty])
    adminApi.approveProperty.mockResolvedValue({})

    renderPage()

    await screen.findByText('2BHK in Hinjewadi')
    fireEvent.click(screen.getByText('Approve'))

    await waitFor(() => expect(adminApi.approveProperty).toHaveBeenCalledWith(1))
  })

  it('calls deleteProperty when Delete is clicked', async () => {
    adminApi.listPropertiesAdmin.mockResolvedValue([activeProperty])
    adminApi.deleteProperty.mockResolvedValue({})

    renderPage()

    await screen.findByText('3BHK in Wakad')
    fireEvent.click(screen.getByText('Delete'))

    await waitFor(() => expect(adminApi.deleteProperty).toHaveBeenCalledWith(2))
  })

  it('opens the edit modal with fetched property data', async () => {
    adminApi.listPropertiesAdmin.mockResolvedValue([activeProperty])
    adminApi.getProperty.mockResolvedValue({ ...activeProperty, description: 'Nice place', propertyType: 'APARTMENT', furnishing: 'FURNISHED', parking: true, bhk: 3, area: 1400, agentId: 4 })

    renderPage()

    await screen.findByText('3BHK in Wakad')
    fireEvent.click(screen.getByText('Edit'))

    expect(await screen.findByText('Edit Property')).toBeInTheDocument()
  })
})
