package com.dlvb.asyncbankapp.repository;

import com.dlvb.asyncbankapp.model.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepositRepository extends JpaRepository<Deposit, String> {
}
