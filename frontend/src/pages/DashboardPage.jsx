import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import Card from '../components/common/Card.jsx'
import Button from '../components/common/Button.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import EmptyState from '../components/common/EmptyState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import PropertyForm from '../components/property/PropertyForm.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { useToast } from '../context/ToastContext.jsx'
import { create, listVisitsByAgent } from '../api/propertyApi.js'

function BuyerDashboard({ user }) {
  return (
    <Card>
      <h1>Welcome, {user.email}</h1>
      <div style={{ display: 'flex', gap: 'var(--space-md)', marginTop: 'var(--space-md)' }}>
        <Link to="/properties"><Button>Search Properties</Button></Link>
        <Link to="/favorites"><Button variant="secondary">My Favorites</Button></Link>
        <Link to="/visits"><Button variant="secondary">My Visits</Button></Link>
      </div>
    </Card>
  )
}

function AgentDashboard({ user }) {
  const [visits, setVisits] = useState([]);
  const [status, setStatus] = useState('loading')
  const { showToast } = useToast()

  useEffect(() => {
    let active = true
    listVisitsByAgent(user.id)
      .then((data) => {
        if (active) {
          setVisits(data)
          setStatus('success')
        }
      })
      .catch(() => active && setStatus('error'))
    return () => { active = false }
  }, [user.id])

  async function handleCreate(values) {
    try {
      await create({ ...values, agentId: user.id })
      showToast('Property created successfully', 'success')
    } catch (error) {
      showToast(error.message || 'Unable to create property', 'error')
    }
  }

  return (
    <>
      <Card>
        <h1>Welcome, {user.email}</h1>
        <div style={{ display: 'flex', gap: 'var(--space-md)', marginTop: 'var(--space-md)' }}>
          <Link to="/properties"><Button variant="secondary">Browse Properties</Button></Link>
        </div>
      </Card>
      <Card style={{ marginTop: 'var(--space-md)' }}>
        <h2>Create Property</h2>
        <PropertyForm onSubmit={handleCreate} submitLabel="Create Property" />
      </Card>
      <Card style={{ marginTop: 'var(--space-md)' }}>
        <h2>Scheduled Visits</h2>
        {status === 'loading' && <LoadingState />}
        {status === 'error' && <ErrorState message="Unable to load visits." />}
        {status === 'success' && visits.length === 0 && <EmptyState message="No visits scheduled yet." />}
        {status === 'success' && visits.map((visit) => (
          <p key={visit.id}>{visit.propertyTitle} — {new Date(visit.scheduledAt).toLocaleString()} ({visit.status})</p>
        ))}
      </Card>
    </>
  )
}

export default function DashboardPage() {
  const { user } = useAuth()

  if (!user) {
    return <EmptyState message="Please log in." />
  }

  return user.role === 'AGENT' ? <AgentDashboard user={user} /> : <BuyerDashboard user={user} />
}
