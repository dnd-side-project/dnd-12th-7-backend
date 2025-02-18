package com.dnd.moddo.domain.group.service;

import com.dnd.moddo.domain.group.dto.request.GroupAccountRequest;
import com.dnd.moddo.domain.group.dto.request.GroupPasswordRequest;
import com.dnd.moddo.domain.group.dto.request.GroupRequest;
import com.dnd.moddo.domain.group.dto.response.GroupPasswordResponse;
import com.dnd.moddo.domain.group.dto.response.GroupResponse;
import com.dnd.moddo.domain.group.entity.Group;
import com.dnd.moddo.domain.group.service.implementation.GroupCreator;
import com.dnd.moddo.domain.group.service.implementation.GroupReader;
import com.dnd.moddo.domain.group.service.implementation.GroupUpdater;
import com.dnd.moddo.domain.group.service.implementation.GroupValidator;
import com.dnd.moddo.global.jwt.dto.GroupTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandGroupServiceTest {

    @Mock
    private GroupCreator groupCreator;

    @Mock
    private GroupUpdater groupUpdater; // 추가

    @Mock
    private GroupReader groupReader;

    @Mock
    private GroupValidator groupValidator;

    @InjectMocks
    private CommandGroupService commandGroupService;

    private GroupRequest groupRequest;
    private GroupResponse groupResponse;
    private GroupAccountRequest groupAccountRequest;
    private Group group;
    private GroupTokenResponse expectedResponse;

    @BeforeEach
    void setUp() {
        groupRequest = new GroupRequest("GroupName", "password123", LocalDateTime.now());
        groupResponse = new GroupResponse(1L, 1L, LocalDateTime.now(), LocalDateTime.now().minusDays(1), "bank", "1234-1234");
        groupAccountRequest = new GroupAccountRequest("newBank", "5678-5678");
        expectedResponse = new GroupTokenResponse("group-token");

        group = mock(Group.class);
    }

    @Test
    @DisplayName("그룹을 생성할 수 있다.")
    void createGroup() {
        // Given
        when(groupCreator.createGroup(any(GroupRequest.class), anyLong())).thenReturn(expectedResponse);

        // When
        GroupTokenResponse response = commandGroupService.createGroup(groupRequest, 1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.groupToken()).isEqualTo("group-token");

        verify(groupCreator, times(1)).createGroup(any(GroupRequest.class), anyLong());
    }

    @Test
    @DisplayName("그룹의 계좌 정보를 업데이트할 수 있다.")
    void updateGroupAccount() {
        // Given
        when(groupReader.read(anyLong())).thenReturn(group);
        when(groupUpdater.updateAccount(any(GroupAccountRequest.class), anyLong())).thenReturn(group);
        doNothing().when(groupValidator).checkGroupAuthor(any(Group.class), anyLong());

        // When
        GroupResponse result = commandGroupService.updateAccount(groupAccountRequest, group.getWriter(), group.getId());

        // Then
        assertThat(result).isNotNull();
        verify(groupReader, times(1)).read(anyLong());
        verify(groupValidator, times(1)).checkGroupAuthor(any(Group.class), anyLong());
        verify(groupUpdater, times(1)).updateAccount(any(GroupAccountRequest.class), anyLong());
    }

    @Test
    @DisplayName("올바른 비밀번호를 입력하면 확인 메시지를 반환한다.")
    void VerifyPassword_Success() {
        // Given
        GroupPasswordRequest request = new GroupPasswordRequest("correctPassword");
        GroupPasswordResponse expectedResponse = GroupPasswordResponse.from("확인되었습니다.");

        when(groupReader.read(group.getId())).thenReturn(group);
        doNothing().when(groupValidator).checkGroupAuthor(group, 1L);
        when(groupValidator.checkGroupPassword(request, group.getPassword())).thenReturn(expectedResponse);

        // When
        GroupPasswordResponse response = commandGroupService.isPasswordMatch(group.getId(), 1L, request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo("확인되었습니다.");

        verify(groupReader, times(1)).read(group.getId());
        verify(groupValidator, times(1)).checkGroupAuthor(group, 1L);
        verify(groupValidator, times(1)).checkGroupPassword(request, group.getPassword());
    }

    @Test
    @DisplayName("잘못된 비밀번호를 입력하면 예외가 발생한다.")
    void VerifyPassword_Fail_WrongPassword() {
        // Given
        GroupPasswordRequest request = new GroupPasswordRequest("wrongPassword");
        String storedPassword = "correctPassword";

        when(groupReader.read(group.getId())).thenReturn(group);
        doNothing().when(groupValidator).checkGroupAuthor(group, 1L);
        when(group.getPassword()).thenReturn(storedPassword);

        doThrow(new RuntimeException("비밀번호가 일치하지 않습니다."))
                .when(groupValidator).checkGroupPassword(request, storedPassword);

        // When & Then
        assertThatThrownBy(() -> commandGroupService.isPasswordMatch(group.getId(), 1L, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다.");

        verify(groupReader, times(1)).read(group.getId());
        verify(groupValidator, times(1)).checkGroupAuthor(group, 1L);
        verify(groupValidator, times(1)).checkGroupPassword(request, storedPassword);
    }
}
