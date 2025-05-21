package ru.yandex.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.dto.grpc.hub.GrpcHubEvent;
import ru.yandex.practicum.dto.grpc.sensor.GrpcSensorEvent;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class CollectorGrpcController extends CollectorControllerGrpc.CollectorControllerImplBase {

    private final Map<SensorEventProto.PayloadCase, GrpcSensorEvent> sensorHandlers;
    private final Map<HubEventProto.PayloadCase, GrpcHubEvent> hubHandlers;

    public CollectorGrpcController(Set<GrpcSensorEvent> sensorHandlers,
                                   Set<GrpcHubEvent> hubHandlers) {
        this.sensorHandlers = sensorHandlers.stream()
                .collect(Collectors.toMap(GrpcSensorEvent::getMessageType, Function.identity()));
        this.hubHandlers = hubHandlers.stream()
                .collect(Collectors.toMap(GrpcHubEvent::getMessageType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            var handler = sensorHandlers.get(request.getPayloadCase());
            if (handler == null) throw new IllegalArgumentException("Unknown sensor type");
            handler.handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    new StatusRuntimeException(
                            Status.INTERNAL
                                    .withDescription(e.getMessage())
                                    .withCause(e)
                    )
            );
        }
    }

    @Override
    public void collectHubEvent(HubEventProto request, StreamObserver<Empty> responseObserver) {
        try {
            var handler = hubHandlers.get(request.getPayloadCase());
            if (handler == null) throw new IllegalArgumentException("Unknown hub type");
            handler.handle(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    new StatusRuntimeException(
                            Status.INTERNAL
                                    .withDescription(e.getMessage())
                                    .withCause(e)
                    )
            );
        }
    }
}