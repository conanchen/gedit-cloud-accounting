package com.github.conanchen.gedit.accounting.grpc;

import com.github.conanchen.gedit.accounting.balance.grpc.AccountingBalanceApiGrpc;
import org.lognet.springboot.grpc.GRpcService;

/**
 * @author hai
 * @description 约
 * @email hilin2333@gmail.com
 * @date 23/01/2018 9:52 AM
 */
@GRpcService
public class BalanceService extends AccountingBalanceApiGrpc.AccountingBalanceApiImplBase {
}
