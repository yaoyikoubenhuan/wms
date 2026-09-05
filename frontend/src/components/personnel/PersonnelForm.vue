<template>
  <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
    <el-form-item label="姓名" prop="name">
      <el-input v-model="form.name" placeholder="请输入姓名" />
    </el-form-item>
    <el-form-item label="部门" prop="departmentId">
      <el-tree-select
        v-model="form.departmentId"
        :data="personnelStore.departmentTree"
        :props="{ label: 'name', value: 'id', children: 'children' }"
        placeholder="请选择部门"
        check-strictly
        style="width: 100%;"
      />
    </el-form-item>
    <el-form-item label="职位" prop="position">
      <el-input v-model="form.position" placeholder="请输入职位" />
    </el-form-item>
    <el-form-item label="角色" prop="roleId">
      <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%;">
        <el-option
          v-for="role in personnelStore.roles"
          :key="role.id"
          :label="role.name"
          :value="role.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="手机号" prop="phone">
      <el-input v-model="form.phone" placeholder="请输入手机号" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="form.email" placeholder="请输入邮箱" />
    </el-form-item>
    <el-form-item label="入职日期">
      <el-date-picker v-model="form.entryDate" type="date" placeholder="请选择入职日期" value-format="YYYY-MM-DD" style="width: 100%;" />
    </el-form-item>
  </el-form>
</template>

<script setup>
import { usePersonnelStore } from '@/stores/personnel'

const props = defineProps({
  initialData: { type: Object, default: () => ({}) }
})

const personnelStore = usePersonnelStore()
const formRef = ref(null)

const form = ref({
  name: '',
  departmentId: '',
  position: '',
  roleId: 'role-member',
  phone: '',
  email: '',
  entryDate: null,
  ...props.initialData
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }],
}

watch(() => props.initialData, (val) => {
  if (val && val.id) {
    form.value = { ...form.value, ...val }
  } else {
    // 新增场景：重置为默认值，避免残留上一次编辑的数据
    form.value = {
      name: '',
      departmentId: '',
      position: '',
      roleId: 'role-member',
      phone: '',
      email: '',
      entryDate: null,
    }
  }
}, { deep: true })

async function validate() {
  return formRef.value.validate().catch(() => false)
}

function getData() {
  return { ...form.value }
}

defineExpose({ validate, getData })
</script>
