import type { PageParam, PageResult } from '@/http/types'
import { http } from '@/http/http'

/** 学生课程（一对多子表） */
export interface Demo03Course {
  id?: number
  studentId?: number
  name: string // 名字
  score?: number // 分数
}

/** 学生班级（一对一子表） */
export interface Demo03Grade {
  id?: number
  studentId?: number
  name: string // 名字
  teacher?: string // 班主任
}

/** 学生（主表，主子表内嵌模式：子表随主表一起提交） */
export interface Demo03Student {
  id?: number
  name: string // 名字
  sex?: number // 性别
  birthday?: number // 出生日期
  description?: string // 简介
  createTime?: number // 创建时间
  demo03Courses?: Demo03Course[] // 学生课程（注意驼峰）
  demo03Grade?: Demo03Grade // 学生班级（注意驼峰）
}

const BASE = '/infra/demo03-student-inner'

/** 获取学生分页 */
export function getDemo03StudentPage(params: PageParam) {
  return http.get<PageResult<Demo03Student>>(`${BASE}/page`, params)
}

/** 获取学生详情 */
export function getDemo03Student(id: number) {
  return http.get<Demo03Student>(`${BASE}/get?id=${id}`)
}

/** 创建学生（含子表） */
export function createDemo03Student(data: Demo03Student) {
  return http.post<number>(`${BASE}/create`, data)
}

/** 更新学生（含子表） */
export function updateDemo03Student(data: Demo03Student) {
  return http.put<boolean>(`${BASE}/update`, data)
}

/** 删除学生 */
export function deleteDemo03Student(id: number) {
  return http.delete<boolean>(`${BASE}/delete?id=${id}`)
}

/** 获取某学生的课程列表 */
export function getDemo03CourseListByStudentId(studentId: number) {
  return http.get<Demo03Course[]>(`${BASE}/demo03-course/list-by-student-id?studentId=${studentId}`)
}

/** 获取某学生的班级 */
export function getDemo03GradeByStudentId(studentId: number) {
  return http.get<Demo03Grade>(`${BASE}/demo03-grade/get-by-student-id?studentId=${studentId}`)
}
