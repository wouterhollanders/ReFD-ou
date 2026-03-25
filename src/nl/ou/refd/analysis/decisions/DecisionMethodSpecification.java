package nl.ou.refd.analysis.decisions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nl.ou.refd.locations.specifications.MethodSpecification;

public class DecisionMethodSpecification {
    private MethodSpecification methodSpec;
    private Map<String, RefactoringDecision> decisions;
    
    public DecisionMethodSpecification(MethodSpecification spec) {
        this.methodSpec = spec;
        this.decisions = new HashMap<>();
    }
    
    public void recordDecision(String riskId, RefactoringDecision decision) {
        this.decisions.put(riskId, decision);
    }
    
    public MethodSpecification getMethodSpecification() {
        return methodSpec;
    }
    
    public Map<String, RefactoringDecision> getDecisions() {
        return Collections.unmodifiableMap(decisions);
    }
}
