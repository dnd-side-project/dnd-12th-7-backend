package com.dnd.moddo.domain.memberExpense.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.domain.memberExpense.dto.request.MemberExpenseRequest;
import com.dnd.moddo.domain.memberExpense.entity.MemberExpense;
import com.dnd.moddo.domain.memberExpense.repotiroy.MemberExpenseRepository;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class MemberExpenseCreatorTest {
	@Mock
	private MemberExpenseRepository memberExpenseRepository;
	@InjectMocks
	private MemberExpenseCreator memberExpenseCreator;

	private Group mockGroup;
	private GroupMember mockGroupMember;
	private MemberExpenseRequest mockMemberExpenseRequest;

	@BeforeEach
	void setUp() {
		mockGroup = GroupTestFactory.createDefault();

		mockGroupMember = GroupMember.builder()
			.name("박완수")
			.group(mockGroup)
			.role(ExpenseRole.MANAGER)
			.isPaid(true)
			.build();

		mockMemberExpenseRequest = mock(MemberExpenseRequest.class);
	}

	@DisplayName("지출내역, 참여자 정보가 모두 유효할 때 참여자 지출 내역 생성에 성공한다.")
	@Test
	void createMemberExpenseSuccess() {
		//given
		Long expenseId = 1L;
		MemberExpense mockMemberExpense = new MemberExpense(expenseId, mockGroupMember, 10000L);

		when(mockMemberExpenseRequest.toEntity(expenseId, mockGroupMember)).thenReturn(mockMemberExpense);
		when(memberExpenseRepository.save(any(MemberExpense.class))).thenReturn(mockMemberExpense);

		//when
		MemberExpense result = memberExpenseCreator.create(expenseId, mockGroupMember, mockMemberExpenseRequest);

		//then
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo(mockMemberExpense);
		assertThat(result.getAmount()).isEqualTo(10000L);
		assertThat(result.getGroupMember()).isEqualTo(mockGroupMember);

		verify(memberExpenseRepository, times(1)).save(any(MemberExpense.class));
	}
}
