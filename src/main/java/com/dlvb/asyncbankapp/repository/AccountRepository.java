package com.dlvb.asyncbankapp.repository;

import com.dlvb.asyncbankapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для манипуляций с {@link Account}.
 * @author Matushkin Anton
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
}
