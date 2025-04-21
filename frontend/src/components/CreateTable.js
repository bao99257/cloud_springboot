import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  TextField,
  Button,
  MenuItem
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';

function CreateTable() {
  const [formData, setFormData] = useState({
    number: '',
    description: '',
    isReserved: 'Chưa đặt' // Giá trị mặc định là chuỗi
  });

  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async () => {
    try {
      await AuthService.createTable(formData); // Gửi nguyên văn
      navigate('/tables');
    } catch (error) {
      console.error('Lỗi khi tạo bàn:', error);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4" gutterBottom>
          Tạo bàn mới
        </Typography>

        <TextField
          name="number"
          label="Số bàn"
          value={formData.number}
          onChange={handleChange}
          fullWidth
          margin="normal"
        />
        <TextField
          name="description"
          label="Mô tả bàn"
          value={formData.description}
          onChange={handleChange}
          fullWidth
          margin="normal"
        />
        <TextField
          name="isReserved"
          label="Trạng thái"
          select
          value={formData.isReserved}
          onChange={handleChange}
          fullWidth
          margin="normal"
        >
          <MenuItem value="Chưa đặt">Chưa đặt</MenuItem>
          <MenuItem value="Đã đặt">Đã đặt</MenuItem>
        </TextField>

        <Box sx={{ mt: 2 }}>
          <Button variant="contained" onClick={handleSubmit}>
            Tạo bàn
          </Button>
        </Box>
      </Box>
    </Container>
  );
}

export default CreateTable;
