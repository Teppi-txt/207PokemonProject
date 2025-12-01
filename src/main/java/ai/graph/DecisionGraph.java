package ai.graph;

import ai.graph.nodes.*;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.GraphStateException;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * Decision engine using LangGraph4j StateGraph to orchestrate AI decision-making.
 * Builds a graph with nodes for analysis, evaluation, LLM decision, and validation.
 */
public class DecisionGraph {

    private CompiledGraph<BattleDecisionState> compiledGraph;

    public DecisionGraph() {
        this.compiledGraph = buildStateGraph();
    }

    /**
     * Build the LangGraph4j StateGraph with all decision nodes.
     */
    private CompiledGraph<BattleDecisionState> buildStateGraph() {
        try {
            // Create node instances
            AnalyzeBattleStateNode analyzeNode = new AnalyzeBattleStateNode();
            EvaluateOptionsNode evaluateNode = new EvaluateOptionsNode();
            LLMDecisionNode llmNode = new LLMDecisionNode();
            ValidateDecisionNode validateNode = new ValidateDecisionNode();

            // Build the StateGraph using LangGraph4j API
            // Wrap nodes with node_async to convert NodeAction to AsyncNodeAction
            StateGraph<BattleDecisionState> stateGraph = new StateGraph<>(
                BattleDecisionState.SCHEMA,
                BattleDecisionState::new
            )
            .addNode("analyze", node_async(analyzeNode))
            .addNode("evaluate", node_async(evaluateNode))
            .addNode("llm_decision", node_async(llmNode))
            .addNode("validate", node_async(validateNode))
            .addEdge(StateGraph.START, "analyze")
            .addEdge("analyze", "evaluate")
            .addEdge("evaluate", "llm_decision")
            .addEdge("llm_decision", "validate")
            .addEdge("validate", END);

            return stateGraph.compile();
        } catch (GraphStateException e) {
            throw new RuntimeException("Failed to build decision graph", e);
        }
    }

    /**
     * Execute the decision graph using LangGraph4j and return the final decision.
     */
    public Decision execute(BattleDecisionState initialState) throws Exception {
        System.out.println("[DecisionGraph] Starting graph execution...");
        System.out.println("[DecisionGraph] Difficulty: " + initialState.getDifficulty());

        // Convert state to initial data map for LangGraph4j
        java.util.Map<String, Object> initialData = stateToMap(initialState);

        // Execute the compiled graph using LangGraph4j's invoke method
        java.util.Optional<BattleDecisionState> resultOpt = compiledGraph.invoke(initialData);

        if (!resultOpt.isPresent()) {
            System.out.println("[DecisionGraph] ERROR: Graph execution failed - no result returned");
            throw new Exception("Graph execution failed - no result returned");
        }

        BattleDecisionState finalState = resultOpt.get();

        // Extract final decision
        Decision finalDecision = finalState.getCurrentDecision();
        if (finalDecision == null) {
            finalDecision = (Decision) finalState.getMetadata().get("currentDecision");
        }

        System.out.println("[DecisionGraph] Graph execution complete. Decision: " +
            (finalDecision != null ? finalDecision.toString() : "null"));

        return finalDecision;
    }

    /**
     * Convert BattleDecisionState to Map for LangGraph4j processing.
     */
    private java.util.Map<String, Object> stateToMap(BattleDecisionState state) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("battle", state.getBattle());
        map.put("aiPlayer", state.getAiPlayer());
        map.put("opponent", state.getOpponent());
        map.put("turnHistory", state.getTurnHistory());
        map.put("difficulty", state.getDifficulty());
        map.put("metadata", state.getMetadata());
        return map;
    }

    /**
     * Build a decision graph for the specified difficulty level.
     */
    public static DecisionGraph buildGraph(String difficulty) {
        // For now, all difficulty levels use the same graph structure
        // The difficulty affects behavior within the nodes (prompts, fallback probability, etc.)
        return new DecisionGraph();
    }
}
