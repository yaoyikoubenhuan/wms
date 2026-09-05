import request from './request'

// ---- 认证 ----
export const authApi = {
  login: (data) => request.post('/auth/login', data),
  logout: () => request.post('/auth/logout'),
  me: () => request.get('/auth/me'),
}

// ---- 部门 ----
export const departmentApi = {
  tree: () => request.get('/departments/tree'),
  list: () => request.get('/departments'),
  create: (data) => request.post('/departments', data),
  update: (id, data) => request.put(`/departments/${id}`, data),
  remove: (id) => request.delete(`/departments/${id}`),
}

// ---- 角色 ----
export const roleApi = {
  list: () => request.get('/roles'),
  create: (data) => request.post('/roles', data),
  update: (id, data) => request.put(`/roles/${id}`, data),
  remove: (id) => request.delete(`/roles/${id}`),
}

// ---- 人员 ----
export const personApi = {
  list: (params) => request.get('/personnel', { params }),
  get: (id) => request.get(`/personnel/${id}`),
  create: (data) => request.post('/personnel', data),
  update: (id, data) => request.put(`/personnel/${id}`, data),
  remove: (id) => request.delete(`/personnel/${id}`),
}

// ---- 任务 ----
export const taskApi = {
  list: (params) => request.get('/tasks', { params }),
  get: (id) => request.get(`/tasks/${id}`),
  statistics: () => request.get('/tasks/statistics'),
  create: (data) => request.post('/tasks', data),
  update: (id, data) => request.put(`/tasks/${id}`, data),
  changeStatus: (id, status) => request.patch(`/tasks/${id}/status`, { status }),
  remove: (id) => request.delete(`/tasks/${id}`),
}
