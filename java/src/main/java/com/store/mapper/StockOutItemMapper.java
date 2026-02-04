package com.store.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.store.entity.StockOutItemEntity;
import com.store.vo.StockOutItemVo;

@Mapper
public interface StockOutItemMapper extends BaseMapper<StockOutItemEntity> {
  @Select({
      "SELECT r.out_no AS outNo, r.created_at AS createdAt,",
      "u.name AS operatorName,",
      "i.product_name AS productName, i.barcode AS barcode, i.qty AS qty,",
      "i.sale_price AS salePrice, i.cost_price AS costPrice, i.profit AS profit",
      "FROM stock_out_item i",
      "JOIN stock_out_record r ON r.id = i.record_id",
      "LEFT JOIN sys_user u ON u.id = r.operator_id",
      "WHERE (#{beginDate} IS NULL OR r.created_at >= #{beginDate})",
      "AND (#{endDate} IS NULL OR r.created_at <= #{endDate})",
      "ORDER BY r.created_at DESC",
      "LIMIT #{limit} OFFSET #{offset}"
  })
  List<StockOutItemVo> selectPagedItems(
      @Param("beginDate") String beginDate,
      @Param("endDate") String endDate,
      @Param("limit") long limit,
      @Param("offset") long offset);

  @Select({
      "SELECT COUNT(1)",
      "FROM stock_out_item i",
      "JOIN stock_out_record r ON r.id = i.record_id",
      "WHERE (#{beginDate} IS NULL OR r.created_at >= #{beginDate})",
      "AND (#{endDate} IS NULL OR r.created_at <= #{endDate})"
  })
  long countItems(@Param("beginDate") String beginDate, @Param("endDate") String endDate);
}
