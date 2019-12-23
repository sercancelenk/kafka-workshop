package com.turkcell.sardis.config;

import com.google.common.collect.ImmutableMap;
import com.turkcell.sardis.config.model.Consumer;
import com.turkcell.sardis.config.model.Step;
import com.turkcell.sardis.dto.EventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.*;
import org.springframework.util.backoff.FixedBackOff;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.*;

@Configuration
@EnableKafka
@RequiredArgsConstructor
@Slf4j
public class KafkaConfig {

    private final EventsConfig eventsConfig;
    private final KafkaCustomProducerConfig kafkaCustomProducerConfig;

    @Autowired
    private ApplicationContext applicationContext;

    private ImmutableMap<String, ImmutableMap<Integer,KafkaTemplate<String, EventDto>>> producers;

    private ImmutableMap<String,ImmutableMap<Integer,ConcurrentMessageListenerContainer>> consumerContainers;

    public KafkaTemplate<String, EventDto> getProducer(String eventName, int step){
        return producers.get(eventName).get(step);
    }

    public ConcurrentMessageListenerContainer getConsumerContainer(String eventName,int step){
        return consumerContainers.get(eventName).get(step);
    }


    @PostConstruct
    private void init(){
        consumerContainers = createAndStartConsumers();
        producers = createProducers();
    }

    private ImmutableMap<String, ImmutableMap<Integer, KafkaTemplate<String, EventDto>>> createProducers() {
        return eventsConfig.getEvents()
                .entrySet().stream()
                .reduce(ImmutableMap.of(),
                        (acc1,entry) -> {
                            String eventName = entry.getKey();
                            Map<Integer,Step> stepMap = entry.getValue();
                            ImmutableMap<Integer, KafkaTemplate<String, EventDto>> eventProducers = stepMap.values().stream().reduce(ImmutableMap.of(),
                                    (acc2,step) ->
                                            ofNullable(step.getProducer()).map(p -> {
                                                DefaultKafkaProducerFactory<String, EventDto> kafkaProducerFactory =
                                                        new DefaultKafkaProducerFactory<>(eventsConfig.mergeProps(p.getProps(), eventsConfig.getProducerPropsDefaults()));
                                                KafkaTemplate<String, EventDto> kafkaTemplate = new KafkaTemplate<>(kafkaProducerFactory);
                                                return new ImmutableMap.Builder<Integer, KafkaTemplate<String, EventDto>>()
                                                        .putAll(acc2)
                                                        .put(step.getId(),kafkaTemplate)
                                                        .build();
                                            }).orElse(ImmutableMap.<Integer, KafkaTemplate<String, EventDto>>builder().putAll(acc2).build())
                                    ,(map1,map2) -> ImmutableMap.<Integer, KafkaTemplate<String, EventDto>>builder().putAll(map1).putAll(map2).build());
                            return ImmutableMap.<String, ImmutableMap<Integer, KafkaTemplate<String, EventDto>>>builder()
                                    .putAll(acc1).put(eventName,eventProducers).build();
                        },(map1,map2) -> ImmutableMap.<String, ImmutableMap<Integer, KafkaTemplate<String, EventDto>>>builder().putAll(map1).putAll(map2).build());
    }

    private ImmutableMap<String, ImmutableMap<Integer, ConcurrentMessageListenerContainer>> createAndStartConsumers() {
        return eventsConfig.getEvents()
                .entrySet().stream()
                .reduce(ImmutableMap.of(),
                        (acc1,entry) -> {
                            String eventName = entry.getKey();
                            Map<Integer,Step> stepMap = entry.getValue();
                            ImmutableMap<Integer, ConcurrentMessageListenerContainer> eventContainers = stepMap.values().stream().reduce(ImmutableMap.of(),
                                    (acc2,step) -> {
                                        ConcurrentMessageListenerContainer container = createMessageListenerContainer(step);
                                        return ImmutableMap.<Integer, ConcurrentMessageListenerContainer>builder()
                                                .putAll(acc2).put(step.getId(),container).build();
                                    },(map1,map2) -> ImmutableMap.<Integer, ConcurrentMessageListenerContainer>builder()
                                            .putAll(map1).putAll(map2).build());
                            return ImmutableMap.<String, ImmutableMap<Integer, ConcurrentMessageListenerContainer>>builder()
                                    .putAll(acc1).put(eventName,eventContainers).build();
                        },(map1,map2) -> ImmutableMap.<String, ImmutableMap<Integer, ConcurrentMessageListenerContainer>>builder()
                                .putAll(map1).putAll(map2).build());
    }

    private ConcurrentMessageListenerContainer createMessageListenerContainer(Step step) {
        Consumer consumer = step.getConsumer();
        DefaultKafkaConsumerFactory<String, EventDto> kafkaConsumerFactory =
                new DefaultKafkaConsumerFactory<>(eventsConfig.mergeProps(consumer.getProps(), eventsConfig.getConsumerPropsDefaults()));
        ContainerProperties containerProperties = new ContainerProperties(consumer.getTopic());
        containerProperties.setMissingTopicsFatal(false);
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setSyncCommits(consumer.isSyncCommit());
        containerProperties.setSyncCommitTimeout(Duration.ofSeconds(consumer.getSyncCommitTimeoutSecond()));
        containerProperties.setMessageListener(applicationContext.getBean(consumer.getEventListenerBeanName()));
        ConcurrentMessageListenerContainer container =
                new ConcurrentMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);
        SeekToCurrentBatchErrorHandler errorHandler = new SeekToCurrentBatchErrorHandler();
        FixedBackOff fixedBackOff =
                new FixedBackOff(consumer.getBackoffIntervalMillis(), Long.MAX_VALUE);
        errorHandler.setBackOff(fixedBackOff);
        container.setBatchErrorHandler(errorHandler);
        container.setConcurrency(step.getConsumer().getConcurrency());
        container.start();
        return container;
    }


    @Bean("customProducer")
    public KafkaTemplate<String, EventDto> customProducer(){
        DefaultKafkaProducerFactory<String, EventDto> kafkaProducerFactory =
                new DefaultKafkaProducerFactory<>(kafkaCustomProducerConfig.getProps());
        return new KafkaTemplate<>(kafkaProducerFactory);
    }

    @PreDestroy
    public void shutdownHook(){
        consumerContainers
                .entrySet()
                .stream()
                .forEach(consumerMapEntry-> consumerMapEntry.getValue().entrySet().stream().forEach(consumerEntry -> {
                    try {
                        if (Optional.ofNullable(consumerEntry.getValue()).isPresent())
                            consumerEntry.getValue().stop();
                    }catch (Exception ex){
                        log.warn("Consumer can not stopping on shutdown hook. {}", ex.getMessage(), ex);
                    }
                }));
    }

}
