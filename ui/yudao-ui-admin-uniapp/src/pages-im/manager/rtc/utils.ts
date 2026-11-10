/** 计算并格式化通话时长（接通到结束） */
export function formatCallDuration(acceptTime?: string, endTime?: string): string {
  if (!acceptTime || !endTime) {
    return '-'
  }
  const seconds = Math.floor((new Date(endTime).getTime() - new Date(acceptTime).getTime()) / 1000)
  if (Number.isNaN(seconds) || seconds < 0) {
    return '-'
  }
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  const pad = (n: number) => String(n).padStart(2, '0')
  return h > 0 ? `${h}:${pad(m)}:${pad(s)}` : `${pad(m)}:${pad(s)}`
}
