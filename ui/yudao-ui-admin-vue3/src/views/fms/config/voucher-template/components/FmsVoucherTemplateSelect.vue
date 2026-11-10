<template>
  <Dialog v-model="dialogVisible" title="凭证模板库" width="680px">
    <el-form inline>
      <el-form-item label="模板分类">
        <el-select v-model="categoryId" clearable placeholder="全部分类" class="!w-200px">
          <el-option
            v-for="item in categories"
            :key="item.id"
            :label="item.name"
            :value="item.id!"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="loading"
      :data="filteredList"
      border
      stripe
      highlight-current-row
      @row-dblclick="selectTemplate"
    >
      <el-table-column label="分类" prop="categoryName" min-width="180" />
      <el-table-column label="模板名称" prop="name" min-width="260" />
      <el-table-column label="分录数" align="center" width="90">
        <template #default="scope">{{ scope.row.entries.length }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="130">
        <template #default="scope">
          <el-button link type="primary" @click="selectTemplate(scope.row)">套用</el-button>
          <el-button
            v-hasPermi="['fms:config:voucher-template:delete']"
            link
            type="danger"
            @click="deleteTemplate(scope.row)"
            v-if="fmsStore.isAccountSetWritable"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="mt-10px text-12px text-[var(--el-text-color-secondary)]">
      双击模板可直接套用到当前凭证
    </div>
  </Dialog>
</template>

<script lang="ts" setup>
import { FmsVoucherTemplateApi } from '@/api/fms/config/voucher-template'
import type { FmsVoucherTemplateVO } from '@/api/fms/config/voucher-template'
import {
  FmsVoucherTemplateCategoryApi,
  type FmsVoucherTemplateCategoryVO
} from '@/api/fms/config/voucher-template-category'
import { useFmsStore } from '@/views/fms/store/fms'

defineOptions({ name: 'FmsVoucherTemplateSelect' })

const emit = defineEmits<{ select: [template: FmsVoucherTemplateVO] }>()
const message = useMessage() // 消息弹窗
const fmsStore = useFmsStore() // FMS 状态

const dialogVisible = ref(false) // 弹窗的是否展示
const loading = ref(false) // 列表的加载中
const accountSetId = ref<number>() // 当前账套编号
const categoryId = ref<number>() // 模板分类编号
const categories = ref<FmsVoucherTemplateCategoryVO[]>([]) // 模板分类列表
const list = ref<FmsVoucherTemplateVO[]>([]) // 凭证模板列表
const filteredList = computed(() =>
  categoryId.value ? list.value.filter((item) => item.categoryId === categoryId.value) : list.value
)

/** 打开弹窗 */
async function open(id: number) {
  accountSetId.value = id
  categoryId.value = undefined
  dialogVisible.value = true
  await getList()
}

/** 查询凭证模板和分类 */
async function getList() {
  if (!accountSetId.value) return
  loading.value = true
  try {
    ;[categories.value, list.value] = await Promise.all([
      FmsVoucherTemplateCategoryApi.getVoucherTemplateCategorySimpleList(accountSetId.value),
      FmsVoucherTemplateApi.getVoucherTemplateSimpleList(accountSetId.value)
    ])
  } finally {
    loading.value = false
  }
}

/** 套用凭证模板 */
function selectTemplate(row: FmsVoucherTemplateVO) {
  emit('select', row)
  dialogVisible.value = false
}

/** 删除凭证模板 */
async function deleteTemplate(row: FmsVoucherTemplateVO) {
  if (!accountSetId.value) return
  try {
    await message.delConfirm(`确认删除凭证模板“${row.name}”吗？`)
    await FmsVoucherTemplateApi.deleteVoucherTemplate(accountSetId.value, row.id!)
    message.success('删除成功')
    await getList()
  } catch {}
}

defineExpose({ open })
</script>
