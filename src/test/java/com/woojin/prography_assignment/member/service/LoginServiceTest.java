package com.woojin.prography_assignment.member.service;

import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.UnauthorizedException;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.domain.MemberRole;
import com.woojin.prography_assignment.member.domain.MemberStatus;
import com.woojin.prography_assignment.member.dto.request.LoginRequest;
import com.woojin.prography_assignment.member.dto.response.MemberResponse;
import com.woojin.prography_assignment.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 단위 테스트")
class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private static final String LOGIN_ID = "woojin";
    private static final String PASSWORD = "password123";
    private static final String ENCODED_PASSWORD = "$2a$12$encodedPassword";
    private static final String NAME = "김우진";
    private static final String PHONE = "010-1234-5678";

    @Nested
    @DisplayName("로그인 성공")
    class Success {

        @Test
        @DisplayName("일반 회원 로그인")
        void member() {
            // Given
            LoginRequest request = new LoginRequest(LOGIN_ID, PASSWORD);
            Member member = Member.createMember(LOGIN_ID, ENCODED_PASSWORD, NAME, PHONE);

            given(memberRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(member));
            given(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).willReturn(true);

            // When
            MemberResponse response = loginService.login(request);

            // Then
            assertThat(response.loginId()).isEqualTo(LOGIN_ID);
            assertThat(response.name()).isEqualTo(NAME);
            assertThat(response.role()).isEqualTo(MemberRole.MEMBER);
            assertThat(response.status()).isEqualTo(MemberStatus.ACTIVE);
        }

        @Test
        @DisplayName("관리자 로그인")
        void admin() {
            // Given
            LoginRequest request = new LoginRequest("admin", "admin1234");
            Member admin = Member.createAdmin("admin", ENCODED_PASSWORD, "관리자", "010-0000-0000");

            given(memberRepository.findByLoginId("admin")).willReturn(Optional.of(admin));
            given(passwordEncoder.matches("admin1234", ENCODED_PASSWORD)).willReturn(true);

            // When
            MemberResponse response = loginService.login(request);

            // Then
            assertThat(response.loginId()).isEqualTo("admin");
            assertThat(response.role()).isEqualTo(MemberRole.ADMIN);
        }
    }

    @Nested
    @DisplayName("로그인 실패")
    class Failure {

        @Test
        @DisplayName("존재하지 않는 회원")
        void memberNotFound() {
            // Given
            LoginRequest request = new LoginRequest("unknown", PASSWORD);
            given(memberRepository.findByLoginId("unknown")).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loginService.login(request))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining(ErrorCode.LOGIN_FAILED.getMessage());

            then(passwordEncoder).should(never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("비밀번호 불일치")
        void wrongPassword() {
            // Given
            LoginRequest request = new LoginRequest(LOGIN_ID, "wrongPass");
            Member member = Member.createMember(LOGIN_ID, ENCODED_PASSWORD, NAME, PHONE);

            given(memberRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(member));
            given(passwordEncoder.matches("wrongPass", ENCODED_PASSWORD)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> loginService.login(request))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining(ErrorCode.LOGIN_FAILED.getMessage());
        }

        @Test
        @DisplayName("탈퇴한 회원")
        void withdrawnMember() {
            // Given
            LoginRequest request = new LoginRequest(LOGIN_ID, PASSWORD);
            Member member = Member.createMember(LOGIN_ID, ENCODED_PASSWORD, NAME, PHONE);
            member.withdraw();

            given(memberRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(member));
            given(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).willReturn(true);

            // When & Then
            assertThatThrownBy(() -> loginService.login(request))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining(ErrorCode.MEMBER_WITHDRAWN.getMessage());
        }
    }

    @Nested
    @DisplayName("검증 순서")
    class ValidationOrder {

        @Test
        @DisplayName("회원 없으면 비밀번호 검증 안함")
        void skipPasswordValidation() {
            // Given
            LoginRequest request = new LoginRequest("unknown", PASSWORD);
            given(memberRepository.findByLoginId("unknown")).willReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> loginService.login(request))
                    .isInstanceOf(UnauthorizedException.class);

            then(passwordEncoder).should(never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("비밀번호 틀리면 상태 검증 안함")
        void skipStatusValidation() {
            // Given
            LoginRequest request = new LoginRequest(LOGIN_ID, "wrongPass");
            Member member = Member.createMember(LOGIN_ID, ENCODED_PASSWORD, NAME, PHONE);

            given(memberRepository.findByLoginId(LOGIN_ID)).willReturn(Optional.of(member));
            given(passwordEncoder.matches("wrongPass", ENCODED_PASSWORD)).willReturn(false);

            // When & Then
            assertThatThrownBy(() -> loginService.login(request))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessageContaining(ErrorCode.LOGIN_FAILED.getMessage());
        }
    }
}