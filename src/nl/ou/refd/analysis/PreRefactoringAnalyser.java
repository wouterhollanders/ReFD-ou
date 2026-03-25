package nl.ou.refd.analysis;

import java.util.List;

import nl.ou.refd.analysis.decisions.RefactoringDecisionPoint;

/**
 * Scans code before Refactoring to identify possible risks (which form decision points).
 */
public interface PreRefactoringAnalyser {
	/**
	 * Scans the given context and returns decision points to present to the actor.
	 */
	List<RefactoringDecisionPoint> scan(PreRefactoringContext context);
}
