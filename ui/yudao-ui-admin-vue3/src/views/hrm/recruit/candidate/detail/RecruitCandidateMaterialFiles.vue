<template>
  <ContentWrap>
    <!-- 列表 -->
    <el-table
      :data="candidate.resumeUrls || []"
      :show-header="false"
      :stripe="true"
      empty-text="暂无材料附件"
    >
      <el-table-column min-width="500">
        <template #default="scope">
          {{ getFileNameFromUrl(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column align="center" width="160">
        <template #default="scope">
          <el-button link type="primary" @click="openSafeUrl(scope.row)">预览</el-button>
          <el-button link type="primary" @click="handleDownload(scope.row)">下载</el-button>
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import * as RecruitCandidateApi from '@/api/hrm/recruit/candidate'
import { getFileNameFromUrl } from '@/utils/file'
import { downloadByUrl } from '@/utils/filt'
import { openSafeUrl } from '@/utils/url'

defineOptions({ name: 'HrmRecruitCandidateMaterialFiles' })

defineProps<{
  candidate: RecruitCandidateApi.HrmRecruitCandidateVO
}>()

/** 下载材料附件 */
function handleDownload(url: string) {
  downloadByUrl({ url, fileName: getFileNameFromUrl(url) })
}
</script>
