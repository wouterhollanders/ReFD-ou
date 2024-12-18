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
import nl.ou.refd.plugin.ui.JavaFileReader;

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
	
		String javaFilePath1 = "C:\\Users\\w.hollanders\\runtime-EclipseApplication\\java-refactoring-examples\\Form Template Method\\src\\refactoring\\examples\\FormTemplateMethod\\LifelineSite.java";
		String javaFilePath2 = "C:\\Users\\w.hollanders\\runtime-EclipseApplication\\java-refactoring-examples\\Form Template Method\\src\\refactoring\\examples\\FormTemplateMethod\\ResidentialSite.java";
		String formattedContent1 = JavaFileReader.readAndFormatJavaFile(javaFilePath1);
		String formattedContent2 = JavaFileReader.readAndFormatJavaFile(javaFilePath2);

		Shell shell = new Shell();

		CodeComparisonSelectionDialog dialog = new CodeComparisonSelectionDialog(shell, formattedContent1, formattedContent2);
		dialog.open();		

		
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
