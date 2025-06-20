package com.dnd.moddo.domain.group.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dnd.moddo.domain.group.dto.response.GroupDetailResponse;
import com.dnd.moddo.domain.group.dto.response.GroupHeaderResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.exception.GroupNotFoundException;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.group.service.implementation.GroupValidator;
import com.dnd.moddo.domain.groupMember.entity.GroupMember;
import com.dnd.moddo.domain.groupMember.entity.type.ExpenseRole;
import com.dnd.moddo.global.support.GroupTestFactory;

@ExtendWith(MockitoExtension.class)
class QueryGroupServiceTest {

	@InjectMocks
	private QueryGroupService queryGroupService;

	@Mock
	private GroupReader groupReader;

	@Mock
	private GroupValidator groupValidator;

	private Group group;
	private GroupMember groupMember;

	@BeforeEach
	void setUp() {
		group = GroupTestFactory.createDefault();
		groupMember = new GroupMember("김완숙", 1, group, false, ExpenseRole.MANAGER);

		setField(group, "id", 1L);
	}

	@Test
	@DisplayName("그룹 상세 정보를 정상적으로 조회할 수 있다.")
	void FindOne_Success() {
		// Given
		when(groupReader.read(anyLong())).thenReturn(group);
		when(groupReader.findByGroup(group.getId())).thenReturn(List.of(groupMember));
		doNothing().when(groupValidator).checkGroupAuthor(group, 1L);

		// When
		GroupDetailResponse response = queryGroupService.findOne(group.getId(), 1L);

		// Then
		assertThat(response).isNotNull();
		assertThat(response.id()).isEqualTo(group.getId());
		assertThat(response.groupName()).isEqualTo(group.getName());
		assertThat(response.members()).hasSize(1);
		assertThat(response.members().get(0).name()).isEqualTo(groupMember.getName());

		verify(groupReader, times(1)).read(1L);
		verify(groupReader, times(1)).findByGroup(group.getId());
		verify(groupValidator, times(1)).checkGroupAuthor(group, 1L);
	}

	@Test
	@DisplayName("그룹 작성자가 아닐 경우 예외가 발생한다.")
	void FindOne_Failure_WhenNotGroupAuthor() {
		// Given
		when(groupReader.read(anyLong())).thenReturn(group);
		doThrow(new RuntimeException("Not an author")).when(groupValidator).checkGroupAuthor(group, 2L);

		// When & Then
		assertThatThrownBy(() -> queryGroupService.findOne(1L, 2L))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Not an author");

		verify(groupReader, times(1)).read(1L);
		verify(groupValidator, times(1)).checkGroupAuthor(group, 2L);
	}

	@Test
	@DisplayName("그룹을 찾을 수 없을 경우 예외가 발생한다.")
	void FindOne_Failure_WhenGroupNotFound() {
		// Given
		when(groupReader.read(anyLong())).thenThrow(new RuntimeException("Group not found"));

		// When & Then
		assertThatThrownBy(() -> queryGroupService.findOne(1L, 1L))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Group not found");

		verify(groupReader, times(1)).read(1L);
	}

	@Test
	@DisplayName("그룹 헤더를 정상적으로 조회할 수 있다.")
	void FindByGroupHeader_Success() {
		// Given
		GroupHeaderResponse expectedResponse = new GroupHeaderResponse(group.getName(), 1000L,
			LocalDateTime.now().plusDays(1), group.getBank(), group.getAccountNumber());
		when(groupReader.findByHeader(group.getId())).thenReturn(expectedResponse);

		// When
		GroupHeaderResponse response = queryGroupService.findByGroupHeader(group.getId());

		// Then
		assertThat(response).isNotNull();
		assertThat(response.groupName()).isEqualTo(group.getName());
		assertThat(response.bank()).isEqualTo(group.getBank());
		assertThat(response.accountNumber()).isEqualTo(group.getAccountNumber());

		verify(groupReader, times(1)).findByHeader(group.getId());
	}

	@Test
	@DisplayName("그룹 헤더를 찾을 수 없을 경우 예외가 발생한다.")
	void FindByGroupHeader_Failure_WhenHeaderNotFound() {
		// Given
		when(groupReader.findByHeader(anyLong())).thenThrow(new RuntimeException("Header not found"));

		// When & Then
		assertThatThrownBy(() -> queryGroupService.findByGroupHeader(1L))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("Header not found");

		verify(groupReader, times(1)).findByHeader(1L);
	}

	@DisplayName("group code가 유효할 때 group Id를 찾을 수 있다.")
	@Test
	void FindByGroupId_Success() {
		//given
		Long expected = 1L;
		when(groupReader.findIdByGroupCode(anyString())).thenReturn(expected);
		//when
		Long result = queryGroupService.findIdByCode("code");
		//then
		assertThat(result).isEqualTo(expected);
		verify(groupReader, times(1)).findIdByGroupCode(anyString());
	}

	@DisplayName("group code가 존재하지 않을때 예외가 발생한다..")
	@Test
	void FindByGroupId_ThrowException_WhenCodeNotFound() {
		//given
		when(groupReader.findIdByGroupCode(anyString())).thenThrow(new GroupNotFoundException("code"));
		//when & then
		assertThatThrownBy(() -> queryGroupService.findIdByCode("code"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("code");

		verify(groupReader, times(1)).findIdByGroupCode(anyString());
	}

	@DisplayName("group code가 유효할 때 group Id를 찾을 수 있다.")
	@Test
	void FindByGroupIdNoCache_Success() {
		//given
		Long expected = 1L;
		when(groupReader.findIdByGroupCode(anyString())).thenReturn(expected);
		//when
		Long result = queryGroupService.findIdByCodeNoCache("code");
		//then
		assertThat(result).isEqualTo(expected);
		verify(groupReader, times(1)).findIdByGroupCode(anyString());
	}

	@DisplayName("group code가 존재하지 않을때 예외가 발생한다..")
	@Test
	void FindByGroupIdNoCache_ThrowException_WhenCodeNotFound() {
		//given
		when(groupReader.findIdByGroupCode(anyString())).thenThrow(new GroupNotFoundException("code"));
		//when & then
		assertThatThrownBy(() -> queryGroupService.findIdByCodeNoCache("code"))
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("code");

		verify(groupReader, times(1)).findIdByGroupCode(anyString());
	}
}
