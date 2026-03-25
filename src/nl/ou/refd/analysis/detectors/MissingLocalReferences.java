//Part of the idea of this detector is from a student of W. Brinksma.
//I did small improvements on the code for fetching the instructions.
package nl.ou.refd.analysis.detectors;

import nl.ou.refd.analysis.DetectorVisitor;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.locations.streams.InstructionStream;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.collections.MethodSet;
import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.Tags;
import java.util.*;
import java.util.stream.Collectors;

public final class MissingLocalReferences {
	private MissingLocalReferences() {
	}

	//TODO: Move logic to subdetectors but for now this was used for fast usage/validation.
	public static class Body extends Detector<InstructionSet> {

		private final InstructionStream selectedBodyInstructions;
		private final MethodSpecification targetMethod;

		public Body(InstructionStream selectedBodyInstructions, MethodSpecification targetMethod) {
			this.selectedBodyInstructions = selectedBodyInstructions;
			this.targetMethod = targetMethod;
		}

		public InstructionSet actualRisks() {
			// guard if the stream is empty
			InstructionSet selectedInstructions = selectedBodyInstructions == null ? null
					: selectedBodyInstructions.collect();
			if (selectedInstructions == null || selectedInstructions.size() == 0) {
				return new InstructionSet(Collections.emptySet());
			}

			GraphQuery selectedBody_q = new GraphQuery(selectedInstructions.locations());
			nl.ou.refd.locations.collections.MethodSet parentMethods = selectedBodyInstructions.parentMethods()
					.collect();

			if (parentMethods.size() != 1) {
				return new InstructionSet(Collections.emptySet());
			}

			ProgramLocation parentMethod = parentMethods.singleLocation();
			Set<ProgramLocation> parentMethodSet = Collections.singleton(parentMethod);
			Set<ProgramLocation> full_body = new MethodSet(parentMethodSet).stream().bodies().collect().locations();

			GraphQuery full_body_q = new GraphQuery(full_body);
			GraphQuery body_difference_q = full_body_q.difference(selectedBody_q);

			List<ProgramLocation> variables_non_selected = setToList(body_difference_q.contained()
					.locationsTaggedWithAll(Tags.ProgramLocation.INITIALIZATION).locations());

			Set<ProgramLocation> actualRisks = new HashSet<>();
			var params = targetMethod.getParameters();

			for (ProgramLocation variable : variables_non_selected) {
				boolean variableInParam = false;
				
				if (!params.isEmpty()) {
					var variableName = variable.getAttribute(Tags.Attributes.NAME);
					ProgramLocation paramFound = null;
					var targetMethodSet = new MethodSet(targetMethod);
					var targetMethodProgramLocation = targetMethodSet.singleLocation();

					for (var edge : targetMethodProgramLocation.out(Tags.Relation.HAS_PARAMETER)) {
						ProgramLocation p = edge.to();
						String name = p.getAttribute(Tags.Attributes.NAME);
						if (variableName.equals(name)) {
							paramFound = p;
							variableInParam = true;
							//todo; it would be better to validate the dataflow link here but doing so with the paramfound location results in false when the controlflowchecker results is true with the same code.
							break;
						}
					}
				}
				
				if(variableInParam) {
					break;
				}
				// collect targets reachable via outgoing edges from the variable
				Set<ProgramLocation> outTargets = variable.out().stream().map(r -> r.to()).collect(Collectors.toSet());
				// also collect sources that point to the variable (some relations are incoming)
				Set<ProgramLocation> inSources = variable.in().stream().map(r -> r.from()).collect(Collectors.toSet());

				// build a query including both directions to be conservative
				GraphQuery variable_outward_links = new GraphQuery(outTargets).union(new GraphQuery(inSources));

				// include both the selected nodes and their contained descendants as the
				// selection space
				GraphQuery selection_space = selectedBody_q.union(selectedBody_q.contained());
				GraphQuery risks = selection_space.intersection(variable_outward_links);

				if (risks.locationCount() > 0) {
					actualRisks.add(variable);
				}
			}

			return new InstructionSet(actualRisks);
		}

		public void accept(DetectorVisitor visitor) {
			visitor.visit(this);
		}

		public static <T> List<T> setToList(Set<T> s) {
			return s.stream().collect(Collectors.toList());
		}
	}
}