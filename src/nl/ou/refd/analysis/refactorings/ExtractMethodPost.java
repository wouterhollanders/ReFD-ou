package nl.ou.refd.analysis.refactorings;

import nl.ou.refd.analysis.DangerAggregator;
import nl.ou.refd.analysis.VerdictFunction;
import nl.ou.refd.analysis.detectors.CorrespondingSubclassSpecification;
import nl.ou.refd.analysis.detectors.LostSpecification;
import nl.ou.refd.analysis.detectors.MissingAbstractImplementation;
import nl.ou.refd.analysis.detectors.MissingDefinition;
import nl.ou.refd.analysis.detectors.MissingSuperImplementation;
import nl.ou.refd.analysis.detectors.RemovedConcreteOverride;
import nl.ou.refd.analysis.microsteps.AddMethod;
import nl.ou.refd.analysis.microsteps.AddMethodBody;
import nl.ou.refd.locations.collections.ClassSet;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.collections.MethodSet;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.MethodSpecification;

//Should not be used. Only for post phase feasibility purposes.
public class ExtractMethodPost extends Refactoring{

	private String name = "Extract Method [POST]";
	
	public ExtractMethodPost(InstructionSet instructions, MethodSpecification newMethod) {
		microstep(new AddMethod(newMethod));
		microstep(new AddMethodBody(instructions, newMethod));
	}

	@Override
	public VerdictFunction verdictFunction(DangerAggregator aggregator) {
		
		return new VerdictFunction(aggregator) {

		};
		
	}
	
	@Override
	public String getName() {
		
		return name;
	}
}
