import React, { useState } from 'react';
import { Container, Typography, Box, TextField, Button, MenuItem } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AuthService from '../../services/AuthService';

function CreateUser() {
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    name: '',
    age: '',
    address: '',
    role: 'ROLE_',
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const handleSubmit = async () => {
    try {
      await AuthService.createUser(formData);
      navigate('/admin');
    } catch (error) {
      console.error('Error creating user:', error);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" gutterBottom>
          Tạo người dùng mới
        </Typography>
        <TextField
          name="username"
          label="Tên đăng nhập"
          value={formData.username}
          onChange={handleChange}
          fullWidth
          margin="normal"
        />
        <TextField
          name="password"
          label="Mật khẩu"
          type="password"
          value={formData.password}
          onChange={handleChange}
          fullWidth
          margin="normal"
        />
        <TextField
          name="name"
          label="Họ tên"
          value={formData.name}
          onChange={handleChange}
          fullWidth
          margin="normal"
        />
        <TextField
          name="age"
          label="Tuổi"
          type="number"
          value={formData.age}
          onChange={handleChange}
          fullWidth
          margin="normal"
        />
        <TextField
          name="address"
          label="Địa chỉ"
          value={formData.address}
          onChange={handleChange}
          fullWidth
          margin="normal"
        />
        <TextField
          name="role"
          label="Vai trò"
          value={formData.role}
          onChange={handleChange}
          select
          fullWidth
          margin="normal"
        >
          <MenuItem value="ROLE_USER">USER</MenuItem>
          <MenuItem value="ROLE_ADMIN">ADMIN</MenuItem>
        </TextField>
        <Box sx={{ mt: 2 }}>
          <Button variant="contained" onClick={handleSubmit}>
            Tạo mới
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

export default CreateUser;