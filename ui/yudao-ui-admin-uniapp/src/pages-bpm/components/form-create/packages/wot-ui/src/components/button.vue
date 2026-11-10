<template>
  <view class="fc-button">
    <wd-button
      :block="rule.props?.block !== false"
      :disabled="disabled || rule.props?.disabled"
      :loading="rule.props?.loading"
      :round="rule.props?.round"
      :size="buttonSize"
      :type="buttonType"
      :variant="buttonVariant"
      @click="handleClick"
    >
      {{ buttonText }}
    </wd-button>
  </view>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../types/typing'
import { computed } from 'vue'

const props = defineProps<{
  disabled?: boolean
  rule: NormalizedFormCreateRule
}>()

const emit = defineEmits<{
  click: []
}>()

const buttonText = computed(() => {
  const childrenText = Array.isArray(props.rule.children)
    ? props.rule.children.filter(item => typeof item === 'string').join('')
    : ''
  const text = props.rule.props?.text
  return String(
    (typeof text === 'string' || typeof text === 'number' ? text : '')
    || props.rule.props?.label
    || props.rule.title
    || props.rule.value
    || childrenText
    || '按钮',
  )
})
const buttonType = computed(() => {
  const type = props.rule.props?.type || 'primary'
  return type === 'default' ? 'info' : type
})
const buttonSize = computed(() => props.rule.props?.size || 'medium')
const buttonVariant = computed(() => {
  if (props.rule.props?.variant) {
    return props.rule.props.variant
  }
  if (props.rule.props?.link || props.rule.props?.text === true) {
    return 'text'
  }
  if (props.rule.props?.plain) {
    return 'plain'
  }
  return 'base'
})

function handleClick() {
  if (props.disabled || props.rule.props?.disabled) {
    return
  }
  emit('click')
}
</script>

<style lang="scss" scoped>
.fc-button {
  padding: 20rpx 24rpx;
}
</style>
