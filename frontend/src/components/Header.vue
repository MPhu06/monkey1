<template>
  <header class="bg-white border-b border-slate-200 sticky top-0 z-50 backdrop-blur-sm bg-white/95">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
      <div class="flex items-center justify-between h-16">
        <RouterLink to="/" class="flex items-center gap-3 group">
          <div class="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-700 rounded-xl
                      flex items-center justify-center shadow-sm group-hover:shadow-md transition-shadow">
            <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zm0 0h12a2 2 0 002-2v-4a2 2 0 00-2-2h-2.343M11 7.343l1.657-1.657a2 2 0 012.828 0l2.829 2.829a2 2 0 010 2.828l-8.486 8.485M7 17h.01" />
            </svg>
          </div>
          <div>
            <h1 class="text-lg font-bold text-slate-900 leading-tight">Slide to Diagram</h1>
            <p class="text-xs text-slate-500">AI Diagram Generator</p>
          </div>
        </RouterLink>

        <nav class="flex items-center gap-1">
          <RouterLink to="/" class="nav-link" :class="{ active: route.path === '/' }">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6" />
            </svg>
            Trang chủ
          </RouterLink>
          <RouterLink to="/history" class="nav-link" :class="{ active: route.path === '/history' }">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                    d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            Lịch sử
          </RouterLink>
        </nav>
      </div>
    </div>
  </header>
</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()
</script>

<style scoped>
.nav-link {
  @apply flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium
         text-slate-600 hover:text-slate-900 hover:bg-slate-100
         transition-all duration-200;
}

.nav-link.active {
  @apply text-primary-700 bg-primary-50 hover:bg-primary-100;
}
</style>
