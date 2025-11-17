package ai.config;

/**
 * Configuration settings for different AI difficulty levels.
 * Controls behavior, reasoning depth, and fallback probability.
 */
public class DifficultyConfig {
    private final String level;
    private final boolean useSimplePrompts;
    private final double fallbackProbability;
    private final int turnHistoryLimit;
    private final boolean useTypeEffectiveness;
    private final boolean usePowerAnalysis;
    private final boolean useStrategicThinking;

    private DifficultyConfig(String level, boolean useSimplePrompts, double fallbackProbability,
                            int turnHistoryLimit, boolean useTypeEffectiveness,
                            boolean usePowerAnalysis, boolean useStrategicThinking) {
        this.level = level;
        this.useSimplePrompts = useSimplePrompts;
        this.fallbackProbability = fallbackProbability;
        this.turnHistoryLimit = turnHistoryLimit;
        this.useTypeEffectiveness = useTypeEffectiveness;
        this.usePowerAnalysis = usePowerAnalysis;
        this.useStrategicThinking = useStrategicThinking;
    }

    /**
     * Easy difficulty: Random-like behavior with occasional correct decisions
     */
    public static DifficultyConfig easy() {
        return new DifficultyConfig(
                "easy",
                true,          // Simple prompts
                0.3,           // 30% chance to use fallback (intentional mistakes)
                0,             // No turn history
                true,          // Basic type effectiveness only
                false,         // No power analysis
                false          // No strategic thinking
        );
    }

    /**
     * Medium difficulty: Balanced strategic play
     */
    public static DifficultyConfig medium() {
        return new DifficultyConfig(
                "medium",
                false,         // Balanced prompts
                0.1,           // 10% chance for fallback
                3,             // Last 3 turns in context
                true,          // Type effectiveness
                true,          // Power + accuracy consideration
                false          // Limited strategic thinking
        );
    }

    /**
     * Hard difficulty: Advanced strategic thinking
     */
    public static DifficultyConfig hard() {
        return new DifficultyConfig(
                "hard",
                false,         // Strategic prompts
                0.0,           // No intentional fallback (only on API error)
                Integer.MAX_VALUE, // Full turn history
                true,          // Type effectiveness
                true,          // Power + accuracy
                true           // Multi-turn planning, stat changes, etc.
        );
    }

    /**
     * Get configuration for a difficulty level string
     */
    public static DifficultyConfig forLevel(String level) {
        if (level == null) {
            return medium();
        }

        switch (level.toLowerCase()) {
            case "easy":
                return easy();
            case "hard":
                return hard();
            case "medium":
            default:
                return medium();
        }
    }

    public String getLevel() {
        return level;
    }

    public boolean useSimplePrompts() {
        return useSimplePrompts;
    }

    public double getFallbackProbability() {
        return fallbackProbability;
    }

    public int getTurnHistoryLimit() {
        return turnHistoryLimit;
    }

    public boolean useTypeEffectiveness() {
        return useTypeEffectiveness;
    }

    public boolean usePowerAnalysis() {
        return usePowerAnalysis;
    }

    public boolean useStrategicThinking() {
        return useStrategicThinking;
    }

    /**
     * Check if fallback should be used based on random chance
     */
    public boolean shouldUseFallback() {
        return Math.random() < fallbackProbability;
    }

    @Override
    public String toString() {
        return "DifficultyConfig{" +
                "level='" + level + '\'' +
                ", fallbackProbability=" + fallbackProbability +
                ", turnHistoryLimit=" + turnHistoryLimit +
                '}';
    }
}
