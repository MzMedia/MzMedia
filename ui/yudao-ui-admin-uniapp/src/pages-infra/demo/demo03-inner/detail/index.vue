<template>
  <view class="yd-page-container">
    <!-- 顶部导航栏 -->
    <wd-navbar
      title="学生详情"
      left-arrow placeholder safe-area-inset-top fixed
      @click-left="handleBack"
    />

    <view class="pb-160rpx">
      <!-- 主表 -->
      <wd-cell-group border title="学生信息">
        <wd-cell title="名字" :value="formData?.name ?? '-'" />
        <wd-cell title="性别">
          <dict-tag :type="DICT_TYPE.SYSTEM_USER_SEX" :value="formData?.sex" />
        </wd-cell>
        <wd-cell title="出生日期" :value="formData?.birthday ? formatDate(formData.birthday) : '-'" />
        <wd-cell title="简介" :value="formData?.description ?? '-'" />
        <wd-cell title="创建时间" :value="formatDateTime(formData?.createTime)" />
      </wd-cell-group>

      <!-- 子表：学生课程 -->
      <view class="mt-20rpx px-24rpx py-16rpx text-28rpx text-[#999]">
        学生课程（{{ courses.length }}）
      </view>
      <view class="px-24rpx">
        <view v-if="!courses.length" class="rounded-12rpx bg-white py-40rpx text-center text-26rpx text-[#999]">
          暂无课程
        </view>
        <view
          v-for="course in courses"
          :key="course.id"
          class="mb-16rpx flex items-center justify-between rounded-12rpx bg-white p-24rpx shadow-sm"
        >
          <text class="min-w-0 flex-1 truncate text-30rpx text-[#333]">{{ course.name }}</text>
          <text class="ml-16rpx shrink-0 text-28rpx text-[#1677ff]">{{ course.score ?? '-' }} 分</text>
        </view>
      </view>

      <!-- 子表：学生班级 -->
      <view class="mt-20rpx px-24rpx py-16rpx text-28rpx text-[#999]">
        学生班级
      </view>
      <view class="px-24rpx">
        <view class="rounded-12rpx bg-white p-24rpx shadow-sm">
          <view v-if="grade" class="text-30rpx text-[#333]">
            {{ grade.name }}
            <text class="ml-16rpx text-26rpx text-[#999]">班主任：{{ grade.teacher || '-' }}</text>
          </view>
          <view v-else class="text-center text-26rpx text-[#999]">
            暂无班级
          </view>
        </view>
      </view>
    </view>

    <!-- 底部操作按钮 -->
    <view class="yd-detail-footer">
      <view class="yd-detail-footer-actions">
        <wd-button
          v-if="hasAccessByCodes(['infra:demo03-student:update'])"
          class="flex-1" type="warning" @click="handleEdit"
        >
          编辑
        </wd-button>
        <wd-button
          v-if="hasAccessByCodes(['infra:demo03-student:delete'])"
          class="flex-1" type="danger" :loading="deleting" @click="handleDelete"
        >
          删除
        </wd-button>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import type { Demo03Course, Demo03Grade, Demo03Student } from '@/api/infra/demo/demo03/inner'
import { useDialog } from '@wot-ui/ui/components/wd-dialog'
import { useToast } from '@wot-ui/ui/components/wd-toast'
import { onShow } from '@dcloudio/uni-app'
import { ref } from 'vue'
import {
  deleteDemo03Student,
  getDemo03CourseListByStudentId,
  getDemo03GradeByStudentId,
  getDemo03Student,
} from '@/api/infra/demo/demo03/inner'
import { useAccess } from '@/hooks/useAccess'
import { delay, navigateBackPlus } from '@/utils'
import { DICT_TYPE } from '@/utils/constants'
import { formatDate, formatDateTime } from '@/utils/date'

const props = defineProps<{
  id?: number | any
}>()

definePage({
  style: {
    navigationBarTitleText: '',
    navigationStyle: 'custom',
  },
})

const { hasAccessByCodes } = useAccess()
const toast = useToast()
const dialog = useDialog()
const formData = ref<Demo03Student>() // 主表数据
const courses = ref<Demo03Course[]>([]) // 学生课程
const grade = ref<Demo03Grade>() // 学生班级
const deleting = ref(false) // 删除状态

/** 返回上一页 */
function handleBack() {
  navigateBackPlus('/pages-infra/demo/demo03-inner/index')
}

/** 加载详情（主表 + 子表） */
async function getDetail() {
  if (!props.id) {
    return
  }
  const [student, courseList, gradeData] = await Promise.all([
    getDemo03Student(props.id),
    getDemo03CourseListByStudentId(props.id),
    getDemo03GradeByStudentId(props.id),
  ])
  formData.value = student
  courses.value = courseList || []
  grade.value = gradeData
}

/** 编辑 */
function handleEdit() {
  uni.navigateTo({ url: `/pages-infra/demo/demo03-inner/form/index?id=${props.id}` })
}

/** 删除 */
async function handleDelete() {
  try {
    await dialog.confirm({ title: '提示', msg: '确定要删除该学生吗？' })
  } catch {
    return
  }
  deleting.value = true
  try {
    await deleteDemo03Student(props.id)
    toast.success('删除成功')
    uni.$emit('infra:demo03-inner:reload')
    delay(handleBack)
  } finally {
    deleting.value = false
  }
}

/** 进入页面加载（含编辑返回后刷新） */
onShow(() => {
  getDetail()
})
</script>
