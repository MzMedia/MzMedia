<template>
  <wd-form-item :title="rule.title" :title-width="titleWidth" :prop="rule.field" layout="vertical">
    <view class="fc-color-picker" :class="{ 'is-disabled': disabled }">
      <view class="fc-color-picker__input">
        <view class="fc-color-picker__preview" :style="{ backgroundColor: previewColor }" />
        <wd-input
          :model-value="inputValue"
          :placeholder="placeholder"
          :disabled="disabled"
          clearable
          v-bind="inputProps"
          @update:model-value="handleInput"
        />
      </view>

      <view v-if="presetColors.length > 0" class="fc-color-picker__presets">
        <view
          v-for="color in presetColors"
          :key="color"
          class="fc-color-picker__swatch"
          :class="{ 'is-active': color === inputValue }"
          :style="{ backgroundColor: color }"
          @click="selectColor(color)"
        />
      </view>
    </view>
  </wd-form-item>
</template>

<script lang="ts" setup>
import type { NormalizedFormCreateRule } from '../../../../types/typing'
import { computed } from 'vue'
import { getPlaceholder } from '../core/utils'

defineOptions({
  name: 'FcColorPicker',
})

const props = defineProps<{
  disabled?: boolean
  modelValue?: any
  rule: NormalizedFormCreateRule
  titleWidth?: string | number
}>()

const emit = defineEmits<{
  'update:modelValue': [value: any]
  'change': [value: any]
}>()

const DEFAULT_COLORS = [
  '#f5222d',
  '#fa8c16',
  '#fadb14',
  '#52c41a',
  '#13c2c2',
  '#1677ff',
  '#722ed1',
  '#eb2f96',
  '#000000',
  '#ffffff',
]

const inputValue = computed(() => props.modelValue === undefined || props.modelValue === null ? '' : String(props.modelValue))
const placeholder = computed(() => getPlaceholder(props.rule))
const previewColor = computed(() => inputValue.value || '#fff')
const presetColors = computed(() => {
  const colors = props.rule.props?.predefine || props.rule.props?.predefineColors || props.rule.props?.colors
  return Array.isArray(colors) ? colors.map(color => String(color)).filter(Boolean) : DEFAULT_COLORS
})
const inputProps = computed(() => {
  const {
    colors: _colors,
    colorFormat: _colorFormat,
    predefine: _predefine,
    predefineColors: _predefineColors,
    showAlpha: _showAlpha,
    ...rest
  } = props.rule.props || {}
  return rest
})

function handleInput(value: any) {
  const nextValue = value === '' || value === undefined || value === null ? undefined : String(value)
  emit('update:modelValue', nextValue)
  emit('change', nextValue)
}

function selectColor(color: string) {
  if (props.disabled) {
    return
  }
  emit('update:modelValue', color)
  emit('change', color)
}
</script>

<style lang="scss" scoped>
.fc-color-picker {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
  width: 100%;
}

.fc-color-picker.is-disabled {
  opacity: 0.65;
}

.fc-color-picker__input {
  display: flex;
  align-items: center;
  gap: 16rpx;
  width: 100%;
}

.fc-color-picker__preview {
  box-sizing: border-box;
  flex-shrink: 0;
  width: 52rpx;
  height: 52rpx;
  border: 1rpx solid #dcdfe6;
  border-radius: 8rpx;
}

.fc-color-picker__presets {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
}

.fc-color-picker__swatch {
  box-sizing: border-box;
  width: 44rpx;
  height: 44rpx;
  border: 2rpx solid #dcdfe6;
  border-radius: 8rpx;
}

.fc-color-picker__swatch.is-active {
  border-color: #1677ff;
  box-shadow: 0 0 0 4rpx rgba(22, 119, 255, 0.12);
}
</style>
