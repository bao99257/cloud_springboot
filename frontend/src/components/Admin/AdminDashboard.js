import React, { useEffect, useState } from 'react';
import AuthService from '../../services/AuthService';
import {
  Container,
  Typography,
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  IconButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  MenuItem,
  Select,
  InputLabel,
  FormControl
} from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';

function AdminDashboard() {
  const [users, setUsers] = useState([]);
  const [open, setOpen] = useState(false);
  const [editingUser, setEditingUser] = useState(null);
  const [formData, setFormData] = useState({
    username: '',
    name: '',
    age: '',
    address: '',
    role: 'USER',
    password: ''
  });

  const [isAdmin, setIsAdmin] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchUsers();
    checkAdminStatus();
  }, []);

  const checkAdminStatus = () => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode(token);
        const roles = decoded.roles || [];
        setIsAdmin(roles.includes('ROLE_ADMIN'));
      } catch (error) {
        console.error('Error decoding token:', error);
      }
    }
  };

  const fetchUsers = async () => {
    try {
      const response = await AuthService.getAllUsers();
      setUsers(response.data);
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const handleOpen = (user) => {
    setEditingUser(user);
    // Xóa mật khẩu khỏi form hiển thị
    setFormData({ ...user, password: '' });
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setEditingUser(null);
  };

  const handleChange = (e) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const updatedData = { ...formData };
  
      // Nếu password để trống, không gửi lên
      if (!updatedData.password || updatedData.password.trim() === '') {
        delete updatedData.password;
      }
  
      await AuthService.updateUser(editingUser.id, updatedData);
      fetchUsers();
      handleClose();
    } catch (error) {
      console.error('Error updating user:', error);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Bạn có chắc muốn xóa người dùng này?')) {
      try {
        await AuthService.deleteUser(id);
        fetchUsers();
      } catch (error) {
        console.error('Error deleting user:', error);
      }
    }
  };

  return (
    <Container maxWidth="lg" sx={{ mt: 6 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 4 }}>
        <Typography variant="h4" sx={{ fontWeight: 'bold', color: '#37474F' }}>
          Quản lý người dùng (Admin)
        </Typography>
        <Box>
          {/* Chỉ hiển thị nút "Thêm người dùng" cho admin */}
          {isAdmin && (
            <Button 
              variant="contained" 
              sx={{ mr: 2, backgroundColor: '#FF7043', '&:hover': { backgroundColor: '#FF5722' } }}
              onClick={() => navigate('/create')}
            >
              Thêm người dùng
            </Button>
          )}
          <Button 
            variant="contained" 
            color="error" 
            sx={{ backgroundColor: '#E53935', '&:hover': { backgroundColor: '#D32F2F' } }}
            onClick={handleLogout}
          >
            Đăng xuất
          </Button>
        </Box>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead sx={{ backgroundColor: '#ECEFF1' }}>
            <TableRow>
              <TableCell><strong>ID</strong></TableCell>
              <TableCell><strong>Tên đăng nhập</strong></TableCell>
              <TableCell><strong>Họ tên</strong></TableCell>
              <TableCell><strong>Tuổi</strong></TableCell>
              <TableCell><strong>Địa chỉ</strong></TableCell>
              <TableCell><strong>Vai trò</strong></TableCell>
              <TableCell><strong>Hành động</strong></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.id}</TableCell>
                <TableCell>{user.username}</TableCell>
                <TableCell>{user.name}</TableCell>
                <TableCell>{user.age}</TableCell>
                <TableCell>{user.address}</TableCell>
                <TableCell>{user.role}</TableCell>
                <TableCell>
                  {/* Chỉ hiển thị các hành động cho admin */}
                  {isAdmin && (
                    <>
                      <IconButton 
                        color="primary" 
                        onClick={() => handleOpen(user)}
                        sx={{ '&:hover': { backgroundColor: '#E3F2FD' } }}
                      >
                        <Edit />
                      </IconButton>
                      <IconButton 
                        color="error" 
                        onClick={() => handleDelete(user.id)}
                        sx={{ '&:hover': { backgroundColor: '#FFEBEE' } }}
                      >
                        <Delete />
                      </IconButton>
                    </>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Dialog chỉ dùng để chỉnh sửa người dùng */}
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Sửa người dùng</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          <TextField
            name="username"
            label="Tên đăng nhập"
            value={formData.username}
            onChange={handleChange}
            fullWidth
            disabled
          />
          <TextField
            name="password"
            label="Mật Khẩu"
            value={formData.password}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            name="name"
            label="Họ tên"
            value={formData.name}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            name="age"
            label="Tuổi"
            type="number"
            value={formData.age}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            name="address"
            label="Địa chỉ"
            value={formData.address}
            onChange={handleChange}
            fullWidth
          />
          {/* Chỉ Admin mới có thể sửa vai trò */}
          {isAdmin && (
            <FormControl fullWidth>
              <InputLabel id="role-select-label">Vai trò</InputLabel>
              <Select
                labelId="role-select-label"
                name="role"
                value={formData.role}
                onChange={handleChange}
              >
                <MenuItem value="ROLE_USER">USER</MenuItem>
                <MenuItem value="ROLE_ADMIN">ADMIN</MenuItem>
              </Select>
            </FormControl>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Hủy</Button>
          <Button onClick={handleSubmit} variant="contained" color="primary">
            Cập nhật
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}

export default AdminDashboard;
