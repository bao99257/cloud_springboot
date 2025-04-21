import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import Profile from './components/Profile';
import AdminDashboard from './components/Admin/AdminDashboard';
import CreateUserPage from './components/Admin/CreateUser'; // Assuming this component exists
import PrivateRoute from './components/PrivateRoute';
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material';
import Home from './components/Home'; // Đảm bảo bạn đã tạo component Home
import AdminHome from './components/AdminHome'; // Import AdminHome component
import AdminTableList from './components/AdminTableList'; // Import your component
import CreateTable from './components/CreateTable';
import UserTables from './components/TableUser'; // Import component hiển thị danh sách bàn cho user




const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
  },
});

function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <Routes>
          {/* Đăng ký và đăng nhập */}
          <Route path="/register" element={<Register />} />
          <Route path="/login" element={<Login />} />

          {/* Trang home của người dùng sau khi đăng nhập */}
          <Route path="/home" element={
            <PrivateRoute>
              <Home />
            </PrivateRoute>
          } />

          {/* Trang profile của người dùng */}
          <Route path="/profile" element={
            <PrivateRoute>
              <Profile />
            </PrivateRoute>
          } />

          {/* Quản trị viên */}
          <Route path="/admin" element={
            <PrivateRoute adminOnly>
              <AdminDashboard />
            </PrivateRoute>
          } />
          
          {/* Trang tạo người dùng mới cho admin */}
          <Route path="/create" element={
            <PrivateRoute adminOnly>
              <CreateUserPage />
            </PrivateRoute>
          } />

<Route path="/adminhome" element={
  <PrivateRoute adminOnly>
    <AdminHome />
  </PrivateRoute>
} />
<Route path="/tables" element={
  <PrivateRoute adminOnly>
    <AdminTableList />
  </PrivateRoute>
} />
<Route path="/create-tables" element={
  <PrivateRoute adminOnly>
    <CreateTable />
  </PrivateRoute>
} />
<Route path="/user-tables" element={
  <PrivateRoute>
    <UserTables />
  </PrivateRoute>
} />


          {/* Route mặc định chuyển đến trang Profile sau khi đăng nhập */}
          <Route path="/" element={
            <PrivateRoute>
              <Profile />
            </PrivateRoute>
          } />
        </Routes>
      </BrowserRouter>
    </ThemeProvider>
  );
}

export default App;
