const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export function validateRegister({ email, password, role }) {
  const errors = {}

  if (!email || !email.trim()) {
    errors.email = 'Email is required'
  } else if (!EMAIL_PATTERN.test(email)) {
    errors.email = 'Email must be a valid email address'
  }

  if (!password) {
    errors.password = 'Password is required'
  } else if (password.length < 8) {
    errors.password = 'Password must be at least 8 characters long'
  }

  if (!role) {
    errors.role = 'Role is required'
  }

  return errors
}

export function validateLogin({ email, password }) {
  const errors = {}

  if (!email || !email.trim()) {
    errors.email = 'Email is required'
  }

  if (!password) {
    errors.password = 'Password is required'
  }

  return errors
}
