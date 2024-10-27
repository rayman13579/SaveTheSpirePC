package at.rayman.savethespirepc;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

public class NetworkService {

    private static NetworkService instance;

    private static HttpClient httpClient;

    private NetworkService() {
        httpClient = HttpClients.custom()
            .setUserAgent(Constants.USER_AGENT)
            .build();
    }

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NetworkService();
        }
        return instance;
    }

    public Result uploadSave() {
        try {
            HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody(Constants.ZIP_NAME, Paths.get(Constants.ZIP_PATH).toFile())
                .build();
            HttpPut request = new HttpPut(new URI(Constants.URL + "/upload"));
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            return Result.success("Backend returned status: " + response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    public Result downloadSave() {
        try {
            HttpGet request = new HttpGet(new URI(Constants.URL + "/download"));
            HttpResponse response = httpClient.execute(request);
            FileUtils.copyInputStreamToFile(response.getEntity().getContent(), new File(Constants.ZIP_PATH));
            return Result.success("Backend returned status: " + response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

}
