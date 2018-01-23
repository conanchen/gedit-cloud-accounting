package com.github.conanchen.gedit.accounting.grpc;

import com.github.conanchen.gedit.accounting.account.grpc.*;
import com.github.conanchen.gedit.accounting.model.Account;
import com.github.conanchen.gedit.accounting.model.StatisticBalance;
import com.github.conanchen.gedit.accounting.repository.AccountRepository;
import com.github.conanchen.gedit.accounting.repository.StatisticBalanceRepository;
import com.github.conanchen.gedit.accounting.utils.EntityUtils;
import com.github.conanchen.gedit.common.grpc.AccountType;
import com.github.conanchen.gedit.common.grpc.Status;
import com.martiansoftware.validation.Hope;
import com.martiansoftware.validation.UncheckedValidationException;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

import static io.grpc.Status.Code.INVALID_ARGUMENT;
import static io.grpc.Status.Code.OK;

/**
 * @author hai
 * @description account service
 * @email hilin2333@gmail.com
 * @date 23/01/2018 9:52 AM
 */
@GRpcService
public class AccountService extends AccountingAccountApiGrpc.AccountingAccountApiImplBase {
    @Resource
    private AccountRepository accountRepository;
    @Resource
    private StatisticBalanceRepository statisticBalanceRepository;
    @Override
    public void upsertAccounts(UpsertAccountsRequest request, StreamObserver<AccountResponse> responseObserver) {
        Status status;
        try {
            String userUuid = Hope.that(request.getUserUuid()).named("userUuid").isNotNullOrEmpty().value();
            UUID uuid = UUID.fromString(userUuid);
            List<String> accountTypes = new ArrayList<>(AccountType.values().length - 1);
            for (AccountType accountType : AccountType.values()) {
                accountTypes.add(accountType.toString());
            }
            List<Account> accountList = accountRepository.findByUserUuidAndAccountTypeIn(uuid, accountTypes);
            status = Status.newBuilder()
                    .setCode(String.valueOf(OK.value()))
                    .setDetails("success")
                    .build();
            if (!CollectionUtils.isEmpty(accountList)){
                List<StatisticBalance> balanceList = statisticBalanceRepository.findByAccountTypeInAndUserUuid(accountTypes,uuid);
                Map<String,StatisticBalance> statisticBalanceMap = EntityUtils.createEntityMapByString(balanceList,"accountType");
                for (Account account : accountList) {
                    StatisticBalance balance = statisticBalanceMap == null ? null : statisticBalanceMap.get(account.getAccountType());
                    responseObserver.onNext(buildResponse(status,account,balance));
                    try { Thread.sleep(500); } catch (InterruptedException e) {}
                }
            }
            Date now = new Date();
            List<Account> newAccounts = new ArrayList<>();
            for (AccountType accountType : AccountType.values()){
                if (!accountList.stream()
                        .filter(account -> account.getAccountType().equals(accountType.toString()))
                        .findFirst()
                        .isPresent()){
                    Account account = Account.builder()
                            .createdDate(now)
                            .updatedDate(now)
                            .userUuid(uuid)
                            .accountType(accountType.toString())
                            .balance(0)
                            .build();
                    newAccounts.add(account);
                    responseObserver.onNext(buildResponse(status,account,null));
                }
            }
            accountRepository.save(newAccounts);
        } catch (UncheckedValidationException e) {
            status = Status.newBuilder()
                    .setCode(String.valueOf(INVALID_ARGUMENT.value()))
                    .setDetails(e.getMessage())
                    .build();
            responseObserver.onNext(AccountResponse.newBuilder().setStatus(status).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getAccount(GetAccountRequest request, StreamObserver<AccountResponse> responseObserver) {
    }

    @Override
    public void findAccountBy(FindAccountRequest request, StreamObserver<AccountResponse> responseObserver) {
    }

    @Override
    public void listMyAccount(ListMyAccountRequest request, StreamObserver<AccountResponse> responseObserver) {
    }

    private AccountResponse buildResponse(Status status, Account account, StatisticBalance balance) {
        int preBalance = balance == null ? 0 : balance.getBalance();
        return AccountResponse.newBuilder()
                .setStatus(status)
                .setAccount(com.github.conanchen.gedit.accounting.account.grpc.Account
                        .newBuilder()
                        .setCreated(account.getCreatedDate().getTime())
                        .setLastUpdated(account.getUpdatedDate().getTime())
                        .setCurrentBalance(account.getBalance())
                        .setCurrentDate(account.getCreatedDate().getTime())
                        .setPreviousBalance(balance.getBalance() == null ? 0 : balance.getBalance())
                        .setPreviousDate(balance.getCreatedDate() == null ? 0L : balance.getCreatedDate().getTime())
                        .setUserUuid(account.getUserUuid().toString())
                        .setCurrentChanges(account.getBalance() - preBalance)
                        .build())
                .build();
    }

}
