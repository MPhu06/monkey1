<template>
  <div class="space-y-4">
    <div class="flex gap-2">
      <button v-for="type in inputTypes" :key="type.value"
              class="flex-1 py-2.5 px-4 rounded-lg text-sm font-semibold transition-all duration-200"
              :class="selected === type.value
                ? 'bg-primary-600 text-white shadow-sm'
                : 'bg-slate-100 text-slate-600 hover:bg-slate-200'"
              @click="$emit('update:selected', type.value)">
        <span class="flex items-center justify-center gap-2">
          <span v-html="type.icon"></span>
          {{ type.label }}
        </span>
      </button>
    </div>

    <div v-if="selected === 'text'" class="space-y-2">
      <label class="block text-sm font-medium text-slate-700">Nội dung văn bản</label>
      <textarea
        :value="textValue"
        @input="$emit('update:textValue', $event.target.value)"
        class="textarea-field"
        rows="8"
        placeholder="Nhập nội dung slide của bạn vào đây...

Ví dụ:
Bước 1: Đăng nhập
Bước 2: Chọn sản phẩm
Bước 3: Thanh toán
Bước 4: Hoàn thành

Hoặc sử dụng mũi tên:
Người dùng --> Hệ thống --> Kho hàng --> Khách hàng"
      ></textarea>
      <p class="text-xs text-slate-400 text-right">{{ (textValue || '').length }} ký tự</p>
    </div>

    <div v-else class="space-y-2">
      <FileUploader :model-value="fileValue" @update:model-value="$emit('update:fileValue', $event)" />
      <p class="text-xs text-slate-400">Hỗ trợ file .pptx có kích thước tối đa 10MB</p>
    </div>
  </div>
</template>

<script setup>
import FileUploader from '@/components/FileUploader.vue'

const props = defineProps({
  selected: {
    type: String,
    default: 'text'
  },
  textValue: {
    type: String,
    default: ''
  },
  fileValue: {
    type: File,
    default: null
  }
})

defineEmits(['update:selected', 'update:textValue', 'update:fileValue'])

const inputTypes = [
  {
    value: 'text',
    label: 'Nhập Text',
    icon: `<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                   d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
           </svg>`
  },
  {
    value: 'pptx',
    label: 'Upload PPTX',
    icon: `<svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                   d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
           </svg>`
  }
]
</script>
