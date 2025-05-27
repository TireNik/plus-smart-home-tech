package ru.yandex.practicum.aggregator.kafka;

import deserializer.SensorEventDeserializer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaProperties kafkaProperties;

    @PostConstruct
    public void init() {
        this.consumer = new KafkaConsumer<>(consumerProperties());
    }

    private Properties consumerProperties() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getKeyDeserializer());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaProperties.getConsumer().getValueDeserializer());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaProperties.getConsumer().isEnableAutoCommit());
        return props;
    }

    public void wakeup() {
        consumer.wakeup();
    }
    public ConsumerRecords<String, SensorEventAvro> poll(Duration timeout) {
        return consumer.poll(timeout);
    }

    public void commit() {
        consumer.commitSync();
    }

    public void close() {
        consumer.close();
    }

    public void subscribe(List<String> topics) {
        consumer.subscribe(topics);
    }

    @PreDestroy
    public void shutdown() {
        consumer.close();
    }
}

