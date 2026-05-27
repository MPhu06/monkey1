<template>
  <div class="card overflow-hidden">
    <div v-if="mermaidCode" class="flex flex-col h-full">
      <div class="flex items-center justify-between px-5 py-3.5 border-b border-slate-200 bg-slate-50">
        <div class="flex items-center gap-2">
          <div class="flex gap-1.5">
            <span class="w-3 h-3 rounded-full bg-red-400"></span>
            <span class="w-3 h-3 rounded-full bg-yellow-400"></span>
            <span class="w-3 h-3 rounded-full bg-green-400"></span>
          </div>
          <span class="text-xs font-medium text-slate-500 ml-2">{{ diagramType }}</span>
        </div>
        <div class="flex items-center gap-2">
          <button @click="showCode = !showCode"
                  class="btn-ghost text-xs px-3 py-1.5 flex items-center gap-1.5">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M10 20l4-16m4 4l4 4-4 4M6 16l-4-4 4-4" />
            </svg>
            {{ showCode ? 'Ẩn code' : 'Xem code' }}
          </button>
          <button @click="copyCode"
                  class="btn-ghost text-xs px-3 py-1.5 flex items-center gap-1.5">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />
            </svg>
            {{ copied ? 'Đã copy!' : 'Copy' }}
          </button>
        </div>
      </div>

      <div v-if="showCode" class="border-b border-slate-200 bg-slate-900 p-4">
        <pre class="text-xs text-green-400 font-mono overflow-x-auto whitespace-pre-wrap
                    scrollbar-thin max-h-48 overflow-y-auto"><code>{{ mermaidCode }}</code></pre>
      </div>

      <div class="flex-1 p-4 flex items-center justify-center bg-white min-h-[300px]"
           ref="previewContainer">
        <div v-if="isRendering" class="flex flex-col items-center gap-3 text-slate-400">
          <svg class="w-8 h-8 animate-spin" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
          </svg>
          <span class="text-xs">Đang vẽ sơ đồ...</span>
        </div>
        <div v-else-if="renderError" class="text-red-500 text-sm p-4 bg-red-50 rounded-lg">
          {{ renderError }}
        </div>
        <div v-else class="diagram-container" v-html="renderedSvg"></div>
      </div>

      <div class="flex items-center justify-between px-5 py-3.5 border-t border-slate-200 bg-slate-50">
        <span class="text-xs text-slate-500">{{ status || 'Ready' }}</span>
        <div class="flex items-center gap-2">
          <button @click="$emit('download', 'png')"
                  class="btn-secondary text-xs px-3 py-1.5 flex items-center gap-1.5">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
            PNG
          </button>
          <button @click="$emit('download', 'svg')"
                  class="btn-primary text-xs px-3 py-1.5 flex items-center gap-1.5">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            SVG
          </button>
        </div>
      </div>
    </div>

    <div v-else class="flex flex-col items-center justify-center py-20 px-6 text-center">
      <div class="w-20 h-20 rounded-3xl bg-slate-100 flex items-center justify-center mb-4">
        <svg class="w-10 h-10 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
      </div>
      <h3 class="text-lg font-semibold text-slate-700 mb-2">Chưa có sơ đồ</h3>
      <p class="text-sm text-slate-500 max-w-xs">
        Nhập nội dung hoặc upload file PowerPoint, sau đó nhấn "Tạo sơ đồ" để xem kết quả.
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, onMounted } from 'vue'

const props = defineProps({
  mermaidCode: {
    type: String,
    default: ''
  },
  diagramType: {
    type: String,
    default: ''
  },
  status: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['download'])

const showCode = ref(false)
const copied = ref(false)
const renderedSvg = ref('')
const isRendering = ref(false)
const renderError = ref('')
const previewContainer = ref(null)

let mermaid = null

async function initMermaid() {
  if (mermaid) return
  const mod = await import('mermaid')
  mermaid = mod.default
  mermaid.initialize({
    startOnLoad: false,
    theme: 'base',
    themeVariables: {
      primaryColor: '#EEF2FF',
      primaryTextColor: '#1E293B',
      primaryBorderColor: '#6366F1',
      lineColor: '#94A3B8',
      secondaryColor: '#F0FDF4',
      tertiaryColor: '#FAFAFA',
      fontSize: '14px'
    },
    flowchart: {
      htmlLabels: true,
      curve: 'basis'
    },
    securityLevel: 'loose'
  })
}

async function renderDiagram() {
  if (!props.mermaidCode) {
    renderedSvg.value = ''
    isRendering.value = false
    return
  }

  isRendering.value = true
  renderError.value = ''

  try {
    await initMermaid()
    const id = 'mermaid-' + Date.now()
    const { svg } = await mermaid.render(id, props.mermaidCode)
    renderedSvg.value = svg
  } catch (e) {
    console.error('Mermaid render error:', e)
    renderError.value = 'Lỗi render: ' + e.message
  } finally {
    isRendering.value = false
  }
}

async function copyCode() {
  try {
    await navigator.clipboard.writeText(props.mermaidCode)
    copied.value = true
    setTimeout(() => { copied.value = false }, 2000)
  } catch {
    copied.value = false
  }
}

watch(() => props.mermaidCode, async (newVal) => {
  if (!newVal) {
    renderedSvg.value = ''
    isRendering.value = false
    return
  }
  await nextTick()
  renderDiagram()
}, { immediate: false })

onMounted(() => {
  if (props.mermaidCode) {
    renderDiagram()
  }
})
</script>

<style scoped>
.diagram-container :deep(svg) {
  max-width: 100%;
  height: auto;
}

.diagram-container :deep(.node) {
  transition: all 0.2s ease;
}

.diagram-container :deep(.node:hover) {
  filter: brightness(0.95);
}
</style>
