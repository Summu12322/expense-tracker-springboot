package com.sumant.expensetracker.dto;

import java.math.BigDecimal;

public record DashboardSummaryResponse(Long totalCount, BigDecimal totalAmount,
                                       BigDecimal highestExpense, Double averageExpense) {}
