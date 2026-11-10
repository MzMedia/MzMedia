<!-- 商品范围选择器：优惠券模板「指定商品/指定分类」复用，多选商品 SPU 或商品分类编号回写父组件 -->
<template>
  <view>
    <!-- 已选项 + 打开按钮 -->
    <view class="flex flex-wrap items-center gap-12rpx">
      <wd-tag
        v-for="item in selectedOptions"
        :key="item.id"
        plain
        closable
        @close="handleRemove(item.id)"
      >
        {{ item.name }}
      </wd-tag>
      <wd-button size="small" variant="plain" @click="handleOpen">
        选择{{ scopeLabel }}
      </wd-button>
    </view>

    <!-- 多选弹窗 -->
    <wd-popup
      v-model="visible"
      position="bottom"
      closable
      custom-style="border-radius: 24rpx 24rpx 0 0; height: 70vh;"
      @close="visible = false"
    >
      <view class="h-70vh flex flex-col p-24rpx">
        <view class="mb-16rpx text-32rpx text-[#333] font-semibold">
          选择{{ scopeLabel }}
        </view>
        <wd-search v-model="keyword" :placeholder="`搜索${scopeLabel}名称`" hide-cancel @search="() => {}" />
        <scroll-view class="mt-16rpx min-h-0 flex-1" scroll-y>
          <wd-checkbox-group v-model="tempSelected">
            <wd-checkbox
              v-for="item in filteredOptions"
              :key="item.id"
              :name="item.id"
              class="border-b border-[#f5f5f5] py-16rpx"
            >
              {{ item.name }}
            </wd-checkbox>
          </wd-checkbox-group>
          <view v-if="!filteredOptions.length" class="py-48rpx text-center text-26rpx text-[#999]">
            暂无{{ scopeLabel }}
          </view>
        </scroll-view>
        <view class="mt-16rpx flex gap-20rpx">
          <wd-button class="flex-1" variant="plain" @click="visible = false">
            取消
          </wd-button>
          <wd-button class="flex-1" type="primary" @click="handleConfirm">
            确定（{{ tempSelected.length }}）
          </wd-button>
        </view>
      </view>
    </wd-popup>
  </view>
</template>

<script lang="ts" setup>
import { computed, onMounted, ref, watch } from 'vue'
import { getProductCategoryList } from '@/api/mall/product/category'
import { getSimpleProductSpuList } from '@/api/mall/product/spu'
import { PromotionProductScopeEnum } from '@/utils/constants'

const props = defineProps<{
  modelValue?: number[] // 已选编号
  scope: number // 商品范围：PromotionProductScopeEnum.SPU 指定商品、CATEGORY 指定分类
}>()

const emit = defineEmits<{
  'update:modelValue': [values: number[]]
}>()

const visible = ref(false) // 弹窗显示状态
const keyword = ref('') // 搜索关键字
const options = ref<{ id?: number, name?: string }[]>([]) // 全部可选项（商品或分类）
const tempSelected = ref<number[]>([]) // 弹窗内临时选中

const scopeLabel = computed(() => props.scope === PromotionProductScopeEnum.CATEGORY ? '分类' : '商品') // 范围文案
const selectedOptions = computed(() => (props.modelValue || []).map(id => ({
  id,
  name: options.value.find(item => item.id === id)?.name || String(id),
}))) // 已选项（用于标签展示）
const filteredOptions = computed(() => { // 按关键字过滤
  const word = keyword.value.trim()
  if (!word) {
    return options.value
  }
  return options.value.filter(item => (item.name || '').includes(word))
})

/** 加载范围选项（商品或分类） */
async function loadOptions() {
  options.value = props.scope === PromotionProductScopeEnum.CATEGORY
    ? await getProductCategoryList()
    : await getSimpleProductSpuList()
}

/** 打开弹窗 */
async function handleOpen() {
  if (!options.value.length) {
    await loadOptions()
  }
  tempSelected.value = [...(props.modelValue || [])]
  visible.value = true
}

/** 确认选择 */
function handleConfirm() {
  emit('update:modelValue', [...tempSelected.value])
  visible.value = false
}

/** 移除单个已选项 */
function handleRemove(id?: number) {
  emit('update:modelValue', (props.modelValue || []).filter(item => item !== id))
}

/** 进入即加载选项：存在回显值时，已选标签需显示具体名称而非编号 */
onMounted(() => {
  if (props.modelValue?.length) {
    loadOptions()
  }
})

/** 范围切换时重置选项缓存与已选项（商品↔分类编号语义不同，避免旧 ID 当作新范围提交） */
watch(() => props.scope, () => {
  options.value = []
  keyword.value = ''
  emit('update:modelValue', [])
})
</script>
