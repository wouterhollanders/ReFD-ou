package nl.ou.refd.plugin.ui.topbarmenu;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import com.ensoftcorp.open.commons.ui.utilities.DisplayUtils;
import com.ensoftcorp.open.commons.utilities.MappingUtils;
import nl.ou.refd.exceptions.NoActiveProjectException;
import nl.ou.refd.locations.collections.ClassSet;
import nl.ou.refd.locations.collections.MethodSet;
import nl.ou.refd.locations.generators.ProjectProgramComponentsGenerator;
import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.graph.SelectionUtil;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.LocationSpecification.AccessModifier;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.locations.specifications.ParameterSpecification;
import nl.ou.refd.locations.streams.ClassStream;
import nl.ou.refd.plugin.Controller;
import nl.ou.refd.plugin.ui.CodeComparisonSelectionDialog;
import nl.ou.refd.plugin.ui.EclipseUtil;
import nl.ou.refd.plugin.ui.JavaFileReader;
import nl.ou.refd.plugin.ui.MethodSpecificationDialog;

/**
 * Class representing the menu button for the Form Template Method refactoring.
 * option. The presence of this button can be configured in plugin.xml.
 */
public class FormTemplateMethodButton extends MenuButtonHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(ExecutionEvent event) {

		try {
			MappingUtils.mapWorkspace();
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		IProject currentProject;

		try {
			currentProject = EclipseUtil.currentProject();
		} catch (NoActiveProjectException e) {
			DisplayUtils.showMessage("Error: No active project");
			return;
		}

		// Select the super class in where to form the template method.
		ElementListSelectionDialog destinationSelector = new ElementListSelectionDialog(
				HandlerUtil.getActiveShell(event), new LabelProvider());
		destinationSelector.setElements(new ProjectProgramComponentsGenerator(currentProject.getName()).stream()
				.classes().allSuperClasses().collect().toLocationSpecifications().toArray());

		destinationSelector.setTitle("Select superclass to open subclasses for method inspection");
		destinationSelector.create();
		destinationSelector.getOkButton().setText("Next");
		destinationSelector.open();

		ClassSpecification destination = (ClassSpecification) destinationSelector.getResult()[0];

		// Now fetch the subclasses of this super class.
		ElementListSelectionDialog subclass = new ElementListSelectionDialog(HandlerUtil.getActiveShell(event),
				new LabelProvider());
		
		var subClasses = new ClassSet(destination).stream().allSubclasses().collect().toLocationSpecifications()
				.toArray();
		
		List<ClassSpecification> subClassesResult = Arrays
				.asList(Arrays.copyOf(subClasses, subClasses.length, ClassSpecification[].class));
		
//		//Placeholder Extract Method
//		String javaFilePath1 = "C:\\Users\\w.hollanders\\runtime-EclipseApplication\\java-refactoring-examples\\Form Template Method\\src\\refactoring\\examples\\FormTemplateMethod\\LifelineSite.java";
//		String javaFilePath2 = "C:\\Users\\w.hollanders\\runtime-EclipseApplication\\java-refactoring-examples\\Form Template Method\\src\\refactoring\\examples\\FormTemplateMethod\\ResidentialSite.java";
//		String formattedContent1 = JavaFileReader.readAndFormatJavaFile(javaFilePath1);
//		String formattedContent2 = JavaFileReader.readAndFormatJavaFile(javaFilePath2);
//
//		Shell shell = new Shell();
//
//		CodeComparisonSelectionDialog dialog = new CodeComparisonSelectionDialog(shell, formattedContent1,
//				formattedContent2);
//		dialog.open();

		//Now create the different methods (extract) from the subclasses.
		Shell shell = new Shell();
		MethodSpecificationDialog dialog = new MethodSpecificationDialog(shell, subClassesResult);
		List<MethodSpecification> methods = null;
		if (dialog.open() == MethodSpecificationDialog.OK) {
			methods = dialog.getMethods();

			// Process the list of methods
			methods.forEach(method -> {
				System.out.println("Added method: " + method.getMethodName());
			});
		}

		// Now select the Template Method.
		ElementListSelectionDialog methodSelector = new ElementListSelectionDialog(HandlerUtil.getActiveShell(event),
				new LabelProvider());

		methodSelector.setElements(new ProjectProgramComponentsGenerator(currentProject.getName()).stream().classes().allSubclasses()
				.methods().collect().toLocationSpecifications().toArray(new MethodSpecification[] {}));

		methodSelector.setTitle("Select identical method(s) as Template Method");
		methodSelector.setMultipleSelection(true);
		methodSelector.create();
		methodSelector.getOkButton().setText("Next");
		methodSelector.open();

		Object[] methodSelectorResult = methodSelector.getResult();
		List<MethodSpecification> methodsToPullUp = Arrays
				.asList(Arrays.copyOf(methodSelectorResult, methodSelectorResult.length, MethodSpecification[].class));

//		// Subclass Methods to Create
//		List<MethodSpecification> methodsToCreate = new ArrayList<MethodSpecification>();
//
//		List<ParameterSpecification> parameterTypes = new ArrayList<ParameterSpecification>();
//		for (ClassSpecification subClass : subClassesResult) {
//			methodsToCreate.add(new MethodSpecification("Method1", parameterTypes, AccessModifier.PUBLIC, false, false,
//					"void", subClass));
//		}

		try {
			Controller.getController().formTemplateMethod(destination, methodsToPullUp,
					subClassesResult, methods);
		} catch (NoActiveProjectException e) {
			DisplayUtils.showMessage("Error: No active project");
			return;
		}

		// Add new Template Method to the superclass



	}

	private String GetFilePath(IProject currentProject, ClassSpecification classSpec) {

		String resourceName = classSpec.getClassName().replace('.', '/') + ".java"; // Assuming you're dealing with
																					// source files

		return null;
	}
}
