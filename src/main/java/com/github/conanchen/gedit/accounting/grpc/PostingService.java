package com.github.conanchen.gedit.accounting.grpc;

import com.github.conanchen.gedit.accounting.posting.grpc.AccountingPostingApiGrpc;
import org.lognet.springboot.grpc.GRpcService;

/**
 * @author hai
 * @description
 * @email hilin2333@gmail.com
 * @date 23/01/2018 9:52 AM
 */
@GRpcService
public class PostingService extends AccountingPostingApiGrpc.AccountingPostingApiImplBase{
}
