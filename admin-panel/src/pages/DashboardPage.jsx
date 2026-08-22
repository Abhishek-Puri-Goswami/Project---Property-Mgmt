import { useEffect, useState } from 'react'
import Card from '../components/common/Card.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import Table from '../components/common/Table.jsx'
import { listUsers, listPropertiesAdmin, getAnalytics } from '../api/adminApi.js'

function StatTile({ label, value }) {
  return (
    <div className="clay" style={{ padding: 'var(--space-md)', textAlign: 'center', flex: 1 }}>
      <div style={{ fontSize: '1.8rem', fontWeight: 700 }}>{value}</div>
      <div style={{ color: 'var(--color-text-muted)' }}>{label}</div>
    </div>
  )
}

export default function DashboardPage() {
  const [users, setUsers] = useState([])
  const [properties, setProperties] = useState([])
  const [analytics, setAnalytics] = useState(null)
  const [status, setStatus] = useState('loading')

  useEffect(() => {
    Promise.all([listUsers(), listPropertiesAdmin(), getAnalytics()])
      .then(([usersData, propertiesData, analyticsData]) => {
        setUsers(usersData)
        setProperties(propertiesData)
        setAnalytics(analyticsData)
        setStatus('success')
      })
      .catch(() => setStatus('error'))
  }, [])

  if (status === 'loading') {
    return <LoadingState />
  }
  if (status === 'error') {
    return <ErrorState message="Unable to load dashboard data." />
  }

  const totalAgents = users.filter((user) => user.role === 'AGENT').length
  const recentProperties = [...properties].slice(-5).reverse()
  const recentUsers = [...users].slice(-5).reverse()

  return (
    <>
      <div style={{ display: 'flex', gap: 'var(--space-md)', marginBottom: 'var(--space-md)' }}>
        <StatTile label="Total Properties" value={properties.length} />
        <StatTile label="Total Users" value={users.length} />
        <StatTile label="Total Agents" value={totalAgents} />
        <StatTile label="AI Conversations" value={analytics.totalConversations} />
      </div>

      <Card style={{ marginBottom: 'var(--space-md)' }}>
        <h2>Recent Properties</h2>
        <Table
          rowKey="id"
          rows={recentProperties}
          columns={[
            { key: 'title', header: 'Property' },
            { key: 'city', header: 'Location' },
            { key: 'price', header: 'Price', render: (p) => `₹${Number(p.price).toLocaleString('en-IN')}` },
            { key: 'status', header: 'Status' },
          ]}
        />
      </Card>

      <Card>
        <h2>Recent Users</h2>
        <Table
          rowKey="id"
          rows={recentUsers}
          columns={[
            { key: 'email', header: 'Email' },
            { key: 'role', header: 'Role' },
            { key: 'createdAt', header: 'Created Date', render: (u) => new Date(u.createdAt).toLocaleDateString() },
          ]}
        />
      </Card>
    </>
  )
}
