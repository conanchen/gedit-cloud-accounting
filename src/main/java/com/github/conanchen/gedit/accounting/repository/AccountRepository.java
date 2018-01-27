package com.github.conanchen.gedit.accounting.repository;

import com.github.conanchen.gedit.accounting.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author hai
 * @description 账户表
 * @email hilin2333@gmail.com
 * @date 23/01/2018 11:13 AM
 */
public interface AccountRepository extends JpaRepository<Account,String> {
    List<Account> findByUserUuid(String userUuid);

    List<Account> findByUserUuidAndUpdatedDateAfter(String userUuid,Date date);

    List<Account> findAllByUserUuid(String userUuid);

    Optional<Account> findByAccountTypeAndUserUuid(String accountType,String userUuid);
}
