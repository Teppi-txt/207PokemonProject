package ai.prompts;

/**
 * Prompt templates for LLM-based battle decisions.
 * Different templates for different difficulty levels and decision types.
 */
public class PromptTemplates {

    // System prompts
    public static final String SYSTEM_PROMPT_EASY =
        "You are a beginner Pokemon battle AI. Make simple decisions based on basic type advantages. " +
        "Keep your reasoning brief and focus only on type matchups.";

    public static final String SYSTEM_PROMPT_MEDIUM =
        "You are an intermediate Pokemon battle AI. Consider type effectiveness, move power, and accuracy " +
        "when making decisions. Think tactically about the current turn.";

    public static final String SYSTEM_PROMPT_HARD =
        "You are an expert Pokemon battle AI. Consider type effectiveness, move power, accuracy, stat advantages, " +
        "HP percentages, and multi-turn strategy. Think several moves ahead and make optimal decisions.";

    // Move selection prompts
    public static final String MOVE_SELECTION_PROMPT_EASY =
        "Choose the best move for your Pokemon to use.\n\n" +
        "{context}\n\n" +
        "Pick a move based on type effectiveness. Respond with:\n" +
        "MOVE: [move number 1-4]\n" +
        "REASONING: [brief explanation]";

    public static final String MOVE_SELECTION_PROMPT_MEDIUM =
        "Analyze the battle situation and choose the best move.\n\n" +
        "{context}\n\n" +
        "Consider:\n" +
        "- Type effectiveness (super effective, not very effective, etc.)\n" +
        "- Move power and accuracy\n" +
        "- Current HP of both Pokemon\n\n" +
        "Respond with:\n" +
        "MOVE: [move number 1-4]\n" +
        "REASONING: [your tactical analysis]";

    public static final String MOVE_SELECTION_PROMPT_HARD =
        "Analyze the battle strategically and choose the optimal move.\n\n" +
        "{context}\n\n" +
        "Consider:\n" +
        "- Type effectiveness and damage multipliers\n" +
        "- Move power, accuracy, and priority\n" +
        "- HP percentages and potential KOs\n" +
        "- Stat advantages/disadvantages\n" +
        "- Multi-turn strategy and positioning\n" +
        "- Recent battle history and patterns\n\n" +
        "Respond with:\n" +
        "MOVE: [move number 1-4]\n" +
        "REASONING: [detailed strategic analysis]";

    // Switch selection prompts
    public static final String SWITCH_SELECTION_PROMPT_EASY =
        "Your Pokemon needs to be switched out. Choose a replacement.\n\n" +
        "{context}\n\n" +
        "Pick a Pokemon with a type advantage. Respond with:\n" +
        "SWITCH: [Pokemon name]\n" +
        "REASONING: [brief explanation]";

    public static final String SWITCH_SELECTION_PROMPT_MEDIUM =
        "Analyze the situation and choose which Pokemon to switch in.\n\n" +
        "{context}\n\n" +
        "Consider:\n" +
        "- Type matchups against opponent's active Pokemon\n" +
        "- HP and available moves of each Pokemon\n" +
        "- Which Pokemon has the best chance to win\n\n" +
        "Respond with:\n" +
        "SWITCH: [Pokemon name]\n" +
        "REASONING: [your tactical analysis]";

    public static final String SWITCH_SELECTION_PROMPT_HARD =
        "Strategically analyze the battle and choose the optimal Pokemon to switch in.\n\n" +
        "{context}\n\n" +
        "Consider:\n" +
        "- Type matchups and effectiveness\n" +
        "- HP percentages and battle readiness\n" +
        "- Available moves and their utility\n" +
        "- Stat advantages/disadvantages\n" +
        "- Long-term battle strategy\n" +
        "- Preserving strong Pokemon for later\n\n" +
        "Respond with:\n" +
        "SWITCH: [Pokemon name]\n" +
        "REASONING: [detailed strategic analysis]";

    /**
     * Get system prompt for difficulty level
     */
    public static String getSystemPrompt(String difficulty) {
        if (difficulty == null) {
            return SYSTEM_PROMPT_MEDIUM;
        }

        switch (difficulty.toLowerCase()) {
            case "easy":
                return SYSTEM_PROMPT_EASY;
            case "hard":
                return SYSTEM_PROMPT_HARD;
            case "medium":
            default:
                return SYSTEM_PROMPT_MEDIUM;
        }
    }

    /**
     * Get move selection prompt for difficulty level
     */
    public static String getMovePrompt(String difficulty, String context) {
        String template;

        if (difficulty == null) {
            template = MOVE_SELECTION_PROMPT_MEDIUM;
        } else {
            switch (difficulty.toLowerCase()) {
                case "easy":
                    template = MOVE_SELECTION_PROMPT_EASY;
                    break;
                case "hard":
                    template = MOVE_SELECTION_PROMPT_HARD;
                    break;
                case "medium":
                default:
                    template = MOVE_SELECTION_PROMPT_MEDIUM;
                    break;
            }
        }

        return template.replace("{context}", context);
    }

    /**
     * Get switch selection prompt for difficulty level
     */
    public static String getSwitchPrompt(String difficulty, String context) {
        String template;

        if (difficulty == null) {
            template = SWITCH_SELECTION_PROMPT_MEDIUM;
        } else {
            switch (difficulty.toLowerCase()) {
                case "easy":
                    template = SWITCH_SELECTION_PROMPT_EASY;
                    break;
                case "hard":
                    template = SWITCH_SELECTION_PROMPT_HARD;
                    break;
                case "medium":
                default:
                    template = SWITCH_SELECTION_PROMPT_MEDIUM;
                    break;
            }
        }

        return template.replace("{context}", context);
    }
}
