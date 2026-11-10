<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      :title="getTitle"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <!-- 表单区域 -->
    <view>
      <wd-form ref="formRef" :model="formData" :schema="formSchema">
        <wd-cell-group border>
          <wd-form-item title="配置名" title-width="200rpx" prop="name">
            <wd-input
              v-model="formData.name"
              clearable
              placeholder="请输入配置名"
            />
          </wd-form-item>
          <yd-form-picker
            v-model="formData.storage"
            label="存储器"
            prop="storage"
            :dict-type="DICT_TYPE.INFRA_FILE_STORAGE"
            placeholder="请选择存储器"
            :disabled="!!formData.id"
          />
          <wd-form-item title="备注" title-width="200rpx" prop="remark">
            <wd-textarea
              v-model="formData.remark"
              clearable
              placeholder="请输入备注"
            />
          </wd-form-item>
        </wd-cell-group>

        <!-- Local / FTP / SFTP 配置 -->
        <wd-cell-group v-if="isLocalLike" border title="存储配置">
          <wd-form-item title="基础路径" title-width="200rpx" prop="config.basePath">
            <wd-input
              v-model="formData.config!.basePath"
              clearable
              placeholder="请输入基础路径"
            />
          </wd-form-item>
          <!-- FTP / SFTP 配置 -->
          <template v-if="isFtpLike">
            <wd-form-item title="主机地址" title-width="200rpx" prop="config.host">
              <wd-input
                v-model="formData.config!.host"
                clearable
                placeholder="请输入主机地址"
              />
            </wd-form-item>
            <wd-form-item title="主机端口" title-width="200rpx" prop="config.port">
              <wd-input
                v-model.number="formData.config!.port"
                type="number"
                clearable
                placeholder="请输入主机端口"
              />
            </wd-form-item>
            <wd-form-item title="用户名" title-width="200rpx" prop="config.username">
              <wd-input
                v-model="formData.config!.username"
                clearable
                placeholder="请输入用户名"
              />
            </wd-form-item>
            <wd-form-item title="密码" title-width="200rpx" prop="config.password">
              <wd-input
                v-model="formData.config!.password"
                clearable
                placeholder="请输入密码"
              />
            </wd-form-item>
          </template>
          <!-- FTP 连接模式 -->
          <wd-form-item v-if="isFtp" title="连接模式" title-width="200rpx" prop="config.mode" center>
            <wd-radio-group v-model="formData.config!.mode" type="button">
              <wd-radio value="Active">
                主动模式
              </wd-radio>
              <wd-radio value="Passive">
                被动模式
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
        </wd-cell-group>

        <!-- S3 配置 -->
        <wd-cell-group v-if="isS3" border title="S3 配置">
          <wd-form-item title="节点地址" title-width="200rpx" prop="config.endpoint">
            <wd-input
              v-model="formData.config!.endpoint"
              clearable
              placeholder="请输入节点地址"
            />
          </wd-form-item>
          <wd-form-item title="存储 bucket" title-width="200rpx" prop="config.bucket">
            <wd-input
              v-model="formData.config!.bucket"
              clearable
              placeholder="请输入 bucket"
            />
          </wd-form-item>
          <wd-form-item title="accessKey" title-width="200rpx" prop="config.accessKey">
            <wd-input
              v-model="formData.config!.accessKey"
              clearable
              placeholder="请输入 accessKey"
            />
          </wd-form-item>
          <wd-form-item title="accessSecret" title-width="200rpx" prop="config.accessSecret">
            <wd-input
              v-model="formData.config!.accessSecret"
              clearable
              placeholder="请输入 accessSecret"
            />
          </wd-form-item>
          <wd-form-item title="Path Style" title-width="200rpx" prop="config.enablePathStyleAccess" center>
            <wd-radio-group v-model="formData.config!.enablePathStyleAccess" type="button">
              <wd-radio :value="true">
                启用
              </wd-radio>
              <wd-radio :value="false">
                禁用
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
          <wd-form-item title="公开访问" title-width="200rpx" prop="config.enablePublicAccess" center>
            <wd-radio-group v-model="formData.config!.enablePublicAccess" type="button">
              <wd-radio :value="true">
                公开
              </wd-radio>
              <wd-radio :value="false">
                私有
              </wd-radio>
            </wd-radio-group>
          </wd-form-item>
          <wd-form-item title="区域" title-width="200rpx" prop="config.region">
            <wd-input
              v-model="formData.config!.region"
              clearable
              placeholder="请填写区域，一般仅 AWS 需要填写"
            />
          </wd-form-item>
        </wd-cell-group>

        <!-- 通用配置 -->
        <wd-cell-group v-if="formData.storage" border title="通用配置">
          <wd-form-item title="自定义域名" title-width="200rpx" prop="config.domain">
            <wd-input
              v-model="formData.config!.domain"
              clearable
              placeholder="请输入自定义域名"
            />
          </wd-form-item>
        </wd-cell-group>
      </wd-form>
    </view>

    <!-- 底部保存按钮 -->
    <view class="yd-detail-footer">
      <wd-button
        type="primary"
        block
        :loading="formLoading"
        @click="handleSubmit"
      >
        保存
      </wd-button>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { FormInstance } from '@wot-ui/ui/components/wd-form/types'
