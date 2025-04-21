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
  CircularProgress,
  Paper
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
    setError('');

    try {
      const response = await AuthService.login(form.username, form.password);
      if (response.data) {
        localStorage.setItem('token', response.data);
        const decoded = jwtDecode(response.data);
        const roles = Array.isArray(decoded.roles)
          ? decoded.roles
          : [decoded.roles].filter(Boolean);

        if (roles.includes('ROLE_ADMIN')) {
          navigate('/adminhome');
        } else {
          navigate('/home');
        }
      } else {
        setError('Tên đăng nhập hoặc mật khẩu không đúng');
      }
    } catch (error) {
      setError('Đăng nhập thất bại');
    } finally {
      setLoading(false);
    }
  };

  const handleGoToRegister = () => {
    navigate('/register');
  };

  return (
    <Box
      sx={{
        background: 'linear-gradient(to right, #83a4d4, #b6fbff)',
        minHeight: '100vh',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        fontFamily: 'Roboto, sans-serif'
      }}
    >
      <Container maxWidth="sm">
        <Paper
          elevation={10}
          sx={{
            p: 5,
            borderRadius: 5,
            backgroundColor: '#ffffffee',
            boxShadow: '0px 8px 24px rgba(0,0,0,0.1)',
            backdropFilter: 'blur(4px)'
          }}
        >
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <Typography
              component="h1"
              variant="h4"
              fontWeight="bold"
              sx={{ mb: 3, color: '#1e3c72' }}
            >
              ☕ Đăng nhập hệ thống
            </Typography>

            {error && (
              <Typography color="error" sx={{ mt: 1, mb: 2 }}>
                {error}
              </Typography>
            )}

            <Box component="form" onSubmit={handleSubmit} sx={{ width: '100%' }}>
              <TextField
                fullWidth
                margin="normal"
                label="Tên đăng nhập"
                name="username"
                value={form.username}
                onChange={handleChange}
                required
                variant="outlined"
                sx={{
                  backgroundColor: '#f9f9f9',
                  borderRadius: 1
                }}
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
                variant="outlined"
                sx={{
                  backgroundColor: '#f9f9f9',
                  borderRadius: 1
                }}
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{
                  mt: 3,
                  mb: 2,
                  py: 1.5,
                  fontWeight: 'bold',
                  fontSize: '16px',
                  background: 'linear-gradient(to right, #1e3c72, #2a5298)',
                  color: '#fff',
                  '&:hover': {
                    background: 'linear-gradient(to right, #2a5298, #1e3c72)',
                  }
                }}
                disabled={loading}
              >
                {loading ? <CircularProgress size={24} color="inherit" /> : 'Đăng nhập'}
              </Button>
              <Button
                fullWidth
                variant="outlined"
                onClick={handleGoToRegister}
                sx={{
                  py: 1.5,
                  fontWeight: 'bold',
                  color: '#1e3c72',
                  borderColor: '#1e3c72',
                  '&:hover': {
                    backgroundColor: '#f0f7ff',
                    borderColor: '#1e3c72'
                  }
                }}
              >
                Chưa có tài khoản? Đăng ký
              </Button>
            </Box>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}

export default Login;
