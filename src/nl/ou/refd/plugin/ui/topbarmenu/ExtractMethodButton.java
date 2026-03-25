package nl.ou.refd.plugin.ui.topbarmenu;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ensoftcorp.atlas.core.index.common.SourceCorrespondence;
import com.ensoftcorp.open.commons.ui.utilities.DisplayUtils;

import nl.ou.refd.analysis.PreRefactoringContext;
import nl.ou.refd.analysis.decisions.DecisionMethodSpecification;
import nl.ou.refd.analysis.decisions.RefactoringDecision;
import nl.ou.refd.analysis.decisions.RefactoringDecisionPoint;
import nl.ou.refd.analysis.scanners.ExtractMethodPreScanner;
import nl.ou.refd.analysis.refactorings.ExtractMethod;
import nl.ou.refd.analysis.refactorings.Refactoring;
import nl.ou.refd.exceptions.NoActiveProjectException;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.generators.ProgramComponentsGenerator;
import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.SelectionUtil;
import nl.ou.refd.locations.graph.Tags;
import nl.ou.refd.locations.graph.Tags.Attributes;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.plugin.Controller;
import nl.ou.refd.plugin.ui.dialogs.DecisionsDialog;
import nl.ou.refd.plugin.ui.dialogs.MethodSpecificationDialog;
import nl.ou.refd.plugin.ui.dialogs.GuidanceDialog;

public class ExtractMethodButton extends MenuButtonHandler {

	@Override
	public void handle(ExecutionEvent event) {

		// This section could be used from a student of Brinksma. It is a slighlty more
		// efficient way to retrieve the instruction set than my original code.
		TextSelection selection = (TextSelection) com.ensoftcorp.atlas.ui.selection.SelectionUtil
				.getLastSelectionEvent().getWorkbenchSelection();

		List<nl.ou.refd.locations.graph.ProgramLocation> locations = SelectionUtil.getSelection().locations().stream()
				.filter(location -> {
					SourceCorrespondence sc = location.getAttribute(Attributes.SOURCE_CORRESPONDENCE);

					if (sc == null) {
						return false;
					} else {
						return true;
					}

				}).collect(Collectors.toList());

		GraphQuery selectedOffsets_q = SelectionUtil.selectFromSource(
				locations.get(0).<SourceCorrespondence>getAttribute(Attributes.SOURCE_CORRESPONDENCE).sourceFile
						.getName(),
				selection.getOffset(), selection.getOffset() + selection.getLength());

		GraphQuery selectedBody_q = selectedOffsets_q.locationsTaggedWithAll(Tags.ProgramLocation.CONTROL_FLOW_NODE);

		if (selectedBody_q.locationCount() < 1) {
			DisplayUtils.showMessage("Error: No selection made");
			return;
		}

		// End of the copied section.
		var selectedInstructions = new InstructionSet(selectedBody_q.locations());

		ClassSpecification enclosingClass = selectedInstructions.stream().parentMethods().parentClasses().collect()
				.toLocationSpecifications().get(0);
		// Open the Extract Method configuration dialog

		MethodSpecificationDialog dialog = new MethodSpecificationDialog(HandlerUtil.getActiveShell(event),
				enclosingClass);

		int methodDialogResult = dialog.open();

		if (methodDialogResult != MethodSpecificationDialog.OK) {
			return; // User cancelled
		}

		MethodSpecification newMethod = dialog.getMethodSpecification();

		if (newMethod == null) {
			return;
		}

		ExtractMethodPreScanner scanner = new ExtractMethodPreScanner();

		// Build a concrete Refactoring instance and context for generic scanning
		Refactoring refactoring = new ExtractMethod(selectedInstructions, newMethod);
		PreRefactoringContext context = PreRefactoringContext.builder(refactoring).selectedCode(selectedInstructions)
				.targetMethod(newMethod).build();

		List<RefactoringDecisionPoint> decisionPoints = scanner.scan(context);

		System.out.print(decisionPoints);

		DecisionMethodSpecification refactoringSpec = new DecisionMethodSpecification(newMethod);

		if (!decisionPoints.isEmpty()) {
			// Present decisions and apply them to the method specification via the dialog
			DecisionsDialog decisionsDialog = new DecisionsDialog(HandlerUtil.getActiveShell(event), decisionPoints,
					refactoringSpec);

			int decisionsResult = decisionsDialog.open();
			if (decisionsResult != DecisionsDialog.OK) {
				return; // User cancelled decisions
			}

			// Validate if the chosen remedy's preconditions are met.
			// Assume for now we can only do 1 decision point per scan.
			RefactoringDecisionPoint firstPoint = decisionPoints.get(0);
			RefactoringDecision chosenDecision = refactoringSpec.getDecisions().get(firstPoint.getRiskIdentifier());
			List<RefactoringDecisionPoint> followUp = chosenDecision.validatePreconditions(context);

			if (!followUp.isEmpty()) {
				// Present guidance for the follow-up decision point (generic handling in dialog)
				RefactoringDecisionPoint dp = followUp.get(0);
				GuidanceDialog guidanceDialog = new GuidanceDialog(HandlerUtil.getActiveShell(event), dp);
				guidanceDialog.open();
				return;
			}
		}

		try {
			Controller.getController().extractMethod(new InstructionSet(selectedBody_q.locations()), newMethod);
		} catch (NoActiveProjectException e) {
			DisplayUtils.showMessage("Error: No active project");
			return;
		}
	}
}
