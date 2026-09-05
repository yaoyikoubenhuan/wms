import { defineStore } from 'pinia'
import { departmentApi, roleApi, personApi } from '@/api'

export const usePersonnelStore = defineStore('personnel', () => {
  const personnel = ref([])
  const departments = ref([])
  const roles = ref([])
  const loaded = ref(false)

  // ---- getters ----
  const personnelById = computed(() => {
    return (id) => personnel.value.find(p => p.id === id)
  })

  const personnelByDept = computed(() => {
    return (deptId) => personnel.value.filter(p => p.departmentId === deptId)
  })

  const departmentTree = computed(() => {
    const map = {}
    const roots = []
    departments.value.forEach(d => { map[d.id] = { ...d, children: [] } })
    departments.value.forEach(d => {
      if (d.parentId && map[d.parentId]) {
        map[d.parentId].children.push(map[d.id])
      } else {
        roots.push(map[d.id])
      }
    })
    return roots
  })

  const deptById = computed(() => {
    return (id) => departments.value.find(d => d.id === id)
  })

  const roleById = computed(() => {
    return (id) => roles.value.find(r => r.id === id)
  })

  // ---- fetch ----
  async function fetchPersonnel(force = false) {
    if (!force && personnel.value.length) return personnel.value
    personnel.value = await personApi.list()
    return personnel.value
  }

  async function fetchDepartments(force = false) {
    if (!force && departments.value.length) return departments.value
    // 存储平铺列表，部门树由 departmentTree getter 本地构建
    departments.value = await departmentApi.list()
    return departments.value
  }

  async function fetchRoles(force = false) {
    if (!force && roles.value.length) return roles.value
    roles.value = await roleApi.list()
    return roles.value
  }

  async function fetchAll() {
    await Promise.all([fetchPersonnel(true), fetchDepartments(true), fetchRoles(true)])
    loaded.value = true
  }

  // ---- personnel actions ----
  async function addPerson(data) {
    const newPerson = await personApi.create(data)
    personnel.value.unshift(newPerson)
    return newPerson
  }

  async function updatePerson(id, updates) {
    await personApi.update(id, updates)
    const index = personnel.value.findIndex(p => p.id === id)
    if (index !== -1) {
      personnel.value[index] = { ...personnel.value[index], ...updates }
    }
  }

  async function deletePerson(id) {
    await personApi.remove(id)
    personnel.value = personnel.value.filter(p => p.id !== id)
  }

  // ---- department actions ----
  async function addDepartment(data) {
    const newDept = await departmentApi.create(data)
    await fetchDepartments(true)
    return newDept
  }

  async function updateDepartment(id, updates) {
    await departmentApi.update(id, updates)
    await fetchDepartments(true)
  }

  async function deleteDepartment(id) {
    await departmentApi.remove(id)
    await fetchDepartments(true)
  }

  // ---- role actions ----
  async function addRole(data) {
    const newRole = await roleApi.create(data)
    roles.value.push(newRole)
    return newRole
  }

  async function updateRole(id, updates) {
    await roleApi.update(id, updates)
    await fetchRoles(true)
  }

  async function deleteRole(id) {
    await roleApi.remove(id)
    roles.value = roles.value.filter(r => r.id !== id)
  }

  return {
    personnel, departments, roles, loaded,
    personnelById, personnelByDept, departmentTree, deptById, roleById,
    fetchPersonnel, fetchDepartments, fetchRoles, fetchAll,
    addPerson, updatePerson, deletePerson,
    addDepartment, updateDepartment, deleteDepartment,
    addRole, updateRole, deleteRole,
  }
})
