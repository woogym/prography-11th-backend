package com.woojin.prography_assignment.member.dto;

import com.woojin.prography_assignment.member.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MemberCreateRequest(

        @NotBlank(message = "로그인 ID는 필수입니다.")
        String loginId,
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,
        @NotBlank(message = "이름은 필수입니다.")
        String name,
        @Pattern(
                regexp = "^01[0-9]-\\d{3,4}-\\d{4}$",
                message = "전화번호 형식이 올바르지 않습니다."
        )
        @NotBlank(message = "전화번호는 필수입니다.")
        String phone,
        @NotBlank(message = "기수 Id는 필수입니다.")
        Long cohortId,
        Long partId,
        Long teamId
) {

}
