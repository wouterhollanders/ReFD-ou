package nl.ou.refd.analysis.decisions;

import java.util.List;

import nl.ou.refd.analysis.Risk;
import nl.ou.refd.locations.specifications.MethodSpecification;

/**
 * Represents a decision point in a refactoring where the actor must choose
 * how to handle a specific risk. This enables dynamic design decisions.
 */
public abstract class RefactoringDecisionPoint {

	private final Risk risk;

	public RefactoringDecisionPoint(Risk risk) {
		this.risk = risk;
	}

	/**
	 * Gets the list of possible resolutions for this decision point
	 */
	public abstract List<RefactoringDecision> getPossibleDecisions();

	/**
	 * Applies the chosen decision to the refactoring specification
	 */
	public abstract void applyDecision(RefactoringDecision decision,
			MethodSpecification targetMethod);

	public Risk getRisk() { return risk; }
	public String getRiskDescription() { return risk.getDescription(); }
	public String getRiskIdentifier()  { return risk.getRiskId(); }
}
