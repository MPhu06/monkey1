<template>
  <div class="card p-5">
    <div class="space-y-3">
      <label class="block text-sm font-semibold text-slate-700">Loại sơ đồ</label>
      <div class="grid grid-cols-2 sm:grid-cols-3 gap-2">
        <button v-for="type in diagramTypes" :key="type.value"
                class="flex flex-col items-center gap-2 py-3 px-2 rounded-xl text-sm font-medium
                       transition-all duration-200 border-2"
                :class="selected === type.value
                  ? 'border-primary-500 bg-primary-50 text-primary-700 shadow-sm'
                  : 'border-slate-200 bg-white text-slate-600 hover:border-slate-300 hover:bg-slate-50'"
                @click="$emit('update:selected', type.value)">
          <span class="text-2xl" v-html="type.icon"></span>
          <span class="text-xs font-semibold">{{ type.label }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  selected: {
    type: String,
    default: 'flowchart'
  }
})

defineEmits(['update:selected'])

const diagramTypes = [
  {
    value: 'flowchart',
    label: 'Flowchart',
    icon: `<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                   d="M4 5a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1H5a1 1 0 01-1-1V5zm10 0a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 01-1-1V5zM4 15a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1H5a1 1 0 01-1-1v-4zm10 0a1 1 0 011-1h4a1 1 0 011 1v4a1 1 0 01-1 1h-4a1 1 0 01-1-1v-4z" />
           </svg>`
  },
  {
    value: 'sequence',
    label: 'Sequence',
    icon: `<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                   d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
           </svg>`
  },
  {
    value: 'mindmap',
    label: 'Mind Map',
    icon: `<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                   d="M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z" />
           </svg>`
  },
  {
    value: 'er',
    label: 'ER Diagram',
    icon: `<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                   d="M4 7v10c0 2.21 3.582 4 8 4s8-1.79 8-4V7M4 7c0 2.21 3.582 4 8 4s8-1.79 8-4M4 7c0-2.21 3.582-4 8-4s8 1.79 8 4m0 5c0 2.21-3.582 4-8 4s-8-1.79-8-4" />
           </svg>`
  },
  {
    value: 'class',
    label: 'Class UML',
    icon: `<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                   d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
           </svg>`
  },
  {
    value: 'org',
    label: 'Org Chart',
    icon: `<svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
                   d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
           </svg>`
  }
]
</script>
