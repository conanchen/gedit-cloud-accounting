package com.github.conanchen.gedit.accounting;

import com.github.conanchen.gedit.accounting.account.grpc.AccountResponse;
import com.github.conanchen.gedit.accounting.account.grpc.AccountingAccountApiGrpc;
import com.github.conanchen.gedit.accounting.account.grpc.ListMyAccountRequest;
import com.github.conanchen.gedit.accounting.model.Journal;
import com.github.conanchen.gedit.accounting.repository.JournalRepository;
import com.github.conanchen.gedit.accounting.utils.database.ToStringTransformer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestClient {
    private static final Logger log = LoggerFactory.getLogger(TestClient.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private AccountingAccountApiGrpc.AccountingAccountApiBlockingStub blockingStub;
    private ManagedChannel channel;
    private static final String local = "127.0.0.1";
    private static final String remote = "dev.jifenpz.com";
    @Before
    public void init(){
        channel = ManagedChannelBuilder.forAddress(local,9983)
                .usePlaintext(true)
                .build();

        blockingStub = AccountingAccountApiGrpc.newBlockingStub(channel);
        //access token
        String accessToken = "BearereyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAF3NwQqAIBCE4XfZs4fRdUV9GzUDOwUZBNG7l9SpOf2HD-akljpFLdqxWCtG0bZnimRhvIdnpzEFzAV6VOI6BQHApGjp7QcLmw8Wmytj7IH1WN-LIMEIXzeBEd8hdQAAAA.WpD5DxFWFEQOj8CdPELyq8xLYu5T8xSkK-PAPk_QzVjUIqM4XZJr4e7XD6aw5f0dYqU13k5hIZ1K0wfOncZI6A";
        // create a custom header
        Metadata header=new Metadata();
        Metadata.Key<String> key =
                Metadata.Key.of("authorization", Metadata.ASCII_STRING_MARSHALLER);
        header.put(key, accessToken);
        blockingStub = MetadataUtils.attachHeaders(blockingStub, header);
    }
    @After
    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    public void listMyAccount(){
        Iterator<AccountResponse> responseIterator = blockingStub.listMyAccount(ListMyAccountRequest.newBuilder().build());
        responseIterator.forEachRemaining(n ->  log.info("结果：" + gson.toJson(n)));
    }
    @Test
    public void uuidTest(){
        for (int i =0; i < 10;i++) {
            log.info(UUID.randomUUID().toString());
        }
    }

    @Resource
    private JournalRepository journalRepository;

    @Test
    public void save(){
        Journal journal = Journal.builder()
                .createdDate(new Date())
                .updatedDate(new Date())
                .journalType("$")
                .build();
        Journal journal1 = (Journal) journalRepository.save(journal);
        log.info(journal1.getUuid());
    }

    @Test
    public void test(){
        UUID uuid = UUID.randomUUID();
        log.info(uuid.toString());
        String strUuid = ToStringTransformer.INSTANCE.transform(uuid);
        log.info(strUuid);
        UUID parseUuid = ToStringTransformer.INSTANCE.parse(strUuid);
        log.info(parseUuid.toString());
    }
}
