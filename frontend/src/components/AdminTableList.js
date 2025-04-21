import React, { useState, useEffect } from 'react';
import { Container, Typography, Box, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper, Button, IconButton, Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem } from '@mui/material';
import { Edit, Delete } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import AuthService from '../services/AuthService'; // Chú ý là sử dụng '..' để đi lên một cấp trong thư mục

function AdminTableList() {
  const [tables, setTables] = useState([]);
  const [open, setOpen] = useState(false);
  const [editingTable, setEditingTable] = useState(null);
  const [formData, setFormData] = useState({
    number: '',
    description: '',
    status: false // Renaming from isReserved to status
  });
  const [isAdmin, setIsAdmin] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    fetchTables();
    checkAdminStatus();
  }, []);

  // Check if the user is an admin based on their JWT token
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

  // Fetch all tables
  const fetchTables = async () => {
    try {
      const response = await AuthService.getAllTables();
      setTables(response.data);
    } catch (error) {
      console.error('Error fetching tables:', error);
    }
  };

  // Handle opening the edit dialog for a table
  const handleOpen = (table) => {
    setEditingTable(table);
    setFormData({ ...table });
    setOpen(true);
  };

  // Close the dialog
  const handleClose = () => {
    setOpen(false);
    setEditingTable(null);
  };

  // Handle form data change
  const handleChange = (e) => {
    const { name, value } = e.target;

    // If the field is status, convert it to boolean
    if (name === "status") {
      setFormData(prev => ({
        ...prev,
        [name]: value === "true",  // Convert "true" to true and "false" to false
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  // Handle form submission for updating a table
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await AuthService.updateTable(editingTable.id, formData);
      fetchTables(); // Refresh the table list after update
      handleClose();
    } catch (error) {
      console.error('Error updating table:', error);
    }
  };

  // Handle deleting a table
  const handleDelete = async (id) => {
    if (window.confirm('Bạn có chắc muốn xóa bàn này?')) {
      try {
        await AuthService.deleteTable(id);
        fetchTables(); // Refresh the table list after deletion
      } catch (error) {
        console.error('Error deleting table:', error);
      }
    }
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ display: 'flex', justifyContent: 'space-between', my: 4 }}>
        <Typography variant="h4">Danh sách bàn (Admin)</Typography>
        <Box>
          {isAdmin && (
            <Button variant="contained" sx={{ mr: 2 }} onClick={() => navigate('/create-tables')}>
              Thêm bàn
            </Button>
          )}
        </Box>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
           
              <TableCell>Số bàn</TableCell>
              <TableCell>Mô tả bàn</TableCell>
              <TableCell>Trạng thái</TableCell>
              <TableCell>Hành động</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {tables.map((table) => (
              <TableRow key={table.id}>
                
                <TableCell>{table.number}</TableCell>
                <TableCell>{table.notes}</TableCell>
                <TableCell>{table.status}</TableCell>
                <TableCell>
                  {isAdmin && (
                    <>
                      <IconButton color="primary" onClick={() => handleOpen(table)}>
                        <Edit />
                      </IconButton>
                      <IconButton color="error" onClick={() => handleDelete(table.id)}>
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

      {/* Dialog for editing a table */}
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Sửa thông tin bàn</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
          <TextField
            name="number"
            label="Số bàn"
            value={formData.number}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            name="description"
            label="Notes"
            value={formData.notes}
            onChange={handleChange}
            fullWidth
          />
          <TextField
            name="status"
            label="Trạng thái"
            select
            value={formData.status}  // Convert boolean to string
            onChange={handleChange}
            fullWidth
          >
            <MenuItem value="false">Chưa đặt</MenuItem>
            <MenuItem value="true">Đã đặt</MenuItem>
          </TextField>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Hủy</Button>
          <Button onClick={handleSubmit} variant="contained">
            Cập nhật
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}

export default AdminTableList;
