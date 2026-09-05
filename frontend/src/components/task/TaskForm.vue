<template>
  <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
    <el-form-item label="任务名称" prop="title">
      <el-input v-model="form.title" placeholder="请输入任务名称" />
    </el-form-item>
    <el-form-item label="描述">
      <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入任务描述" />
    </el-form-item>
    <el-form-item label="优先级" prop="priority">
      <el-radio-group v-model="form.priority">
        <el-radio-button value="high">高</el-radio-button>
        <el-radio-button value="medium">中</el-radio-button>
        <el-radio-button value="low">低</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="指派给">
      <el-select v-model="form.assigneeId" placeholder="选择负责人" clearable filterable style="width: 100%;">
        <el-option
          v-for="p in personnelStore.personnel"
          :key="p.id"
          :label="p.name"
          :value="p.id"
        >
          <span>{{ p.name }}</span>
          <span style="color: #909399; margin-left: 8px; font-size: 12px;">{{ getDeptName(p.departmentId) }}</span>
        </el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="分类标签">
      <el-input v-model="form.category" placeholder="如：Bug、需求、优化" />
    </el-form-item>
    <el-form-item label="截止日期">
      <el-date-picker v-model="form.deadline" type="date" placeholder="请选择截止日期" value-format="YYYY-MM-DD" style="width: 100%;" />
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
  title: '',
  description: '',
  priority: 'medium',
  assigneeId: null,
  category: '',
  deadline: null,
  ...props.initialData
})

const rules = {
  title: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
  priority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
}

function getDeptName(deptId) {
  return personnelStore.deptById(deptId)?.name || ''
}

watch(() => props.initialData, (val) => {
  if (val && val.id) {
    form.value = { ...form.value, ...val }
  } else {
    // 新建场景：重置为默认值，避免残留上一次编辑的数据
    form.value = {
      title: '',
      description: '',
      priority: 'medium',
      assigneeId: null,
      category: '',
      deadline: null,
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
