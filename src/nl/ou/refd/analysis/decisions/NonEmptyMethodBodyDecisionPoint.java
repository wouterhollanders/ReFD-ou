package nl.ou.refd.analysis.decisions;

import java.util.Collections;
import java.util.List;

import nl.ou.refd.analysis.Risk;
import nl.ou.refd.locations.specifications.MethodSpecification;

/**
 * Decision point indicating that the target method's body is not empty
 * and should be emptied (or a different target chosen) before continuing.
 */
public class NonEmptyMethodBodyDecisionPoint extends RefactoringDecisionPoint {

	private final MethodSpecification targetMethod;

	public NonEmptyMethodBodyDecisionPoint(MethodSpecification targetMethod) {
		super(new Risk() {
			@Override public String getRiskId() { return targetMethod.getMethodName(); }
			@Override public String getDescription() {
				return "Target method body is not empty: "
						+ targetMethod.getEnclosingClass().getClassName()
						+ "." + targetMethod.getMethodName();
			}
		});
		this.targetMethod = targetMethod;
	}

	@Override
	public List<RefactoringDecision> getPossibleDecisions() {
		return Collections.singletonList(
				new RefactoringDecision(
						"GUIDANCE",
						"Empty method body",
						"Remove statements from the target method body or choose a different empty target method, then re-run validation"));
	}

	@Override
	public void applyDecision(RefactoringDecision decision, MethodSpecification targetMethod) {
		// No automatic action; this is guidance-only.
	}

	public MethodSpecification getTargetMethod() {
		return targetMethod;
	}
}
