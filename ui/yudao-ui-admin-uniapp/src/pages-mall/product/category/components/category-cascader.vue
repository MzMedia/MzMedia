<template>
  <!-- 商品分类多级级联选择（搜索筛选样式，对标 yd-search-picker） -->
  <view class="yd-search-form-item">
    <view class="yd-search-form-label">
      {{ label }}
    </view>
    <view class="flex items-center justify-between rounded-12rpx bg-[#f7f8fa] p-24rpx" @click="visible = true">
      <text class="text-28rpx" :class="displayText ? 'text-[#333]' : 'text-[#999]'">
        {{ displayText || placeholder }}
      </text>
      <wd-icon name="arrow-right" size="32rpx" color="#666" />
    </view>
    <wd-cascader
      v-model="innerValue"
      v-model:visible="visible"
      :options="options"
      :title="label"
      @confirm="handleConfirm"
    />
  </view>
</template>

<script lang="ts" setup>
import type { CascaderOption } from '@wot-ui/ui/components/wd-cascader/types'
import { computed, onMounted, ref, watch } from 'vue'
import { getProductCategoryList } from '@/api/mall/product/category'

const props = withDefaults(defineProps<{
  modelValue?: number // 选中的分类编号
  label?: string // 字段标题
  placeholder?: string // 未选择占位
}>(), {
  label: '商品分类',
  placeholder: '请选择商品分类',
})

const emit = defineEmits<{
  'update:modelValue': [value: number | undefined]
}>()

const visible = ref(false) // 级联弹层显示状态
const options = ref<CascaderOption[]>([]) // 分类树
const pathTextMap = ref<Record<number, string>>({}) // 分类编号 → 路径文案（如「数码 / 耳机」）
const innerValue = ref<number | undefined>(props.modelValue) // 级联内部选中值

/** 已选分类的路径文案 */
const displayText = computed(() => props.modelValue != null ? (pathTextMap.value[props.modelValue] || '') : '')

/** 加载分类并构建树 + 路径文案 */
async function loadCategories() {
  const list = (await getProductCategoryList()).filter(item => item.id != null)
  const map = new Map<number, CascaderOption>()
  list.forEach(item => map.set(item.id as number, { value: item.id as number, text: item.name, children: [] }))
  const roots: CascaderOption[] = []
  list.forEach((item) => {
    const node = map.get(item.id as number)!
    const parent = item.parentId ? map.get(item.parentId) : undefined
    if (parent) {
      parent.children!.push(node)
    } else {
      roots.push(node)
    }
  })
  // 叶子节点移除空 children（级联据此判定可选），并记录每个节点的路径文案
  const textMap: Record<number, string> = {}
  const walk = (nodes: CascaderOption[], prefix: string) => {
    nodes.forEach((node) => {
      const text = prefix ? `${prefix} / ${node.text}` : (node.text as string)
      textMap[node.value as number] = text
      if (node.children && node.children.length) {
        walk(node.children, text)
      } else {
        delete node.children
      }
    })
  }
  walk(roots, '')
  pathTextMap.value = textMap
  options.value = roots
}

/** 选择确认：取路径末级（叶子）作为分类编号 */
function handleConfirm({ selectedOptions }: { value: any, selectedOptions?: CascaderOption[] }) {
  const leaf = selectedOptions?.[selectedOptions.length - 1]
  emit('update:modelValue', (leaf?.value as number) ?? undefined)
}

/** 同步外部值（如路由深链） */
watch(() => props.modelValue, (val) => {
  innerValue.value = val
})

onMounted(loadCategories)
</script>
