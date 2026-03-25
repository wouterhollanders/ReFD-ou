package nl.ou.refd.analysis.microsteps;

import java.util.ArrayList;
import nl.ou.refd.analysis.ModelVisitor;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.locations.specifications.ParameterSpecification;

/**
 * Simple microstep that adds a parameter to an existing method in the model.
 * This modifies both the in-memory `MethodSpecification` and the program graph.
 */
public class AddParameter extends CompositeMicrostep {


	public AddParameter(MethodSpecification targetMethod, ParameterSpecification parameter) {
		var destination = targetMethod.copy();
		var paramList = new ArrayList<ParameterSpecification>();
		paramList.add(parameter);
		destination.setParameters(paramList);
		
		compositeMicrostep(new AddMethod(destination));
		compositeMicrostep(new RemoveMethod(targetMethod));

	}

	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}
}
