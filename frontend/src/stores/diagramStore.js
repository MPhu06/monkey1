import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { diagramService } from '@/services/api'

export const useDiagramStore = defineStore('diagram', () => {
  const diagrams = ref([])
  const currentDiagram = ref(null)
  const isLoading = ref(false)
  const error = ref(null)

  const sortedDiagrams = computed(() => {
    return [...diagrams.value].sort((a, b) =>
      new Date(b.createdAt) - new Date(a.createdAt)
    )
  })

  async function fetchAllDiagrams() {
    isLoading.value = true
    error.value = null
    try {
      const response = await diagramService.getAllDiagrams()
      diagrams.value = response.data.data || []
    } catch (e) {
      error.value = e.message
      console.error('Failed to fetch diagrams:', e)
    } finally {
      isLoading.value = false
    }
  }

  async function generateDiagram(formData) {
    isLoading.value = true
    error.value = null
    try {
      const response = await diagramService.generateDiagram(formData)
      const newDiagram = response.data.data
      currentDiagram.value = newDiagram
      diagrams.value.unshift(newDiagram)
      return newDiagram
    } catch (e) {
      error.value = e.response?.data?.message || e.message
      throw e
    } finally {
      isLoading.value = false
    }
  }

  async function deleteDiagram(id) {
    try {
      await diagramService.deleteDiagram(id)
      diagrams.value = diagrams.value.filter(d => d.id !== id)
      if (currentDiagram.value?.id === id) {
        currentDiagram.value = null
      }
    } catch (e) {
      error.value = e.message
      throw e
    }
  }

  function setCurrentDiagram(diagram) {
    currentDiagram.value = diagram
  }

  function clearError() {
    error.value = null
  }

  return {
    diagrams,
    currentDiagram,
    isLoading,
    error,
    sortedDiagrams,
    fetchAllDiagrams,
    generateDiagram,
    deleteDiagram,
    setCurrentDiagram,
    clearError
  }
})
