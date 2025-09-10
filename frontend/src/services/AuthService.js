import axios from './axiosConfig';

class AuthService {
  // 1. Đăng ký người dùng mới
  register(username, password, name, age, address) {
    return axios.post('/register', {
      username,
      password,
      name,
      age,
      address,
      role: 'ROLE_USER'  // Mặc định role là user
    }).catch(error => {
      console.error('AXIOS Error during register:', error);
      if (error.response) {
        return error.response;
      }
      throw error;
    });
  }

  // 2. Đăng nhập (cho cả Admin và User)
  login(username, password) {
    return axios.post('/generateToken', {
      username,
      password
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      validateStatus: function (status) {
        return status === 200 || status === 401; // Chấp nhận cả status thành công và unauthorized
      }
    }).catch(error => {
      console.error('AXIOS Error during login:', error);
      if (error.response) {
        return error.response;
      }
      throw error;
    });
  }

  // 3. Các chức năng cho User thường
  // 3.1 Lấy thông tin profile
  getProfile() {
    return axios.get('/user/home/profile');
  }

  // 3.2 Cập nhật profile
  updateProfile(name, age, address) {
    return axios.put('/user/home/update', {
      name,
      age,
      address
    });
  }

  // 3.3 Lấy danh sách bàn cho user
  getAllTablesForUser() {
    return axios.get('/user/home/tables/all');
  }

  // 3.4 Đặt bàn
  reserveTable(id, data) {
    return axios.put(`/user/home/tables/${id}/reserve`, data);
  }

  // 4. Các chức năng cho Admin
  // 4.1 Lấy danh sách tất cả users
  getAllUsers() {
    return axios.get('/admin/home/users');
  }

  // 4.2 Tạo user mới
  createUser(userData) {
    return axios.post('/admin/home/users/create', userData);
  }

  // 4.3 Cập nhật thông tin user
  updateUser(id, userData) {
    return axios.put(`/admin/home/users/${id}`, userData);
  }

  // 4.4 Xóa user
  deleteUser(id) {
    return axios.delete(`/admin/home/users/${id}`);
  }

  // 4.5 Quản lý bàn
  getAllTables() {
    return axios.get('/admin/home/tables/all');
  }

  createTable(data) {
    return axios.post('/admin/home/tables/create', data);
  }

  updateTable(id, tableData) {
    const convertedData = {
      ...tableData,
      status: tableData.status === true || tableData.status === 'true' ? 'Đã đặt' : 'Chưa đặt'
    };
    return axios.put(`/admin/home/tables/${id}/update`, convertedData);
  }

  deleteTable(id) {
    return axios.delete(`/admin/home/tables/${id}/delete`);
  }
}

const authServiceInstance = new AuthService();
export default authServiceInstance;