import { defineStore } from 'pinia'
import { taskApi } from '@/api'

const emptyStats = () => ({
  total: 0,
  byStatus: { pending: 0, inProgress: 0, completed: 0, overdue: 0 },
  byPriority: { high: 0, medium: 0, low: 0 },
})

export const useTaskStore = defineStore('task', () => {
  const tasks = ref([])
  const stats = ref(emptyStats())
  const currentTask = ref(null)

  const taskById = computed(() => {
    return (id) => tasks.value.find(t => t.id === id)
  })

  const tasksByAssignee = computed(() => {
    return (personnelId) => tasks.value.filter(t => t.assigneeId === personnelId)
  })

  async function fetchTasks(force = false) {
    if (!force && tasks.value.length) return tasks.value
    tasks.value = await taskApi.list()
    return tasks.value
  }

  async function fetchTaskDetail(id) {
    currentTask.value = await taskApi.get(id)
    return currentTask.value
  }

  async function fetchStats() {
    stats.value = await taskApi.statistics()
    return stats.value
  }

  async function addTask(taskData) {
    const newTask = await taskApi.create(taskData)
    tasks.value.unshift(newTask)
    return newTask
  }

  async function updateTask(id, updates) {
    await taskApi.update(id, updates)
    const index = tasks.value.findIndex(t => t.id === id)
    if (index !== -1) {
      tasks.value[index] = { ...tasks.value[index], ...updates }
    }
  }

  async function deleteTask(id) {
    await taskApi.remove(id)
    tasks.value = tasks.value.filter(t => t.id !== id)
  }

  async function changeTaskStatus(id, newStatus) {
    await taskApi.changeStatus(id, newStatus)
    const index = tasks.value.findIndex(t => t.id === id)
    if (index !== -1) {
      tasks.value[index].status = newStatus
    }
    if (currentTask.value?.id === id) {
      await fetchTaskDetail(id)
    }
  }

  return {
    tasks, stats, currentTask,
    taskById, tasksByAssignee,
    fetchTasks, fetchTaskDetail, fetchStats,
    addTask, updateTask, deleteTask, changeTaskStatus,
  }
})
