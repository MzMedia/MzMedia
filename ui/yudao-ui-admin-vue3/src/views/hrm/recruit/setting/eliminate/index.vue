<template>
  <doc-alert title="【招聘】招聘管理" url="https://doc.iocoder.cn/hrm/recruit/" />

  <ContentWrap title="原因列表">
    <!-- 操作栏 -->
    <div class="mb-16px flex justify-end">
      <el-button v-hasPermi="['hrm:recruit:config:update']" type="primary" plain @click="addReason">
        <Icon icon="ep:plus" class="mr-5px" />新增
      </el-button>
      <el-button
        v-hasPermi="['hrm:recruit:config:update']"
        :loading="saving"
        type="primary"
        @click="saveReasonList"
      >
        <Icon icon="ep:check" class="mr-5px" />保存
      </el-button>
    </div>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="reasonList" border>
      <el-table-column label="序号" type="index" align="center" width="80" />
      <el-table-column label="淘汰原因" min-width="320">
        <template #default="scope">
          <el-input
            v-model="reasonList[scope.$index]"
            maxlength="255"
            placeholder="请输入淘汰原因"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="100">
        <template #default="scope">
          <el-button
            v-hasPermi="['hrm:recruit:config:update']"
            link
            type="danger"
            @click="removeReason(scope.$index)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as RecruitConfigApi from '@/api/hrm/recruit/config'

defineOptions({ name: 'HrmRecruitEliminateReason' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化
const loading = ref(true) // 列表的加载中
const saving = ref(false) // 保存按钮的加载中
const reasonList = ref<string[]>([]) // 淘汰原因列表

/** 查询列表 */
async function getReasonList() {
  loading.value = true
  try {
    reasonList.value = await RecruitConfigApi.getRecruitEliminateReasonList()
  } finally {
    loading.value = false
  }
}

/** 添加按钮操作 */
function addReason() {
  if (reasonList.value.some((reason) => !reason.trim())) {
    message.warning('请先填写新增的淘汰原因')
    return
  }
  reasonList.value.push('')
}

/** 删除按钮操作 */
function removeReason(index: number) {
  reasonList.value.splice(index, 1)
}

/** 保存按钮操作 */
async function saveReasonList() {
  // 校验表单
  const reasons = reasonList.value.map((reason) => reason.trim())
  if (reasons.some((reason) => !reason)) {
    message.warning('淘汰原因不能为空')
    return
  }
  if (new Set(reasons).size !== reasons.length) {
    message.warning('淘汰原因不能重复')
    return
  }

  // 提交请求
  saving.value = true
  try {
    await RecruitConfigApi.saveRecruitEliminateReason(reasons)
    message.success(t('common.updateSuccess'))
    await getReasonList()
  } finally {
    saving.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getReasonList()
})
</script>
