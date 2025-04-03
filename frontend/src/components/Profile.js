import React, { useState, useEffect, useCallback } from 'react';
import AuthService from '../services/AuthService';
import { 
  Container, 
  Typography, 
  Box, 
  Button,
  TextField,
  Paper,
  CircularProgress
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

function Profile() {
  const [user, setUser] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState({
    name: '',
    age: '',
    address: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogout = useCallback(() => {
    localStorage.removeItem('token');
    navigate('/login');
  }, [navigate]);

  const fetchProfile = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const response = await AuthService.getProfile();
      setUser(response.data);
      setForm({
        name: response.data.name,
        age: response.data.age,
        address: response.data.address
      });
    } catch (error) {
      console.error('Lỗi khi lấy thông tin:', error);
      setError('Không thể tải thông tin người dùng');
      if (error.response?.status === 401) {
        handleLogout();
      }
    } finally {
      setLoading(false);
    }
  }, [handleLogout]);

  useEffect(() => {
    fetchProfile();
  }, [fetchProfile]);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      await AuthService.updateProfile(form.name, form.age, form.address);
      await fetchProfile();
      setEditMode(false);
    } catch (error) {
      console.error('Lỗi khi cập nhật:', error);
      setError('Cập nhật thông tin thất bại');
    } finally {
      setLoading(false);
    }
  };

  const isAdmin = useCallback(() => {
    const token = localStorage.getItem('token');
    if (!token) return false;
    try {
      const decoded = jwtDecode(token);
      return decoded.roles.includes('ROLE_ADMIN');
    } catch {
      return false;
    }
  }, []);

  if (loading && !user) {
    return (
      <Container maxWidth="md" sx={{ display: 'flex', justifyContent: 'center', mt: 4 }}>
        <CircularProgress />
      </Container>
    );
  }

  if (!user) {
    return (
      <Container maxWidth="md" sx={{ mt: 4 }}>
        <Typography color="error">{error || 'Không thể tải thông tin người dùng'}</Typography>
        <Button 
          variant="contained" 
          sx={{ mt: 2 }}
          onClick={() => window.location.reload()}
        >
          Thử lại
        </Button>
      </Container>
    );
  }

  return (
    <Container maxWidth="md">
      <Paper elevation={3} sx={{ p: 4, mt: 4 }}>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
          <Typography variant="h4">Thông tin cá nhân</Typography>
          <Box sx={{ display: 'flex', gap: 2 }}>
            {isAdmin() && (
              <Button 
                variant="contained" 
                color="secondary"
                onClick={() => navigate('/admin')}
                disabled={loading}
              >
                Trang quản trị
              </Button>
            )}
            <Button 
              variant="contained" 
              color="error" 
              onClick={handleLogout}
              disabled={loading}
            >
              Đăng xuất
            </Button>
          </Box>
        </Box>

        {error && (
          <Typography color="error" sx={{ mb: 2 }}>
            {error}
          </Typography>
        )}

        {!editMode ? (
          <Box>
            <Typography variant="h6" gutterBottom>
              <strong>Tên:</strong> {user.name}
            </Typography>
            <Typography variant="h6" gutterBottom>
              <strong>Tuổi:</strong> {user.age}
            </Typography>
            <Typography variant="h6" gutterBottom>
              <strong>Địa chỉ:</strong> {user.address}
            </Typography>
            <Box sx={{ mt: 3, display: 'flex', gap: 2 }}>
              <Button 
                variant="contained" 
                onClick={() => setEditMode(true)}
                disabled={loading}
              >
                {loading ? <CircularProgress size={24} /> : 'Chỉnh sửa'}
              </Button>
            </Box>
          </Box>
        ) : (
          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              fullWidth
              margin="normal"
              label="Tên"
              name="name"
              value={form.name}
              onChange={handleChange}
              required
              disabled={loading}
            />
            <TextField
              fullWidth
              margin="normal"
              label="Tuổi"
              name="age"
              type="number"
              value={form.age}
              onChange={handleChange}
              required
              inputProps={{ min: 1 }}
              disabled={loading}
            />
            <TextField
              fullWidth
              margin="normal"
              label="Địa chỉ"
              name="address"
              value={form.address}
              onChange={handleChange}
              required
              disabled={loading}
            />
            <Box sx={{ mt: 2, display: 'flex', gap: 2 }}>
              <Button 
                type="submit" 
                variant="contained"
                disabled={loading}
              >
                {loading ? <CircularProgress size={24} /> : 'Lưu thay đổi'}
              </Button>
              <Button 
                variant="outlined" 
                onClick={() => setEditMode(false)}
                disabled={loading}
              >
                Hủy
              </Button>
            </Box>
          </Box>
        )}
      </Paper>
    </Container>
  );
}

export default Profile;