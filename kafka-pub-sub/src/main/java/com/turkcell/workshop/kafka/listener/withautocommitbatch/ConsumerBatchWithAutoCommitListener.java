package com.turkcell.workshop.kafka.listener.withautocommitbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;

//@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerBatchWithAutoCommitListener {
    public static final int COUNT = 20;

    private CountDownLatch latch = new CountDownLatch(COUNT);
    public CountDownLatch getLatch() {
        return latch;
    }

    @KafkaListener(id = "batch-listener-1",
            topics = "${kafka.consumer-with-autocommit-batch.topic}", groupId = "${kafka.consumer-with-autocommit-batch.props[group.id]}",
    containerFactory = "consumerBatchWithAutoCommitListenerFactory")
    public void receive(@Payload List<String> messages,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        log.info("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        log.info("beginning to consume batch messages");

        for (int i = 0; i < messages.size(); i++) {

            log.info("received message='{}' with partition-offset='{}'",
                    messages.get(i), partitions.get(i) + "-" + offsets.get(i));

            if ("throw".equals(messages.get(i))) throw new RuntimeException("xxxx");

            latch.countDown();
        }

        log.info("all batch messages consumed");
    }
}
