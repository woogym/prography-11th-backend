package com.woojin.prography_assignment.member.service;

import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.UnauthorizedException;
import com.woojin.prography_assignment.member.domain.Member;
import com.woojin.prography_assignment.member.dto.request.LoginRequest;
import com.woojin.prography_assignment.member.dto.response.MemberResponse;
import com.woojin.prography_assignment.member.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public MemberResponse login(LoginRequest request) {

        Member member = findByLoginId(request.loginId());

        validatePassword(request.password(), member.getPassword());
        validateMemberStatus(member);

        return MemberResponse.from(member);
    }

    private void validateMemberStatus(Member member) {
        if (member.isWithdraw()) {
            throw new UnauthorizedException(ErrorCode.MEMBER_WITHDRAWN,
                    ErrorCode.MEMBER_WITHDRAWN.getMessage());
        }
    }

    private void validatePassword(String requestPassword, String password) {
        if (!passwordEncoder.matches(requestPassword, password)) {
            throw new UnauthorizedException(ErrorCode.LOGIN_FAILED,
                    ErrorCode.LOGIN_FAILED.getMessage());
        }
    }

    private Member findByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.LOGIN_FAILED,
                        ErrorCode.LOGIN_FAILED.getMessage()));
    }


}
