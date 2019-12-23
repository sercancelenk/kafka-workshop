package com.turkcell.workshop.kafka.listener.withautocommitsingle;

import com.turkcell.workshop.kafka.commons.props.KafkaProducerConsumerProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.time.Duration;

@EnableKafka
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ConsumerWithAutoCommitConfig {
    private final KafkaProducerConsumerProps kafkaProducerConsumerProps;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProducerConsumerProps.getConsumerWithAutoCommit().getProps());
    }

    @Bean("batchRetryTemplate")
    RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(kafkaProducerConsumerProps.getConsumerWithAutoCommit().getBackoffIntervalMillis());
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(kafkaProducerConsumerProps.getConsumerWithAutoCommit().getRetryCount());
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.getContainerProperties().setSyncCommits(kafkaProducerConsumerProps.getConsumerWithAutoCommit().isSyncCommit());
        factory.getContainerProperties().setSyncCommitTimeout(Duration.ofSeconds(kafkaProducerConsumerProps.getConsumerWithAutoCommit().getSyncCommitTimeoutSecond()));
        factory.setConcurrency(kafkaProducerConsumerProps.getConsumerWithAutoCommit().getConcurrency());
        factory.setRetryTemplate(retryTemplate());
        factory.setRecoveryCallback(context -> {
            log.error("RetryPolicy limit has been exceeded! You should really handle this better.");
            return null;
        });

        return factory;
    }
}
