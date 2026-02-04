package com.store.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.store.common.Result;
import com.store.service.DashboardService;
import com.store.vo.RevenueVo;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @GetMapping("/revenue")
  public Result<RevenueVo> revenue() {
    return Result.ok(dashboardService.getRevenue());
  }
}
