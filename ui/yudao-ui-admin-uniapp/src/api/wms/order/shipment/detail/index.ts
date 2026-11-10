/** WMS 出库单明细 */
export interface ShipmentOrderDetail {
  id?: number
  orderId?: number
  itemId?: number
  itemCode?: string
  itemName?: string
  unit?: string
  skuId?: number
  skuCode?: string
  skuName?: string
  warehouseId?: number
  warehouseName?: string
  quantity?: number
  price?: number
  totalPrice?: number
  createTime?: Date
}
