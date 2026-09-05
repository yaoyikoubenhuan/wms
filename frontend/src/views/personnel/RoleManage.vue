<template>
  <div class="page-container">
    <div class="table-header">
      <h2>角色权限管理</h2>
      <el-button type="primary" @click="showDialog = true; editingRole = null;">
        <el-icon><Plus /></el-icon> 新增角色
      </el-button>
    </div>

    <el-table :data="personnelStore.roles" stripe>
      <el-table-column prop="name" label="角色名称" width="150" />
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column label="权限" min-width="300">
        <template #default="{ row }">
          <el-tag
            v-for="perm in row.permissions"
            :key="perm"
            size="small"
            style="margin: 2px 4px 2px 0;"
          >
            {{ getPermLabel(perm) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="使用人数" width="100">
        <template #default="{ row }">
          {{ personnelStore.personnel.filter(p => p.roleId === row.id).length }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该角色？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="editingRole ? '编辑角色' : '新增角色'" v-model="showDialog" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="权限">
          <el-checkbox-group v-model="form.permissions">
            <el-checkbox
              v-for="perm in ALL_PERMISSIONS"
              :key="perm.key"
              :label="perm.key"
              :value="perm.key"
            >
              {{ perm.label }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { usePersonnelStore } from '@/stores/personnel'
import { ALL_PERMISSIONS } from '@/utils/constants'

const personnelStore = usePersonnelStore()

const showDialog = ref(false)
const editingRole = ref(null)
const formRef = ref(null)

const form = ref({
  name: '',
  description: '',
  permissions: []
})

const rules = {
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

function getPermLabel(key) {
  return ALL_PERMISSIONS.find(p => p.key === key)?.label || key
}

function handleEdit(role) {
  editingRole.value = role
  form.value = { name: role.name, description: role.description || '', permissions: [...role.permissions] }
  showDialog.value = true
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  if (editingRole.value) {
    await personnelStore.updateRole(editingRole.value.id, { ...form.value })
    ElMessage.success('角色已更新')
  } else {
    await personnelStore.addRole({ ...form.value })
    ElMessage.success('角色已添加')
  }
  showDialog.value = false
  resetForm()
}

async function handleDelete(id) {
  try {
    await personnelStore.deleteRole(id)
    ElMessage.success('角色已删除')
  } catch {
    // 删除失败（角色使用中）的提示已由请求拦截器统一处理
  }
}

function resetForm() {
  editingRole.value = null
  form.value = { name: '', description: '', permissions: [] }
}

onMounted(() => {
  Promise.all([
    personnelStore.fetchRoles(true),
    personnelStore.fetchPersonnel(true),
  ])
})
</script>
