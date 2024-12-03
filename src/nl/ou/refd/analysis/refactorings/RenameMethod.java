package nl.ou.refd.analysis.refactorings;

import nl.ou.refd.analysis.DangerAggregator;
import nl.ou.refd.analysis.VerdictFunction;
import nl.ou.refd.analysis.detectors.BrokenLocalReferences;
import nl.ou.refd.analysis.detectors.CorrespondingSubclassSpecification;
import nl.ou.refd.analysis.detectors.LostSpecification;
import nl.ou.refd.analysis.detectors.MissingAbstractImplementation;
import nl.ou.refd.analysis.detectors.MissingDefinition;
import nl.ou.refd.analysis.detectors.MissingSuperImplementation;
import nl.ou.refd.analysis.detectors.RemovedConcreteOverride;
import nl.ou.refd.analysis.microsteps.MoveMethod;
import nl.ou.refd.locations.collections.ClassSet;
import nl.ou.refd.locations.collections.MethodSet;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.locations.streams.ClassStream;
import nl.ou.refd.locations.streams.InstructionStream;

/**
 * Class representing a Rename Method refactoring. This refactoring can be analyzed by
 * using a DangerAnalyzer object.
 */
public class RenameMethod extends Refactoring {

	private final MethodSpecification target;
	
	private final boolean toDirectSuperclass;
	
	private String name = "Rename Method";
	
	/**
	 *  
	 *  
	 * @param target method that should be pull upped.
	 * @param string name of new method.
	 */
	public RenameMethod(MethodSpecification target, String newMethodName) {
		this.target = target;
		this.toDirectSuperclass = false;
		
		MethodSpecification newLocation = target.copy();
		newLocation.setMethodName(newMethodName);
		newLocation.setEnclosingClass(target.getEnclosingClass());
		
		microstep(new MoveMethod(target, newLocation));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public VerdictFunction verdictFunction(DangerAggregator aggregator) {
		
		return new VerdictFunction(aggregator) {
			@Override
			public void visit(CorrespondingSubclassSpecification.Method detector) {
				partial(detector, detector.actualRisks().stream().differenceWithMethods(new MethodSet(target).stream()).collect());
			}
			
			@Override
			public void visit(MissingDefinition.Method detector) {
				none(detector);
			}
			
			@Override
			public void visit(MissingAbstractImplementation.Method detector) {
				none(detector);
			}
			
			@Override
			public void visit(RemovedConcreteOverride.Method detector) {
				if (toDirectSuperclass) {
					none(detector);
				}
				else {
					all(detector);
				}
			}
			
			@Override
			public void visit(LostSpecification.Method detector) {
				if (toDirectSuperclass) {
					none(detector);
				}
				else {
					all(detector);
				}
			}
			
			@Override
			public void visit(MissingSuperImplementation.Method detector) {
				if (toDirectSuperclass) {
					none(detector);
				}
				else {
					all(detector);
				}
			}
			
		};
		
	}

	@Override
	public String getName() {
		return this.name;
	}	
}
