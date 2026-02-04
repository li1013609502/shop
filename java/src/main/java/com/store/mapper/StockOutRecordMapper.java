package com.store.mapper;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.store.entity.StockOutRecordEntity;

@Mapper
public interface StockOutRecordMapper extends BaseMapper<StockOutRecordEntity> {
  @Select({
      "SELECT COALESCE(SUM(total_amount), 0)",
      "FROM stock_out_record",
      "WHERE DATE(created_at) = CURDATE()"
  })
  BigDecimal selectTodayRevenue();

  @Select({
      "SELECT COALESCE(SUM(total_amount), 0)",
      "FROM stock_out_record",
      "WHERE DATE_FORMAT(created_at, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')"
  })
  BigDecimal selectMonthRevenue();

  @Select({
      "SELECT COALESCE(SUM(total_amount), 0)",
      "FROM stock_out_record",
      "WHERE (#{beginDate} IS NULL OR created_at >= #{beginDate})",
      "AND (#{endDate} IS NULL OR created_at <= #{endDate})"
  })
  BigDecimal selectSalesAmount(@Param("beginDate") String beginDate, @Param("endDate") String endDate);

  @Select({
      "SELECT COALESCE(SUM(total_profit), 0)",
      "FROM stock_out_record",
      "WHERE (#{beginDate} IS NULL OR created_at >= #{beginDate})",
      "AND (#{endDate} IS NULL OR created_at <= #{endDate})"
  })
  BigDecimal selectProfitAmount(@Param("beginDate") String beginDate, @Param("endDate") String endDate);
}
