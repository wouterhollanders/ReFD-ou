package nl.ou.refd.analysis.decisions;

import nl.ou.refd.analysis.PreRefactoringContext;
import nl.ou.refd.locations.generators.ProgramComponentsGenerator;
import nl.ou.refd.locations.specifications.MethodSpecification;

/**
 * Precondition: the target method signature must already exist in the enclosing class.
 * When violated, guidance is shown to prompt the actor to create the method signatur first.
 */
public class MethodSignatureExistsPrecondition extends Precondition {

	@Override
	public boolean isSatisfied(PreRefactoringContext context) {
		MethodSpecification targetMethod = context.getTargetMethod()
				.orElseThrow(() -> new IllegalArgumentException("Extract Method requires target MethodSpecification"));

		var locations = new ProgramComponentsGenerator().stream().classes()
				.classesByName(targetMethod.getEnclosingClass().getClassName()).methods()
				.methodsWithSignature(targetMethod.getMethodName(), targetMethod.getParameterTypes()).collect();

		return locations.size() > 0;
	}

	@Override
	public RefactoringDecisionPoint guidanceWhenViolated(PreRefactoringContext context) {
		MethodSpecification targetMethod = context.getTargetMethod()
				.orElseThrow(() -> new IllegalArgumentException("Extract Method requires target MethodSpecification"));

		return new MissingMethodSignatureDecisionPoint(targetMethod);
	}
}
