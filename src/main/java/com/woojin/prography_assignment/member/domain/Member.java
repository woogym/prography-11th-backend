package com.woojin.prography_assignment.member.domain;


import com.woojin.prography_assignment.common.BaseTimeEntity;
import com.woojin.prography_assignment.common.exception.ErrorCode;
import com.woojin.prography_assignment.common.exception.model.BusinessException;
import com.woojin.prography_assignment.common.exception.model.InvalidInputException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import javax.sound.midi.MetaMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "phone_number", nullable = false, length = 100)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MemberStatus status;

    private Member(String loginId,
                   String encodedPassword,
                   String name,
                   String phone,
                   MemberRole role) {
        validateLoginId(loginId);
        validatePassword(encodedPassword);
        validateName(name);
        validatePhone(phone);

        this.loginId = loginId;
        this.password = encodedPassword;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.status = MemberStatus.ACTIVE;
    }

    public static Member createMember(String loginId,
                                      String encodedPassword,
                                      String name,
                                      String phone) {
        return new Member(loginId, encodedPassword, name, phone, MemberRole.MEMBER);
    }

    public static Member createAdmin(String loginId,
                                     String encodedPassword,
                                     String name,
                                     String phone) {
        return new Member(loginId, encodedPassword, name, phone, MemberRole.ADMIN);
    }

    public void updateInfo(String name, String phone) {
        if (name != null) {
            validateName(name);
            this.name = name;
        }
        if (phone != null) {
            validatePhone(phone);
            this.phone = phone;
        }
    }

    public void changePassword(String encodedPassword) {
        validatePassword(encodedPassword);
        this.password = encodedPassword;
    }

    public void withdraw() {
        if (isWithdraw()) {
            throw new BusinessException(ErrorCode.MEMBER_ALREADY_WITHDRAWN,
                    ErrorCode.MEMBER_ALREADY_WITHDRAWN.getMessage());
        }

        this.status = MemberStatus.WITHDRAWN;
    }

    public boolean isWithdraw() {
        return this.status == MemberStatus.WITHDRAWN;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }

    public boolean isAdmin() {
        return this.role == MemberRole.ADMIN;
    }

    private void validateLoginId(String loginId) {
        if (loginId == null || loginId.isBlank()) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "로그인 ID는 필수입니다.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "비밀번호는 필수입니다.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "이름은 필수입니다.");
        }
    }

    private void validatePhone(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new InvalidInputException(ErrorCode.INVALID_INPUT, "전화번호는 필수입니다.");
        }
    }
}
