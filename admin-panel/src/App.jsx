import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext.jsx'
import { ToastProvider } from './context/ToastContext.jsx'
import Toast from './components/common/Toast.jsx'
import AdminLayout from './components/layout/AdminLayout.jsx'
import ProtectedRoute from './components/layout/ProtectedRoute.jsx'
import LoginPage from './pages/LoginPage.jsx'
import DashboardPage from './pages/DashboardPage.jsx'
import UsersPage from './pages/UsersPage.jsx'
import AgentsPage from './pages/AgentsPage.jsx'
import PropertiesPage from './pages/PropertiesPage.jsx'
import AiAnalyticsPage from './pages/AiAnalyticsPage.jsx'
import SystemStatusPage from './pages/SystemStatusPage.jsx'

function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <AdminLayout />
                </ProtectedRoute>
              }
            >
              <Route index element={<DashboardPage />} />
              <Route path="properties" element={<PropertiesPage />} />
              <Route path="users" element={<UsersPage />} />
              <Route path="agents" element={<AgentsPage />} />
              <Route path="ai-analytics" element={<AiAnalyticsPage />} />
              <Route path="system-status" element={<SystemStatusPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
        <Toast />
      </ToastProvider>
    </AuthProvider>
  )
}

export default App
