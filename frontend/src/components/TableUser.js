import React, { useState, useEffect } from 'react';
import {
  Container, Typography, Box, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Button, Snackbar, Alert, TextField
} from '@mui/material';
import AuthService from '../services/AuthService';

function UserTableList() {
  const [tables, setTables] = useState([]);
  const [message, setMessage] = useState('');
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [notes, setNotes] = useState({}); // State để lưu trữ ghi chú tạm thời cho từng bàn

  useEffect(() => {
    fetchTables();
  }, []);

  const fetchTables = async () => {
    try {
      const response = await AuthService.getAllTablesForUser();
      setTables(response.data);
    } catch (error) {
      console.error('Lỗi khi tải danh sách bàn:', error);
    }
  };

  const handleNoteChange = (id, value) => {
    setNotes((prev) => ({
      ...prev,
      [id]: value,
    }));
  };

  const handleUpdate = async (id) => {
    try {
      const note = notes[id] || ''; // Lấy ghi chú từ state, mặc định là chuỗi rỗng nếu không có
      const response = await AuthService.reserveTable(id, { notes: note });
      setMessage(response.data);
      setSnackbarOpen(true);
      setNotes((prev) => ({ ...prev, [id]: '' })); // Xóa ghi chú tạm thời sau khi cập nhật
      fetchTables(); // Làm mới danh sách bàn
    } catch (error) {
      setMessage(error.response?.data || 'Lỗi khi cập nhật ghi chú.');
      setSnackbarOpen(true);
    }
  };

  return (
    <Container maxWidth="lg">
      <Box sx={{ my: 4 }}>
        <Typography variant="h4">Danh sách bàn (Người dùng)</Typography>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
      
              <TableCell>Số bàn</TableCell>
              <TableCell>Mô tả</TableCell>
              <TableCell>Trạng thái</TableCell>
              <TableCell>Thêm Notes</TableCell>
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
                  <TextField
                    size="small"
                    value={notes[table.id] || ''}
                    onChange={(e) => handleNoteChange(table.id, e.target.value)}
                    placeholder="Nhập ghi chú mới"
                    fullWidth
                  />
                </TableCell>
                <TableCell>
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={() => handleUpdate(table.id)}
                  >
                    Cập nhật
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      <Snackbar
        open={snackbarOpen}
        autoHideDuration={3000}
        onClose={() => setSnackbarOpen(false)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert onClose={() => setSnackbarOpen(false)} severity="info" sx={{ width: '100%' }}>
          {message}
        </Alert>
      </Snackbar>
    </Container>
  );
}

export default UserTableList;







