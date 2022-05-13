package pl.edu.agh.apdvbackend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pl.edu.agh.apdvbackend.deserializer.DataDeserializer;
import pl.edu.agh.apdvbackend.models.DataTypes;
import pl.edu.agh.apdvbackend.models.body_models.DataHubDataRequest;
import pl.edu.agh.apdvbackend.models.body_models.DataResponse;
import pl.edu.agh.apdvbackend.models.body_models.SensorInfo;
import pl.edu.agh.apdvbackend.models.body_models.SensorLocation;
import pl.edu.agh.apdvbackend.utilities.StreamUtilities;

import java.util.Iterator;
import java.util.List;

@Service
public class DataHubService {
    private final static String RESULTS = "results";
    private final WebClient webClient;
    private final StreamUtilities streamUtilities;
    private final DataDeserializer dataDeserializer;

    @Autowired
    public DataHubService(WebClient webClient,
                          StreamUtilities streamUtilities,
                          DataDeserializer dataDeserializer) {
        this.webClient = webClient;
        this.streamUtilities = streamUtilities;
        this.dataDeserializer = dataDeserializer;
    }

    public List<DataResponse> getData(DataHubDataRequest dataHubDataRequest) {
        return streamUtilities.asStream(
                makeRequestAndGetResults(dataHubDataRequest.url())
        ).map(jsonNode -> new DataResponse(
                        dataDeserializer.getDoubleValue(DataTypes.TEMPERATURE, jsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.PRESSURE, jsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.HUMIDITY, jsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.PM1_0, jsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.PM2_5, jsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.PM10, jsonNode)
                )
        ).toList();
    }

    public SensorInfo getSensorInfo(DataHubDataRequest dataHubDataRequest) {
        JsonNode firstJsonNode = makeRequestAndGetResults(dataHubDataRequest.url()).next();
        return new SensorInfo(
                dataDeserializer.getStringValue(DataTypes.LABEL, firstJsonNode),
                dataDeserializer.getStringValue(DataTypes.ID, firstJsonNode),
                new SensorLocation(
                        dataDeserializer.getDoubleValue(DataTypes.LOCATION_ALT, firstJsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.LOCATION_LAT, firstJsonNode),
                        dataDeserializer.getDoubleValue(DataTypes.LOCATION_LON, firstJsonNode),
                        dataDeserializer.getStringValue(DataTypes.LOCATION_NAME, firstJsonNode)
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
}
