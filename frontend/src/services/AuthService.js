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
        // Chấp nhận cả status 401 để xử lý
        return status === 200 || status === 401; 
      }
    }).catch(error => {
      console.error('AXIOS Error:', error);
      if (error.response) {
        // Trả về response để xử lý trong component
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
}

const authServiceInstance = new AuthService();
export default authServiceInstance;