import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 80,
    host: true,
    allowedHosts: ['.ngrok-free.dev', '.serveo.net', 'localhost'],
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    },
    hmr: {
      host: '192.168.1.24'
    }
  },
  build: {
    outDir: 'dist'
  }
})
