package nl.ou.refd.plugin.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.LocationSpecification.AccessModifier;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.locations.specifications.ParameterSpecification;

import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class MethodSpecificationDialog extends Dialog {

	private Text methodNameText;
	private Text parametersText;
	private Text returnTypeText;
	private Combo visibilityCombo;
	private Button isStaticCheckbox;
	private Button isAbstractCheckbox;

	private TableViewer methodTableViewer;
	private List<MethodSpecification> methods = new ArrayList<>();
	private List<ClassSpecification> enclosingClasses;

	public MethodSpecificationDialog(Shell parentShell, List<ClassSpecification> enclosingClasses) {
		super(parentShell);
		this.enclosingClasses = enclosingClasses;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));

		// Form Fields
		createFormFields(container);

		// Method List Viewer
		createMethodTableViewer(container);

		return container;
	}

	private void createFormFields(Composite container) {
		// Method Name
		new Label(container, SWT.NONE).setText("Method Name:");
		methodNameText = new Text(container, SWT.BORDER);
		methodNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Parameters
		new Label(container, SWT.NONE).setText("Parameters (e.g., int param1, string param2):");
		parametersText = new Text(container, SWT.BORDER);
		parametersText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Return Type
		new Label(container, SWT.NONE).setText("Return Type:");
		returnTypeText = new Text(container, SWT.BORDER);
		returnTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Visibility
		new Label(container, SWT.NONE).setText("Visibility:");
		visibilityCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		visibilityCombo.setItems(new String[] { "PUBLIC", "PRIVATE", "PROTECTED", "DEFAULT" });
		visibilityCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Is Static
		new Label(container, SWT.NONE).setText("Is Static:");
		isStaticCheckbox = new Button(container, SWT.CHECK);

		// Is Abstract
		new Label(container, SWT.NONE).setText("Is Abstract:");
		isAbstractCheckbox = new Button(container, SWT.CHECK);
	}

	private void createMethodTableViewer(Composite container) {
		Label label = new Label(container, SWT.NONE);
		label.setText("Created Methods:");
		GridData labelGridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		labelGridData.horizontalSpan = 2;
		label.setLayoutData(labelGridData);

		methodTableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		methodTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		methodTableViewer.setLabelProvider(new MethodLabelProvider());

		Table table = methodTableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		// Configure columns (if you want a detailed table view)
		TableColumn methodNameColumn = new TableColumn(table, SWT.LEFT);
		methodNameColumn.setText("Method Name");
		methodNameColumn.setWidth(200);

		TableColumn returnTypeColumn = new TableColumn(table, SWT.LEFT);
		returnTypeColumn.setText("Return Type");
		returnTypeColumn.setWidth(150);
		
		TableColumn classColumn = new TableColumn(table, SWT.LEFT);
		classColumn.setText("Enclosing Class");
		classColumn.setWidth(200); // Adjust the width as needed


		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Set input to empty list initially
		methodTableViewer.setInput(methods);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Add Button
		createButton(parent, 1001, "Add", true).addListener(SWT.Selection, e -> onAdd());

		// Finish Button
		createButton(parent, IDialogConstants.OK_ID, "Finish", true);

		// Cancel Button
		createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
	}

	private void onAdd() {
		String methodName = methodNameText.getText();
		String returnType = returnTypeText.getText();
		AccessModifier visibility = AccessModifier.valueOf(visibilityCombo.getText().toUpperCase());
		boolean isStatic = isStaticCheckbox.getSelection();
		boolean isAbstract = isAbstractCheckbox.getSelection();

		// Parse Parameters
		String parametersInput = parametersText.getText();
		List<ParameterSpecification> parameters = new ArrayList<>();
		
		if (!parametersInput.isEmpty()) {
			// Split input by commas, and then parse each parameter
			String[] paramArray = parametersInput.split(",");
			for (String param : paramArray) {
				String[] paramParts = param.trim().split("\\s+"); // Split by whitespace
				if (paramParts.length == 2) {
					String type = paramParts[0];
					String name = paramParts[1];
					parameters.add(new ParameterSpecification(name, type));
				} else {
					// Handle invalid input gracefully
					System.err.println("Invalid parameter format: " + param);
				}
			}
		}

		for (ClassSpecification enclosingClass : enclosingClasses) {

			MethodSpecification method = new MethodSpecification(methodName, parameters, visibility, isStatic,
					isAbstract, returnType, enclosingClass);

			methods.add(method);
		}

		// Update TableViewer
		methodTableViewer.refresh();

		// Clear inputs for the next entry
		methodNameText.setText("");
	    parametersText.setText("");
		returnTypeText.setText("");
		visibilityCombo.deselectAll();
		isStaticCheckbox.setSelection(false);
		isAbstractCheckbox.setSelection(false);
	}

	@Override
	protected org.eclipse.swt.graphics.Point getInitialSize() {
		// Set the dialog size (width, height)
		return new org.eclipse.swt.graphics.Point(550, 450); // Adjust the size as needed
	}

	public List<MethodSpecification> getMethods() {
		return methods;
	}

	private static class MethodLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public String getColumnText(Object element, int columnIndex) {
		    if (element instanceof MethodSpecification) {
		        MethodSpecification method = (MethodSpecification) element;
		        switch (columnIndex) {
		            case 0:
		                return method.getMethodName(); // Method Name Column
		            case 1:
		                return method.getReturnType(); // Return Type Column
		            case 2:
	                    // Enclosing Class Column
	                    return method.getEnclosingClass() != null ? method.getEnclosingClass().getClassName() : "None";
		        }
		    }
		    return null;
		}


		@Override
		public org.eclipse.swt.graphics.Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
	}
}