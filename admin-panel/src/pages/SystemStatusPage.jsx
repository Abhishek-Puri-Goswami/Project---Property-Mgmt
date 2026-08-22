import { useEffect, useState } from 'react'
import Card from '../components/common/Card.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import EmptyState from '../components/common/EmptyState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import Table from '../components/common/Table.jsx'
import { listServiceInstances } from '../api/adminApi.js'

export default function SystemStatusPage() {
  const [instances, setInstances] = useState([])
  const [status, setStatus] = useState('loading')

  useEffect(() => {
    listServiceInstances()
      .then((data) => {
        setInstances(data)
        setStatus(data.length === 0 ? 'empty' : 'success')
      })
      .catch(() => setStatus('error'))
  }, [])

  return (
    <Card>
      <h1>System Status</h1>
      {status === 'loading' && <LoadingState />}
      {status === 'error' && <ErrorState message="Unable to load service status." />}
      {status === 'empty' && <EmptyState message="No registered services found." />}
      {status === 'success' && (
        <Table
          rowKey="id"
          rows={instances}
          columns={[
            { key: 'name', header: 'Service', render: (i) => i.registration?.name || 'Unknown' },
            { key: 'status', header: 'Status', render: (i) => i.statusInfo?.status || 'UNKNOWN' },
            { key: 'serviceUrl', header: 'URL', render: (i) => i.registration?.serviceUrl || '—' },
          ]}
        />
      )}
    </Card>
  )
}
