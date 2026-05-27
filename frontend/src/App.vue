<template>
  <div class="min-h-screen bg-slate-50 flex flex-col">
    <Header />
    <main class="flex-1">
      <RouterView />
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import Header from '@/components/Header.vue'
import Footer from '@/components/Footer.vue'
import { useDiagramStore } from '@/stores/diagramStore'

const diagramStore = useDiagramStore()

onMounted(() => {
  diagramStore.fetchAllDiagrams()
})
</script>
