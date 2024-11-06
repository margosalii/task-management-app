package app.task.management.service.impl;

import app.task.management.exceptions.DropboxException;
import app.task.management.service.DropboxService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DropboxServiceImpl implements DropboxService {
    @Value("${dropbox.access.token}")
    private String accessToken;

    @Value("${dropbox.upload.url}")
    private String uploadFileUrl;

    @Value("${dropbox.download.url}")
    private String downloadFileUrl;

    @Value("${dropbox.delete.url}")
    private String deleteFileUrl;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(uploadFileUrl))
                .header("Authorization", "Bearer " + accessToken)
                .header("Dropbox-API-Arg", "{\"path\": \"/"
                    + file.getOriginalFilename()
                    + "\", \"mode\": \"add\", \"autorename\": true, \"mute\": false}")
                .header("Content-Type", "application/octet-stream")
                .POST(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new DropboxException("Failed to upload file to Dropbox: " + e.getMessage());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper.readValue(response.body(), new TypeReference<>() {});
        return (String) map.get("id");
    }

    @Override
    public Resource getFile(String dropboxFileId) {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(downloadFileUrl))
                .header("Authorization", "Bearer " + accessToken)
                .header("Dropbox-API-Arg", "{\"path\": \"" + dropboxFileId + "\"}")
                .GET()
                .build();

        HttpResponse<byte[]> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());
        } catch (InterruptedException | IOException e) {
            throw new DropboxException("Failed to get file from Dropbox" + e);
        }
        return new ByteArrayResource(response.body());
    }

    @Override
    public void delete(String dropboxFileId) {
        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(deleteFileUrl))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"path\": \"" + dropboxFileId + "\"}"))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            throw new DropboxException("Failed to delete file from Dropbox" + e);
        }
        if (response.statusCode() != 200) {
            throw new DropboxException("Failed to delete file from Dropbox");
        }
    }
}
