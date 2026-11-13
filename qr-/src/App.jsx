import { BrowserRouter, Route, Routes, Navigate } from 'react-router-dom';
import './index.css';

// Pages
import SignUp from './pages/SignUp';
import ProfilePage from './pages/ProfilePage'; 
import QrScanner from './pages/QrScanner';
import Dashboard from './pages/Dashboard';
  

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Redirect root to signup */}
        <Route path="/" element={<Navigate to="/signup" replace />} />

        {/* Authentication Routes */}
        <Route path="/signup" element={<SignUp />} />
        
        {/* User shows their QR here */}
        <Route path="/profile" element={<ProfilePage />} />
        
        {/* Kiosk camera scans QR here */}
        <Route path="/scan" element={<QrScanner />} />

        <Route path="/dashboard" element={<Dashboard />} />

        {/* 404 Fallback */}
        <Route path="*" element={<div className="p-10 text-center">404 - Page Not Found</div>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;