<template>
  <Dialog v-model="dialogVisible" title="凭证模板分类" width="560px">
    <el-form
      ref="categoryFormRef"
      :model="categoryFormData"
      :rules="categoryFormRules"
      class="mb-16px flex w-full gap-8px [&_.el-form-item]:!mb-0 [&_.el-form-item]:flex-1"
    >
      <el-form-item prop="name">
        <el-input v-model="categoryFormData.name" maxlength="255" placeholder="请输入分类名称" />
      </el-form-item>
      <div class="flex">
        <el-button
          v-if="categoryFormData.id && fmsStore.isAccountSetWritable"
          v-hasPermi="['fms:config:voucher-template-category:update']"
          type="primary"
          :loading="submitting"
          @click="saveCategory"
        >
          保存
        </el-button>
        <el-button
          v-else-if="fmsStore.isAccountSetWritable"
          v-hasPermi="['fms:config:voucher-template-category:create']"
          type="primary"
          :loading="submitting"
          @click="saveCategory"
        >
          新增
        </el-button>
        <el-button v-if="categoryFormData.id" @click="resetCategoryForm">取消</el-button>
      </div>
    </el-form>
    <el-table :data="categories" border stripe @row-dblclick="selectCategory">
      <el-table-column label="分类名称" prop="name" min-width="260" />
      <el-table-column label="操作" align="center" width="150">
        <template #default="scope">
          <el-button
            v-if="fmsStore.isAccountSetWritable"
            v-hasPermi="['fms:config:voucher-template-category:update']"
            link
            type="primary"
            @click="editCategory(scope.row)"
          >
            编辑
          </el-button>
          <el-button
            v-if="fmsStore.isAccountSetWritable"
            v-hasPermi="['fms:config:voucher-template-category:delete']"
            link
            type="danger"
            @click="deleteCategory(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <div class="mt-10px text-12px text-[var(--el-text-color-secondary)]">双击分类可直接选中</div>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import {
  FmsVoucherTemplateCategoryApi,
  type FmsVoucherTemplateCategoryVO
} from '@/api/fms/config/voucher-template-category'
import { useFmsStore } from '@/views/fms/store/fms'

defineOptions({ name: 'FmsVoucherTemplateCategoryManage' })

const props = defineProps<{
  accountSetId?: number
}>()
const emit = defineEmits<{
  change: [categories: FmsVoucherTemplateCategoryVO[]]
  select: [categoryId: number]
}>()

const message = useMessage() // 消息弹窗
const fmsStore = useFmsStore() // FMS 状态
const dialogVisible = ref(false) // 弹窗的是否展示
const submitting = ref(false) // 表单提交的加载中
const categories = ref<FmsVoucherTemplateCategoryVO[]>([]) // 模板分类列表
const categoryFormRef = ref<FormInstance>() // 模板分类表单 Ref
const categoryFormData = reactive({
  id: undefined as number | undefined,
  name: ''
})
const categoryFormRules = reactive<FormRules>({
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
})

/** 打开分类管理弹窗 */
async function open() {
  resetCategoryForm()
  dialogVisible.value = true
  await getCategoryList()
}

/** 查询模板分类列表 */
async function getCategoryList() {
  if (!props.accountSetId) {
    categories.value = []
    emit('change', [])
    return
  }
  categories.value = await FmsVoucherTemplateCategoryApi.getVoucherTemplateCategoryList(
    props.accountSetId
  )
  emit('change', categories.value)
}

/** 编辑模板分类 */
function editCategory(row: FmsVoucherTemplateCategoryVO) {
  categoryFormData.id = row.id
  categoryFormData.name = row.name
  nextTick(() => categoryFormRef.value?.clearValidate())
}

/** 重置模板分类表单 */
function resetCategoryForm() {
  categoryFormData.id = undefined
  categoryFormData.name = ''
  categoryFormRef.value?.resetFields()
}

/** 保存模板分类 */
async function saveCategory() {
  if (!props.accountSetId || !categoryFormRef.value) return
  const valid = await categoryFormRef.value.validate()
  if (!valid) return

  submitting.value = true
  try {
    if (categoryFormData.id) {
      await FmsVoucherTemplateCategoryApi.updateVoucherTemplateCategory({
        id: categoryFormData.id,
        accountSetId: props.accountSetId,
        name: categoryFormData.name
      })
      message.success('修改成功')
    } else {
      const categoryId = await FmsVoucherTemplateCategoryApi.createVoucherTemplateCategory({
        accountSetId: props.accountSetId,
        name: categoryFormData.name
      })
      message.success('新增成功')
      await getCategoryList()
      emit('select', categoryId)
      resetCategoryForm()
      return
    }
    resetCategoryForm()
    await getCategoryList()
  } finally {
    submitting.value = false
  }
}

/** 删除模板分类 */
async function deleteCategory(row: FmsVoucherTemplateCategoryVO) {
  if (!props.accountSetId) return
  try {
    await message.delConfirm('确认删除该模板分类吗？')
    await FmsVoucherTemplateCategoryApi.deleteVoucherTemplateCategory(props.accountSetId, row.id!)
    message.success('删除成功')
    await getCategoryList()
  } catch {}
}

/** 选择模板分类 */
function selectCategory(row: FmsVoucherTemplateCategoryVO) {
  if (!row.id) return
  emit('select', row.id)
  dialogVisible.value = false
}

defineExpose({ open })
</script>
