<!-- FMS 账套切换器：恢复账套缓存、切换当前账套，并加载当前会计期间 -->
<template>
  <div v-if="isFmsRoute" class="mr-8px w-285px flex-shrink-0 lt-xl:w-190px">
    <el-select
      v-model="selectedAccountSetId"
      :disabled="switching"
      :loading="loading"
      class="w-full"
      filterable
      placeholder="请选择账套"
      @change="handleChange"
      @visible-change="handleVisibleChange"
    >
      <template #label="{ label }">
        <div class="min-w-0 flex items-center gap-6px">
          <Icon icon="ep:office-building" />
          <span
            class="min-w-0 overflow-hidden text-ellipsis whitespace-nowrap"
            :title="String(label)"
          >
            {{ label }}
          </span>
          <span
            v-if="currentMonthText"
            class="flex-shrink-0 border-l border-l-[var(--el-border-color)] border-l-solid pl-8px text-12px text-[var(--el-text-color-secondary)] lt-xl:hidden"
          >
            {{ currentMonthText }}
          </span>
        </div>
      </template>
      <el-option
        v-for="item in accountSetList"
        :key="item.id"
        :disabled="!item.initialized"
        :label="item.companyName"
        :value="item.id!"
      >
        <div class="flex items-center justify-between gap-12px">
          <span>{{ item.companyName }}</span>
          <div class="flex flex-shrink-0 gap-4px">
            <el-tag v-if="item.defaultStatus" effect="plain" size="small">默认</el-tag>
            <el-tag v-if="!item.initialized" effect="plain" size="small" type="info">
              未初始化
            </el-tag>
          </div>
        </div>
      </el-option>
    </el-select>
  </div>
  <FmsAccountSetGuide ref="accountSetGuideRef" />
</template>

<script lang="ts" setup>
import { useTagsViewStore } from '@/store/modules/tagsView'
import { FmsAccountUserApi } from '@/api/fms/config/account-user'
import { useFmsStore } from '@/views/fms/store/fms'
import FmsAccountSetGuide from './FmsAccountSetGuide.vue'

defineOptions({ name: 'FmsAccountSetSwitch' })

// ==================== 路由与状态 ====================

const FMS_ROUTE_PREFIX = '/fms' // FMS 路由前缀
const FMS_HOME_PATH = '/fms/home' // FMS 首页
const FMS_ACCOUNT_SET_PATH = '/fms/config/account-set' // 账套管理

const route = useRoute()
const router = useRouter()
const message = useMessage()
const fmsStore = useFmsStore()
const tagsViewStore = useTagsViewStore()

const loading = ref(false) // 账套列表的加载中
const switching = ref(false) // 是否正在切换账套
const accountSetListLoaded = ref(false) // 是否已成功加载账套列表
const accountSetGuideShown = ref(false) // 是否已展示账套开通引导，避免重复弹出
const selectedAccountSetId = ref<number>() // 选择器当前选中的账套编号
const accountSetGuideRef = ref<InstanceType<typeof FmsAccountSetGuide>>() // 账套开通引导

const isFmsRoute = computed(() => isFmsRoutePath(route.path))
const accountSetId = computed(() => fmsStore.getAccountSetId)
const accountSetList = computed(() => fmsStore.getAccountSetList)
const currentMonthText = computed(() => formatCurrentMonth(fmsStore.getCurrentMonth))

// 同步其他 FMS 页面变更的账套
watch(
  accountSetId,
  (id) => {
    selectedAccountSetId.value = id
  },
  { immediate: true }
)

// 仅进入 FMS 路由时加载账套，避免影响其他业务模块
watch(
  isFmsRoute,
  async (visible) => {
    if (visible) {
      await loadAccountSetList()
    }
  },
  { immediate: true }
)

// 从账套管理进入其他 FMS 页面时，仍需提示未完成的账套配置
watch(
  () => route.path,
  () => showAccountSetGuide()
)

