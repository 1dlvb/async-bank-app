package com.dlvb.asyncbankapp.repository;

import com.dlvb.asyncbankapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для манипуляций с {@link Transaction}.
 * @author Matushkin Anton
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
