package nl.ou.refd.analysis.microsteps;

import nl.ou.refd.analysis.ModelVisitor;
import nl.ou.refd.analysis.detectors.LostVariableMutation;
import nl.ou.refd.analysis.detectors.MissingLocalReferences;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.collections.MethodSet;
import nl.ou.refd.locations.graph.Graph;
import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.Relation;
import nl.ou.refd.locations.graph.Tags;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.specifications.MethodSpecification;

/**
 * Class representing an AddMethod microstep.
 */
public class AddMethodBody extends Microstep {

	private final MethodSpecification method;
	private final InstructionSet instructions;

	/**
	 * Creates the microstep with specification of the method to add.
	 * 
	 * @param classToAdd the specification of the method to add
	 */
	public AddMethodBody(InstructionSet instructions, MethodSpecification method) {
		this.instructions = instructions;
		this.method = method;

		// potentialRisk(new BrokenLocalReferences.Body(instructions.stream(),
		// method.getEnclosingClass()));
		potentialRisk(new MissingLocalReferences.Body(instructions.stream(), method));
		potentialRisk(new LostVariableMutation.Body(instructions.stream(), method));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(ModelVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeOnGraph(Graph graph) {
		// should be placed in the instruction set ideally but this is more convienient
		// for now.
		ProgramLocation methodLocation = new MethodSet(method).singleLocation();

		// add link to each existing instruction to the new method
		for (nl.ou.refd.locations.graph.ProgramLocation instr : this.instructions.locations()) {
			// ensure the instruction is tagged as a control-flow node
			instr.tag(nl.ou.refd.locations.graph.Tags.ProgramLocation.CONTROL_FLOW_NODE);

			// create a relation from method -> instruction and tag it as control-flow (this
			// is how bodies are queried)
			nl.ou.refd.locations.graph.Relation hasCFR = graph.createRelation(methodLocation, instr);
			hasCFR.tag(nl.ou.refd.locations.graph.Tags.Relation.HAS_CONTROL_FLOW);

			// also mark containment to match other constructs that use CONTAINS for
			// containment traversals
			hasCFR.tag(nl.ou.refd.locations.graph.Tags.Relation.CONTAINS);
		}

		// Reroute variable uses inside the copied instructions to matching method
		// parameters
		// If a parameter with the same name (and ideally type) exists, ensure DATAFLOW
		// comes from the parameter
		// so MissingLocalReferences no longer flags the variable as an external
		// dependency.
		// Collect method parameters by name
		java.util.Map<String, ProgramLocation> paramsByName = new java.util.HashMap<>();
		for (Relation r : methodLocation.out(nl.ou.refd.locations.graph.Tags.Relation.HAS_PARAMETER)) {
			ProgramLocation p = r.to();
			Object n = p.getAttribute(nl.ou.refd.locations.graph.Tags.Attributes.NAME);
			if (n instanceof String) {
				paramsByName.put((String) n, p);
			}
		}

		// Compute dataflow from the parameter and intersect with the method body
		GraphQuery methodBody = Graph.query(methodLocation).contained();
		for (java.util.Map.Entry<String, ProgramLocation> entry : paramsByName.entrySet()) {
			ProgramLocation matchingLocalVar = null;
			for (var edge : methodLocation.out(Tags.Relation.HAS_VARIABLE)) {
				ProgramLocation v = edge.to();
				Object vn = v.getAttribute(Tags.Attributes.NAME);
				if (vn instanceof String && entry.getKey().equals(vn)) {
					matchingLocalVar = v; // parameter has a corresponding local variable usage in the body
					break;
				}
			}

			if (matchingLocalVar != null) {
				ProgramLocation paramLocation = entry.getValue();
				boolean alreadyLinked = false;
				for (Relation df : paramLocation.out(Tags.Relation.DATAFLOW)) {
					if (df.to().equals(matchingLocalVar)) {
						alreadyLinked = true;
						break;
					}
				}
				if (!alreadyLinked) {
					Relation link = graph.createRelation(paramLocation, matchingLocalVar);
					link.tag(Tags.Relation.DATAFLOW);
				}
			}

		}
	}

}
