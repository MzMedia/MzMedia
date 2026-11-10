<!-- 微信渠道配置 -->
<template>
  <view>
    <wd-form-item title="微信 AppID" title-width="220rpx">
      <wd-input v-model="cfg.appId" clearable placeholder="请输入微信 AppID" />
    </wd-form-item>
    <wd-form-item title="商户号" title-width="220rpx">
      <wd-input v-model="cfg.mchId" clearable placeholder="请输入商户号" />
    </wd-form-item>
    <wd-form-item title="API 版本" title-width="220rpx" center>
      <wd-radio-group v-model="cfg.apiVersion" type="button">
        <wd-radio value="v2">
          v2
        </wd-radio>
        <wd-radio value="v3">
          v3
        </wd-radio>
      </wd-radio-group>
    </wd-form-item>

    <!-- v2 -->
    <template v-if="cfg.apiVersion === 'v2'">
      <wd-form-item title="商户密钥" title-width="220rpx">
        <wd-input v-model="cfg.mchKey" clearable placeholder="请输入商户密钥" />
      </wd-form-item>
      <wd-form-item title="apiclient_cert.p12 证书" title-width="220rpx">
        <wd-textarea v-model="cfg.keyContent" auto-height clearable placeholder="请粘贴 apiclient_cert.p12 证书内容" />
      </wd-form-item>
    </template>

    <!-- v3 -->
    <template v-if="cfg.apiVersion === 'v3'">
      <wd-form-item title="API V3 密钥" title-width="220rpx">
        <wd-input v-model="cfg.apiV3Key" clearable placeholder="请输入 API V3 密钥" />
      </wd-form-item>
      <wd-form-item title="apiclient_key.pem 证书" title-width="220rpx">
        <wd-textarea v-model="cfg.privateKeyContent" auto-height clearable placeholder="请粘贴 apiclient_key.pem 证书内容" />
      </wd-form-item>
      <wd-form-item title="证书序列号" title-width="220rpx">
        <wd-input v-model="cfg.certSerialNo" clearable placeholder="请输入证书序列号" />
      </wd-form-item>
      <wd-form-item title="公钥证书" title-width="220rpx">
        <wd-textarea v-model="cfg.publicKeyContent" auto-height clearable placeholder="请粘贴公钥证书内容（可选）" />
      </wd-form-item>
      <wd-form-item title="公钥 ID" title-width="220rpx">
        <wd-input v-model="cfg.publicKeyId" clearable placeholder="请输入公钥 ID" />
      </wd-form-item>
    </template>
  </view>
</template>

<script lang="ts" setup>
import { computed, watch } from 'vue'

const props = defineProps<{
  config: Record<string, any> // 渠道配置对象
}>()

const cfg = computed(() => props.config) // 可编辑的配置别名

const defaultConfig: Record<string, any> = {
  appId: '',
  mchId: '',
  apiVersion: 'v3',
  mchKey: '',
  keyContent: '',
  privateKeyContent: '',
  certSerialNo: '',
  apiV3Key: '',
  publicKeyContent: '',
  publicKeyId: '',
} // 渠道默认配置

watch(() => props.config, (config) => {
  if (!config) {
    return
  }
  Object.entries(defaultConfig).forEach(([key, value]) => {
    if (config[key] === undefined) {
      config[key] = value
    }
  })
}, { immediate: true })

/** 校验渠道配置必填项，返回首个错误文案（空串表示通过） */
function validate(): string {
  if (!cfg.value.appId) {
    return '请输入微信 AppID'
  }
  if (!cfg.value.mchId) {
    return '请输入商户号'
  }
  if (cfg.value.apiVersion === 'v2') {
    if (!cfg.value.mchKey) {
      return '请输入商户密钥'
    }
    if (!cfg.value.keyContent) {
      return '请粘贴 apiclient_cert.p12 证书内容'
    }
    return ''
  }
  if (!cfg.value.apiV3Key) {
    return '请输入 API V3 密钥'
  }
  if (!cfg.value.privateKeyContent) {
    return '请粘贴 apiclient_key.pem 证书内容'
  }
  if (!cfg.value.certSerialNo) {
    return '请输入证书序列号'
  }
  if (!cfg.value.publicKeyId) {
    return '请输入公钥 ID'
  }
  return ''
}

defineExpose({ validate })
</script>
