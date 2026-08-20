import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import Card from '../components/common/Card.jsx'
import Input from '../components/common/Input.jsx'
import Select from '../components/common/Select.jsx'
import Button from '../components/common/Button.jsx'
import ErrorState from '../components/common/ErrorState.jsx'
import { useToast } from '../context/ToastContext.jsx'
import { validateRegister } from '../validation/authValidation.js'
import { register } from '../api/authApi.js'

const ROLE_OPTIONS = [
  { value: 'BUYER', label: 'Buyer' },
  { value: 'AGENT', label: 'Agent' },
]

export default function RegisterPage() {
  const [values, setValues] = useState({ email: '', password: '', role: 'BUYER' })
  const [errors, setErrors] = useState({})
  const [formError, setFormError] = useState(null)
  const [submitting, setSubmitting] = useState(false)
  const { showToast } = useToast()
  const navigate = useNavigate()

  function handleChange(field) {
    return (event) => setValues((current) => ({ ...current, [field]: event.target.value }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const validationErrors = validateRegister(values)
    setErrors(validationErrors)
    setFormError(null)
    if (Object.keys(validationErrors).length > 0) {
      return
    }

    setSubmitting(true)
    try {
      await register(values)
      showToast('Registration successful', 'success')
      navigate('/login')
    } catch (error) {
      setFormError(error.message || 'Unable to register')
      showToast(error.message || 'Unable to register', 'error')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div style={{ maxWidth: '420px', margin: '80px auto' }}>
      <Card>
        <h1>Register</h1>
        {formError && <ErrorState message={formError} />}
        <form onSubmit={handleSubmit}>
          <Input label="Email" name="email" type="email" value={values.email} onChange={handleChange('email')} error={errors.email} />
          <Input label="Password" name="password" type="password" value={values.password} onChange={handleChange('password')} error={errors.password} />
          <Select label="Role" name="role" value={values.role} onChange={handleChange('role')} options={ROLE_OPTIONS} error={errors.role} />
          <Button type="submit" disabled={submitting}>{submitting ? 'Registering...' : 'Register'}</Button>
        </form>
        <p>Already have an account? <Link to="/login">Login</Link></p>
      </Card>
    </div>
  )
}
