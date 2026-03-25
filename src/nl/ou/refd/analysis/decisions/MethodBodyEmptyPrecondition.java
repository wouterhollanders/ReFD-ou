package nl.ou.refd.analysis.decisions;

import nl.ou.refd.analysis.PreRefactoringContext;
import nl.ou.refd.locations.generators.ProgramComponentsGenerator;
import nl.ou.refd.locations.specifications.MethodSpecification;

/**
 * Precondition: the target method must have an empty body so that extracted code
 * can be placed inside it. When violated, guidance is shown to prompt the actor
 * to empty the method body or pick a different target method.
 */
public class MethodBodyEmptyPrecondition extends Precondition {

	//TODO: Does not completely work but expresses what it should do.
	@Override
	public boolean isSatisfied(PreRefactoringContext context) {
		MethodSpecification targetMethod = context.getTargetMethod()
				.orElseThrow(() -> new IllegalArgumentException("Extract Method requires target MethodSpecification"));

		var emptyMethodsInClass = new ProgramComponentsGenerator().stream().classes()
				.classesByName(targetMethod.getEnclosingClass().getClassName()).methods()
				.methodsWithEmptyBodies().collect();

		var emptyByName = emptyMethodsInClass.stream()
				.filterByName(targetMethod.getMethodName())
				.collect();

		return emptyByName.size() > 0;
	}

	@Override
	public RefactoringDecisionPoint guidanceWhenViolated(PreRefactoringContext context) {
		MethodSpecification targetMethod = context.getTargetMethod()
				.orElseThrow(() -> new IllegalArgumentException("Extract Method requires target MethodSpecification"));

		return new NonEmptyMethodBodyDecisionPoint(targetMethod);
	}
}
