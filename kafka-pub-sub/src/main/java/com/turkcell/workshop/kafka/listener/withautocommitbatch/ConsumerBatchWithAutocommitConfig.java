package com.turkcell.workshop.kafka.listener.withautocommitbatch;

import com.turkcell.workshop.kafka.commons.props.KafkaProducerConsumerProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentBatchErrorHandler;

import java.time.Duration;

@EnableKafka
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ConsumerBatchWithAutocommitConfig {
    private final KafkaProducerConsumerProps kafkaProducerConsumerProps;

    @Bean("consumerBatchWithAutocommitConsumerFactory")
    public ConsumerFactory<String, String> consumerBatchWithAutocommitConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProducerConsumerProps.getConsumerWithAutoCommitBatch().getProps());
    }


    @Bean("consumerBatchWithAutoCommitListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerBatchWithAutocommitConsumerFactory());
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.BATCH);
        factory.getContainerProperties().setSyncCommits(kafkaProducerConsumerProps.getConsumerWithAutoCommitBatch().isSyncCommit());
        factory.getContainerProperties().setSyncCommitTimeout(Duration.ofSeconds(kafkaProducerConsumerProps.getConsumerWithAutoCommitBatch().getSyncCommitTimeoutSecond()));
        factory.setConcurrency(kafkaProducerConsumerProps.getConsumerWithAutoCommitBatch().getConcurrency());
        SeekToCurrentBatchErrorHandler errorHandler = new SeekToCurrentBatchErrorHandler();
//        FixedBackOff fixedBackOff =
//                new FixedBackOff(kafkaProducerConsumerProps.getConsumerWithAutoCommitBatch().getBackoffIntervalMillis(), kafkaProducerConsumerProps.getConsumerWithAutoCommitBatch().getRetryCount());
//        errorHandler.setBackOff(fixedBackOff);
//        factory.setBatchErrorHandler(errorHandler);
        factory.setBatchListener(true);
        return factory;
    }

}
