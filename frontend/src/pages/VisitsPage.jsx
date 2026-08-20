import { useEffect, useState } from 'react'
import Card from '../components/common/Card.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import EmptyState from '../components/common/EmptyState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { listVisitsByUser, listVisitsByAgent } from '../api/propertyApi.js'

export default function VisitsPage() {
  const { user } = useAuth()
  const [visits, setVisits] = useState([])
  const [status, setStatus] = useState('loading')

  useEffect(() => {
    const fetchVisits = user.role === 'AGENT' ? listVisitsByAgent(user.id) : listVisitsByUser(user.id)
    fetchVisits
      .then((data) => {
        setVisits(data)
        setStatus(data.length === 0 ? 'empty' : 'success')
      })
      .catch(() => setStatus('error'))
  }, [user.id, user.role])

  return (
    <Card>
      <h1>Scheduled Visits</h1>
      {status === 'loading' && <LoadingState />}
      {status === 'error' && <ErrorState message="Unable to load visits." />}
      {status === 'empty' && <EmptyState message="No visits scheduled yet." />}
      {status === 'success' && visits.map((visit) => (
        <div key={visit.id} className="clay" style={{ padding: 'var(--space-md)', marginBottom: 'var(--space-sm)' }}>
          <strong>{visit.propertyTitle}</strong>
          <p>{new Date(visit.scheduledAt).toLocaleString()} — {visit.status}</p>
          {visit.notes && <p>{visit.notes}</p>}
        </div>
      ))}
    </Card>
  )
}
