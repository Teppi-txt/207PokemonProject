package ai.graph.nodes;

import ai.config.DifficultyConfig;
import ai.fallback.RuleBasedDecisionMaker;
import ai.graph.BattleDecisionState;
import ai.graph.Decision;
import ai.parsers.LLMResponseParser;
import ai.prompts.PromptTemplates;
import ai.serializers.BattleContextSerializer;
import org.bsc.langgraph4j.action.NodeAction;
import shared.GroqAPIClient;
import shared.GroqAPIClient.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LangGraph4j node that calls the Groq LLM to make battle decisions.
 * Includes fallback logic for API failures.
 */
public class LLMDecisionNode implements NodeAction<BattleDecisionState> {

    private final GroqAPIClient groqClient;

    public LLMDecisionNode() {
        try {
            this.groqClient = new GroqAPIClient();
        } catch (GroqAPIClient.GroqAPIException e) {
            throw new RuntimeException("Failed to initialize GroqAPIClient", e);
        }
    }

    public LLMDecisionNode(GroqAPIClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public Map<String, Object> apply(BattleDecisionState state) throws Exception {
        System.out.println("[LLMDecisionNode] Node executing...");
        Map<String, Object> updates = new HashMap<>();

        // Check if we should use fallback based on difficulty
        DifficultyConfig config = DifficultyConfig.forLevel(state.getDifficulty());
        if (config.shouldUseFallback()) {
            System.out.println("[LLMDecisionNode] Using fallback (difficulty-based skip)");
            updates.put("useFallback", true);
            updates.put("fallbackReason", "Intentional fallback for difficulty level");
            Decision fallbackDecision = makeFallbackDecision(state);
            updates.put("currentDecision", fallbackDecision);
            return updates;
        }

        try {
            // Serialize battle context
            String context = BattleContextSerializer.serializeForLLM(state);

            // Determine if this is a move or switch decision
            boolean isSwitch = "switch".equals(state.getMetadata().get("decisionType"));
            System.out.println("[LLMDecisionNode] Decision type: " + (isSwitch ? "SWITCH" : "MOVE"));

            // Build messages for LLM
            List<Message> messages = buildMessages(state.getDifficulty(), context, isSwitch);

            // Call Groq API
            System.out.println("[LLMDecisionNode] >>> Calling Groq LLM API...");
            long startTime = System.currentTimeMillis();
            GroqAPIClient.ChatCompletionResponse response = groqClient.createChatCompletion(messages);
            long endTime = System.currentTimeMillis();
            System.out.println("[LLMDecisionNode] <<< LLM API response received in " + (endTime - startTime) + "ms");
            System.out.println("[LLMDecisionNode] Model: " + response.getModel() + ", Tokens: " + response.getTotalTokens());

            String llmResponse = response.getContent();
            System.out.println("[LLMDecisionNode] LLM Response: " + llmResponse.substring(0, Math.min(100, llmResponse.length())) + "...");

            // Parse response into Decision
            Decision decision = LLMResponseParser.parseDecision(llmResponse, state);

            if (decision != null) {
                System.out.println("[LLMDecisionNode] Parsed decision: " + decision.toString());
                updates.put("currentDecision", decision);
                updates.put("llmResponse", llmResponse);
                updates.put("useFallback", false);
            } else {
                // Failed to parse - use fallback
                System.out.println("[LLMDecisionNode] Failed to parse LLM response, using fallback");
                updates.put("useFallback", true);
                updates.put("fallbackReason", "Failed to parse LLM response");
                Decision fallbackDecision = makeFallbackDecision(state);
                updates.put("currentDecision", fallbackDecision);
            }

        } catch (GroqAPIClient.GroqAPIException e) {
            // API error - use fallback
            System.out.println("[LLMDecisionNode] API ERROR: " + e.getMessage() + ", using fallback");
            updates.put("useFallback", true);
            updates.put("fallbackReason", "API error: " + e.getMessage());
            updates.put("errorMessage", e.getMessage());
            Decision fallbackDecision = makeFallbackDecision(state);
            updates.put("currentDecision", fallbackDecision);
        }

        return updates;
    }

    /**
     * Build messages for LLM
     */
    private List<Message> buildMessages(String difficulty, String context, boolean isSwitch) {
        List<Message> messages = new ArrayList<>();

        // System message
        String systemPrompt = PromptTemplates.getSystemPrompt(difficulty);
        messages.add(new Message("system", systemPrompt));

        // User message with context
        String userPrompt;
        if (isSwitch) {
            userPrompt = PromptTemplates.getSwitchPrompt(difficulty, context);
        } else {
            userPrompt = PromptTemplates.getMovePrompt(difficulty, context);
        }
        messages.add(new Message("user", userPrompt));

        return messages;
    }

    /**
     * Make fallback decision using rule-based logic
     */
    private Decision makeFallbackDecision(BattleDecisionState state) {
        boolean isSwitch = "switch".equals(state.getMetadata().get("decisionType"));
        return RuleBasedDecisionMaker.makeDecision(state, isSwitch);
    }
}
