<template>
  <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
    <div class="mb-8">
      <h1 class="text-3xl font-extrabold text-slate-900 tracking-tight">
        Tạo sơ đồ từ Slide
      </h1>
      <p class="mt-2 text-slate-500 text-base">
        Upload file PowerPoint hoặc nhập nội dung text — chúng tôi sẽ chuyển thành sơ đồ đẹp mắt cho bạn.
      </p>
    </div>

    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="space-y-5">
        <div class="card p-5">
          <h2 class="text-sm font-semibold text-slate-700 mb-4 flex items-center gap-2">
            <svg class="w-4 h-4 text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
            </svg>
            Nguồn dữ liệu
          </h2>
          <InputSection v-model:selected="inputType" v-model:textValue="inputText"
                        v-model:fileValue="uploadFile" />
        </div>

        <DiagramTypeSelector v-model:selected="diagramType" />

        <div class="card p-5">
          <h2 class="text-sm font-semibold text-slate-700 mb-4 flex items-center gap-2">
            <svg class="w-4 h-4 text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
            Hành động
          </h2>
          <button @click="handleGenerate"
                  :disabled="isLoading || (!inputText && !uploadFile)"
                  class="btn-primary w-full py-3 text-base flex items-center justify-center gap-2">
            <svg v-if="isLoading" class="w-5 h-5 animate-spin" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor"
                    d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M13 10V3L4 14h7v7l9-11h-7z" />
            </svg>
            {{ isLoading ? 'Đang tạo sơ đồ...' : 'Tạo sơ đồ' }}
          </button>

          <div v-if="error" class="mt-4 p-3 bg-red-50 border border-red-200 rounded-lg">
            <p class="text-sm text-red-600 flex items-start gap-2">
              <svg class="w-4 h-4 mt-0.5 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              {{ error }}
            </p>
          </div>

          <div v-if="successMessage" class="mt-4 p-3 bg-emerald-50 border border-emerald-200 rounded-lg">
            <p class="text-sm text-emerald-600 flex items-center gap-2">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              {{ successMessage }}
            </p>
          </div>
        </div>
      </div>

      <div class="space-y-5">
        <div class="sticky top-24">
          <h2 class="text-sm font-semibold text-slate-700 mb-3 flex items-center gap-2">
            <svg class="w-4 h-4 text-primary-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
            </svg>
            Xem trước sơ đồ
          </h2>
          <DiagramPreview :mermaid-code="currentMermaidCode" :diagram-type="diagramType"
                          :status="previewStatus" @download="handleDownload" />
        </div>
      </div>
    </div>

    <div v-if="recentDiagrams.length > 0" class="mt-8">
      <h2 class="text-sm font-semibold text-slate-700 mb-3">Gần đây</h2>
      <HistoryList :diagrams="recentDiagrams" @view="handleViewDiagram" @delete="handleDeleteDiagram" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import InputSection from '@/components/InputSection.vue'
import DiagramTypeSelector from '@/components/DiagramTypeSelector.vue'
import DiagramPreview from '@/components/DiagramPreview.vue'
import HistoryList from '@/components/HistoryList.vue'
import { useDiagramStore } from '@/stores/diagramStore'

const router = useRouter()
const diagramStore = useDiagramStore()

const inputType = ref('text')
const inputText = ref('')
const uploadFile = ref(null)
const diagramType = ref('flowchart')
const error = ref('')
const successMessage = ref('')
const previewStatus = ref('')

const isLoading = computed(() => diagramStore.isLoading)
const currentMermaidCode = computed(() => diagramStore.currentDiagram?.mermaidCode || '')
const recentDiagrams = computed(() => diagramStore.sortedDiagrams.slice(0, 5))

async function handleGenerate() {
  error.value = ''
  successMessage.value = ''
  previewStatus.value = 'Đang tạo...'

  if (inputType.value === 'text' && !inputText.value.trim()) {
    error.value = 'Vui lòng nhập nội dung text.'
    previewStatus.value = ''
    return
  }

  if (inputType.value === 'pptx' && !uploadFile.value) {
    error.value = 'Vui lòng upload file PowerPoint.'
    previewStatus.value = ''
    return
  }

  try {
    const formData = new FormData()
    formData.append('inputType', inputType.value)
    formData.append('diagramType', diagramType.value)

    if (inputType.value === 'text') {
      formData.append('inputText', inputText.value)
    } else {
      formData.append('file', uploadFile.value)
    }

    const diagram = await diagramStore.generateDiagram(formData)
    previewStatus.value = 'Đã tạo thành công!'
    successMessage.value = `Sơ đồ "${diagram.diagramType}" đã được tạo thành công.`
    error.value = ''
  } catch (e) {
    error.value = diagramStore.error || 'Đã xảy ra lỗi khi tạo sơ đồ. Vui lòng thử lại.'
    previewStatus.value = ''
  }
}

function handleViewDiagram(diagram) {
  diagramStore.setCurrentDiagram(diagram)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function handleDeleteDiagram(id) {
  try {
    await diagramStore.deleteDiagram(id)
  } catch (e) {
    error.value = 'Không thể xoá sơ đồ.'
  }
}

function handleDownload(format) {
  if (!currentMermaidCode.value) return

  const svgEl = document.querySelector('.diagram-container svg')
  if (!svgEl) return

  if (format === 'svg') {
    const svgClone = svgEl.cloneNode(true)
    svgClone.setAttribute('xmlns', 'http://www.w3.org/2000/svg')
    svgClone.setAttribute('xmlns:xlink', 'http://www.w3.org/1999/xlink')
    const svgData = new XMLSerializer().serializeToString(svgClone)
    const blob = new Blob([svgData], { type: 'image/svg+xml;charset=utf-8' })
    const url = URL.createObjectURL(blob)
    downloadFile(url, `diagram-${Date.now()}.svg`)
    URL.revokeObjectURL(url)
  } else {
    downloadPngFromSvg(svgEl)
  }
}

function downloadFile(url, filename) {
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
}

async function downloadPngFromSvg(svgEl) {
  const svgClone = svgEl.cloneNode(true)

  const bbox = svgEl.getBBox()
  let width = svgEl.getAttribute('width') || bbox.width + 40
  let height = svgEl.getAttribute('height') || bbox.height + 40

  if (!width || width < 10) width = 800
  if (!height || height < 10) height = 600

  svgClone.setAttribute('width', width)
  svgClone.setAttribute('height', height)
  svgClone.setAttribute('xmlns', 'http://www.w3.org/2000/svg')

  svgClone.querySelectorAll('[style]').forEach(el => {
    el.removeAttribute('style')
  })

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
        downloadFile(url, `diagram-${Date.now()}.png`)
        URL.revokeObjectURL(url)
      }
    }, 'image/png')
  }

  img.onerror = () => {
    URL.revokeObjectURL(svgUrl)
  }

  img.src = svgUrl
}
</script>
