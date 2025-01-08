package nl.ou.refd.analysis.refactorings;

import java.util.ArrayList;
import java.util.List;

import nl.ou.refd.analysis.DangerAggregator;
import nl.ou.refd.analysis.VerdictFunction;
import nl.ou.refd.analysis.detectors.CorrespondingSubclassSpecification;
import nl.ou.refd.analysis.detectors.LostSpecification;
import nl.ou.refd.analysis.detectors.MissingAbstractImplementation;
import nl.ou.refd.analysis.detectors.MissingDefinition;
import nl.ou.refd.analysis.detectors.MissingSuperImplementation;
import nl.ou.refd.analysis.detectors.RemovedConcreteOverride;
import nl.ou.refd.analysis.microsteps.AddClass;
import nl.ou.refd.analysis.microsteps.AddMethod;
import nl.ou.refd.analysis.microsteps.MoveMethod;
import nl.ou.refd.analysis.microsteps.RemoveMethod;
import nl.ou.refd.locations.collections.MethodSet;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.MethodSpecification;

/**
 * Class representing a Form Template Method refactoring. This refactoring can
 * be analyzed by using a DangerAnalyzer object.
 */
public class FormTemplateMethod extends Refactoring {

	private String name = "Form Template Method";
	private List<Refactoring> subRefactorings = new ArrayList<>();
	// private final MethodSpecification target;

	/**
	 * Creates the Form Template Method refactoring with a destination class which
	 * will be the super class, and a list of target methods which will be moved to
	 * the super class. The subclasses are defined as a list of ClassSpecification.
	 * The methods with shared common logic are defined within a list of
	 * MethodSpecification.
	 * 
	 * @param destination class which will be created
	 * @param targets     list of methods which will be moved to the newly created
	 *                    destination class
	 */
	public FormTemplateMethod(ClassSpecification destinationSuper, List<MethodSpecification> methodsToPull,
			List<ClassSpecification> destinationSubClasses, List<MethodSpecification> methodsToCreate) {

		List<MethodSpecification> distinctList = new ArrayList<>();

		for (MethodSpecification subClassMethod : methodsToCreate) {

			// Add the method to every subclass.
			microstep(new AddMethod(subClassMethod));

			// Add the abstract definition for the super class.
			if (distinctList.stream().noneMatch(m -> m.getMethodName().equals(subClassMethod.getMethodName()))) {
				distinctList.add(CreateAbstractMethod(subClassMethod, destinationSuper));
			}

		}

		for (MethodSpecification distinctItem : distinctList) {

			// Add every abstract definition to the super class.
			microstep(new AddMethod(distinctItem));
		}

		MethodSpecification method = methodsToPull.get(0);
		var pullUpMethod = new PullUpMethod(method, destinationSuper);
		this.subRefactorings.add(pullUpMethod);
		
		List<MethodSpecification> modifiableList = new ArrayList<>(methodsToPull);

		modifiableList.remove(method);

		for (MethodSpecification pullUp : modifiableList) {

			// Add every abstract definition to the super class.
			microstep(new RemoveMethod(pullUp));
		}
	}

	private MethodSpecification CreateAbstractMethod(MethodSpecification method, ClassSpecification enclosingClass) {
		var abstractMethod = new MethodSpecification(method.getMethodName(), method.getParameters(),
				method.getVisibility(), method.isStatic(), true, method.getReturnType(), enclosingClass);

		return abstractMethod;
	}

	public List<Refactoring> getSubRefactorings() {
		return subRefactorings;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VerdictFunction verdictFunction(DangerAggregator aggregator) {
		// TODO: No MissingSuperImplementation when the overridden method is also
		// combined
		return new VerdictFunction(aggregator) {
		};

	}

	@Override
	public String getName() {
		return this.name;
	}

}
