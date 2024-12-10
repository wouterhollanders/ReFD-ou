package nl.ou.refd.plugin.ui.topbarmenu;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

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
import nl.ou.refd.plugin.ui.CodeComparisonSelectionDialog;

/**
 * Class representing the menu button for the Form Template Method  refactoring.
 * option. The presence of this button can be configured in plugin.xml.
 */
public class FormTemplateMethodButton extends MenuButtonHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(ExecutionEvent event) {
		
		Shell shell = new Shell();
		CodeComparisonSelectionDialog dialog = new CodeComparisonSelectionDialog(shell, "public void method1() {}", "public void method2() {}");
		dialog.open();


//		GraphQuery selectedElement = SelectionUtil.getSelection();
//
//		if (selectedElement.locationCount() < 1) {
//			DisplayUtils.showMessage("Error: No selection made");
//			return;
//		}
//
//		ProgramLocation location = null;
//		try {
//			location = selectedElement.singleLocation();
//		} catch (Exception ex) {
//			DisplayUtils.showMessage("Error: Multiple selections found, please select only one location.");
//			ex.printStackTrace();
//			return;
//		}
//
//		MethodSpecification methodSource = null;
//
//		if (MethodSpecification.locationIsMethod(location)) {
//			methodSource = new MethodSpecification(location);
//		} else {
//			DisplayUtils.showMessage("Error: Selection was not a method");
//			return;
//		}
//
//		try {
//			MappingUtils.mapWorkspace();
//			Thread.sleep(1000);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		ElementListSelectionDialog destinationSelector = new ElementListSelectionDialog(
//				HandlerUtil.getActiveShell(event), new LabelProvider());
//		destinationSelector.setElements(new MethodSet(methodSource).stream().parentClasses().allSuperClasses().collect()
//				.toLocationSpecifications().toArray());
//		destinationSelector.setTitle("Select destination superclass");
//		destinationSelector.open();
//
//		ClassSpecification destination = (ClassSpecification) destinationSelector.getResult()[0];
//
//		try {
//			Controller.getController().pullUpMethod(methodSource, destination);
//		} catch (NoActiveProjectException e) {
//			DisplayUtils.showMessage("Error: No active project");
//			return;
//		}
	}
}
