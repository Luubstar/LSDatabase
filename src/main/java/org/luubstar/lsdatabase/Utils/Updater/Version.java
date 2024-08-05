package org.luubstar.lsdatabase.Utils.Updater;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Objects;

public class Version {

    private static final Logger logger = LoggerFactory.getLogger(Updater.class);
    protected static boolean isBiggerVersion(String owner, String repo){
        String url = String.format("https://api.github.com/repos/%s/%s/releases/latest", owner, repo);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/vnd.github.v3+json")
                .build();

        try {
            String latestVersion =  Objects.requireNonNull(Requester.launchRequest(client, request, url)).path("tag_name").asText();
            if(compareVersions(latestVersion, getVersion())){
                /*FileWriter writer = new FileWriter("version.txt");
                writer.write(latestVersion);
                writer.close();*/
                return true;
            }
        }
        catch (Exception e){
            logger.error("Error accediendo a la red para la comprobación del fichero ", e);
        }
        return false;
    }


    protected static boolean compareVersions(String version, String thisVersion)  {
        int[] vWeb = convertStringToIntArray(version);
        int[] vLocal = convertStringToIntArray(thisVersion);

        return compareVersions(vWeb, vLocal);
    }

    protected static boolean compareVersions(int[] vWeb, int[] vLocal){
        for(int i = 0; i < Math.min(vWeb.length, vLocal.length); i++){
            if(vWeb[i] > vLocal[i]){return true;}
        }

        return vWeb.length > vLocal.length;
    }

    protected static int[] convertStringToIntArray(String str) {
        String[] parts = str.split("\\.");

        int[] intArray = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {intArray[i] = Integer.parseInt(parts[i]);}

        return intArray;
    }

    protected static String getVersion() throws IOException {
        File f = new File("version.txt");
        FileInputStream file = new FileInputStream(f);
        String r = new String(file.readAllBytes());
        file.close();
        return r;
    }
}
