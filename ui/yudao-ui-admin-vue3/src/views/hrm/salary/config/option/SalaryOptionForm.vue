<template>
  <Dialog v-model="dialogVisible" :title="dialogTitle" width="560">
    <el-form
      ref="formRef"
      v-loading="formLoading"
      :model="formData"
      :rules="formRules"
      label-width="96px"
    >
      <!-- 工资项信息 -->
      <el-form-item label="工资项分类" prop="parentCode">
        <el-select
          v-model="formData.parentCode"
          disabled
          class="!w-1/1"
          placeholder="请选择工资项分类"
        >
          <el-option
            v-for="option in categoryList"
            :key="option.code"
            :label="option.name"
            :value="option.code"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="工资项名称" prop="name">
        <el-input v-model="formData.name" maxlength="64" placeholder="请输入工资项名称" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          maxlength="255"
          placeholder="请输入备注"
          type="textarea"
          :rows="3"
        />
      </el-form-item>
    </el-form>
    <!-- 表单按钮 -->
    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import type { FormInstance, FormRules } from 'element-plus'
import * as SalaryOptionApi from '@/api/hrm/salary/config/option'
import { HrmSalaryOptionCategoryCode } from '@/views/hrm/utils/constants'

defineOptions({ name: 'HrmSalaryOptionForm' })

const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗
const dialogVisible = ref(false) // 弹窗是否显示
const dialogTitle = ref('') // 弹窗标题
const formLoading = ref(false) // 表单加载中
const optionList = ref<SalaryOptionApi.SalaryOptionVO[]>([]) // 工资项列表
const categoryList = computed(() =>
  optionList.value.filter(
    (item) =>
      item.parentCode === HrmSalaryOptionCategoryCode.ROOT && !item.systemFlag && item.enabled
  )
) // 可新增薪资项的分类列表
const formData = ref<SalaryOptionApi.SalaryOptionSaveReqVO>(createDefaultFormData()) // 表单数据
const formRules = reactive<FormRules>({
  parentCode: [{ required: true, message: '工资项分类不能为空', trigger: 'change' }],
  name: [{ required: true, message: '工资项名称不能为空', trigger: 'blur' }]
}) // 表单校验
const formRef = ref<FormInstance>() // 表单引用

/** 创建默认表单数据 */
function createDefaultFormData(): SalaryOptionApi.SalaryOptionSaveReqVO {
  return {
    parentCode: undefined,
    name: '',
    remark: ''
  }
}

/** 打开表单 */
async function open(parentCode: number) {
  dialogVisible.value = true
  dialogTitle.value = t('action.create')
  resetForm()
  formData.value.parentCode = parentCode
  // 加载薪资项分类
  optionList.value = await SalaryOptionApi.getSalaryOptionList()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调

/** 提交表单 */
async function submitForm() {
  // 校验表单
  await formRef.value?.validate()
  // 提交请求
  formLoading.value = true
  try {
    await SalaryOptionApi.createSalaryOption(formData.value)
    message.success(t('common.createSuccess'))
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 重置表单 */
function resetForm() {
  formData.value = createDefaultFormData()
  formRef.value?.resetFields()
}
</script>
