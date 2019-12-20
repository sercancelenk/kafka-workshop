package com.turkcell.workshop.kafka.listener.manual_ack_mode_singlew;

import com.turkcell.workshop.kafka.commons.props.KafkaProducerConsumerProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Duration;



@EnableKafka
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ConsumerManualSingleAckListenerConfig {
    private final KafkaProducerConsumerProps kafkaProducerConsumerProps;

    @Bean("manualAckKafkaConsumerFactory")
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProducerConsumerProps.getConsumerWithManualAckSingle().getProps());
    }

    @Bean("manualAckKafkaListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory(KafkaTemplate<String, String> kafkaTemplate) {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.getContainerProperties().setSyncCommits(kafkaProducerConsumerProps.getConsumerWithManualAckSingle().isSyncCommit());
        factory.getContainerProperties().setSyncCommitTimeout(Duration.ofSeconds(kafkaProducerConsumerProps.getConsumerWithManualAckSingle().getSyncCommitTimeoutSecond()));
        factory.setConcurrency(kafkaProducerConsumerProps.getConsumerWithManualAckSingle().getConcurrency());
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (r, e) -> {
                    return new TopicPartition(r.topic() + ".other.failures", r.partition());
                });
        ErrorHandler errorHandler = new SeekToCurrentErrorHandler(recoverer, new FixedBackOff(0L, 5L));

        factory.setErrorHandler(errorHandler);

        return factory;
    }
}

