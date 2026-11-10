<template>
  <EmployeePositionChangeForm
    ref="formRef"
    title="办理晋升"
    :change-type="HrmEmployeeChangeType.PROMOTION"
    :submit-request="EmployeeApi.promoteEmployee"
    @success="emit('success')"
  />
</template>

<script lang="ts" setup>
import type { HrmEmployeeVO } from '@/api/hrm/employee'
import * as EmployeeApi from '@/api/hrm/employee'
import { HrmEmployeeChangeType } from '@/views/hrm/utils/constants'
import EmployeePositionChangeForm from './EmployeePositionChangeForm.vue'

defineOptions({ name: 'HrmEmployeePromoteForm' })

const formRef = ref<InstanceType<typeof EmployeePositionChangeForm>>() // 表单 Ref

/** 打开弹窗 */
function open(employee: HrmEmployeeVO) {
  formRef.value?.open(employee)
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
</script>
