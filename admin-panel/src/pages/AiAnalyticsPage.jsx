import { useEffect, useState } from 'react'
import Card from '../components/common/Card.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import EmptyState from '../components/common/EmptyState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import Table from '../components/common/Table.jsx'
import { listConversations, getAnalytics } from '../api/adminApi.js'

export default function AiAnalyticsPage() {
  const [analytics, setAnalytics] = useState(null)
  const [conversations, setConversations] = useState([])
  const [status, setStatus] = useState('loading')

  useEffect(() => {
    Promise.all([getAnalytics(), listConversations()])
      .then(([analyticsData, conversationsData]) => {
        setAnalytics(analyticsData)
        setConversations(conversationsData)
        setStatus('success')
      })
      .catch(() => setStatus('error'))
  }, [])

  if (status === 'loading') {
    return <LoadingState />
  }
  if (status === 'error') {
    return <ErrorState message="Unable to load AI analytics." />
  }

  return (
    <>
      <Card style={{ marginBottom: 'var(--space-md)' }}>
        <h1>AI Activity</h1>
        <div style={{ display: 'flex', gap: 'var(--space-lg)' }}>
          <div>
            <div style={{ fontSize: '1.8rem', fontWeight: 700 }}>{analytics.totalConversations}</div>
            <div style={{ color: 'var(--color-text-muted)' }}>Total Conversations</div>
          </div>
          <div>
            <div style={{ fontSize: '1.8rem', fontWeight: 700 }}>{analytics.totalMessages}</div>
            <div style={{ color: 'var(--color-text-muted)' }}>Total AI Messages</div>
          </div>
        </div>
      </Card>

      <Card>
        <h2>Conversations</h2>
        {conversations.length === 0 ? (
          <EmptyState message="No conversations yet." />
        ) : (
          <Table
            rowKey="id"
            rows={conversations}
            columns={[
              { key: 'title', header: 'Title' },
              { key: 'userId', header: 'User' },
              { key: 'messageCount', header: 'Messages' },
              { key: 'updatedAt', header: 'Last Activity', render: (c) => new Date(c.updatedAt).toLocaleString() },
            ]}
          />
        )}
      </Card>
    </>
  )
}
