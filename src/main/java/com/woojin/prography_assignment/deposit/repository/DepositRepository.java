package com.woojin.prography_assignment.deposit.repository;

import com.woojin.prography_assignment.deposit.domain.DepositHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<DepositHistory, Long> {
}