// 进入 FMS 或切换账套后加载当前会计期间
watch(
  [isFmsRoute, accountSetId],
  async ([visible, id]) => {
    if (visible && id && !fmsStore.getCurrentMonth) {
      await fmsStore.loadCurrentMonth()
    }
  },
  { immediate: true }
)

// ==================== 账套加载 ====================

/**
 * 加载账套列表
 *
 * 依次选择缓存账套、默认账套、第一条已初始化账套；没有可用账套时展示开通引导。
 */
async function loadAccountSetList(force = false) {
  if (loading.value) return
  loading.value = true
  try {
    await fmsStore.loadAccountSetList(force)
    accountSetListLoaded.value = true
    const accountSet = accountSetList.value.find((item) => item.id === accountSetId.value)
    if (!accountSet) {
      selectedAccountSetId.value = undefined
      await showAccountSetGuide()
      return
    }
    accountSetGuideShown.value = false
    accountSetGuideRef.value?.close()
    selectedAccountSetId.value = accountSet.id
    fmsStore.setAccountSet({
      id: accountSet.id!,
      companyName: accountSet.companyName,
      level: accountSet.level!
    })
  } finally {
    loading.value = false
  }
}

/** 展示账套开通引导 */
async function showAccountSetGuide() {
  if (
    !accountSetListLoaded.value ||
    accountSetGuideShown.value ||
    !isFmsRoute.value ||
    route.path === FMS_ACCOUNT_SET_PATH ||
    accountSetId.value
  ) {
    return
  }
  await nextTick()
  if (!accountSetGuideRef.value) return
  accountSetGuideShown.value = true
  accountSetGuideRef.value.open(accountSetList.value.length === 0 ? 'empty' : 'uninitialized')
}

/** 展开账套选择器时刷新账套列表 */
async function handleVisibleChange(visible: boolean) {
  if (visible) {
    await loadAccountSetList(true)
  }
}

// ==================== 账套切换 ====================

/** 切换账套，并关闭旧账套对应的 FMS 标签页 */
async function handleChange(id: number) {
  const previousAccountSetId = accountSetId.value
  if (id === previousAccountSetId) return
  const accountSet = accountSetList.value.find((item) => item.id === id)
  if (!accountSet?.initialized) {
    selectedAccountSetId.value = previousAccountSetId
    return
  }
  try {
    await message.confirm(
      `切换账套后将关闭所有财务管理标签页，未保存的内容不会保留。确认切换至“${accountSet.companyName}”吗？`,
      '切换账套'
    )
  } catch {
    selectedAccountSetId.value = previousAccountSetId
    return
  }

  switching.value = true
  try {
    await FmsAccountUserApi.updateAccountSetDefaultStatus(accountSet.id!)
    // 更新账套后关闭旧账套标签，并进入新账套首页
    fmsStore.setAccountSet({
      id: accountSet.id!,
      companyName: accountSet.companyName,
      level: accountSet.level!
    })
    closeFmsTags()
    await router.replace(FMS_HOME_PATH)
    message.success(`已切换至账套“${accountSet.companyName}”`)
  } finally {
    switching.value = false
  }
}

/** 关闭全部 FMS 标签并重建页面缓存 */
function closeFmsTags() {
  tagsViewStore.getVisitedViews
    .filter((view) => isFmsRoutePath(view.path))
    .forEach((view) => tagsViewStore.delVisitedView(view))
  tagsViewStore.addCachedView()
}

/** 判断路由是否属于 FMS 模块 */
function isFmsRoutePath(path: string) {
  return path === FMS_ROUTE_PREFIX || path.startsWith(`${FMS_ROUTE_PREFIX}/`)
}

/** 将会计期间格式化为顶部展示文案 */
function formatCurrentMonth(currentMonth?: string) {
  const match = currentMonth?.match(/^(\d{4})-(\d{2})$/)
  return match ? `${match[1]} 年第 ${match[2]} 期` : currentMonth || ''
}
</script>
