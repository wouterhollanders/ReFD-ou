package nl.ou.refd.analysis.decisions;

import java.util.List;

import nl.ou.refd.analysis.Risk;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.graph.Graph;
import nl.ou.refd.locations.graph.Tags;
import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.locations.specifications.ParameterSpecification;

/**
 * Decision point for handling missing local references in Extract Method.
 * The associated risk is a missing variable reference that the extracted method
 * cannot access without an explicit remedy.
 */
public class MissingReferenceDecisionPoint extends RefactoringDecisionPoint {

	private final ProgramLocation missingReference;
	private final String variableName;
	private final InstructionSet selectedInstructions;
	private final List<RefactoringDecision> allowedDecisions;

	public MissingReferenceDecisionPoint(ProgramLocation reference, String variableName, InstructionSet context,
			List<RefactoringDecision> allowedDecisions) {
		super(new Risk() {
			@Override public String getRiskId()      { return variableName; }
			@Override public String getDescription() { return "Handle missing reference: " + variableName; }
		});
		this.variableName = variableName;
		this.missingReference = reference;
		this.selectedInstructions = context;
		this.allowedDecisions = allowedDecisions;
	}

	@Override
	public List<RefactoringDecision> getPossibleDecisions() {
		return allowedDecisions;
	}

	@Override
	public void applyDecision(RefactoringDecision decision, MethodSpecification targetMethod) {
		if ("PARAM".equals(decision.getDecisionId())) {
			addAsParameter(decision, targetMethod);
		} else if ("FIELD".equals(decision.getDecisionId())) {
			addAsField();
		}
	}

	private void addAsParameter(RefactoringDecision decision, MethodSpecification targetMethod) {
		String variableType = inferType(missingReference);
		List<ParameterSpecification> params = targetMethod.getParameters();
		params.add(new ParameterSpecification(variableName, variableType));
		targetMethod.setParameters(params);
	}

	private void addAsField() {
		// Store decision to apply during AddMethodBody microstep
		// This would be handled within the refactoring itself
	}

	private String inferType(ProgramLocation location) {
		try {
			GraphQuery typeQuery = Graph.query(location).forwardDifference(Tags.Relation.TYPE_OF);

			if (typeQuery.locationCount() > 0) {
				String typeName = typeQuery.singleLocation().<String>getAttribute(Tags.Attributes.NAME);
				return typeName;
			}
		} catch (Exception e) {
			System.err.println("Could not infer type for: " + location.getAttribute(Tags.Attributes.NAME));
		}

		return "Object";
	}
}
