import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../../services/AuthService';
import { jwtDecode } from 'jwt-decode';
import { 
  TextField, 
  Button, 
  Container, 
  Typography, 
  Box,
  CircularProgress
} from '@mui/material';

function Login() {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };



  const handleSubmit = async (e) => {
  e.preventDefault();
  setLoading(true);

  try {
    const response = await AuthService.login(form.username, form.password);
    if (response.data) {
      localStorage.setItem('token', response.data);
      const decoded = jwtDecode(response.data);
      
      // Debug log
      console.log('Login Decoded:', decoded);

      // Điều hướng dựa trên role
      const roles = Array.isArray(decoded.roles) 
        ? decoded.roles 
        : [decoded.roles].filter(Boolean);

      if (roles.includes('ROLE_ADMIN')) {
        window.location.href = '/admin'; // Sử dụng window.location để đảm bảo reload
      } else {
        window.location.href = '/profile';
      }
    }
  } catch (error) {
    setError('Đăng nhập thất bại');
  } finally {
    setLoading(false);
  }
};

  // const handleSubmit = async (e) => {
  //   e.preventDefault();
  //   setLoading(true);
  //   setError('');
  
  //   try {
  //     const response = await AuthService.login(form.username, form.password);
      
  //     if (response.status === 200 && response.data) {
  //       localStorage.setItem('token', response.data);
  //       const decoded = jwtDecode(response.data);
        
  //       // Sửa cách kiểm tra role
  //       if (decoded.roles && decoded.roles.includes('ROLE_ADMIN')) {
  //         window.location.href = '/admin'; // Sử dụng window.location thay vì navigate
  //       } else {
  //         window.location.href = '/profile';
  //       }
  //     } else {
  //       setError('Tên đăng nhập hoặc mật khẩu không đúng');
  //     }
  //   } catch (err) {
  //     console.error('Login error:', err);
  //     setError(err.response?.data?.message || 'Lỗi kết nối đến server');
  //   } finally {
  //     setLoading(false);
  //   }
  // };

  return (
    <Container maxWidth="sm">
      <Box sx={{ mt: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography component="h1" variant="h5">Đăng nhập</Typography>
        {error && <Typography color="error" sx={{ mt: 2 }}>{error}</Typography>}
        <Box component="form" onSubmit={handleSubmit} sx={{ mt: 3, width: '100%' }}>
          <TextField
            fullWidth
            margin="normal"
            label="Tên đăng nhập"
            name="username"
            value={form.username}
            onChange={handleChange}
            required
          />
          <TextField
            fullWidth
            margin="normal"
            label="Mật khẩu"
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            required
          />
          <Button
            type="submit"
            fullWidth
            variant="contained"
            sx={{ mt: 3, mb: 2 }}
            disabled={loading}
          >
            {loading ? <CircularProgress size={24} /> : 'Đăng nhập'}
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

export default Login;