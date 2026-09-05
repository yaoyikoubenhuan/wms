<template>
  <div class="page-container">
    <div class="table-header">
      <h2>人员列表</h2>
      <div class="search-area">
        <el-input v-model="searchText" placeholder="搜索姓名/职位" prefix-icon="Search" clearable style="width: 240px;" />
        <el-tree-select
          v-model="filterDept"
          :data="personnelStore.departmentTree"
          :props="{ label: 'name', value: 'id', children: 'children' }"
          placeholder="筛选部门"
          check-strictly
          clearable
          style="width: 180px;"
        />
        <el-button type="primary" @click="showDialog = true">
          <el-icon><Plus /></el-icon> 新增人员
        </el-button>
      </div>
    </div>

    <el-table :data="filteredPersonnel" stripe style="width: 100%;">
      <el-table-column prop="name" label="姓名" width="120">
        <template #default="{ row }">
          <el-link type="primary" @click="goDetail(row.id)">{{ row.name }}</el-link>
        </template>
      </el-table-column>
      <el-table-column prop="position" label="职位" width="150" />
      <el-table-column label="部门" width="120">
        <template #default="{ row }">
          {{ getDeptName(row.departmentId) }}
        </template>
      </el-table-column>
      <el-table-column label="角色" width="120">
        <template #default="{ row }">
          {{ getRoleName(row.roleId) }}
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="140" />
      <el-table-column prop="email" label="邮箱" min-width="180" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'danger'" size="small">
            {{ row.status === 'active' ? '在职' : '离职' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-popconfirm title="确定删除该人员？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="editingPerson ? '编辑人员' : '新增人员'" v-model="showDialog" width="500px" @close="resetForm">
      <PersonnelForm ref="personnelFormRef" :initial-data="editingPerson || {}" />
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { usePersonnelStore } from '@/stores/personnel'
import PersonnelForm from '@/components/personnel/PersonnelForm.vue'

const router = useRouter()
const personnelStore = usePersonnelStore()

const searchText = ref('')
const filterDept = ref('')
const showDialog = ref(false)
const editingPerson = ref(null)
const submitting = ref(false)
const personnelFormRef = ref(null)

const filteredPersonnel = computed(() => {
  let list = personnelStore.personnel
  if (searchText.value) {
    const text = searchText.value.toLowerCase()
    list = list.filter(p => p.name.toLowerCase().includes(text) || (p.position || '').toLowerCase().includes(text))
  }
  if (filterDept.value) {
    list = list.filter(p => p.departmentId === filterDept.value)
  }
  return list
})

function getDeptName(deptId) {
  return personnelStore.deptById(deptId)?.name || '未分配'
}

function getRoleName(roleId) {
  return personnelStore.roleById(roleId)?.name || '未设置'
}

function goDetail(id) {
  router.push(`/personnel/${id}`)
}

function handleEdit(person) {
  editingPerson.value = { ...person }
  showDialog.value = true
}

async function handleSubmit() {
  const valid = await personnelFormRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    const data = personnelFormRef.value.getData()

    if (editingPerson.value) {
      await personnelStore.updatePerson(editingPerson.value.id, data)
      ElMessage.success('人员信息已更新')
    } else {
      await personnelStore.addPerson(data)
      ElMessage.success('人员已添加')
    }

    showDialog.value = false
    resetForm()
  } finally {
    submitting.value = false
  }
}

async function handleDelete(id) {
  await personnelStore.deletePerson(id)
  ElMessage.success('人员已删除')
}

function resetForm() {
  editingPerson.value = null
}

onMounted(() => {
  personnelStore.fetchAll()
})
</script>
