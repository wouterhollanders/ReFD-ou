package nl.ou.refd.analysis.decisions;

import java.util.Collections;
import java.util.List;

import nl.ou.refd.analysis.PreRefactoringContext;

/**
 * Represents a single remedy option at a decision point.
 * A decision may carry preconditions that must be satisfied before it can be applied.
 */
public class RefactoringDecision {

	private final String decisionId;
	private final String label;
	private final String description;
	private final List<Precondition> preconditions;

	public RefactoringDecision(String id, String label, String description) {
		this(id, label, description, Collections.emptyList());
	}

	public RefactoringDecision(String id, String label, String description, List<Precondition> preconditions) {
		this.decisionId = id;
		this.label = label;
		this.description = description;
		this.preconditions = preconditions;
	}

	public String getDecisionId() { return this.decisionId; }
	public String getLabel()      { return this.label; }

	/**
	 * Returns the preconditions that must hold before this remedy can be applied.
	 */
	public List<Precondition> getPreconditions() { return preconditions; }

	/**
	 * Validates all preconditions of this remedy against the given context.
	 * Returns guidance decision points for the first unsatisfied precondition,
	 * or an empty list if all preconditions are met.
	 */
	public List<RefactoringDecisionPoint> validatePreconditions(PreRefactoringContext context) {
		for (Precondition precondition : preconditions) {
			if (!precondition.isSatisfied(context)) {
				return List.of(precondition.guidanceWhenViolated(context));
			}
		}
		return List.of();
	}
}
