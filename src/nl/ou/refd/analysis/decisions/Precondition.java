package nl.ou.refd.analysis.decisions;

import nl.ou.refd.analysis.PreRefactoringContext;

/**
 * A condition that must be satisfied before a RemedyOption (RefactoringDecision)
 * can be successfully applied. If unsatisfied, it provides a guidance decision
 * point to steer the actor toward resolving the blocker.
 */
public abstract class Precondition {

	/**
	 * Returns true when this precondition holds for the given context.
	 */
	public abstract boolean isSatisfied(PreRefactoringContext context);

	/**
	 * Returns a decision point that guides the actor on how to satisfy this
	 * precondition. Called only when is satisfied returns false.
	 */
	public abstract RefactoringDecisionPoint guidanceWhenViolated(PreRefactoringContext context);
}
