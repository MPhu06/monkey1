<template>
  <div class="card overflow-hidden">
    <div class="flex items-center gap-3 px-5 py-4 border-b border-slate-200 bg-slate-50">
      <span class="text-lg">📋</span>
      <h2 class="text-base font-semibold text-slate-800">Lịch sử tạo sơ đồ</h2>
      <span class="ml-auto text-xs font-medium text-slate-500 bg-slate-200 px-2.5 py-1 rounded-full">
        {{ diagrams.length }} sơ đồ
      </span>
    </div>

    <div v-if="diagrams.length === 0" class="flex flex-col items-center justify-center py-16 px-6 text-center">
      <div class="w-16 h-16 rounded-2xl bg-slate-100 flex items-center justify-center mb-4">
        <svg class="w-8 h-8 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
        </svg>
      </div>
      <h3 class="text-base font-semibold text-slate-700 mb-1">Chưa có lịch sử</h3>
      <p class="text-sm text-slate-500">Các sơ đồ bạn tạo sẽ xuất hiện ở đây.</p>
    </div>

    <div v-else class="divide-y divide-slate-100">
      <div v-for="diagram in diagrams" :key="diagram.id"
           class="flex items-center gap-4 px-5 py-4 hover:bg-slate-50 transition-colors duration-150 group">
        <div class="flex-shrink-0 w-12 h-12 rounded-xl bg-primary-50 border border-primary-100
                    flex items-center justify-center">
          <span class="text-xl">{{ getDiagramIcon(diagram.diagramType) }}</span>
        </div>

        <div class="flex-1 min-w-0">
          <div class="flex items-center gap-2">
            <h3 class="text-sm font-semibold text-slate-800 truncate">
              {{ getDiagramTypeLabel(diagram.diagramType) }}
            </h3>
            <span class="inline-flex items-center gap-1 text-[10px] font-medium px-2 py-0.5 rounded-full"
                  :class="diagram.status === 'completed'
                    ? 'bg-emerald-50 text-emerald-600'
                    : 'bg-amber-50 text-amber-600'">
              <span class="w-1.5 h-1.5 rounded-full"
                    :class="diagram.status === 'completed' ? 'bg-emerald-400' : 'bg-amber-400'"></span>
              {{ diagram.status }}
            </span>
          </div>
          <p class="text-xs text-slate-500 mt-0.5 truncate">
            {{ diagram.inputText || 'File PPTX uploaded' }}
          </p>
          <p class="text-[11px] text-slate-400 mt-1">
            {{ formatDate(diagram.createdAt) }}
          </p>
        </div>

        <div class="flex-shrink-0 flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
          <button @click="$emit('view', diagram)"
                  class="btn-ghost p-2" title="Xem chi tiết">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
            </svg>
          </button>
          <button @click="$emit('delete', diagram.id)"
                  class="btn-ghost p-2 text-red-500 hover:text-red-600 hover:bg-red-50" title="Xoá">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
            </svg>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  diagrams: {
    type: Array,
    default: () => []
  }
})

defineEmits(['view', 'delete'])

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

function getDiagramIcon(type) {
  const icons = {
    flowchart: '📊',
    sequence: '🔄',
    mindmap: '🧠',
    er: '🗃️',
    class: '📦',
    org: '👥'
  }
  return icons[type] || '📈'
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
</script>
