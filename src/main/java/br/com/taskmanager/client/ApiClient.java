package br.com.taskmanager.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:7000";

    public static void main(String[] args) throws IOException {
        System.out.println("### INICIANDO CLIENTE DA API DE TAREFAS ###");

        createTask();

        getAllTasks();

        UUID idParaBuscar = UUID.fromString("b57b0a7c-f053-4db8-abdf-e92a07b4037d");
        getTaskById(idParaBuscar);

        getStatus();

        System.out.println("### CLIENTE FINALIZADO ###");
    }

    public static void createTask() throws IOException {
        System.out.println("--- 1: Criando uma nova tarefa (POST /tarefas) ---");
        URL url = new URL(BASE_URL + "/tarefas");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = "{\"titulo\": \"Apresentação Projeto de Bloco\", \"descricao\": \"Se preparar para a apresentação do trabalho em grupo do Projeto de Bloco dia 17.\"}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Código da Resposta: " + responseCode);

        InputStream inputStream = (responseCode >= 200 && responseCode <= 299)
                ? connection.getInputStream()
                : connection.getErrorStream();

        String responseBody = readResponse(inputStream);
        System.out.println("Corpo da Resposta:\n" + responseBody + "\n");
        connection.disconnect();
    }

    public static void getAllTasks() throws IOException {
        System.out.println("--- 2: Listando todas as tarefas (GET /tarefas) ---");
        URL url = new URL(BASE_URL + "/tarefas");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Código da Resposta: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String responseBody = readResponse(connection.getInputStream());
            System.out.println("Corpo da Resposta:\n" + responseBody + "\n");
        }
        connection.disconnect();
    }

    public static void getTaskById(UUID id) throws IOException {
        System.out.println("--- 3: Buscando tarefa específica (GET /tarefas/" + id + ") ---");
        URL url = new URL(BASE_URL + "/tarefas/" + id.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Código da Resposta: " + responseCode);

        InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                ? connection.getInputStream()
                : connection.getErrorStream();

        String responseBody = readResponse(inputStream);
        System.out.println("Corpo da Resposta:\n" + responseBody + "\n");
        connection.disconnect();
    }

    public static void getStatus() throws IOException {
        System.out.println("--- 4: Verificando o status da API (GET /status) ---");
        URL url = new URL(BASE_URL + "/status");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        System.out.println("Código da Resposta: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String responseBody = readResponse(connection.getInputStream());
            System.out.println("Corpo da Resposta:\n" + responseBody + "\n");
        }
        connection.disconnect();
    }

    private static String readResponse(InputStream stream) throws IOException {
        if (stream == null) return "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        }
    }
}