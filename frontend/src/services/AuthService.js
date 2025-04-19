import axios from './axiosConfig';

class AuthService {
  register(username, password, name, age, address) {
    return axios.post('/register', {
      username,
      password,
      name,
      age,
      address,
      role: 'ROLE_USER'
    });
  }

  login(username, password) {
    return axios.post('/generateToken', {
      username,
      password
    }, {
      headers: {
        'Content-Type': 'application/json'
      },
      validateStatus: function (status) {
        return status === 200 || status === 401; 
      }
    }).catch(error => {
      console.error('AXIOS Error:', error);
      if (error.response) {
        return error.response;
      }
      throw error;
    });
  }

  getProfile() {
    return axios.get('/user/profile');
  }

  updateProfile(name, age, address) {
    return axios.put('/user/update', {
      name,
      age,
      address
    });
  }

  getAllUsers() {
    return axios.get('/admin/users');
  }

  // 👇 Thêm API tạo người dùng (dành cho Admin)
  createUser(userData) {
    return axios.post('/admin/users/create', userData)
      .catch(error => {
        console.error('AXIOS Error:', error);
        if (error.response) {
          return error.response;
        }
        throw error;
      });
  }

  // 👇 Cập nhật người dùng theo ID
  updateUser(id, userData) {
    return axios.put(`/admin/users/${id}`, userData);
  }

  // 👇 Xóa người dùng theo ID
  deleteUser(id) {
    return axios.delete(`/admin/users/${id}`);
  }
}

const authServiceInstance = new AuthService();
export default authServiceInstance;
