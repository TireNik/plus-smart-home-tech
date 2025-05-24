package ru.yandex.practicum.client;

import com.google.protobuf.util.Timestamps;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;

@Slf4j
@Service
public class HubRouterGrpcClient {

    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub client;

    public HubRouterGrpcClient(@Value("${grpc.client.hub-router.address}") String address) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(address)
                .usePlaintext()
                .build();
        this.client = HubRouterControllerGrpc.newBlockingStub(channel);
    }

    public void sendDeviceAction(String hubId, String scenarioName, String sensorId, Integer value, String type) {
        try{
            log.info("Отправка команды устройству по gRPC: hubId={}, scenario={}, sensorId={}, value={}, type={}",
                    hubId, scenarioName, sensorId, value, type);
        DeviceActionProto action = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(ActionTypeProto.valueOf(type.toUpperCase()))
                .setValue(value)
                .build();

        DeviceActionRequest request = DeviceActionRequest.newBuilder()
                .setHubId(hubId)
                .setScenarioName(scenarioName)
                .setAction(action)
                .setTimestamp(Timestamps.fromMillis(System.currentTimeMillis()))
                .build();

        client.handleDeviceAction(request);
        } catch (Exception e) {
            log.error("Ошибка отправки запроса в HubRouter: {}", e.getMessage(), e);
        }
    }
}