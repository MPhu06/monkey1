<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="mb-8">
      <h1 class="text-3xl font-extrabold text-slate-900 tracking-tight">
        Lịch sử sơ đồ
      </h1>
      <p class="mt-2 text-slate-500 text-base">
        Xem lại các sơ đồ bạn đã tạo gần đây.
      </p>
    </div>

    <div class="space-y-6">
      <HistoryList :diagrams="diagramStore.sortedDiagrams"
                   @view="handleView" @delete="handleDelete" />
    </div>

    <Teleport to="body">
      <div v-if="selectedDiagram"
           class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm"
           @click.self="selectedDiagram = null">
        <div class="bg-white rounded-2xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-auto">
          <div class="sticky top-0 bg-white border-b border-slate-200 px-6 py-4 flex items-center justify-between">
            <div>
              <h2 class="text-lg font-bold text-slate-900">
                {{ getDiagramTypeLabel(selectedDiagram.diagramType) }}
              </h2>
              <p class="text-xs text-slate-500 mt-0.5">
                Tạo lúc {{ formatDate(selectedDiagram.createdAt) }}
              </p>
            </div>
            <button @click="selectedDiagram = null"
                    class="p-2 hover:bg-slate-100 rounded-lg transition-colors">
              <svg class="w-5 h-5 text-slate-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>

          <div class="p-6">
            <DiagramPreview :mermaid-code="selectedDiagram.mermaidCode"
                            :diagram-type="selectedDiagram.diagramType"
                            :status="selectedDiagram.status"
                            @download="handleDownloadModal" />

            <div v-if="selectedDiagram.inputText" class="mt-6">
              <h3 class="text-sm font-semibold text-slate-700 mb-2">Nội dung gốc</h3>
              <div class="bg-slate-50 rounded-lg p-4 max-h-40 overflow-y-auto">
                <p class="text-sm text-slate-600 whitespace-pre-wrap">{{ selectedDiagram.inputText }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import HistoryList from '@/components/HistoryList.vue'
import DiagramPreview from '@/components/DiagramPreview.vue'
import { useDiagramStore } from '@/stores/diagramStore'

const router = useRouter()
const diagramStore = useDiagramStore()
const selectedDiagram = ref(null)

onMounted(() => {
  diagramStore.fetchAllDiagrams()
})

function handleView(diagram) {
  selectedDiagram.value = diagram
}

async function handleDelete(id) {
  try {
    await diagramStore.deleteDiagram(id)
  } catch (e) {
    console.error('Failed to delete:', e)
  }
}

function getDiagramTypeLabel(type) {
  const labels = {
    flowchart: 'Flowchart',
    sequence: 'Sequence Diagram',
    mindmap: 'Mind Map',
    er: 'ER Diagram',
    class: 'Class Diagram',
    org: 'Org Chart'
  }
  return labels[type] || type
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function handleDownloadModal(format) {
  if (!selectedDiagram.value) return

  const svgEl = document.querySelector('.diagram-container svg')
  if (!svgEl) return

  const filename = `diagram-${selectedDiagram.value.id}`

  if (format === 'svg') {
    const svgClone = svgEl.cloneNode(true)
    svgClone.setAttribute('xmlns', 'http://www.w3.org/2000/svg')
    const svgData = new XMLSerializer().serializeToString(svgClone)
    const blob = new Blob([svgData], { type: 'image/svg+xml;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    downloadFile(url, `${filename}.svg`)
    URL.revokeObjectURL(url)
  } else {
    downloadPngFromSvgModal(svgEl, filename)
  }
}

function downloadFile(url, fileName) {
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.click()
}

async function downloadPngFromSvgModal(svgEl, baseName) {
  const svgClone = svgEl.cloneNode(true)

  const bbox = svgEl.getBBox()
  let width = svgEl.getAttribute('width') || bbox.width + 40
  let height = svgEl.getAttribute('height') || bbox.height + 40
  if (!width || width < 10) width = 800
  if (!height || height < 10) height = 600

  svgClone.setAttribute('width', width)
  svgClone.setAttribute('height', height)
  svgClone.setAttribute('xmlns', 'http://www.w3.org/2000/svg')

  svgClone.querySelectorAll('[style]').forEach(el => el.removeAttribute('style'))

  const svgStr = '<?xml version="1.0" encoding="UTF-8"?>' +
    new XMLSerializer().serializeToString(svgClone)

  const svgBlob = new Blob([svgStr], { type: 'image/svg+xml;charset=utf-8' })
  const svgUrl = URL.createObjectURL(svgBlob)

  const img = new Image()
  img.onload = () => {
    const canvas = document.createElement('canvas')
    const scale = 2
    canvas.width = img.naturalWidth * scale
    canvas.height = img.naturalHeight * scale
    const ctx = canvas.getContext('2d')
    ctx.fillStyle = '#ffffff'
    ctx.fillRect(0, 0, canvas.width, canvas.height)
    ctx.drawImage(img, 0, 0, canvas.width, canvas.height)
    URL.revokeObjectURL(svgUrl)

    canvas.toBlob(blob => {
      if (blob) {
        const url = URL.createObjectURL(blob)
        downloadFile(url, `${baseName}.png`)
        URL.revokeObjectURL(url)
      }
    }, 'image/png')
  }
  img.onerror = () => URL.revokeObjectURL(svgUrl)
  img.src = svgUrl
}
</script>
