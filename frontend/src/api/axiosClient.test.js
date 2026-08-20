import { describe, it, expect, beforeEach } from 'vitest'
import axiosClient from './axiosClient.js'
import { STORAGE_KEY } from '../context/AuthContext.jsx'

describe('axiosClient', () => {
  beforeEach(() => {
    localStorage.clear()
  })

  it('attaches Authorization header when a token exists', () => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({ user: null, token: 'jwt-token' }))

    const requestInterceptor = axiosClient.interceptors.request.handlers[0].fulfilled
    const config = requestInterceptor({ headers: {} })

    expect(config.headers.Authorization).toBe('Bearer jwt-token')
  })

  it('omits Authorization header when no token exists', () => {
    const requestInterceptor = axiosClient.interceptors.request.handlers[0].fulfilled
    const config = requestInterceptor({ headers: {} })

    expect(config.headers.Authorization).toBeUndefined()
  })

  it('normalizes a backend ErrorResponse into a consistent shape', async () => {
    const responseErrorInterceptor = axiosClient.interceptors.response.handlers[0].rejected
    const error = {
      response: {
        status: 400,
        data: { status: 400, error: 'VALIDATION_ERROR', message: 'Validation failed', fieldErrors: { email: 'required' } },
      },
    }

    await expect(responseErrorInterceptor(error)).rejects.toEqual({
      status: 400,
      error: 'VALIDATION_ERROR',
      message: 'Validation failed',
      fieldErrors: { email: 'required' },
    })
  })

  it('normalizes a network error without a response', async () => {
    const responseErrorInterceptor = axiosClient.interceptors.response.handlers[0].rejected
    const error = { message: 'Network Error' }

    await expect(responseErrorInterceptor(error)).rejects.toEqual({
      status: null,
      error: 'NETWORK_ERROR',
      message: 'Network Error',
      fieldErrors: null,
    })
  })
})
