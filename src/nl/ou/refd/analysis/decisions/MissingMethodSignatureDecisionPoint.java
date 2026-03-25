package nl.ou.refd.analysis.decisions;

import java.util.Collections;
import java.util.List;

import nl.ou.refd.analysis.Risk;
import nl.ou.refd.locations.specifications.MethodSpecification;

/**
 * Guidance-only decision point indicating that the target method signature
 * does not exist yet and should be created before continuing.
 */
public class MissingMethodSignatureDecisionPoint extends RefactoringDecisionPoint {

	private final MethodSpecification targetMethod;

	public MissingMethodSignatureDecisionPoint(MethodSpecification targetMethod) {
		super(new Risk() {
			@Override public String getRiskId() { return targetMethod.getMethodName(); }
			@Override public String getDescription() {
				return "Missing target method signature: "
						+ targetMethod.getEnclosingClass().getClassName()
						+ "." + targetMethod.getMethodName()
						+ targetMethod.getParameterTypes();
			}
		});
		this.targetMethod = targetMethod;
	}

	@Override
	public List<RefactoringDecision> getPossibleDecisions() {
		return Collections.singletonList(
				new RefactoringDecision(
						"GUIDANCE",
						"Create method",
						"Add the method with the specified signature in the enclosing class, then re-run validation"));
	}

	@Override
	public void applyDecision(RefactoringDecision decision, MethodSpecification targetMethod) {
		// No-op: this decision conveys guidance; the user must create the method in code.
	}

	public MethodSpecification getTargetMethod() {
		return targetMethod;
	}
}
