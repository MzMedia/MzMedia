<!-- FMS 账套开通引导：提示用户创建账套或完成账套初始化 -->
<template>
  <Dialog v-model="dialogVisible" title="账套开通引导" width="560px" :close-on-click-modal="false">
    <el-result icon="warning" :title="title" :sub-title="description">
      <template #extra>
        <el-button v-if="canHandle" type="primary" @click="goAccountSet"> 前往账套管理 </el-button>
        <el-button @click="close">{{ canHandle ? '稍后处理' : '我知道了' }}</el-button>
      </template>
    </el-result>
  </Dialog>
</template>

<script lang="ts" setup>
import { checkPermi } from '@/utils/permission'

defineOptions({ name: 'FmsAccountSetGuide' })

type GuideReason = 'empty' | 'uninitialized'

const FMS_ACCOUNT_SET_PATH = '/fms/config/account-set'

const router = useRouter()
const dialogVisible = ref(false) // 弹窗是否可见
const reason = ref<GuideReason>('empty') // 引导原因

/** 当前用户是否可以自行处理账套问题 */
const canHandle = computed(() =>
  reason.value === 'empty'
    ? checkPermi(['fms:config:account-set:create'])
    : checkPermi(['fms:config:account-set:initialize'])
)
/** 引导标题 */
const title = computed(() => (reason.value === 'empty' ? '当前账号暂无账套' : '当前账套尚未初始化'))
/** 引导说明 */
const description = computed(() => {
  if (reason.value === 'empty') {
    return canHandle.value
      ? '请先创建账套并完成初始化，再进入财务管理。'
      : '请联系管理员创建账套，或将当前账号加入已有账套。'
  }
  return canHandle.value
    ? '请前往账套管理，选择账套并点击【开始记账】完成初始化。'
    : '请联系管理员完成账套初始化后再进入财务管理。'
})

/** 打开账套开通引导 */
function open(guideReason: GuideReason) {
  reason.value = guideReason
  dialogVisible.value = true
}

/** 关闭账套开通引导 */
function close() {
  dialogVisible.value = false
}

/** 前往账套管理处理 */
function goAccountSet() {
  close()
  router.push(FMS_ACCOUNT_SET_PATH)
}

defineExpose({ open, close })
</script>
