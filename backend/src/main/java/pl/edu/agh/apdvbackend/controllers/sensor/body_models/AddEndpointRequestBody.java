package pl.edu.agh.apdvbackend.controllers.sensor.body_models;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddEndpointRequestBody(
        @Schema(required = true) int endpointNumber,
        @Schema(required = true) String label,
        @Schema(required = true) String sensorUrl
) {
}