import type { FileConfig } from '@/api/infra/file/config'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { computed, onMounted, ref } from 'vue'
import { createFileConfig, getFileConfig, updateFileConfig } from '@/api/infra/file/config'
import { delay, navigateBackPlus } from '@/utils'
import { DICT_TYPE, InfraFileStorageEnum } from '@/utils/constants'
import { createFormSchema } from '@/utils/wot'

const props = defineProps<{
  id?: number | any
}>()

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const toast = useToast()
const getTitle = computed(() => props.id ? '编辑文件配置' : '新增文件配置')
const formLoading = ref(false) // 表单提交状态
const formData = ref<FileConfig>({
  id: undefined,
  name: '',
  storage: undefined,
  remark: '',
  config: {}, // 配置随 storage 按需填充
}) // 表单数据
const formSchema = createFormSchema({
  'name': [{ required: true, message: '配置名不能为空' }],
  'storage': [{ required: true, message: '存储器不能为空' }],
  // 存储配置按 storage 动态必填（嵌套 prop 由 createFormSchema 的 getValueByPath 解析）
  'config.basePath': [{ required: m => [InfraFileStorageEnum.LOCAL, InfraFileStorageEnum.FTP, InfraFileStorageEnum.SFTP].includes(m?.storage), message: '基础路径不能为空' }],
  'config.host': [{ required: m => [InfraFileStorageEnum.FTP, InfraFileStorageEnum.SFTP].includes(m?.storage), message: '主机地址不能为空' }],
  'config.port': [{ required: m => [InfraFileStorageEnum.FTP, InfraFileStorageEnum.SFTP].includes(m?.storage), message: '主机端口不能为空' }],
  'config.username': [{ required: m => [InfraFileStorageEnum.FTP, InfraFileStorageEnum.SFTP].includes(m?.storage), message: '用户名不能为空' }],
  'config.password': [{ required: m => [InfraFileStorageEnum.FTP, InfraFileStorageEnum.SFTP].includes(m?.storage), message: '密码不能为空' }],
  'config.mode': [{ required: m => m?.storage === InfraFileStorageEnum.FTP, message: '连接模式不能为空' }],
  'config.endpoint': [{ required: m => m?.storage === InfraFileStorageEnum.S3, message: '节点地址不能为空' }],
  'config.bucket': [{ required: m => m?.storage === InfraFileStorageEnum.S3, message: 'bucket 不能为空' }],
  'config.accessKey': [{ required: m => m?.storage === InfraFileStorageEnum.S3, message: 'accessKey 不能为空' }],
  'config.accessSecret': [{ required: m => m?.storage === InfraFileStorageEnum.S3, message: 'accessSecret 不能为空' }],
  // Path Style / 公开访问后端 @NotNull（false 非空、可通过；undefined 拦截）
  'config.enablePathStyleAccess': [{ required: m => m?.storage === InfraFileStorageEnum.S3, message: '请选择是否 Path Style' }],
  'config.enablePublicAccess': [{ required: m => m?.storage === InfraFileStorageEnum.S3, message: '请选择是否公开访问' }],
  // 自定义域名：非 S3 必填；S3 仅七牛（endpoint 含 qiniucs.com）必填
  'config.domain': [{ required: m => m?.storage != null && (m.storage !== InfraFileStorageEnum.S3 || !!m.config?.endpoint?.includes('qiniucs.com')), message: '自定义域名不能为空' }],
})
const formRef = ref<FormInstance>() // 表单组件引用
const isLocalLike = computed(() => [InfraFileStorageEnum.LOCAL, InfraFileStorageEnum.FTP, InfraFileStorageEnum.SFTP].includes(formData.value.storage as number)) // 本地/FTP/SFTP：有基础路径
const isFtpLike = computed(() => [InfraFileStorageEnum.FTP, InfraFileStorageEnum.SFTP].includes(formData.value.storage as number)) // FTP/SFTP：有主机/凭证
const isFtp = computed(() => formData.value.storage === InfraFileStorageEnum.FTP) // FTP：有连接模式
const isS3 = computed(() => formData.value.storage === InfraFileStorageEnum.S3) // S3：对象存储

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/file/index')
}

/** 加载详情 */
async function getDetail() {
  if (!props.id) {
    return
  }
  const data = await getFileConfig(props.id)
  formData.value = {
    ...data,
    config: data.config || {},
  }
}

/** 提交表单 */
async function handleSubmit() {
  const { valid } = await formRef.value.validate()
  if (!valid) {
    return
  }

  formLoading.value = true
  try {
    if (props.id) {
      await updateFileConfig(formData.value)
      toast.success('修改成功')
    } else {
      await createFileConfig(formData.value)
      toast.success('新增成功')
    }
    uni.$emit('infra:file-config:reload')
    delay(handleBack)
  } finally {
    formLoading.value = false
  }
}

/** 初始化 */
onMounted(() => {
  getDetail()
})
</script>
