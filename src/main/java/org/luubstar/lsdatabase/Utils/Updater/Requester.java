package org.luubstar.lsdatabase.Utils.Updater;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Requester {

    private static final Logger logger = LoggerFactory.getLogger(Updater.class);
    protected static JsonNode launchRequest(HttpClient client, HttpRequest request, String url) throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readTree(response.body());

        } else {
            logger.error("Error en la solicitud {}, devolvió código {}", url, response.statusCode());
        }
        return null;
    }

    protected static String getLatestReleaseAssetUrl(String downloadURL) throws IOException, URISyntaxException {
        JsonNode rootNode = getJsonNode(downloadURL);
        JsonNode assetsNode = rootNode.get("assets");

        if (assetsNode.isArray() && !assetsNode.isEmpty()) {
            for (JsonNode assetNode : assetsNode) {
                String name = assetNode.get("name").asText();
                if (name.endsWith(".jar")) {
                    return assetNode.get("browser_download_url").asText();
                }
            }
        }
        return null;
    }

    protected static JsonNode getJsonNode(String downloadURL) throws URISyntaxException, IOException {
        URL url = new URI(downloadURL).toURL();
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("GET");

        int responseCode = httpConn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error en la conexión a la API de GitHub. Código de respuesta: " + responseCode);
        }

        InputStream inputStream = httpConn.getInputStream();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(inputStream);
    }

    protected static void downloadFile(String fileURL, String saveFilePath) throws IOException, URISyntaxException {
        URL url = new URI(fileURL).toURL();
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        int responseCode = httpConn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpConn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
        } else {
            throw new IOException("Error al descargar el archivo. Código de respuesta: " + responseCode);
        }
        httpConn.disconnect();
    }
}
