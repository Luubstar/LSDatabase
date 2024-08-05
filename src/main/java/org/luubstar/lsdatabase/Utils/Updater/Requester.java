package org.luubstar.lsdatabase.Utils.Updater;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Requester {

    private static final Logger logger = LoggerFactory.getLogger(Updater.class);
    protected static JsonNode launchRequest(HttpClient client, HttpRequest request, String url) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readTree(response.body());

        } else {
            logger.error("Error en la solicitud {}, devolvió código {}", url, response.statusCode());
        }
        return null;
    }
}
