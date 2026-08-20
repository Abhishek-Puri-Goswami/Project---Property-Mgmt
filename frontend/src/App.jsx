import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext.jsx'
import { ToastProvider } from './context/ToastContext.jsx'
import { ComparisonProvider } from './context/ComparisonContext.jsx'
import Toast from './components/common/Toast.jsx'
import AppLayout from './components/layout/AppLayout.jsx'
import ProtectedRoute from './components/layout/ProtectedRoute.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import DashboardPage from './pages/DashboardPage.jsx'
import PropertySearchPage from './pages/PropertySearchPage.jsx'
import PropertyDetailsPage from './pages/PropertyDetailsPage.jsx'
import FavoritesPage from './pages/FavoritesPage.jsx'
import ComparisonPage from './pages/ComparisonPage.jsx'
import VisitsPage from './pages/VisitsPage.jsx'
import AiCopilotPage from './pages/AiCopilotPage.jsx'

function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <ComparisonProvider>
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route
                path="/"
                element={
                  <ProtectedRoute>
                    <AppLayout />
                  </ProtectedRoute>
                }
              >
                <Route index element={<DashboardPage />} />
                <Route path="properties" element={<PropertySearchPage />} />
                <Route path="properties/:id" element={<PropertyDetailsPage />} />
                <Route path="favorites" element={<FavoritesPage />} />
                <Route path="comparison" element={<ComparisonPage />} />
                <Route path="visits" element={<VisitsPage />} />
                <Route path="ai-copilot" element={<AiCopilotPage />} />
              </Route>
            </Routes>
          </BrowserRouter>
          <Toast />
        </ComparisonProvider>
      </ToastProvider>
    </AuthProvider>
  )
}

export default App
