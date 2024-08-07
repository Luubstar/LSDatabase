package org.luubstar.lsdatabase.Utils.Updater;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.FileWriter;
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
            int size = httpConn.getContentLength();
            InputStream inputStream = httpConn.getInputStream();
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
            byte[] buffer = new byte[4096];
            int bytesRead;
            int i = 0;

            String sizeFormated = String.format("%.2f",(double) size/1000000);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                i += bytesRead;
                int finalI = i;
                Platform.runLater(() -> UpdateController.barra.set((double) finalI /size));
                Platform.runLater(() ->  UpdateController.text.set( String.format("%.2f",(double) finalI/1000000) + " MBs / " + sizeFormated + " MBs"));
            }

            outputStream.close();
            inputStream.close();

            FileWriter writer = new FileWriter("version.txt");
            writer.write(Version.lastVersion);
            writer.close();
        } else {
            throw new IOException("Error al descargar el archivo. Código de respuesta: " + responseCode);
        }
        httpConn.disconnect();
    }
}
