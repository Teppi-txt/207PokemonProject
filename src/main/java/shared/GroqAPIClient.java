package shared;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroqAPIClient {
    private static final String API_ENDPOINT = "https://api.groq.com/openai/v1/chat/completions";
    private static final String DEFAULT_MODEL = "openai/gpt-oss-120b";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final String apiKey;

    public static class Message {
        private final String role;
        private final String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    public static class ChatCompletionResponse {
        private final String content;
        private final String model;
        private final int totalTokens;

        public ChatCompletionResponse(String content, String model, int totalTokens) {
            this.content = content;
            this.model = model;
            this.totalTokens = totalTokens;
        }

        public String getContent() {
            return content;
        }

        public String getModel() {
            return model;
        }

        public int getTotalTokens() {
            return totalTokens;
        }

        @Override
        public String toString() {
            return "ChatCompletionResponse{" +
                    "content='" + content + '\'' +
                    ", model='" + model + '\'' +
                    ", totalTokens=" + totalTokens +
                    '}';
        }
    }

    public static class GroqAPIException extends Exception {
        public GroqAPIException(String message) {
            super(message);
        }

        public GroqAPIException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public GroqAPIClient() throws GroqAPIException {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String key = dotenv.get("GROQ_API_KEY");
        if (key == null || key.trim().isEmpty()) {
            throw new GroqAPIException("GROQ_API_KEY is not set. Please set it in the .env file or as an environment variable.");
        }
        this.apiKey = key;
        this.client = new OkHttpClient();
    }

    public GroqAPIClient(String apiKey) throws GroqAPIException {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new GroqAPIException("GROQ_API_KEY is not set. Please set the GROQ_API_KEY environment variable.");
        }
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
    }

    public ChatCompletionResponse createChatCompletion(List<Message> messages) throws GroqAPIException {
        return createChatCompletion(messages, DEFAULT_MODEL);
    }

    public ChatCompletionResponse createChatCompletion(List<Message> messages, String model) throws GroqAPIException {
        try {
            JSONObject requestBody = buildRequestBody(messages, model);
            Request request = new Request.Builder()
                    .url(API_ENDPOINT)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    throw new GroqAPIException("API request failed with code " + response.code() + ": " + errorBody);
                }

                String responseBody = response.body().string();
                return parseResponse(responseBody);
            }
        } catch (IOException | JSONException e) {
            throw new GroqAPIException("Error communicating with Groq API", e);
        }
    }

    private JSONObject buildRequestBody(List<Message> messages, String model) {
        JSONObject requestBody = new JSONObject();
        JSONArray messagesArray = new JSONArray();

        for (Message message : messages) {
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", message.getRole());
            messageObj.put("content", message.getContent());
            messagesArray.put(messageObj);
        }

        requestBody.put("messages", messagesArray);
        requestBody.put("model", model);

        return requestBody;
    }

    private ChatCompletionResponse parseResponse(String responseBody) throws JSONException {
        JSONObject jsonResponse = new JSONObject(responseBody);

        String content = jsonResponse
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        String model = jsonResponse.getString("model");

        int totalTokens = jsonResponse
                .getJSONObject("usage")
                .getInt("total_tokens");

        return new ChatCompletionResponse(content, model, totalTokens);
    }

    public static void main(String[] args) {
        try {
            GroqAPIClient client = new GroqAPIClient();

            List<Message> messages = new ArrayList<>();
            messages.add(new Message("user", "Explain the importance of fast language models"));

            ChatCompletionResponse response = client.createChatCompletion(messages);

            System.out.println("Response: " + response.getContent());
            System.out.println("Model: " + response.getModel());
            System.out.println("Total tokens: " + response.getTotalTokens());

        } catch (GroqAPIException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
