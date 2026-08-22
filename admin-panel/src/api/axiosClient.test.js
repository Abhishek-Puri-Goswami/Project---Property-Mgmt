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
        status: 403,
        data: { status: 403, error: 'FORBIDDEN', message: 'You do not have permission to access this resource', fieldErrors: null },
      },
    }

    await expect(responseErrorInterceptor(error)).rejects.toEqual({
      status: 403,
      error: 'FORBIDDEN',
      message: 'You do not have permission to access this resource',
      fieldErrors: null,
    })
  })
})
