import { describe, it, expect } from 'vitest'
import { validateRegister, validateLogin } from './authValidation.js'

describe('validateRegister', () => {
  it('returns no errors for valid input', () => {
    expect(validateRegister({ email: 'buyer@example.com', password: 'secret123', role: 'BUYER' })).toEqual({})
  })

  it('flags a missing email', () => {
    expect(validateRegister({ email: '', password: 'secret123', role: 'BUYER' }).email).toBeDefined()
  })

  it('flags an invalid email format', () => {
    expect(validateRegister({ email: 'not-an-email', password: 'secret123', role: 'BUYER' }).email).toBeDefined()
  })

  it('flags a short password', () => {
    expect(validateRegister({ email: 'buyer@example.com', password: '123', role: 'BUYER' }).password).toBeDefined()
  })

  it('flags a missing role', () => {
    expect(validateRegister({ email: 'buyer@example.com', password: 'secret123', role: '' }).role).toBeDefined()
  })
})

describe('validateLogin', () => {
  it('returns no errors for valid input', () => {
    expect(validateLogin({ email: 'buyer@example.com', password: 'secret123' })).toEqual({})
  })

  it('flags missing email and password', () => {
    const errors = validateLogin({ email: '', password: '' })
    expect(errors.email).toBeDefined()
    expect(errors.password).toBeDefined()
  })
})
