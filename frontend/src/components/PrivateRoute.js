import { Navigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import React from 'react';

const PrivateRoute = ({ children, adminOnly = false }) => {
  const token = localStorage.getItem('token');

  if (!token) {
    // Nếu không có token, chuyển hướng đến trang login
    return <Navigate to="/login" replace />;
  }

  try {
    const decoded = jwtDecode(token);
    console.log('Decoded Token:', decoded); // Debug log
    
    // Kiểm tra token hết hạn
    if (Date.now() >= decoded.exp * 1000) {
      // Nếu token đã hết hạn, xóa token khỏi localStorage và chuyển hướng đến trang login
      localStorage.removeItem('token');
      return <Navigate to="/login" replace />;
    }

    // Xử lý role (cả trường hợp role là string hoặc array)
    const roles = typeof decoded.roles === 'string' 
      ? [decoded.roles]
      : decoded.roles || [];

    const isAdmin = roles.includes('ROLE_ADMIN');
    console.log(`User roles: ${roles}, isAdmin: ${isAdmin}`); // Debug log

    // Nếu cần quyền admin mà người dùng không phải admin, chuyển hướng đến profile
    if (adminOnly && !isAdmin) {
      return <Navigate to="/profile" replace />;
    }

    // Nếu tất cả điều kiện đều hợp lệ, hiển thị các children (component mà bạn muốn bảo vệ)
    return children;
  } catch (error) {
    console.error('Token error:', error);
    // Nếu gặp lỗi trong quá trình giải mã token, xóa token và chuyển hướng đến login
    localStorage.removeItem('token');
    return <Navigate to="/login" replace />;
  }
};

export default PrivateRoute;
