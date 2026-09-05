<template>
  <div class="page-container">
    <el-page-header @back="router.back()" :title="'返回'" :content="task?.title || '任务详情'" />

    <template v-if="task">
      <el-row :gutter="16" style="margin-top: 20px;">
        <el-col :span="16">
          <el-card>
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span style="font-size: 18px; font-weight: 600;">{{ task.title }}</span>
                <div style="display: flex; gap: 8px;">
                  <TaskStatusTag :status="task.status" />
                  <TaskPriorityTag :priority="task.priority" />
                </div>
              </div>
            </template>
            <el-descriptions :column="2" border>
              <el-descriptions-item label="负责人">{{ assigneeName }}</el-descriptions-item>
              <el-descriptions-item label="部门">{{ deptName }}</el-descriptions-item>
              <el-descriptions-item label="分类">{{ task.category || '未分类' }}</el-descriptions-item>
              <el-descriptions-item label="截止日期">
                <span :style="{ color: isOverdue ? '#f56c6c' : '' }">{{ task.deadline || '未设置' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDateTime(task.createdAt) }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ formatDateTime(task.updatedAt) }}</el-descriptions-item>
              <el-descriptions-item label="完成时间" :span="2">{{ task.completedAt ? formatDateTime(task.completedAt) : '未完成' }}</el-descriptions-item>
              <el-descriptions-item label="任务描述" :span="2">{{ task.description || '无描述' }}</el-descriptions-item>
            </el-descriptions>

            <div style="margin-top: 16px; display: flex; gap: 8px;">
              <el-button v-if="task.status === 'pending'" type="warning" @click="changeStatus('inProgress')">开始处理</el-button>
              <el-button v-if="task.status !== 'completed'" type="success" @click="changeStatus('completed')">标记完成</el-button>
              <el-button v-if="task.status === 'completed'" type="info" @click="changeStatus('pending')">重新打开</el-button>
              <el-button @click="showEditDialog = true">编辑</el-button>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card>
            <template #header>操作时间线</template>
            <TaskTimeline :timeline="task.timeline || []" />
          </el-card>
        </el-col>
      </el-row>
    </template>

    <el-empty v-else description="任务不存在" />

    <!-- 编辑弹窗 -->
    <el-dialog title="编辑任务" v-model="showEditDialog" width="550px">
      <TaskForm ref="taskFormRef" :initial-data="task || {}" />
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { useTaskStore } from '@/stores/task'
import { usePersonnelStore } from '@/stores/personnel'
import { formatDateTime, isOverdue as checkOverdue } from '@/utils/dateUtils'
import TaskStatusTag from '@/components/task/TaskStatusTag.vue'
import TaskPriorityTag from '@/components/task/TaskPriorityTag.vue'
import TaskTimeline from '@/components/task/TaskTimeline.vue'
import TaskForm from '@/components/task/TaskForm.vue'

const route = useRoute()
const router = useRouter()
const taskStore = useTaskStore()
const personnelStore = usePersonnelStore()

const showEditDialog = ref(false)
const taskFormRef = ref(null)

const task = computed(() => taskStore.currentTask)
const assigneeName = computed(() => {
  if (!task.value?.assigneeId) return '未分配'
  return personnelStore.personnelById(task.value.assigneeId)?.name || '未知'
})
const deptName = computed(() => {
  if (!task.value?.assigneeId) return '-'
  const person = personnelStore.personnelById(task.value.assigneeId)
  return person ? personnelStore.deptById(person.departmentId)?.name || '-' : '-'
})
const isOverdue = computed(() => task.value && checkOverdue(task.value.deadline) && task.value.status !== 'completed')

async function loadTask(id) {
  try {
    await taskStore.fetchTaskDetail(id)
  } catch {
    // 任务不存在等错误已由拦截器提示
  }
}

async function changeStatus(newStatus) {
  await taskStore.changeTaskStatus(task.value.id, newStatus)
  const labels = { inProgress: '进行中', completed: '已完成', pending: '待处理' }
  ElMessage.success(`任务已标记为${labels[newStatus]}`)
}

async function handleEdit() {
  const valid = await taskFormRef.value.validate()
  if (!valid) return
  const data = taskFormRef.value.getData()
  await taskStore.updateTask(task.value.id, data)
  ElMessage.success('任务已更新')
  showEditDialog.value = false
  await loadTask(task.value.id)
}

onMounted(async () => {
  await Promise.all([
    loadTask(route.params.id),
    personnelStore.fetchPersonnel(),
  ])
})

watch(() => route.params.id, (id) => {
  if (id) loadTask(id)
})
</script>
