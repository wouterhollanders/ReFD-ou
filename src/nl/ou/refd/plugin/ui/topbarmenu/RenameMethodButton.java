package nl.ou.refd.plugin.ui.topbarmenu;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;

import com.ensoftcorp.open.commons.ui.utilities.DisplayUtils;
import com.ensoftcorp.open.commons.utilities.MappingUtils;

import nl.ou.refd.exceptions.NoActiveProjectException;
import nl.ou.refd.locations.collections.MethodSet;
import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.graph.SelectionUtil;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.plugin.Controller;

/**
 * Class representing the menu button for the Rename Method refactoring
 * option. The presence of this button can be configured in plugin.xml.
 */
/**
 * Class representing the menu button for the Rename Method refactoring option.
 * The presence of this button can be configured in plugin.xml.
 */
public class RenameMethodButton extends MenuButtonHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(ExecutionEvent event) {
		GraphQuery selectedElement = SelectionUtil.getSelection();

		if (selectedElement.locationCount() < 1) {
			DisplayUtils.showMessage("Error: No selection made");
			return;
		}

		ProgramLocation location = null;
		try {
			location = selectedElement.singleLocation();
		} catch (Exception ex) {
			DisplayUtils.showMessage("Error: Multiple selections found, please select only one location.");
			ex.printStackTrace();
			return;
		}

		MethodSpecification methodSource = null;

		if (MethodSpecification.locationIsMethod(location)) {
			methodSource = new MethodSpecification(location);
		} else {
			DisplayUtils.showMessage("Error: Selection was not a method");
			return;
		}

		try {
			MappingUtils.mapWorkspace();
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Prompt the user for the new method name
		InputDialog inputDialog = new InputDialog(HandlerUtil.getActiveShell(event), "Rename Method",
				"Enter the new name for the method:", methodSource.getMethodName(), // Default value
				null // Validator can be added here if needed
		);

		if (inputDialog.open() == Window.OK) {
			String newMethodName = inputDialog.getValue();

			try {
				Controller.getController().renameMethod(methodSource, newMethodName);
			} catch (Exception e) {
				DisplayUtils.showMessage("Error: Unable to rename method. " + e.getMessage());
			}
		} else {
			System.out.println("User canceled the rename operation.");
		}
	}
}
