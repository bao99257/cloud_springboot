import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Typography, Box, IconButton, Paper } from '@mui/material';
import { Coffee as CoffeeIcon, AccountCircle as AccountIcon } from '@mui/icons-material';
import { jwtDecode } from 'jwt-decode';

function AdminHome() {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [isAdmin, setIsAdmin] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      const decoded = jwtDecode(token);
      setUsername(decoded.sub);
      setIsAdmin(decoded.roles?.includes('ROLE_ADMIN'));
    }
  }, []);

  return (
    <Container maxWidth="lg" sx={{ mt: 6 }}>
      <Paper 
        elevation={12}
        sx={{
          p: 5,
          borderRadius: 3,
          backgroundColor: '#F4F6F8',
          boxShadow: '0px 15px 35px rgba(0, 0, 0, 0.12)',
          backdropFilter: 'blur(4px)',
          mb: 4,
          border: '1px solid #E0E0E0'
        }}
      >
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          {/* Coffee Icon */}
          <IconButton
            color="primary"
            onClick={() => navigate('/tables')}
            sx={{
              transition: 'transform 0.3s, color 0.3s',
              '&:hover': {
                transform: 'scale(1.1)',
                color: '#FF7043',
              }
            }}
          >
            <CoffeeIcon sx={{ fontSize: 35 }} />
          </IconButton>

          {/* Title */}
          <Typography variant="h4" fontWeight="bold" sx={{ textAlign: 'center', flex: 1, color: '#37474F' }}>
            Quản lý Coffee Shop (Admin)
          </Typography>

          {/* Account Icon */}
          <IconButton
            color="primary"
            onClick={() => navigate('/admin')}
            sx={{
              transition: 'transform 0.3s, color 0.3s',
              '&:hover': {
                transform: 'scale(1.1)',
                color: '#FF7043',
              }
            }}
          >
            <AccountIcon sx={{ fontSize: 35 }} />
          </IconButton>
        </Box>

        {/* Welcome Message */}
        <Box sx={{ mt: 5, textAlign: 'center' }}>
          <Typography variant="h5" sx={{ color: '#37474F', fontWeight: 600 }}>
            Xin chào, <span style={{ color: '#FF7043' }}>{username}</span>
          </Typography>
        </Box>
      </Paper>
    </Container>
  );
}

export default AdminHome;
