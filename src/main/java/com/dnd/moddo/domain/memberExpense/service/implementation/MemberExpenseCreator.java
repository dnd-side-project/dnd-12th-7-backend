package com.dnd.moddo.domain.memberExpense.service.implementation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.repotiroy.MemberExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberExpenseCreator {
	private final MemberExpenseRepository memberExpenseRepository;

	public MemberExpense create(Long expenseId, GroupMember groupMember, MemberExpenseRequest memberExpenseRequest) {
		MemberExpense memberExpense = memberExpenseRequest.toEntity(expenseId, groupMember);
		return memberExpenseRepository.save(memberExpense);
	}
}
