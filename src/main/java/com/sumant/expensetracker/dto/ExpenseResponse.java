package com.sumant.expensetracker.dto;

import com.sumant.expensetracker.entity.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(Integer id, String title, BigDecimal amount,
                              String category, LocalDate expenseDate) {
    public static ExpenseResponse from(Expense e) {
        return new ExpenseResponse(e.getId(), e.getTitle(), e.getAmount(),
                e.getCategory(), e.getExpenseDate());
    }
}
