package com.dnd.moddo.domain.group.service.implementation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.expense.repository.ExpenseRepository;
import com.dnd.moddo.domain.group.dto.response.GroupHeaderResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.exception.GroupNotFoundException;
import com.dnd.moddo.domain.group.repository.GroupRepository;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.repository.GroupMemberRepository;

@ExtendWith(MockitoExtension.class)
class GroupReaderTest {

	@Mock
	private GroupRepository groupRepository;

	@Mock
	private ExpenseRepository expenseRepository;

	@Mock
	private GroupMemberRepository groupMemberRepository;

	@InjectMocks
	private GroupReader groupReader;

	@Test
	@DisplayName("그룹 ID를 통해 그룹을 정상적으로 조회할 수 있다.")
	void readGroup_Success() {
		// Given
		Long groupId = 1L;
		Group mockGroup = mock(Group.class);

		when(groupRepository.getById(anyLong())).thenReturn(mockGroup);

		// When
		Group result = groupReader.read(groupId);

		// Then
		assertThat(result).isNotNull();
		verify(groupRepository, times(1)).getById(groupId);
	}

	@Test
	@DisplayName("그룹을 통해 그룹 멤버 목록을 정상적으로 조회할 수 있다.")
	void findByGroup_Success() {
		// Given
		Group mockGroup = mock(Group.class);
		when(mockGroup.getId()).thenReturn(1L);
		List<GroupMember> mockMembers = List.of(mock(GroupMember.class), mock(GroupMember.class));

		when(groupMemberRepository.findByGroupId(anyLong())).thenReturn(mockMembers);

		// When
		List<GroupMember> result = groupReader.findByGroup(mockGroup.getId());

		// Then
		assertThat(result).hasSize(2);
		verify(groupMemberRepository, times(1)).findByGroupId(mockGroup.getId());
	}

	@Test
	@DisplayName("그룹 ID를 통해 그룹 헤더 정보를 정상적으로 조회할 수 있다.")
	void findByHeader_Success() {
		// Given
		Long groupId = 1L;
		Group mockGroup = mock(Group.class);
		when(mockGroup.getName()).thenReturn("모임 이름");
		when(mockGroup.getBank()).thenReturn("은행");
		when(mockGroup.getAccountNumber()).thenReturn("1234-1234");
		when(groupRepository.getById(anyLong())).thenReturn(mockGroup);

		Long totalAmount = 1000L;
		when(expenseRepository.sumAmountByGroup(any(Group.class))).thenReturn(totalAmount);

		// When
		GroupHeaderResponse result = groupReader.findByHeader(groupId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.groupName()).isEqualTo("모임 이름");
		assertThat(result.totalAmount()).isEqualTo(1000L);
		assertThat(result.bank()).isEqualTo("은행");
		assertThat(result.accountNumber()).isEqualTo("1234-1234");
		verify(groupRepository, times(1)).getById(groupId);
		verify(expenseRepository, times(1)).sumAmountByGroup(mockGroup);
	}

	@DisplayName("group code로 group Id를 찾을 수 있다.")
	@Test
	void whenValidGroupCode_thenReturnsGroupId() {
		//given
		Long expected = 1L;
		when(groupRepository.getIdByCode(anyString())).thenReturn(expected);
		//when
		Long result = groupReader.findIdByGroupCode("code");
		//then
		assertThat(result).isEqualTo(expected);
		verify(groupRepository, times(1)).getIdByCode(anyString());
	}

	@DisplayName("group code로 group id를 찾을 수 없을때 예외가 발생한다.")
	@Test
	void whenInvalidGroupCode_thenThrowsException() {
		//given
		when(groupRepository.getIdByCode(anyString())).thenThrow(new GroupNotFoundException("code"));
		//when & then
		assertThatThrownBy(() -> groupReader.findIdByGroupCode("code"))
			.isInstanceOf(GroupNotFoundException.class)
			.hasMessageContaining("code");

		verify(groupRepository, times(1)).getIdByCode(anyString());
	}
}
