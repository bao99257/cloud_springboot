import { Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import React from 'react';

const PrivateRoute = ({ children, adminOnly = false }) => {
  const token = localStorage.getItem('token');

  if (!token) {
    return <Navigate to="/login" replace />;
  }

  try {
    const decoded = jwtDecode(token);
    console.log('Decoded Token:', decoded); // Debug log
    
    // Kiểm tra token hết hạn
    if (Date.now() >= decoded.exp * 1000) {
      localStorage.removeItem('token');
      return <Navigate to="/login" replace />;
    }

    // Xử lý role (cả trường hợp role là string hoặc array)
    const roles = typeof decoded.roles === 'string' 
      ? [decoded.roles]
      : decoded.roles || [];

    const isAdmin = roles.includes('ROLE_ADMIN');
    console.log(`User roles: ${roles}, isAdmin: ${isAdmin}`); // Debug log

    if (adminOnly && !isAdmin) {
      return <Navigate to="/profile" replace />;
    }

    return children;
  } catch (error) {
    console.error('Token error:', error);
    localStorage.removeItem('token');
    return <Navigate to="/login" replace />;
  }
};

export default PrivateRoute;