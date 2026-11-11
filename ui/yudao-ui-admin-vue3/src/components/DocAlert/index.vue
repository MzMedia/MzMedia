<template>
  <!-- 文档提示已全局禁用 -->
</template>
<script setup lang="tsx">
import { propTypes } from '@/utils/propTypes'

defineOptions({ name: 'DocAlert' })

defineProps({
  title: propTypes.string,
  url: propTypes.string
})
</script>
