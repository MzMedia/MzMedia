<template>
  <doc-alert title="【组织】工作台、组织架构" url="https://doc.iocoder.cn/hrm/organization/" />

  <!-- 搜索工作栏 -->
  <ContentWrap>
    <el-form
      ref="queryFormRef"
      :inline="true"
      :model="queryParams"
      class="-mb-15px"
      label-width="82px"
    >
      <el-form-item label="部门名称" prop="name">
        <el-input
          v-model="queryParams.name"
          clearable
          placeholder="请输入部门名称"
          class="!w-240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px" />
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px" />
          重置
        </el-button>
        <el-button
          v-hasPermi="['system:dept:create']"
          type="primary"
          plain
          @click="openDeptManagement"
        >
          <Icon icon="ep:plus" class="mr-5px" />
          新建部门
        </el-button>
      </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 组织树 -->
  <ContentWrap>
    <el-alert
      class="mb-15px"
      :closable="false"
      show-icon
      title="人数格式为：直属人数（包含下级部门人数）"
      type="info"
    />
    <el-table
      v-loading="loading"
      :data="filteredDeptList"
      :show-overflow-tooltip="true"
      :stripe="true"
      default-expand-all
      row-key="id"
    >
      <el-table-column label="部门名称" min-width="280">
        <template #default="{ row }">
          <el-link type="primary" underline="never" @click="openDetail(row.id)">
            {{ row.name }}
          </el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" min-width="180">
        <template #header>
          <el-tooltip content="直属在职人数（包含下级部门在职人数）" placement="top">
            <span>在职员工</span>
          </el-tooltip>
        </template>
        <template #default="{ row }">
          {{ formatStatistics(row.directStatistics, row.totalStatistics, 'activeCount') }}
        </template>
      </el-table-column>
      <el-table-column align="center" min-width="180">
        <template #header>
          <el-tooltip content="直属全职人数（包含下级部门全职人数）" placement="top">
            <span>全职员工</span>
          </el-tooltip>
        </template>
        <template #default="{ row }">
          {{ formatStatistics(row.directStatistics, row.totalStatistics, 'fullTimeCount') }}
        </template>
      </el-table-column>
      <el-table-column align="center" min-width="180">
        <template #header>
          <el-tooltip content="直属非全职人数（包含下级部门非全职人数）" placement="top">
            <span>非全职人数</span>
          </el-tooltip>
        </template>
        <template #default="{ row }">
          {{ formatStatistics(row.directStatistics, row.totalStatistics, 'nonFullTimeCount') }}
        </template>
      </el-table-column>
    </el-table>
  </ContentWrap>
</template>

<script lang="ts" setup>
import type { FormInstance } from 'element-plus'
import * as EmployeeApi from '@/api/hrm/employee'
import * as DeptApi from '@/api/system/dept'
import { handleTree } from '@/utils/tree'

defineOptions({ name: 'HrmDept' })

interface EmployeeStatistics {
  activeCount: number
  fullTimeCount: number
  nonFullTimeCount: number
}

interface DeptTreeNode extends DeptApi.DeptVO {
  children?: DeptTreeNode[]
  directStatistics: EmployeeStatistics
  totalStatistics: EmployeeStatistics
}

const EMPTY_STATISTICS: EmployeeStatistics = {
  activeCount: 0,
  fullTimeCount: 0,
  nonFullTimeCount: 0
}

const router = useRouter() // 路由

const loading = ref(true) // 组织树加载中
const deptTree = ref<DeptTreeNode[]>([]) // 组织树
const queryParams = reactive({
  name: undefined as string | undefined
})
const appliedName = ref<string>() // 已应用的部门名称
const queryFormRef = ref<FormInstance>() // 搜索表单 Ref
const filteredDeptList = computed(() => filterDeptTree(deptTree.value, appliedName.value)) // 过滤后的部门列表

/** 查询组织树和员工统计 */
async function getList() {
  loading.value = true
  try {
    const [deptList, statisticsList] = await Promise.all([
      DeptApi.getSimpleDeptList(),
      EmployeeApi.getEmployeeDeptStatistics()
    ])
    deptTree.value = buildDeptTree(deptList, statisticsList)
  } finally {
    loading.value = false
  }
}

/** 搜索部门 */
function handleQuery() {
  appliedName.value = queryParams.name
}

/** 重置部门搜索 */
function resetQuery() {
  queryFormRef.value?.resetFields()
  appliedName.value = undefined
}

/** 前往部门管理 */
function openDeptManagement() {
  router.push('/system/dept')
}

/** 打开组织详情 */
function openDetail(id: number) {
  router.push({ name: 'HrmDeptDetail', params: { id } })
}

/** 构建包含直属与下级人数统计的组织树 */
function buildDeptTree(
  deptList: DeptApi.DeptVO[],
  statisticsList: EmployeeApi.HrmEmployeeDeptStatisticsVO[]
): DeptTreeNode[] {
  function buildNode(dept: DeptApi.DeptVO): DeptTreeNode {
    const children = (dept.children || []).map(buildNode)
    const directStatistics = statisticsList.find((statistics) => statistics.deptId === dept.id) || {
      ...EMPTY_STATISTICS
    }
    const totalStatistics = children.reduce<EmployeeStatistics>(
      (statistics, child) => ({
        activeCount: statistics.activeCount + child.totalStatistics.activeCount,
        fullTimeCount: statistics.fullTimeCount + child.totalStatistics.fullTimeCount,
        nonFullTimeCount: statistics.nonFullTimeCount + child.totalStatistics.nonFullTimeCount
      }),
      { ...directStatistics }
    )
    return {
      ...dept,
      children,
      directStatistics,
      totalStatistics
    }
  }

  return (handleTree(deptList) as DeptApi.DeptVO[]).map(buildNode)
}

/** 按部门名称过滤组织树，并保留命中节点的上级路径 */
function filterDeptTree(deptList: DeptTreeNode[], name?: string): DeptTreeNode[] {
  const keyword = name?.trim()
  if (!keyword) {
    return deptList
  }
  return deptList.reduce<DeptTreeNode[]>((result, dept) => {
    const children = filterDeptTree(dept.children || [], keyword)
    if (dept.name.includes(keyword) || children.length > 0) {
      result.push({ ...dept, children })
    }
    return result
  }, [])
}

/** 格式化直属人数和包含下级的人数 */
function formatStatistics(
  directStatistics: EmployeeStatistics = EMPTY_STATISTICS,
  totalStatistics: EmployeeStatistics = EMPTY_STATISTICS,
  field: keyof EmployeeStatistics
) {
  return `${directStatistics[field]}（${totalStatistics[field]}）`
}

/** 初始化 */
onMounted(() => {
  getList()
})
</script>
