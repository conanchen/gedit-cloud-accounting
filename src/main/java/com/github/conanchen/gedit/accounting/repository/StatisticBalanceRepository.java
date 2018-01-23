package com.github.conanchen.gedit.accounting.repository;

import com.github.conanchen.gedit.accounting.model.Posting;
import com.github.conanchen.gedit.accounting.model.StatisticBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author hai
 * @description 余额统计表
 * @email hilin2333@gmail.com
 * @date 23/01/2018 11:13 AM
 */
public interface StatisticBalanceRepository extends JpaRepository<StatisticBalance,String> {
    List<StatisticBalance> findByAccountTypeInAndUserUuidAndStatisticDate(List<String> accountTypes, String userUuid, Date date);

    StatisticBalance findByAccountTypeAndStatisticDate(String accountType, Date date);
}
