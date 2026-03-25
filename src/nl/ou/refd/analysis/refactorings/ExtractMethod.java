package nl.ou.refd.analysis.refactorings;

import nl.ou.refd.analysis.DangerAggregator;
import nl.ou.refd.analysis.VerdictFunction;
import nl.ou.refd.analysis.microsteps.AddMethod;
import nl.ou.refd.analysis.microsteps.AddMethodBody;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.generators.ProgramComponentsGenerator;
import nl.ou.refd.locations.specifications.MethodSpecification;

public class ExtractMethod extends Refactoring{

	private String name = "Extract Method";
	
	public ExtractMethod(InstructionSet instructions, MethodSpecification newMethod) {
		// Check if the target method signature already exists in the enclosing class
		var existingMethods = new ProgramComponentsGenerator()
				.stream()
				.classes()
				.classesByName(newMethod.getEnclosingClass().getClassName())
				.methods()
				.methodsWithSignature(newMethod.getMethodName(), newMethod.getParameterTypes())
				.collect();

		// Only add the method if it doesn't already exist
		if (existingMethods.size() == 0) {
			microstep(new AddMethod(newMethod));
		}
		
		//who is responsible for adding the reference from the param? Let's use the current mynewmethod and scan with atlas how these varibales are connected and how i must link this.
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
