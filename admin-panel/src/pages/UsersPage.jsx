import { useEffect, useState } from 'react'
import Card from '../components/common/Card.jsx'
import LoadingState from '../components/common/LoadingState.jsx'
import EmptyState from '../components/common/EmptyState.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import Table from '../components/common/Table.jsx'
import { listUsers } from '../api/adminApi.js'

export default function UsersPage({ roleFilter, title = 'Users' }) {
  const [users, setUsers] = useState([])
  const [status, setStatus] = useState('loading')

  useEffect(() => {
    listUsers()
      .then((data) => {
        const filtered = roleFilter ? data.filter((user) => user.role === roleFilter) : data
        setUsers(filtered)
        setStatus(filtered.length === 0 ? 'empty' : 'success')
      })
      .catch(() => setStatus('error'))
  }, [roleFilter])

  return (
    <Card>
      <h1>{title}</h1>
      {status === 'loading' && <LoadingState />}
      {status === 'error' && <ErrorState message="Unable to load users." />}
      {status === 'empty' && <EmptyState message="No users found." />}
      {status === 'success' && (
        <Table
          rowKey="id"
          rows={users}
          columns={[
            { key: 'email', header: 'Email' },
            { key: 'role', header: 'Role' },
            { key: 'createdAt', header: 'Created Date', render: (u) => new Date(u.createdAt).toLocaleDateString() },
          ]}
        />
      )}
    </Card>
  )
}
