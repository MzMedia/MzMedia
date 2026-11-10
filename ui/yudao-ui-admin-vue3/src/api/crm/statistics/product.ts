import request from '@/config/axios'

export interface CrmStatisticsProductSalesRespVO {
  categoryId: number
  categoryName: string
  productId: number
  productName: string
  contractId: number
  contractNo: string
  contractName: string
  ownerUserId: number
  ownerUserName: string
  customerId: number
  customerName: string
  productPrice: number
  productCount: number
  productTotalPrice: number
}

export interface CrmStatisticsProductCategoryRespVO {
  categoryId: number
  categoryName: string
  contractCount: number
  productCount: number
  productTotalPrice: number
}

// 产品分析 API
export const StatisticsProductApi = {
  // 获得产品销售情况统计
  getProductSalesList: (params: any) => {
    return request.get({
      url: '/crm/statistics-product/get-product-sales-list',
      params
    })
  },
  // 获得产品分类销售分析
  getProductCategorySummary: (params: any) => {
    return request.get({
      url: '/crm/statistics-product/get-product-category-summary',
      params
    })
  }
}
