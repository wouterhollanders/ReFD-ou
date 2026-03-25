package nl.ou.refd.analysis.scanners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.ou.refd.analysis.PreRefactoringAnalyser;
import nl.ou.refd.analysis.PreRefactoringContext;
import nl.ou.refd.analysis.decisions.MethodBodyEmptyPrecondition;
import nl.ou.refd.analysis.decisions.MethodSignatureExistsPrecondition;
import nl.ou.refd.analysis.decisions.MissingReferenceDecisionPoint;
import nl.ou.refd.analysis.decisions.RefactoringDecision;
import nl.ou.refd.analysis.decisions.RefactoringDecisionPoint;
import nl.ou.refd.analysis.detectors.MissingLocalReferences;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.locations.graph.Tags;

/**
 * Scans Extract Method refactoring for missing reference decision points
 */
public class ExtractMethodPreScanner implements PreRefactoringAnalyser {

	@Override
	public List<RefactoringDecisionPoint> scan(PreRefactoringContext context) {

		// Ensure required inputs for Extract Method are present
		InstructionSet selectedCode = context.getSelectedCode().orElseThrow(
				() -> new IllegalArgumentException("Extract Method requires selected code (InstructionSet)"));

		MethodSpecification targetMethod = context.getTargetMethod()
				.orElseThrow(() -> new IllegalArgumentException("Extract Method requires target MethodSpecification"));

		// Phase 1: detect missing local references
		MissingLocalReferences.Body detector = new MissingLocalReferences.Body(selectedCode.stream(), targetMethod);

		InstructionSet missingRefs = detector.actualRisks();
		List<RefactoringDecisionPoint> decisions = new ArrayList<>();

		for (ProgramLocation ref : missingRefs.locations()) {
			String varName = ref.getAttribute(Tags.Attributes.NAME);
			List<RefactoringDecision> remedies = buildMissingRefRemedyOptions(varName);
			decisions.add(new MissingReferenceDecisionPoint(ref, varName, selectedCode, remedies));
		}

		return decisions;
	}

	private List<RefactoringDecision> buildMissingRefRemedyOptions(String variableName) {
		return Arrays.asList(
				new RefactoringDecision("NONE", "Do nothing",
						"Leave " + variableName + " unresolved"),
				new RefactoringDecision("PARAM", "Add as Parameter",
						"Add " + variableName + " as a method parameter",
						Arrays.asList(new MethodSignatureExistsPrecondition(), new MethodBodyEmptyPrecondition())),
				new RefactoringDecision("FIELD", "Add as Field",
						"Access " + variableName + " as an instance field"));
	}
}
