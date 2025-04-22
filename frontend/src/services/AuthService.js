import axios from './axiosConfig';

class AuthService {
  // Register user (for general users)
  register(username, password, name, age, address) {
    return axios.post('/register', {
      username,
      password,
      name,
      age,
      address,
      role: 'ROLE_USER'
    }).catch(error => {
      console.error('AXIOS Error during register:', error);
      if (error.response) {
        return error.response;
      }
      throw error;
    });
  }

  // Login (for both Admin and User)
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
      console.error('AXIOS Error during login:', error);
      if (error.response) {
        return error.response;
      }
      throw error;
    });
  }

  // Fetch the user's profile
  getProfile() {
    return axios.get('/user/home/profile').catch(error => {
      console.error('AXIOS Error fetching profile:', error);
      if (error.response) {
        return error.response;
      }
      throw error;
    });
  }

  // Update the user's profile
  updateProfile(name, age, address) {
    return axios.put('/user/home/update', {
      name,
      age,
      address
    }).catch(error => {
      console.error('AXIOS Error updating profile:', error);
      if (error.response) {
        return error.response;
      }
      throw error;
    });
  }

  // Fetch all users (Admin only)
  getAllUsers() {
    return axios.get('/admin/home/users').catch(error => {
      console.error('AXIOS Error fetching all users:', error);
      if (error.response) {
        return error.response;
      }
      throw error;
    });
  }

  // Create a new table (Admin only)
  createTable(data) {
    return axios.post('/admin/home/tables/create', data)  // Cập nhật lại endpoint để tạo bàn
   
  }
// Create user (Admin only)
createUser(userData) {
  return axios.post('/admin/home/users/create', userData)
    .catch(error => {
      console.error('AXIOS Error creating user:', error);
      if (error.response) {
        return error.response;
      }
      throw error;
    });
}

  // Update user by ID (Admin only)
  updateUser(id, userData) {
    return axios.put(`/admin/home/users/${id}`, userData)
      .catch(error => {
        console.error('AXIOS Error updating user:', error);
        if (error.response) {
          return error.response;
        }
        throw error;
      });
  }

  // Delete user by ID (Admin only)
  deleteUser(id) {
    return axios.delete(`/admin/home/users/${id}`)
      .catch(error => {
        console.error('AXIOS Error deleting user:', error);
        if (error.response) {
          return error.response;
        }
        throw error;
      });
  }

  // Fetch all tables (Admin only)
  getAllTables() {
    return axios.get('/admin/home/tables/all').catch(error => {
      console.error('AXIOS Error fetching all tables:', error);
      if (error.response) {
        return error.response;
      }
      throw error;
    });
  }

  // Update a table by ID (Admin only)
  updateTable(id, tableData) {
    // Convert boolean-like status to readable string
    const convertedData = {
      ...tableData,
      status: tableData.status === true || tableData.status === 'true' ? 'Đã đặt' : 'Chưa đặt'
    };
  
    return axios.put(`/admin/home/tables/${id}/update`, convertedData)
      .catch(error => {
        console.error('AXIOS Error updating table:', error);
        if (error.response) {
          return error.response;
        }
        throw error;
      });
  }

  // Delete a table by ID (Admin only)
  deleteTable(id) {
    return axios.delete(`/admin/home/tables/${id}/delete`)
      .catch(error => {
        console.error('AXIOS Error deleting table:', error);
        if (error.response) {
          return error.response;
        }
        throw error;
      });
  }
  // Lấy danh sách bàn cho người dùng
// Lấy danh sách bàn cho người dùng
getAllTablesForUser() {
  return axios.get('/user/home/tables/all').catch(error => {
    console.error('AXIOS Error fetching user tables:', error);
    if (error.response) {
      return error.response;
    }
    throw error;
  });
}


// Đặt bàn (user)
reserveTable(id, data) {
  return axios.put(`/user/home/tables/${id}/reserve`, data);
}


}

const authServiceInstance = new AuthService();
export default authServiceInstance;
