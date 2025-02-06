package com.dnd.moddo.domain.expense.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.moddo.domain.expense.entity.Expense;
import com.dnd.moddo.domain.expense.exception.ExpenseNotFoundException;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
	List<Expense> findByGroupId(Long groupId);

	default Expense getById(Long id) {
		return findById(id)
			.orElseThrow(() -> new ExpenseNotFoundException(id));
	}
}
