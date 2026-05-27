import axios from 'axios'

const API_BASE = 'https://slide-to-diagram-backend-6a6f.onrender.com'

const api = axios.create({
  baseURL: API_BASE,
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true
})

api.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

api.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export const diagramService = {
  generateDiagram(formData) {
    return api.post('/diagrams/generate', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  getAllDiagrams() {
    return api.get('/diagrams')
  },

  getDiagramById(id) {
    return api.get(`/diagrams/${id}`)
  },

  deleteDiagram(id) {
    return api.delete(`/diagrams/${id}`)
  }
}

export const healthService = {
  check() {
    return api.get('/health')
  }
}

export default api
