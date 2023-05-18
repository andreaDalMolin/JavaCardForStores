package be.masi_liege_g2_2223;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;

public class APIClientUtil {
    private static final HttpClient httpClient;
    private static final String urlLogin = "https://192.168.0.246:443/auth/signin";
    private static final String urlSetPoints = "https://192.168.0.246/users/points/update";
    private static final String username = "user_card";
    private static final String password = "password_card";

    static {
        //TODO remove this when we have a domain name
        Properties props = System.getProperties();
        props.setProperty("jdk.internal.httpclient.disableHostnameVerification", Boolean.TRUE.toString());

        try {
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(X509Certificate[] certs, String authType) { }
            } };

            SSLContext sc = SSLContext.getInstance("TLSv1.3");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .sslContext(sc)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing HttpClient", e);
        }
    }

    public static String login() throws Exception {
        String body = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
        Map<String, String> headers = Map.of("Content-Type", "application/json");

        String loginResponse = sendPostRequest(urlLogin, headers, body, null);
        String token = null;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(loginResponse);

            token = jsonNode.get("token").asText();
            System.out.println("Token: " + token);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        }
        return token;
    }

    public static int getUserId(String username, String token) {
        String user = getUser(username, token);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(user);
            JsonNode resultsNode = rootNode.get("results");

            if (resultsNode.isArray()) {
                for (JsonNode resultNode : resultsNode) {
                    String userName = resultNode.get("userName").asText();
                    if (userName.equals(username)) {
                        return resultNode.get("userId").asInt();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("No such user with provided username");
            return -1;
        }
        return -1;
    }

    public static int getUserPoints(String userId, String token) {
        String user = getUser(username, token);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(user);
            JsonNode resultsNode = rootNode.get("results");

            if (resultsNode.isArray()) {
                for (JsonNode resultNode : resultsNode) {
                    String objId = resultNode.get("userId").asText();
                    if (objId.equals(userId)) {
                        return resultNode.get("points").asInt();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("No such user with provided username");
            return -1;
        }
        return -1;
    }

    public static void setUserPoints(int id, int pointsDiff, String token) {
        String body = String.format("{\"userId\": %s, \"points\": %s}", id, pointsDiff);
        Map<String, String> headers = Map.of("Content-Type", "application/json");

        try {
            String response = sendPostRequest(urlSetPoints, headers, body, token);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("Error updating points on the API end");
        }
    }

    private static String getUser(String username, String token) {
        String url = "https://192.168.0.246/users?=username=" + username;

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET();

        requestBuilder.header("Authorization", "Bearer " + token);

        HttpRequest request = requestBuilder.build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            System.out.println("An error has occured while requesting the ID of the user");
            return null;
        }
    }

    private static String sendPostRequest(String url, Map<String, String> headers, String body, String token) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body));

        if (headers != null) {
            headers.forEach(requestBuilder::header);
        }

        if (token != null) {
            requestBuilder.header("Authorization", "Bearer " + token);
        }

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

}
