import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Typography, Box, IconButton } from '@mui/material';
import { Home as HomeIcon, AccountCircle as AccountIcon } from '@mui/icons-material';
import { jwtDecode } from 'jwt-decode';

function Home() {
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
    <Container maxWidth="md" sx={{ mt: 4 }}>
      {/* Header */}
      <Box 
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          mb: 3
        }}
      >
        {/* Chuyển hướng tới trang user-tables khi click HomeIcon */}
        <IconButton color="primary" onClick={() => navigate('/user-tables')}>
          <HomeIcon />
        </IconButton>
        <Typography variant="h5" fontWeight="bold">
          Quản lý Coffee Shop
        </Typography>
        <IconButton color="primary" onClick={() => navigate('/profile')}>
          <AccountIcon />
        </IconButton>
      </Box>

      {/* Chào mừng người dùng */}
      <Box sx={{ mt: 4, textAlign: 'center' }}>
  <Typography variant="h6" gutterBottom>
    Chào mừng {username ? `${username}` : 'bạn'} đến với hệ thống!
    {isAdmin && <Typography variant="body2" color="primary">Bạn là quản trị viên</Typography>}
  </Typography>
</Box>

    </Container>
  );
}

export default Home;
