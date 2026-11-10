<template>
  <Dialog v-model="dialogVisible" title="账套授权" width="820px">
    <el-alert
      :closable="false"
      show-icon
      type="info"
      title="查看者可以查看账套数据，会计可以维护账套数据，主管可以管理账套及成员"
    />

    <div class="mt-16px mb-12px flex items-center justify-between">
      <div>
        <span class="text-14px text-[var(--el-text-color-secondary)]">账套名称：</span>
        <span class="font-600">{{ accountSet?.companyName }}</span>
      </div>
      <el-button type="primary" @click="openAddForm">
        <Icon icon="ep:plus" class="mr-5px" /> 添加成员
      </el-button>
    </div>

    <el-table v-loading="formLoading" :data="memberList" border max-height="420">
      <el-table-column type="index" label="序号" width="70" align="center" />
      <el-table-column label="姓名" min-width="140">
        <template #default="scope">
          {{ scope.row.nickname || `用户 #${scope.row.userId}` }}
          <el-tag v-if="scope.row.founder" class="ml-6px" type="success" effect="plain">
            创建人
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="deptName" label="部门" min-width="140" show-overflow-tooltip>
        <template #default="scope">{{ scope.row.deptName || '-' }}</template>
      </el-table-column>
      <el-table-column prop="mobile" label="手机号码" width="140">
        <template #default="scope">{{ scope.row.mobile || '-' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template #default="scope">
          <dict-tag :type="DICT_TYPE.COMMON_STATUS" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="权限级别" width="130" align="center">
        <template #default="scope">
          <dict-tag
            v-if="scope.row.founder"
            :type="DICT_TYPE.FMS_ACCOUNT_USER_LEVEL"
            :value="scope.row.level"
          />
          <el-select v-else v-model="scope.row.level" class="!w-100px">
            <el-option
              v-for="dict in getIntDictOptions(DICT_TYPE.FMS_ACCOUNT_USER_LEVEL)"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="90" align="center" fixed="right">
        <template #default="scope">
          <el-button
            link
            type="danger"
            :disabled="scope.row.founder"
            @click="removeMember(scope.$index)"
          >
            移出
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button :disabled="formLoading" type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>

  <!-- 弹窗：添加账套成员 -->
  <Dialog v-model="addDialogVisible" title="添加成员" width="560px" append-to-body>
    <el-form ref="addFormRef" :model="addFormData" :rules="addFormRules" label-width="88px">
      <el-form-item label="选择用户" prop="userIds">
        <UserSelectV2
          v-model="addFormData.userIds"
          :multiple="true"
          :disabled-ids="memberUserIds"
          placeholder="请选择需要加入账套的用户"
          @change="handleAddUserChange"
        />
      </el-form-item>
      <el-form-item label="权限级别" prop="level">
        <el-select v-model="addFormData.level" class="!w-100%" placeholder="请选择权限级别">
          <el-option
            v-for="dict in getIntDictOptions(DICT_TYPE.FMS_ACCOUNT_USER_LEVEL)"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button type="primary" @click="submitAddForm">确 定</el-button>
      <el-button @click="addDialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>

<script lang="ts" setup>
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import { CommonStatusEnum } from '@/utils/constants'
import type { UserVO } from '@/api/system/user'
import type { FmsAccountSetVO } from '@/api/fms/config/account-set'
import { FmsAccountUserApi } from '@/api/fms/config/account-user'
import type { FmsAccountUserVO } from '@/api/fms/config/account-user'
import UserSelectV2 from '@/views/system/user/components/UserSelectV2.vue'
import type { FormInstance, FormRules } from 'element-plus'

defineOptions({ name: 'FmsAccountSetMemberForm' })

const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 账套授权弹窗的是否展示
const formLoading = ref(false) // 成员列表加载和表单提交的加载中
const accountSet = ref<FmsAccountSetVO>() // 当前账套
const memberList = ref<FmsAccountUserVO[]>([]) // 账套成员列表

/** 打开账套授权弹窗 */
async function open(row: FmsAccountSetVO) {
  dialogVisible.value = true
  accountSet.value = row
  formLoading.value = true
  try {
    memberList.value = await FmsAccountUserApi.getAccountUserList(row.id!)
  } finally {
    formLoading.value = false
  }
}

defineExpose({ open }) // 提供 open 方法，用于打开弹窗

const emit = defineEmits<{ success: [] }>() // 定义 success 事件，用于操作成功后的回调

/** 保存账套成员及其权限级别 */
async function submitForm() {
  if (!accountSet.value) {
    return
  }
  // 提交请求
  formLoading.value = true
  try {
    await FmsAccountUserApi.updateAccountUserList({
      accountSetId: accountSet.value.id!,
      members: memberList.value.map((member) => ({
        userId: member.userId,
        level: member.level
      }))
    })
    message.success('账套授权已保存')
    dialogVisible.value = false
    // 发送操作成功的事件
    emit('success')
  } finally {
    formLoading.value = false
  }
}

/** 移出账套成员 */
function removeMember(index: number) {
  memberList.value.splice(index, 1)
}

const addDialogVisible = ref(false) // 添加成员弹窗的是否展示
const addFormData = ref({
  userIds: [] as number[],
  level: undefined as number | undefined
}) // 添加成员表单数据
const addFormRules = reactive<FormRules>({
  userIds: [{ required: true, message: '请选择需要添加的用户', trigger: 'change' }],
  level: [{ required: true, message: '请选择权限级别', trigger: 'change' }]
})
const addFormRef = ref<FormInstance>() // 添加成员表单 Ref
const addUserList = ref<UserVO[]>([]) // 待添加的用户列表
const memberUserIds = computed(() => memberList.value.map((member) => member.userId)) // 已授权用户编号

/** 打开添加成员弹窗 */
function openAddForm() {
  addDialogVisible.value = true
  resetAddForm()
}

/** 记录用户选择器返回的用户信息 */
function handleAddUserChange(users: UserVO | UserVO[] | undefined) {
  addUserList.value = Array.isArray(users) ? users : users ? [users] : []
}

/** 提交添加成员表单 */
async function submitAddForm() {
  // 校验表单
  if (!addFormRef.value) return
  const valid = await addFormRef.value.validate()
  if (!valid) return
  // 添加成员
  addUserList.value.forEach((user) => {
    memberList.value.push({
      userId: user.id,
      nickname: user.nickname,
      deptName: user.deptName,
      status: CommonStatusEnum.ENABLE,
      defaultStatus: false,
      founder: false,
      level: addFormData.value.level!
    })
  })
  addDialogVisible.value = false
}

/** 重置添加成员表单 */
function resetAddForm() {
  addFormData.value = {
    userIds: [],
    level: undefined
  }
  addUserList.value = []
  addFormRef.value?.resetFields()
}
</script>
