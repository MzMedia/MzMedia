<!-- 商品与 SKU 选择器：促销活动复用，负责加载商品 SKU 并把配置行回写给父组件 -->
<template>
  <view>
    <!-- 已选商品 -->
    <view v-if="spu" class="mb-16rpx flex items-center gap-16rpx rounded-8rpx bg-[#f7f8fa] p-16rpx">
      <view v-if="spu.picUrl" class="shrink-0">
        <wd-img :src="spu.picUrl" width="96rpx" height="96rpx" radius="8rpx" mode="aspectFill" />
      </view>
      <view class="min-w-0 flex-1">
        <view class="truncate text-28rpx text-[#333] font-medium">
          {{ spu.name || `商品 #${spu.id}` }}
        </view>
        <view class="mt-4rpx text-24rpx text-[#999]">
          商品编号：{{ spu.id }}
        </view>
      </view>
    </view>

    <!-- 选择商品按钮 -->
    <wd-button v-if="!disabled" size="small" variant="plain" @click="openPicker">
      {{ spu ? '重新选择商品' : '选择商品' }}
    </wd-button>

    <!-- SKU 配置列表 -->
    <view v-if="skuRows.length" class="mt-16rpx">
      <view
        v-for="(row, index) in skuRows"
        :key="row.skuId"
        class="mb-16rpx rounded-8rpx bg-[#f7f8fa] p-16rpx"
      >
        <view class="mb-12rpx flex items-center gap-12rpx">
          <view v-if="row.picUrl" class="shrink-0">
            <wd-img :src="row.picUrl" width="72rpx" height="72rpx" radius="8rpx" mode="aspectFill" />
          </view>
          <view class="min-w-0 flex-1">
            <view class="truncate text-26rpx text-[#333]">
              {{ row.skuName || `SKU #${row.skuId}` }}
            </view>
            <view class="mt-4rpx text-24rpx text-[#999]">
              原价 {{ formatDisplayMoney(row.marketPrice ?? row.price) }} / 库存 {{ row.stock ?? '-' }}
            </view>
          </view>
        </view>
        <!-- 每个 SKU 的活动配置输入，由父级通过插槽逐字段渲染 -->
        <slot name="sku" :row="row" :index="index" />
      </view>
    </view>

    <!-- 商品选择弹窗 -->
    <wd-popup
      v-model="pickerVisible"
      position="bottom"
      closable
      custom-style="border-radius: 24rpx 24rpx 0 0; height: 70vh;"
      @close="pickerVisible = false"
    >
      <view class="h-70vh flex flex-col p-24rpx">
        <view class="mb-16rpx text-32rpx text-[#333] font-semibold">
          选择商品
        </view>
        <wd-search v-model="keyword" placeholder="搜索商品名称" hide-cancel @search="() => {}" />
        <scroll-view class="mt-16rpx min-h-0 flex-1" scroll-y>
          <view
            v-for="item in filteredSpuList"
            :key="item.id"
            class="mb-12rpx flex items-center gap-16rpx rounded-8rpx bg-[#f7f8fa] p-16rpx"
            @click="handleSelectSpu(item)"
          >
            <view v-if="item.picUrl" class="shrink-0">
              <wd-img :src="item.picUrl" width="80rpx" height="80rpx" radius="8rpx" mode="aspectFill" />
            </view>
            <view class="min-w-0 flex-1">
              <view class="truncate text-28rpx text-[#333]">
                {{ item.name || `商品 #${item.id}` }}
              </view>
              <view class="mt-4rpx text-24rpx text-[#999]">
                {{ formatDisplayMoney(item.price) }}
              </view>
            </view>
          </view>
          <view v-if="!filteredSpuList.length" class="py-48rpx text-center text-26rpx text-[#999]">
            暂无商品
          </view>
        </scroll-view>
      </view>
    </wd-popup>
  </view>
</template>

