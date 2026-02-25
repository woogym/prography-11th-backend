package com.woojin.prography_assignment.attendance.service;

import com.woojin.prography_assignment.attendance.domain.AttendanceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PenaltyCalculator 단위 테스트")
class PenaltyCalculatorTest {

    private final PenaltyCalculator calculator = new PenaltyCalculator();

    @Nested
    @DisplayName("페널티 없음")
    class NoPenalty {

        @Test
        @DisplayName("출석")
        void present() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.PRESENT, null);

            // Then
            assertThat(penalty).isEqualTo(0);
        }

        @Test
        @DisplayName("사유 출석")
        void excused() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.EXCUSED, null);

            // Then
            assertThat(penalty).isEqualTo(0);
        }

        @Test
        @DisplayName("지각 0분")
        void lateZeroMinute() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.LATE, 0);

            // Then
            assertThat(penalty).isEqualTo(0);
        }

        @Test
        @DisplayName("지각이지만 null")
        void lateNull() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.LATE, null);

            // Then
            assertThat(penalty).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("결석 페널티")
    class AbsentPenalty {

        @Test
        @DisplayName("10,000원")
        void absent() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.ABSENT, null);

            // Then
            assertThat(penalty).isEqualTo(10_000);
        }
    }

    @Nested
    @DisplayName("지각 페널티")
    class LatePenalty {

        @Test
        @DisplayName("1분 = 500원")
        void oneMinute() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.LATE, 1);

            // Then
            assertThat(penalty).isEqualTo(500);
        }

        @Test
        @DisplayName("10분 = 5,000원")
        void tenMinutes() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.LATE, 10);

            // Then
            assertThat(penalty).isEqualTo(5_000);
        }

        @Test
        @DisplayName("19분 = 9,500원")
        void nineteenMinutes() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.LATE, 19);

            // Then
            assertThat(penalty).isEqualTo(9_500);
        }

        @Test
        @DisplayName("20분 = 10,000원 (최대)")
        void twentyMinutes() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.LATE, 20);

            // Then
            assertThat(penalty).isEqualTo(10_000);
        }

        @Test
        @DisplayName("30분 = 10,000원 (최대 초과)")
        void thirtyMinutes() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.LATE, 30);

            // Then
            assertThat(penalty).isEqualTo(10_000);
        }

        @Test
        @DisplayName("100분 = 10,000원 (최대 초과)")
        void hundredMinutes() {
            // When
            int penalty = calculator.calculate(AttendanceStatus.LATE, 100);

            // Then
            assertThat(penalty).isEqualTo(10_000);
        }
    }
}