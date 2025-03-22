package nl.ou.refd.analysis.refactorings;

import java.util.List;

import nl.ou.refd.analysis.DangerAggregator;
import nl.ou.refd.analysis.VerdictFunction;
import nl.ou.refd.analysis.microsteps.AddClass;
import nl.ou.refd.analysis.microsteps.AddInstruction;
import nl.ou.refd.analysis.microsteps.MoveBody;
import nl.ou.refd.analysis.microsteps.MoveMethod;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.InstructionSpecification;
import nl.ou.refd.locations.specifications.MethodSpecification;

/**
 * Class representing a Combine Methods into Class refactoring. This refactoring can be analyzed by
 * using a DangerAnalyzer object.
 */
public class ExtractMethod extends Refactoring {
	
	private String name = "Extract Method";
	
	/**
	 * Creates the Combine Methods into Class refactoring with a destinitian class which will be
	 * created, and a list of target methods which will be moved to the newly created destination
	 * class.
	 * @param destination class which will be created
	 * @param targets list of methods which will be moved to the newly created destination class
	 */
	public ExtractMethod(MethodSpecification destinationMethod, MethodSpecification sourceMethod, List<InstructionSpecification> instructionsToCreate, InstructionSet instructionSet) {
		microstep(new MoveBody(destinationMethod, instructionsToCreate.get(0), instructionSet));
		
//		for (MethodSpecification oldLocation : targets) {
//			MethodSpecification newLocation = oldLocation.copy();
//			newLocation.setEnclosingClass(destination);
//			
//			microstep(new MoveMethod(oldLocation, newLocation));
//		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public VerdictFunction verdictFunction(DangerAggregator aggregator) {
		//TODO: No MissingSuperImplementation when the overridden method is also combined
		return new VerdictFunction(aggregator) {};
		
	}
	
	@Override
	public String getName() {
		return this.name;
	}	

}
