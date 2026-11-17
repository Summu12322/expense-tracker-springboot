package com.sumant.expensetracker.repository;

import com.sumant.expensetracker.dto.CategoryTotalResponse;
import com.sumant.expensetracker.dto.DashboardSummaryResponse;
import com.sumant.expensetracker.entity.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {

    List<Expense> findByUserIdOrderByExpenseDateDesc(Integer userId);

    Optional<Expense> findByIdAndUserId(Integer id, Integer userId);

    List<Expense> findByUserIdAndExpenseDateBetweenOrderByExpenseDateAsc(
            Integer userId, LocalDate from, LocalDate to);

    @Query("""
           SELECT e FROM Expense e
           WHERE e.user.id = :userId
             AND (LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(e.category) LIKE LOWER(CONCAT('%', :keyword, '%')))
           ORDER BY e.expenseDate DESC
           """)
    List<Expense> search(@Param("userId") Integer userId, @Param("keyword") String keyword);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.user.id = :userId")
    BigDecimal getTotalSpending(@Param("userId") Integer userId);

    @Query("""
           SELECT new com.sumant.expensetracker.dto.CategoryTotalResponse(e.category, SUM(e.amount))
           FROM Expense e WHERE e.user.id = :userId GROUP BY e.category
           """)
    List<CategoryTotalResponse> getCategoryWiseSpending(@Param("userId") Integer userId);

    @Query("""
           SELECT new com.sumant.expensetracker.dto.DashboardSummaryResponse(
                  COUNT(e), SUM(e.amount), MAX(e.amount), AVG(e.amount))
           FROM Expense e WHERE e.user.id = :userId
           """)
    DashboardSummaryResponse getDashboardSummary(@Param("userId") Integer userId);
}
