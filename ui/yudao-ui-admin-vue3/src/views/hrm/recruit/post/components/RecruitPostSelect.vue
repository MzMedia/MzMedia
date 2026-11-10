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
      v-for="post in postOptions"
      :key="post.id"
      :label="formatPostLabel(post)"
      :value="post.id"
    />
  </el-select>
</template>

<script lang="ts" setup>
import * as RecruitPostApi from '@/api/hrm/recruit/post'

defineOptions({ name: 'HrmRecruitPostSelect' })

const props = withDefaults(
  defineProps<{
    modelValue?: number
    disabled?: boolean
    clearable?: boolean
    filterable?: boolean
    placeholder?: string
  }>(),
  {
    disabled: false,
    clearable: true,
    filterable: true,
    placeholder: '请选择招聘职位'
  }
)

const emit = defineEmits<{
  'update:modelValue': [value: number | undefined]
  change: [post: RecruitPostApi.HrmRecruitPostVO | undefined]
}>() // 定义 modelValue 更新和 change 事件

const postList = ref<RecruitPostApi.HrmRecruitPostVO[]>([]) // 招聘职位列表
const selectedPost = ref<RecruitPostApi.HrmRecruitPostVO>() // 当前回显的停止招聘职位
const loading = ref(false) // 招聘职位列表的加载中
const postOptions = computed(() => {
  const options = postList.value.filter(
    (post): post is RecruitPostApi.HrmRecruitPostVO & { id: number } => post.id !== undefined
  )
  const currentPost = selectedPost.value
  if (currentPost?.id === undefined || options.some((post) => post.id === currentPost.id)) {
    return options
  }
  return [currentPost as RecruitPostApi.HrmRecruitPostVO & { id: number }, ...options]
})
const selectValue = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

/** 格式化招聘职位选项 */
function formatPostLabel(post: RecruitPostApi.HrmRecruitPostVO) {
  return post.deptName ? `${post.postName}（${post.deptName}）` : post.postName
}

/** 补充当前选中的招聘职位，支持已停止招聘的职位回显 */
async function ensureSelectedPost() {
  const postId = props.modelValue
  selectedPost.value = undefined
  if (postId === undefined || postList.value.some((post) => post.id === postId)) {
    return
  }
  const post = await RecruitPostApi.getRecruitPost(postId)
  if (props.modelValue === postId && post?.id === postId) {
    selectedPost.value = post
  }
}

/** 选中变化 */
function handleChange(value: number | undefined) {
  emit(
    'change',
    postOptions.value.find((post) => post.id === value)
  )
}

/** 获得招聘职位列表 */
async function getPostList() {
  loading.value = true
  try {
    postList.value = await RecruitPostApi.getRecruitPostSimpleList()
    await ensureSelectedPost()
  } finally {
    loading.value = false
  }
}

/** 监听选中招聘职位变化 */
watch(
  () => props.modelValue,
  () => {
    ensureSelectedPost()
  }
)

/** 初始化 */
onMounted(() => {
  getPostList()
})
</script>
