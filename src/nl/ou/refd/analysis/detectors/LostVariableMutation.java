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

public final class LostVariableMutation {
	private LostVariableMutation() {
	}

	//TODO: Does not work completely but usable enough for the demonstration of the lost mutation. 
	//If the variable is mutated more than once maybe this should not be a risk anymore? Or only when it is returned (in the Extract Method Context).
	
	public static class Body extends Detector<InstructionSet> {

		private final InstructionStream selectedBodyInstructions;
		private final MethodSpecification targetMethod;

		public Body(InstructionStream selectedBodyInstructions, MethodSpecification targetMethod) {
			this.selectedBodyInstructions = selectedBodyInstructions;
			this.targetMethod = targetMethod;
		}

		public InstructionSet actualRisks() {
			InstructionSet selectedInstructions = selectedBodyInstructions == null ? null : selectedBodyInstructions.collect();
		    if (selectedInstructions == null || selectedInstructions.size() == 0) {
		        return new InstructionSet(Collections.emptySet());
		    }

		    nl.ou.refd.locations.collections.MethodSet parentMethods = selectedBodyInstructions.parentMethods().collect();
		    if (parentMethods.size() != 1) {
		        return new InstructionSet(Collections.emptySet());
		    }
		    var sourceMethod = new MethodSpecification(parentMethods.singleLocation());
		    var sourceMethodBody = sourceMethod.getBody().collect().locations();
		    Set<ProgramLocation> assignmentCFs = selectedBodyInstructions.methodAssignments().collect().locations();
		    
		    GraphQuery selectedBodyQ = new GraphQuery(selectedInstructions.locations());
		    GraphQuery selectionSpace = selectedBodyQ.union(selectedBodyQ.contained());
		    GraphQuery U = selectionSpace.universe();

		    Set<ProgramLocation> actualRisks = new HashSet<>();
		    for (ProgramLocation assignment : assignmentCFs) {
		        // Downstream consumers of the assignment’s produced value
		        GraphQuery downstreamUses = new GraphQuery(Collections.singleton(assignment))
		                .successorsOn(U.relations(Tags.Relation.DATAFLOW))
		                .locations(Tags.ProgramLocation.DATAFLOW);

		        GraphQuery usesInSelection = downstreamUses.intersection(selectionSpace);

		        if (usesInSelection.locationCount() == 0) {
		            actualRisks.add(assignment);
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