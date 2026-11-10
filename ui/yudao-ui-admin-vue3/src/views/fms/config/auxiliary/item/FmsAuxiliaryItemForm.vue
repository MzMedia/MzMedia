<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="520px">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="88px"
    >
      <el-form-item label="编码" prop="code">
        <el-input v-model="formData.code" maxlength="64" placeholder="请输入编码" />
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="formData.name" maxlength="255" placeholder="请输入名称" />
      </el-form-item>
      <template v-if="isInventory">
        <el-form-item label="规格" prop="specification">
          <el-input v-model="formData.specification" maxlength="255" placeholder="请输入规格" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="formData.unit" maxlength="255" placeholder="请输入单位" />
        </el-form-item>
      </template>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          maxlength="500"
          placeholder="请输入备注"
          show-word-limit
          type="textarea"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { FmsAuxiliaryItemApi } from '@/api/fms/config/auxiliary/item'
import type { FmsAuxiliaryItemVO } from '@/api/fms/config/auxiliary/item'
import type { FmsAuxiliaryTypeVO } from '@/api/fms/config/auxiliary/type'
import { useFmsStore } from '@/views/fms/store/fms'
import { FMS_AUXILIARY_TYPE } from '@/views/fms/utils/constants'
import type { FormInstance, FormRules } from 'element-plus'

defineOptions({ name: 'FmsAuxiliaryItemForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const fmsStore = useFmsStore() // FMS 状态

const dialogVisible = ref(false) // 弹窗的是否展示
const dialogTitle = ref('') // 弹窗的标题
const formLoading = ref(false) // 表单的加载中
const currentAuxiliaryType = ref<FmsAuxiliaryTypeVO>() // 当前辅助核算类别
const formData = ref<FmsAuxiliaryItemVO>({
  id: undefined,
  accountSetId: 0,
  auxiliaryTypeId: 0,
  code: '',
  name: ''
})
const formRules = reactive<FormRules>({
  code: [{ required: true, message: '编码不能为空', trigger: 'blur' }],
  name: [{ required: true, message: '名称不能为空', trigger: 'blur' }]
})
const formRef = ref<FormInstance>() // 表单 Ref

const isInventory = computed(
  () => currentAuxiliaryType.value?.type === FMS_AUXILIARY_TYPE.INVENTORY
)

/** 打开弹窗 */
function open(auxiliaryType: FmsAuxiliaryTypeVO, row?: FmsAuxiliaryItemVO) {
  const accountSetId = fmsStore.getAccountSetId
  if (!accountSetId) return
  dialogVisible.value = true
  currentAuxiliaryType.value = auxiliaryType
  dialogTitle.value = row ? '编辑辅助核算' : `新增${auxiliaryType.name}`
  resetForm(accountSetId, auxiliaryType.id!)
  // 修改时，设置数据
  if (row) {
    formData.value = { ...row }
  }
  nextTick(() => formRef.value?.clearValidate())
}
defineExpose({ open })

const emit = defineEmits<{ success: [] }>() // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  if (!formRef.value) return
  const valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  formLoading.value = true
  try {
    if (!isInventory.value) {
      formData.value.specification = undefined
      formData.value.unit = undefined
    }
    if (!formData.value.id) {
      await FmsAuxiliaryItemApi.createAuxiliaryItem(formData.value)
      message.success(t('common.createSuccess'))
    } else {
      await FmsAuxiliaryItemApi.updateAuxiliaryItem(formData.value)
      message.success(t('common.updateSuccess'))
    }
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm(accountSetId: number, auxiliaryTypeId: number) {
  formData.value = {
    id: undefined,
    accountSetId,
    auxiliaryTypeId,
    code: '',
    name: ''
  }
  formRef.value?.resetFields()
}
</script>
