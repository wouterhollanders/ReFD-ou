package nl.ou.refd.plugin.ui.topbarmenu;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ensoftcorp.atlas.core.index.common.SourceCorrespondence;
import com.ensoftcorp.open.commons.ui.utilities.DisplayUtils;

import nl.ou.refd.analysis.decisions.DecisionMethodSpecification;
import nl.ou.refd.analysis.decisions.RefactoringDecisionPoint;
import nl.ou.refd.analysis.scanners.ExtractMethodPreScanner;
import nl.ou.refd.exceptions.NoActiveProjectException;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.SelectionUtil;
import nl.ou.refd.locations.graph.Tags;
import nl.ou.refd.locations.graph.Tags.Attributes;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.plugin.Controller;
import nl.ou.refd.plugin.ui.dialogs.DecisionsDialog;
import nl.ou.refd.plugin.ui.dialogs.MethodSpecificationDialog;

public class ExtractMethodButtonPost extends MenuButtonHandler {

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

		var selectedInstructions = new InstructionSet(selectedBody_q.locations());
		ClassSpecification enclosingClass = selectedInstructions
			.stream()
			.parentMethods()
			.parentClasses()
			.collect()
			.toLocationSpecifications()
			.get(0);
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

		try {
			Controller.getController().extractMethodPost(new InstructionSet(selectedBody_q.locations()), newMethod);
		} catch (NoActiveProjectException e) {
			DisplayUtils.showMessage("Error: No active project");
			return;
		}
	}
}
