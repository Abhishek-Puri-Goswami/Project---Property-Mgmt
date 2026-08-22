import axios from 'axios'
import { STORAGE_KEY } from '../context/AuthContext.jsx'

const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

function readToken() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw).token : null
  } catch {
    return null
  }
}

const axiosClient = axios.create({ baseURL })

axiosClient.interceptors.request.use((config) => {
  const token = readToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

function normalizeError(error) {
  const body = error.response?.data
  if (body && typeof body === 'object' && 'error' in body) {
    return {
      status: error.response.status,
      error: body.error,
      message: body.message,
      fieldErrors: body.fieldErrors ?? null,
    }
  }
  return {
    status: error.response?.status ?? null,
    error: 'NETWORK_ERROR',
    message: error.message || 'Server unavailable',
    fieldErrors: null,
  }
}

axiosClient.interceptors.response.use(
  (response) => response,
  (error) => Promise.reject(normalizeError(error))
)

export default axiosClient
