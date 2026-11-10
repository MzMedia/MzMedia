<!-- 支付宝渠道配置 -->
<template>
  <view>
    <wd-form-item title="开放平台 AppID" title-width="220rpx">
      <wd-input v-model="cfg.appId" clearable placeholder="请输入开放平台 AppID" />
    </wd-form-item>
    <wd-form-item title="网关地址" title-width="220rpx" center>
      <wd-radio-group v-model="cfg.serverUrl" type="button">
        <wd-radio value="https://openapi.alipay.com/gateway.do">
          线上
        </wd-radio>
        <wd-radio value="https://openapi-sandbox.dl.alipaydev.com/gateway.do">
          沙箱
        </wd-radio>
      </wd-radio-group>
    </wd-form-item>
    <wd-form-item title="算法类型" title-width="220rpx" center>
      <wd-radio-group v-model="cfg.signType" type="button">
        <wd-radio value="RSA2">
          RSA2
        </wd-radio>
      </wd-radio-group>
    </wd-form-item>
    <wd-form-item title="公钥类型" title-width="220rpx" center>
      <wd-radio-group v-model="cfg.mode" type="button">
        <wd-radio :value="1">
          公钥模式
        </wd-radio>
        <wd-radio :value="2">
          证书模式
        </wd-radio>
      </wd-radio-group>
    </wd-form-item>
    <wd-form-item title="应用私钥" title-width="220rpx">
      <wd-textarea v-model="cfg.privateKey" auto-height clearable placeholder="请输入应用私钥" />
    </wd-form-item>

    <!-- 公钥模式 -->
    <wd-form-item v-if="cfg.mode === 1" title="支付宝公钥" title-width="220rpx">
      <wd-textarea v-model="cfg.alipayPublicKey" auto-height clearable placeholder="请输入支付宝公钥" />
    </wd-form-item>

    <!-- 证书模式 -->
    <template v-if="cfg.mode === 2">
      <wd-form-item title="应用公钥证书" title-width="220rpx">
        <wd-textarea v-model="cfg.appCertContent" auto-height clearable placeholder="请粘贴应用公钥证书内容" />
      </wd-form-item>
      <wd-form-item title="支付宝公钥证书" title-width="220rpx">
        <wd-textarea v-model="cfg.alipayPublicCertContent" auto-height clearable placeholder="请粘贴支付宝公钥证书内容" />
      </wd-form-item>
      <wd-form-item title="根证书" title-width="220rpx">
        <wd-textarea v-model="cfg.rootCertContent" auto-height clearable placeholder="请粘贴根证书内容" />
      </wd-form-item>
    </template>

    <wd-form-item title="加密方式" title-width="220rpx">
      <wd-input v-model="cfg.encryptType" clearable placeholder="可选，如 AES" />
    </wd-form-item>
    <wd-form-item title="加密密钥" title-width="220rpx">
      <wd-input v-model="cfg.encryptKey" clearable placeholder="可选" />
    </wd-form-item>
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
  serverUrl: 'https://openapi.alipay.com/gateway.do',
  signType: 'RSA2',
  mode: 1,
  privateKey: '',
  alipayPublicKey: '',
  appCertContent: '',
  alipayPublicCertContent: '',
  rootCertContent: '',
  encryptType: '',
  encryptKey: '',
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
    return '请输入开放平台 AppID'
  }
  if (!cfg.value.serverUrl) {
    return '请选择网关地址'
  }
  if (!cfg.value.signType) {
    return '请选择算法类型'
  }
  // 证书模式
  if (cfg.value.mode === 2) {
    if (!cfg.value.appCertContent) {
      return '请粘贴应用公钥证书内容'
    }
    if (!cfg.value.alipayPublicCertContent) {
      return '请粘贴支付宝公钥证书内容'
    }
    if (!cfg.value.rootCertContent) {
      return '请粘贴根证书内容'
    }
    return ''
  }
  // 公钥模式
  if (!cfg.value.privateKey) {
    return '请输入应用私钥'
  }
  if (!cfg.value.alipayPublicKey) {
    return '请输入支付宝公钥'
  }
  return ''
}

defineExpose({ validate })
</script>
