package com.sumant.expensetracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank String title,
        @NotNull @Positive BigDecimal amount,
        String category,
        @NotNull LocalDate expenseDate) {}
