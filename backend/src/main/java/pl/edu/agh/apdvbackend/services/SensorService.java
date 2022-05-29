package pl.edu.agh.apdvbackend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.edu.agh.apdvbackend.deserializer.DataDeserializer;
import pl.edu.agh.apdvbackend.mappers.EndpointInfoMapper;
import pl.edu.agh.apdvbackend.models.DataTypes;
import pl.edu.agh.apdvbackend.models.EndpointInfo;
import pl.edu.agh.apdvbackend.models.body_models.Response;
import pl.edu.agh.apdvbackend.models.body_models.sensors.AddEndpointRequestBody;
import pl.edu.agh.apdvbackend.models.body_models.sensors.Endpoint;
import pl.edu.agh.apdvbackend.models.body_models.sensors.EndpointData;
import pl.edu.agh.apdvbackend.models.body_models.sensors.SensorInfo;
import pl.edu.agh.apdvbackend.models.body_models.sensors.SensorLocation;
import pl.edu.agh.apdvbackend.repositories.EndpointRepository;
import pl.edu.agh.apdvbackend.utilities.ListUtilities;
import pl.edu.agh.apdvbackend.utilities.StreamUtilities;

@Service
public class SensorService {
    private static final String RESULTS = "results";

    private final WebClient webClient;

    private final StreamUtilities streamUtilities;

    private final DataDeserializer dataDeserializer;

    private final EndpointRepository endpointRepository;

    private final ListUtilities listUtilities;

    private final EndpointInfoMapper endpointInfoMapper;

    @Autowired
    public SensorService(WebClient webClient,
                         StreamUtilities streamUtilities,
                         DataDeserializer dataDeserializer,
                         EndpointRepository endpointRepository,
                         ListUtilities listUtilities,
                         EndpointInfoMapper endpointInfoMapper) {
        this.webClient = webClient;
        this.streamUtilities = streamUtilities;
        this.dataDeserializer = dataDeserializer;
        this.endpointRepository = endpointRepository;
        this.listUtilities = listUtilities;
        this.endpointInfoMapper = endpointInfoMapper;
    }

    public Response<Endpoint> getWeatherData(Long sensorId) {
        try {
            final var endpoint =
                    endpointRepository.findById(sensorId).orElseThrow();
            final var endpointData = parseWeatherData(
                    makeRequestAndGetResults(endpoint.getSensorUrl()));
            return Response.withOkStatus(
                    new Endpoint(sensorId, endpoint.getLabel(), endpointData));
        } catch (Exception exception) {
            return Response.withError(exception.getMessage());
        }
    }

    public Response<SensorInfo> getSensorInfo(String sensorUrl) {
        try {
            return Response.withOkStatus(parseSensorInfo(
                    makeRequestAndGetResults(sensorUrl).next()));
        } catch (Exception exception) {
            return Response.withError(exception.getMessage());
        }
    }

    private List<EndpointData> parseWeatherData(
            Iterator<JsonNode> dataIterator) {
        return streamUtilities.asStream(
                dataIterator
        ).map(jsonNode -> new EndpointData(
                        dataDeserializer.getDoubleValue(DataTypes.TEMPERATURE,
                                jsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.PRESSURE, jsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.HUMIDITY, jsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.PM1_0, jsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.PM2_5, jsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.PM10, jsonNode)
                )
        ).toList();
    }

    private SensorInfo parseSensorInfo(JsonNode firstJsonNode) {
        return new SensorInfo(
                dataDeserializer.getStringValue(DataTypes.LABEL, firstJsonNode),
                dataDeserializer.getStringValue(DataTypes.ID, firstJsonNode),
                new SensorLocation(
                        dataDeserializer.getDoubleValue(DataTypes.LOCATION_ALT,
                                firstJsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.LOCATION_LAT,
                                firstJsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.LOCATION_LON,
                                firstJsonNode),
                        dataDeserializer.getStringValue(DataTypes.LOCATION_NAME,
                                firstJsonNode)
                )
        );
    }

    private Iterator<JsonNode> makeRequestAndGetResults(String uri) {
        return makeRequest(uri)
                .get(RESULTS)
                .iterator();
    }

    private ObjectNode makeRequest(String uri) {
        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(ObjectNode.class)
                .block();
    }

    public Response<List<EndpointInfo>> getEndpointsList() {
        return Response.withOkStatus(
                listUtilities.iterableToList(endpointRepository.findAll()));
    }

    public EndpointInfo addEndpoint(
            AddEndpointRequestBody addEndpointRequestBody) {
        return endpointRepository.save(
                endpointInfoMapper.fromAddRequestBodyToEndpointInfo(
                        addEndpointRequestBody));
    }

    public void removeEndpoint(Long endpointId) {
        endpointRepository.deleteById(endpointId);
    }
}
