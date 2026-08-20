import { createContext, useContext, useState } from 'react'

const STORAGE_KEY = 'propertyhub_auth'
const AuthContext = createContext(null)

function readStoredAuth() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : { user: null, token: null }
  } catch {
    return { user: null, token: null }
  }
}

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(readStoredAuth)

  function login(user, token) {
    const next = { user, token }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(next))
    setAuth(next)
  }

  function logout() {
    localStorage.removeItem(STORAGE_KEY)
    setAuth({ user: null, token: null })
  }

  const value = {
    user: auth.user,
    token: auth.token,
    isAuthenticated: Boolean(auth.token),
    login,
    logout,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return ctx
}

export { STORAGE_KEY }
