package com.github.conanchen.gedit.accounting.grpc;

import com.github.conanchen.gedit.accounting.account.grpc.*;
import com.github.conanchen.gedit.accounting.grpc.interceptor.AuthInterceptor;
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
import io.jsonwebtoken.Claims;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
            List<Account> accountList = accountRepository.findAllByUserUuid(userUuid);
            next(userUuid,accountList,responseObserver);
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
        try {
            String uuid = Hope.that(request.getUuid()).named("uuid").isNotNullOrEmpty().value();
            Account account = accountRepository.findOne(uuid);
            LocalDate localDate = LocalDate.now().minusDays(1);
            StatisticBalance balance = statisticBalanceRepository.findByAccountTypeAndStatisticDate(account.getAccountType(),localDateToDate(localDate));
            Status status = Status.newBuilder()
                    .setCode(String.valueOf(OK.value()))
                    .setDetails("success")
                    .build();
            responseObserver.onNext(buildResponse(status,account,balance));
        }catch (UncheckedValidationException e){
            Status status = Status.newBuilder()
                    .setCode(String.valueOf(INVALID_ARGUMENT.value()))
                    .setDetails(e.getMessage())
                    .build();
            responseObserver.onNext(AccountResponse.newBuilder().setStatus(status).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void findAccountBy(FindAccountRequest request, StreamObserver<AccountResponse> responseObserver) {

    }

    @Override
    public void listMyAccount(ListMyAccountRequest request, StreamObserver<AccountResponse> responseObserver) {
        Claims claims = AuthInterceptor.USER_CLAIMS.get();
        Status status;
        try {
            List<Account> accountList;
            if (request.getLastUpdated() == 0L){
                accountList = accountRepository.findAllByUserUuid(claims.getSubject());
            }else {
                accountList = accountRepository.findByUserUuidAndUpdatedDateAfter(claims.getSubject(), new Date(request.getLastUpdated()));
            }
            next(claims.getSubject(),accountList,responseObserver);
        } catch (UncheckedValidationException e) {
            status = Status.newBuilder()
                    .setCode(String.valueOf(INVALID_ARGUMENT.value()))
                    .setDetails(e.getMessage())
                    .build();
            responseObserver.onNext(AccountResponse.newBuilder().setStatus(status).build());
        }
        responseObserver.onCompleted();
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
                        .setPreviousDate(balance.getStatisticDate() == null ? 0L : balance.getStatisticDate().getTime())
                        .setUserUuid(account.getUserUuid().toString())
                        .setCurrentChanges(account.getBalance() - preBalance)
                        .build())
                .build();
    }

    private Date localDateToDate(LocalDate localDate){
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
        return Date.from(zdt.toInstant());
    }
    private List<String> getAccountTypes(){
        List<String> accountTypes = new ArrayList<>(AccountType.values().length - 1);
        for (AccountType accountType : AccountType.values()) {
            if (!accountType.equals(AccountType.UNRECOGNIZED)){
                accountTypes.add(accountType.toString());
            }
        }
        return accountTypes;
    }

    private void next(String userUuid,List<Account> accountList,StreamObserver<AccountResponse> responseObserver){
        List<String> accountTypes = getAccountTypes();
        Status status = Status.newBuilder()
                .setCode(String.valueOf(OK.value()))
                .setDetails("success")
                .build();
        int temp = 0;
        if (!CollectionUtils.isEmpty(accountList)){
            LocalDate localDate = LocalDate.now().minusDays(1);
            List<StatisticBalance> balanceList = statisticBalanceRepository
                    .findByAccountTypeInAndUserUuidAndStatisticDate(accountTypes,userUuid,localDateToDate(localDate));
            Map<String,StatisticBalance> statisticBalanceMap = EntityUtils.createEntityMapByString(balanceList,"accountType");
            for (Account account : accountList) {
                StatisticBalance balance = statisticBalanceMap == null ? null : statisticBalanceMap.get(account.getAccountType());
                responseObserver.onNext(buildResponse(status,account,balance));
                temp++;
                try { Thread.sleep(500); } catch (InterruptedException e) {}
            }
        }
        if (temp < accountTypes.size()) {
            Date now = new Date();
            List<Account> newAccounts = new ArrayList<>();
            for (AccountType accountType : AccountType.values()) {
                if (!accountType.equals(AccountType.UNRECOGNIZED) &&
                        !accountList.stream()
                                .filter(account -> account.getAccountType().equals(accountType.toString()))
                                .findFirst()
                                .isPresent()) {
                    Account account = Account.builder()
                            .createdDate(now)
                            .updatedDate(now)
                            .userUuid(userUuid)
                            .accountType(accountType.toString())
                            .balance(0)
                            .build();
                    newAccounts.add(account);
                    responseObserver.onNext(buildResponse(status, account, null));
                }
            }
            if (CollectionUtils.isEmpty(newAccounts)) {
                accountRepository.save(newAccounts);
            }
        }
    }
}
