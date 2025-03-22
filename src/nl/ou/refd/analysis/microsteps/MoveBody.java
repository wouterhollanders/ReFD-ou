package nl.ou.refd.analysis.microsteps;

import nl.ou.refd.analysis.ModelVisitor;
import nl.ou.refd.analysis.detectors.BrokenLocalReferences;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.specifications.InstructionSpecification;
import nl.ou.refd.locations.specifications.MethodSpecification;


public class MoveBody extends CompositeMicrostep {

	
	public MoveBody(MethodSpecification destinationMethod, InstructionSpecification body, InstructionSet instructionSet) {

		
		var stream = instructionSet.stream();
		potentialRisk(new BrokenLocalReferences.Body(stream, body.getEnclosingMethod().getEnclosingClass()));
		
		compositeMicrostep(new AddInstruction(body));
		//compositeMicrostep(new RemoveMethod(target));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}

}