<script lang="ts" setup>
import type { ProductSku, ProductSpu } from '@/api/mall/product/spu'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, ref } from 'vue'
import { getProductSpu, getSimpleProductSpuList } from '@/api/mall/product/spu'
import { fenToYuan, formatDisplayMoney } from '@/utils/format'

/** SKU 行：合并 SKU 基础信息与活动配置，money 字段已转为元 */
export interface SpuSkuRow extends Record<string, any> {
  skuId: number
  skuName?: string
  picUrl?: string
  price?: number
  marketPrice?: number
  stock?: number
}

const props = defineProps<{
  modelValue: SpuSkuRow[] // 当前 SKU 配置行
  spuId?: number // 当前选中的 SPU 编号
  // 单选模式：只取第一个 SKU（砍价活动）
  single?: boolean
  disabled?: boolean
  // 创建每个 SKU 的活动配置默认值（元）；row 提供 sku 基础信息
  createConfig: (sku: ProductSku) => Record<string, any>
  // 加载详情时，把后端 product（分）合并进配置（自行 fenToYuan 需要的字段）
  mergeConfig?: (config: Record<string, any>, product: Record<string, any>) => Record<string, any>
}>()

const emit = defineEmits<{
  'update:modelValue': [rows: SpuSkuRow[]]
  'update:spuId': [spuId: number | undefined]
}>()

const toast = useToast()
const pickerVisible = ref(false) // 商品选择弹窗
const keyword = ref('') // 商品搜索关键字
const spuList = ref<ProductSpu[]>([]) // 商品精简列表
const spu = ref<ProductSpu>() // 当前选中的 SPU 详情
const skuRows = computed(() => props.modelValue || [])

const filteredSpuList = computed(() => {
  const word = keyword.value.trim()
  if (!word) {
    return spuList.value
  }
  return spuList.value.filter(item => (item.name || '').includes(word))
})

/** 打开商品选择弹窗 */
async function openPicker() {
  if (!spuList.value.length) {
    spuList.value = await getSimpleProductSpuList()
  }
  pickerVisible.value = true
}

/** 选中商品 */
async function handleSelectSpu(item: ProductSpu) {
  pickerVisible.value = false
  await loadSpuDetail(item.id!)
}

/** 加载 SPU 详情并构造 SKU 行 */
async function loadSpuDetail(spuId: number, products?: Record<string, any>[]) {
  toast.loading('加载商品...')
  try {
    const detail = await getProductSpu(spuId)
    spu.value = detail
    let skus = detail.skus || []
    if (props.single && skus.length) {
      // 砍价等单选场景：只保留与传入 product 匹配的 SKU（编辑回显），否则取第一个
      if (products?.length) {
        const matched = skus.filter(sku => products.some(p => p.skuId === sku.id))
        skus = matched.length ? matched : [skus[0]]
      } else {
        skus = [skus[0]]
      }
    }
    const rows: SpuSkuRow[] = skus.map((sku) => {
      let config = props.createConfig(sku)
      const product = products?.find(p => p.skuId === sku.id)
      if (product && props.mergeConfig) {
        config = props.mergeConfig(config, product)
      }
      return {
        ...config,
        skuId: sku.id!,
        spuId: detail.id!,
        skuName: sku.name || (sku.properties || []).map(p => p.valueName).filter(Boolean).join(' ') || `SKU #${sku.id}`,
        picUrl: sku.picUrl || detail.picUrl,
        price: Number(sku.price) || 0,
        marketPrice: Number(sku.marketPrice) || 0,
        stock: sku.stock,
      }
    })
    emit('update:modelValue', rows)
    emit('update:spuId', detail.id)
  } finally {
    toast.close()
  }
}

/** 编辑回显：根据 spuId + 后端 products 重建 SKU 行 */
async function setFromDetail(spuId: number, products?: Record<string, any>[]) {
  await loadSpuDetail(spuId, products)
}

defineExpose({ setFromDetail, fenToYuan })
</script>
