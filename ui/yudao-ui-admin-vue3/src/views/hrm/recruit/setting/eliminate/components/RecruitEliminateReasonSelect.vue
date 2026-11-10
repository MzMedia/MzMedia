<template>
  <el-select
    v-model="selectValue"
    :allow-create="allowCreate"
    :clearable="clearable"
    :disabled="disabled"
    :filterable="filterable"
    :loading="loading"
    :placeholder="placeholder"
    class="w-full"
    default-first-option
    @change="handleChange"
  >
    <el-option v-for="reason in reasonList" :key="reason" :label="reason" :value="reason" />
  </el-select>
</template>

<script lang="ts" setup>
import * as RecruitConfigApi from '@/api/hrm/recruit/config'

defineOptions({ name: 'HrmRecruitEliminateReasonSelect' })

const props = withDefaults(
  defineProps<{
    modelValue?: string
    disabled?: boolean
    clearable?: boolean
    filterable?: boolean
    allowCreate?: boolean
    placeholder?: string
  }>(),
  {
    disabled: false,
    clearable: true,
    filterable: true,
    allowCreate: true,
    placeholder: '请选择或输入淘汰原因'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: string | undefined]
  change: [value: string | undefined]
}>() // 定义 modelValue 更新和 change 事件

const loading = ref(false) // 淘汰原因列表的加载中
const reasonList = ref<string[]>([]) // 淘汰原因列表
const selectValue = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

/** 选中原因变化 */
function handleChange(value: string | undefined) {
  emit('change', value)
}

/** 获得淘汰原因列表 */
async function getReasonList() {
  loading.value = true
  try {
    reasonList.value = await RecruitConfigApi.getRecruitEliminateReasonList()
  } finally {
    loading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getReasonList()
})
</script>
