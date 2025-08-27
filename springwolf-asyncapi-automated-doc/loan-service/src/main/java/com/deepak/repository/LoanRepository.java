package com.deepak.repository;

import com.deepak.entity.LoanDO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<LoanDO, Long> {
}
