<template>
  <div class="card p-6 border-2 border-dashed border-slate-300 hover:border-primary-400
              hover:bg-primary-50/30 transition-all duration-300 cursor-pointer"
       :class="{ 'border-primary-500 bg-primary-50/50': isDragging }"
       @dragover.prevent="isDragging = true"
       @dragleave.prevent="isDragging = false"
       @drop.prevent="handleDrop"
       @click="triggerFileInput">
    <input ref="fileInput" type="file" accept=".pptx" class="hidden" @change="handleFileChange" />

    <div class="flex flex-col items-center justify-center text-center py-6 gap-4">
      <div class="w-16 h-16 rounded-2xl bg-gradient-to-br from-red-100 to-orange-100
                  flex items-center justify-center">
        <svg class="w-8 h-8 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
      </div>
      <div>
        <p class="text-base font-semibold text-slate-700">
          {{ isDragging ? 'Thả file vào đây!' : 'Kéo thả file PowerPoint (.pptx)' }}
        </p>
        <p class="text-sm text-slate-500 mt-1">hoặc nhấn để chọn file</p>
      </div>
      <span v-if="fileName" class="inline-flex items-center gap-2 px-3 py-1.5 bg-emerald-50
                                     text-emerald-700 text-sm font-medium rounded-full">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
        </svg>
        {{ fileName }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  modelValue: {
    type: File,
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

const fileInput = ref(null)
const isDragging = ref(false)
const fileName = ref('')

function triggerFileInput() {
  fileInput.value?.click()
}

function handleFileChange(event) {
  const file = event.target.files[0]
  if (file) {
    setFile(file)
  }
}

function handleDrop(event) {
  isDragging.value = false
  const file = event.dataTransfer.files[0]
  if (file && file.name.toLowerCase().endsWith('.pptx')) {
    setFile(file)
  }
}

function setFile(file) {
  fileName.value = file.name
  emit('update:modelValue', file)
}
</script>
