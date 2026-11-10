import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 学生课程（子表，ERP 模式独立增删改查） */
export interface Demo03Course {
  id?: number
  studentId?: number
  name: string // 名字
  score?: number // 分数
}

/** 学生班级（子表，ERP 模式独立增删改查） */
export interface Demo03Grade {
  id?: number
  studentId?: number
  name: string // 名字
  teacher?: string // 班主任
}

/** 学生（主表，ERP 模式：子表独立提交，主表不含子表） */
export interface Demo03Student {
  id?: number
  name: string // 名字
  sex?: number // 性别
  birthday?: number // 出生日期
  description?: string // 简介
  createTime?: number // 创建时间
}

const BASE = '/infra/demo03-student-erp'

/** 获取学生分页 */
export function getDemo03StudentPage(params: PageParam) {
  return http.get<PageResult<Demo03Student>>(`${BASE}/page`, params)
}

/** 获取学生详情 */
export function getDemo03Student(id: number) {
  return http.get<Demo03Student>(`${BASE}/get?id=${id}`)
}

/** 创建学生 */
export function createDemo03Student(data: Demo03Student) {
  return http.post<number>(`${BASE}/create`, data)
}

/** 更新学生 */
export function updateDemo03Student(data: Demo03Student) {
  return http.put<boolean>(`${BASE}/update`, data)
}

/** 删除学生 */
export function deleteDemo03Student(id: number) {
  return http.delete<boolean>(`${BASE}/delete?id=${id}`)
}

// ==================== 子表：学生课程 ====================

/** 获取学生课程分页 */
export function getDemo03CoursePage(params: PageParam & { studentId: number }) {
  return http.get<PageResult<Demo03Course>>(`${BASE}/demo03-course/page`, params)
}

/** 获取学生课程详情 */
export function getDemo03Course(id: number) {
  return http.get<Demo03Course>(`${BASE}/demo03-course/get?id=${id}`)
}

/** 创建学生课程 */
export function createDemo03Course(data: Demo03Course) {
  return http.post<number>(`${BASE}/demo03-course/create`, data)
}

/** 更新学生课程 */
export function updateDemo03Course(data: Demo03Course) {
  return http.put<boolean>(`${BASE}/demo03-course/update`, data)
}

/** 删除学生课程 */
export function deleteDemo03Course(id: number) {
  return http.delete<boolean>(`${BASE}/demo03-course/delete?id=${id}`)
}

// ==================== 子表：学生班级 ====================

/** 获取学生班级分页 */
export function getDemo03GradePage(params: PageParam & { studentId: number }) {
  return http.get<PageResult<Demo03Grade>>(`${BASE}/demo03-grade/page`, params)
}

/** 获取学生班级详情 */
export function getDemo03Grade(id: number) {
  return http.get<Demo03Grade>(`${BASE}/demo03-grade/get?id=${id}`)
}

/** 创建学生班级 */
export function createDemo03Grade(data: Demo03Grade) {
  return http.post<number>(`${BASE}/demo03-grade/create`, data)
}

/** 更新学生班级 */
export function updateDemo03Grade(data: Demo03Grade) {
  return http.put<boolean>(`${BASE}/demo03-grade/update`, data)
}

/** 删除学生班级 */
export function deleteDemo03Grade(id: number) {
  return http.delete<boolean>(`${BASE}/demo03-grade/delete?id=${id}`)
}
