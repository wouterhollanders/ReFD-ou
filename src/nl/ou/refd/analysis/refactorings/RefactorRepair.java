package nl.ou.refd.analysis.refactorings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.ou.refd.analysis.DangerAggregator;
import nl.ou.refd.analysis.DangerAnalyser;
import nl.ou.refd.analysis.VerdictFunction;
import nl.ou.refd.analysis.microsteps.AddParameter;
import nl.ou.refd.analysis.microsteps.Microstep;
import nl.ou.refd.analysis.refactorings.Refactoring;
import nl.ou.refd.locations.collections.LabeledLocationSet;
import nl.ou.refd.locations.generators.ProgramComponentsGenerator;
import nl.ou.refd.locations.graph.Graph;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.graph.Tags;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.locations.specifications.ParameterSpecification;

/**
 * This was only added to validate the complexity of working with the POST-phase. This is not a real addition to the tool.
 * Simple refactoring that maps selected dangers to repair microsteps,
 * constructs a combined refactoring (original + repairs), re-runs
 * `DangerAnalyser` and returns the validation results.
 */
//This was only added to validate the complexity of working with the POST-phase. This is not a real addition to the tool.
public class RefactorRepair {

	/**
	 * Build repairs for a set of selected dangers and validate. For this example we
	 * only handle `MissingLocalReferences.Body`.
	 * 
	 * @param original        the original refactoring
	 * @param selectedDangers the selected dangers to repair
	 * @return the resulting dangers after applying repairs (simulation)
	 */
	//This was only added to validate the complexity of working with the POST-phase. This is not a real addition to the tool.
	public Refactoring validateWithRepairs(Refactoring original,
			List<LabeledLocationSet> selectedDangers) {

		List<Microstep> repairs = buildRepairsForDangers(selectedDangers);

		// Build combined refactoring that preserves original verdictFunction
		Refactoring combined = new Refactoring() {

			private final String name = original.getName() + " (with repairs)";

			// copy microsteps from original
			{
				original.getMicrosteps().forEach(m -> microstep(m));
				repairs.forEach(r -> microstep(r));
			}

			@Override
			public VerdictFunction verdictFunction(DangerAggregator aggregator) {
				return original.verdictFunction(aggregator);
			}

			@Override
			public String getName() {
				return name;
			}
		};
		
		return combined;
	}

	/**
	 * Builds a refactoring that contains only the repair microsteps derived from
	 * the selected dangers. Useful to apply repairs on top of already executed
	 * original microsteps without re-applying them.
	 */
	public Refactoring repairsOnlyRefactoring(Refactoring original, List<LabeledLocationSet> selectedDangers) {
		List<Microstep> repairs = buildRepairsForDangers(selectedDangers);

		Refactoring repairsOnly = new Refactoring() {
			private final String name = original.getName() + " (repairs only)";

			{
				repairs.forEach(r -> microstep(r));
			}

			@Override
			public VerdictFunction verdictFunction(DangerAggregator aggregator) {
				return original.verdictFunction(aggregator);
			}

			@Override
			public String getName() {
				return name;
			}
		};

		return repairsOnly;
	}

	private List<Microstep> buildRepairsForDangers(List<LabeledLocationSet> selectedDangers) {
		List<Microstep> repairs = new ArrayList<>();

		if (selectedDangers == null) {
			return repairs;
		}

		for (LabeledLocationSet danger : selectedDangers) {

			// For each variable reported, add a parameter to its enclosing method
			Set<ProgramLocation> vars = new HashSet<>(danger.getLocations());
			for (ProgramLocation var : vars) {
				try {
					// Get the actual enclosing method by walking ancestors (Contains) and
					// selecting any node tagged as a method variant
					ProgramLocation parentMethod = Graph.query(var)
						.containers()
						.locations(
								Tags.ProgramLocation.METHOD,
								Tags.ProgramLocation.CLASS_METHOD,
								Tags.ProgramLocation.INSTANCE_METHOD,
								Tags.ProgramLocation.ABSTRACT_METHOD)
						.singleLocation();
					MethodSpecification methodSpec = new MethodSpecification(parentMethod);

					var methodDest = new ProgramComponentsGenerator().stream().classes().classesByName(methodSpec.getEnclosingClass().getClassName()).methods().filterByName("MyNewMethod").collect().toLocationSpecifications().get(0);

					String paramName = var.<String>getAttribute(Tags.Attributes.NAME);
					String paramType = null;
					
					try {
						ProgramLocation typeNode = var.out(Tags.Relation.TYPE_OF).iterator().next().to();
						paramType = typeNode.<String>getAttribute(Tags.Attributes.NAME);
					} catch (Exception e) {
						paramType = "Object";
					}

					ParameterSpecification ps = new ParameterSpecification(paramName, paramType);
					repairs.add(new AddParameter(methodDest, ps));
				} catch (Exception e) {
					System.out.print(e);
					// skip if we can't determine parent method or construct spec
				}
			}

		}

		return repairs;
	}

}
