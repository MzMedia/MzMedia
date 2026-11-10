<template>
  <el-select
    v-model="selectValue"
    :clearable="clearable"
    :disabled="disabled"
    :filterable="filterable"
    :loading="loading"
    :placeholder="placeholder"
    class="w-full"
    @change="handleChange"
  >
    <el-option
      v-for="channel in channelOptions"
      :key="channel.id"
      :label="channel.name"
      :value="channel.id"
    />
  </el-select>
</template>

<script lang="ts" setup>
import * as RecruitChannelApi from '@/api/hrm/recruit/channel'

defineOptions({ name: 'HrmRecruitChannelSelect' })

const props = withDefaults(
  defineProps<{
    modelValue?: number
    disabled?: boolean
    clearable?: boolean
    filterable?: boolean
    excludeIds?: number[]
    placeholder?: string
  }>(),
  {
    disabled: false,
    clearable: true,
    filterable: true,
    excludeIds: () => [],
    placeholder: '请选择招聘渠道'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: number | undefined]
  change: [channel: RecruitChannelApi.HrmRecruitChannelVO | undefined]
}>() // 定义 modelValue 更新和 change 事件

const channelList = ref<RecruitChannelApi.HrmRecruitChannelVO[]>([]) // 招聘渠道列表
const selectedChannel = ref<RecruitChannelApi.HrmRecruitChannelVO>() // 当前回显的停用招聘渠道
const loading = ref(false) // 招聘渠道列表的加载中
const channelOptions = computed(() => {
  const options = channelList.value.filter(
    (channel): channel is RecruitChannelApi.HrmRecruitChannelVO & { id: number } =>
      channel.id !== undefined && !props.excludeIds.includes(channel.id)
  )
  const currentChannel = selectedChannel.value
  if (
    currentChannel?.id === undefined ||
    props.excludeIds.includes(currentChannel.id) ||
    options.some((channel) => channel.id === currentChannel.id)
  ) {
    return options
  }
  return [currentChannel as RecruitChannelApi.HrmRecruitChannelVO & { id: number }, ...options]
})
const selectValue = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

/** 补充当前选中的招聘渠道，支持已停用渠道回显 */
async function ensureSelectedChannel() {
  const channelId = props.modelValue
  selectedChannel.value = undefined
  if (channelId == null || channelList.value.some((channel) => channel.id === channelId)) {
    return
  }
  const channel = await RecruitChannelApi.getRecruitChannel(channelId)
  if (props.modelValue === channelId && channel?.id === channelId) {
    selectedChannel.value = channel
  }
}

/** 选中变化 */
function handleChange(value: number | undefined) {
  emit(
    'change',
    channelOptions.value.find((channel) => channel.id === value)
  )
}

/** 获得招聘渠道列表 */
async function getChannelList() {
  loading.value = true
  try {
    channelList.value = await RecruitChannelApi.getRecruitChannelSimpleList()
    await ensureSelectedChannel()
  } finally {
    loading.value = false
  }
}

/** 监听选中招聘渠道变化 */
watch(
  () => props.modelValue,
  () => {
    ensureSelectedChannel()
  }
)

/** 初始化 */
onMounted(() => {
  getChannelList()
})
</script>
