package nl.ou.refd.plugin.ui.topbarmenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ensoftcorp.open.commons.ui.utilities.DisplayUtils;
import com.ensoftcorp.open.commons.utilities.MappingUtils;

import nl.ou.refd.exceptions.NoActiveProjectException;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.generators.ProgramComponentsGenerator;
import nl.ou.refd.locations.generators.ProjectProgramComponentsGenerator;
import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.graph.SelectionUtil;
import nl.ou.refd.locations.graph.Tags;
import nl.ou.refd.locations.specifications.InstructionSpecification;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.plugin.Controller;
import nl.ou.refd.plugin.ui.EclipseUtil;

/**
 * Class representing the menu button for the Form Template Method refactoring.
 * option. The presence of this button can be configured in plugin.xml.
 */
public class ExtractMethodButton extends MenuButtonHandler {

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

		List<String> selectedTextLines = getSelectedText();
		if (selectedTextLines != null && !selectedTextLines.isEmpty()) {
			System.out.println("Selected text:");
			selectedTextLines.forEach(System.out::println);
		} else {
			System.out.println("No text is selected or an editor is not active.");
		}

		ElementListSelectionDialog destinationSelector = new ElementListSelectionDialog(
				HandlerUtil.getActiveShell(event), new LabelProvider());
		destinationSelector.setElements(new ProjectProgramComponentsGenerator(currentProject.getName()).stream()
				.classes().classesByName("SimpleExample1").methods().collect().toLocationSpecifications()
				.toArray(new MethodSpecification[] {}));

		destinationSelector.setTitle("Select method to extract code from");
		destinationSelector.setMultipleSelection(false);
		destinationSelector.open();

		Object[] result = destinationSelector.getResult();
		MethodSpecification sourceMethod = Arrays
				.asList(Arrays.copyOf(result, result.length, MethodSpecification[].class)).get(0);

		var methodBody = sourceMethod.getBody().collect();
		List<ProgramLocation> methodBodyPl = new ArrayList<>(methodBody.locations());

		List<ProgramLocation> filteredLocations = methodBodyPl.stream().filter(location -> {
			String attributeName = location.getAttribute(Tags.Attributes.NAME);
			// Check if the attributeName matches any of the selectedTextLines
			return attributeName != null && selectedTextLines.stream().anyMatch(attributeName::contains);
		}).collect(Collectors.toList());

		filteredLocations.forEach(System.out::println);
		Set<ProgramLocation> locationSet = new HashSet<>(filteredLocations);
		InstructionSet instructionSet = new InstructionSet(locationSet);

		List<InstructionSpecification> instructions = filteredLocations.stream().map(InstructionSpecification::new)
				.collect(Collectors.toList());

		var destinationMethod = new ProgramComponentsGenerator().stream().classes().classesByName("SimpleExample2").methods()
				.filterByName("ExecuteSimpleExample2").collect().toLocationSpecifications();

		List<InstructionSpecification> instructionsToCreate = instructions.stream()
				.map(poc -> new InstructionSpecification(poc.getInstructionName(), destinationMethod.get(0)))
				.collect(Collectors.toList());

		try {
			Controller.getController().extractMethod(destinationMethod.get(0), sourceMethod, instructionsToCreate, instructionSet);

		} catch (Exception e) {
			DisplayUtils.showMessage("Error: No active project");
			return;
		}
	}

	public static List<String> getSelectedText() {
		// Get the active workbench window's active editor
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (editor != null) {
			// Check if the editor is a text editor
			if (editor.getEditorSite().getSelectionProvider().getSelection() instanceof ITextSelection) {
				// Get the text selection
				ITextSelection textSelection = (ITextSelection) editor.getEditorSite().getSelectionProvider()
						.getSelection();

				// Return the selected text as a list of trimmed strings (split by lines)
				return Arrays.stream(textSelection.getText().split("\\R")).map(String::trim) // Remove leading and
																								// trailing whitespace
						.collect(Collectors.toList());
			}
		}

		return null;

	}
}