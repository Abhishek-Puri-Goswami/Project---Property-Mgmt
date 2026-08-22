import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Card from '../components/common/Card.jsx'
import Input from '../components/common/Input.jsx'
import Button from '../components/common/Button.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import { useAuth } from '../context/AuthContext.jsx'
import { useToast } from '../context/ToastContext.jsx'
import { login } from '../api/adminApi.js'

function validate({ email, password }) {
  const errors = {}
  if (!email || !email.trim()) {
    errors.email = 'Email is required'
  }
  if (!password) {
    errors.password = 'Password is required'
  }
  return errors
}

export default function LoginPage() {
  const [values, setValues] = useState({ email: '', password: '' })
  const [errors, setErrors] = useState({})
  const [formError, setFormError] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const { login: setAuth } = useAuth()
  const { showToast } = useToast()
  const navigate = useNavigate()

  function handleChange(field) {
    return (event) => setValues((current) => ({ ...current, [field]: event.target.value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const validationErrors = validate(values)
    setErrors(validationErrors)
    setFormError(null)
    if (Object.keys(validationErrors).length > 0) {
      return
    }

    setSubmitting(true)
    try {
      const response = await login(values)
      setAuth(response.user, response.token)
      showToast('Login successful', 'success')
      navigate('/')
    } catch (error) {
      setFormError(error.message || 'Unable to log in')
      showToast(error.message || 'Unable to log in', 'error')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div style={{ maxWidth: '420px', margin: '80px auto' }}>
      <Card>
        <h1>PropertyHub Admin</h1>
        {formError && <ErrorState message={formError} />}
        <form onSubmit={handleSubmit}>
          <Input label="Email" name="email" type="email" value={values.email} onChange={handleChange('email')} error={errors.email} />
          <Input label="Password" name="password" type="password" value={values.password} onChange={handleChange('password')} error={errors.password} />
          <Button type="submit" disabled={submitting}>{submitting ? 'Logging in...' : 'Login'}</Button>
        </form>
      </Card>
    </div>
  )
}
