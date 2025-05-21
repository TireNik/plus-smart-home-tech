package ru.yandex.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaEventConsumer;
import ru.yandex.practicum.kafka.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SensorSnapshotService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaEventConsumer consumer;
    private final KafkaEventProducer producer;
    private final SensorSnapshotService snapshotService;

    public void start() {
        log.info("Запуск подписки на топик: telemetry.sensors.v1");
        consumer.subscribe(List.of("telemetry.sensors.v1"));

        try {
            while (true) {
                log.debug("Ожидание сообщений от Kafka...");
                var records = consumer.poll(Duration.ofMillis(100));
                log.debug("Прочитано {} сообщений", records.count());

                for (var record : records) {
                    processEvent(record.value());
                }

                commitOffsets();

            }
        } catch (WakeupException e) {
            log.info("Завершение по сигналу WakeupException");
        } catch (Exception e) {
            log.error("Ошибка в основном цикле обработки", e);
        } finally {
            shutdown();
        }
    }

    private void processEvent(SensorEventAvro event) {
        log.debug("Обработка события: {}", event);
        try {
            snapshotService.updateSnapshot(event).ifPresent(this::sendSnapshot);
        } catch (Exception e) {
            log.error("Ошибка при обновлении снепшота по событию: {}", event, e);
        }
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        try {
            producer.send("telemetry.snapshots.v1", snapshot.getHubId(), snapshot);
            log.info("Снепшот отправлен: hubId={}", snapshot.getHubId());
        } catch (Exception e) {
            log.error("Ошибка при отправке снепшота: hubId={}", snapshot.getHubId(), e);
        }
    }

    private void commitOffsets() {
        try {
            consumer.commit();
            log.debug("Смещения успешно зафиксированы");
        } catch (Exception e) {
            log.error("Ошибка при коммите смещений", e);
        }
    }

    private void shutdown() {
        log.info("Завершение работы AggregationStarter...");

        try {
            producer.flush();
            log.info("Буфер продюсера очищен");

            commitOffsets();

        } catch (Exception e) {
            log.error("Ошибка при финальной обработке", e);
        } finally {
            closeQuietly(producer, "продюсер");
            closeQuietly(consumer, "консьюмер");
        }
    }

    private void closeQuietly(AutoCloseable closeable, String name) {
        try {
            log.info("Закрытие компонента: {}", name);
            closeable.close();
            log.info("{} закрыт", name);
        } catch (Exception e) {
            log.error("Ошибка при закрытии компонента: {}", name, e);
        }
    }
}