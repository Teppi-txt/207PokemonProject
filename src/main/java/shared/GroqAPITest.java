package shared;

import shared.GroqAPIClient.ChatCompletionResponse;
import shared.GroqAPIClient.GroqAPIException;
import shared.GroqAPIClient.Message;

import java.util.ArrayList;
import java.util.List;

public class GroqAPITest {
    public static void main(String[] args) {
        System.out.println("=== Groq API Test ===\n");

        try {
            GroqAPIClient client = new GroqAPIClient();

            List<Message> messages = new ArrayList<>();
            messages.add(new Message("user", "hi what model are you"));

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
