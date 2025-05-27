package ru.yandex.practicum.aggregator.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;
import serializer.AvroSerializer;

import java.util.Properties;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class KafkaEventProducer {

    private KafkaProducer<String, SpecificRecordBase> producer;
    private final KafkaProperties kafkaProperties;

    @PostConstruct
    public void init() {
        this.producer = new KafkaProducer<>(producerProperties());
    }

    private Properties producerProperties() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaProperties.getProducer().getValueSerializer());
        return props;
    }

    public Future<RecordMetadata> send(String topic, String key, SpecificRecordBase value) {
        return producer.send(new ProducerRecord<>(topic, key, value));
    }

    public void flush() {
        producer.flush();
    }

    public void close() {
        producer.close();
    }

    @PreDestroy
    public void shutdown() {
        producer.flush();
        producer.close();
    }
}

