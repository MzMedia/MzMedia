<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      :title="getTitle"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 表单区域 -->
    <view>
      <wd-form ref="formRef" :model="formData" :schema="formSchema">
        <wd-cell-group border>
          <wd-form-item title="名字" title-width="200rpx" prop="name">
            <wd-input v-model="formData.name" clearable placeholder="请输入名字" />
          </wd-form-item>
          <yd-form-picker
            v-model="formData.parentId"
            label="父级编号" prop="parentId"
            :columns="parentOptions"
            label-key="name"
            value-key="id"
          />
        </wd-cell-group>
      </wd-form>
    </view>

    <!-- 底部保存按钮 -->
    <view class="yd-detail-footer">
      <wd-button
        type="primary"
        block
        :loading="formLoading"
        @click="handleSubmit"
      >
        保存
      </wd-button>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { FormInstance } from '@wot-ui/ui/components/wd-form/types'
import type { Demo02Category } from '@/api/infra/demo/demo02'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import { createDemo02Category, getDemo02Category, getDemo02CategoryList, updateDemo02Category } from '@/api/infra/demo/demo02'
import { delay, navigateBackPlus } from '@/utils'
import { handleTree } from '@/utils/tree'
import { createFormSchema } from '@/utils/wot'

const props = defineProps<{
  id?: number | any
}>()

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const toast = useToast()
const getTitle = computed(() => props.id ? '编辑示例分类' : '新增示例分类')
const formLoading = ref(false) // 表单提交状态
const categoryList = ref<Demo02Category[]>([]) // 全部分类（构造父级选项）
const formData = ref<Demo02Category>({
  id: undefined,
  name: '',
  parentId: 0,
}) // 表单数据
const formSchema = createFormSchema({
  name: [{ required: true, message: '名字不能为空' }],
  parentId: [{ required: true, message: '父级编号不能为空' }],
})
const formRef = ref<FormInstance>() // 表单组件引用

/** 父级选项：顶级 + 树形缩进（编辑时排除自身） */
const parentOptions = computed(() => {
  const options: Array<{ id: number, name: string }> = [{ id: 0, name: '顶级分类' }]
  const tree = handleTree(categoryList.value.filter(item => item.id !== props.id))
  const walk = (nodes: Demo02Category[], depth: number) => {
    for (const node of nodes) {
      options.push({ id: node.id!, name: `${'　'.repeat(depth)}${node.name}` })
      if (node.children?.length) {
        walk(node.children, depth + 1)
      }
    }
  }
  walk(tree, 0)
  return options
})

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/demo/demo02/index')
}

/** 加载详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  formData.value = await getDemo02Category(props.id)
}

/** 提交表单 */
async function handleSubmit() {
  const { valid } = await formRef.value.validate()
  if (!valid) {
    return
  }
  formLoading.value = true
  try {
    if (props.id) {
      await updateDemo02Category(formData.value)
      toast.success('修改成功')
    } else {
      await createDemo02Category(formData.value)
      toast.success('新增成功')
    }
    uni.$emit('infra:demo02-category:reload')
    delay(handleBack)
  } finally {
    formLoading.value = false
  }
}

/** 初始化 */
onMounted(async () => {
  categoryList.value = await getDemo02CategoryList()
  await getDetail()
})
</script>
