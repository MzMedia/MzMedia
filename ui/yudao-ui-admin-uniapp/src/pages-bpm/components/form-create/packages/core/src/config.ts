import type { FormCreateOption } from '../../../types/typing'

export default function getConfig(): FormCreateOption {
  return {
    form: {
      border: true,
      errorType: 'toast',
      layout: 'horizontal',
      titleWidth: '200rpx',
      valueAlign: 'right',
    },
    row: {
      show: true,
      gutter: 0,
    },
    submitBtn: false,
    resetBtn: false,
  }
}
