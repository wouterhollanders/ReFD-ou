package nl.ou.refd.plugin.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.locations.specifications.ParameterSpecification;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.LocationSpecification.AccessModifier;

public class MethodSpecificationDialog extends TitleAreaDialog {

	private Text methodNameText;
	private Text methodReturnType;
    private Button publicButton;
    private Button packageButton;
    private Button protectedButton;
    private Button privateButton;
    private Button staticButton;
    private Button abstractButton;
    private MethodSpecification resultMethodSpecification;
    private ClassSpecification enclosingClass;
    private List<ParameterSpecification> parameters;

    
    public MethodSpecificationDialog(Shell parentShell, ClassSpecification enclosingClass) {
        super(parentShell);
        this.enclosingClass = enclosingClass;
        this.parameters = parameters != null ? parameters : new ArrayList<>();
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Extract Method Configuration");
    }

    @Override
    public void create() {
        super.create();
        setTitle("Extract Method");
        setMessage("Configure the settings for the extracted method");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(new GridLayout(2, false));

        createMethodNameGroup(container);
        createReturnTypeGroup(parent);
        createVisibilityGroup(container);
        createModifiersGroup(container);
        createParametersGroup(container);

        return area;
    }

    /**
     * Creates the method name input group
     */
    private void createMethodNameGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Method Name");
        group.setLayout(new GridLayout(2, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        group.setLayoutData(gd);

        Label label = new Label(group, SWT.NONE);
        label.setText("Name:");

        methodNameText = new Text(group, SWT.BORDER);
        methodNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        methodNameText.setText("MyNewMethod");
    }

    /**
     * Creates the visibility (access modifier) selection group
     */
    private void createVisibilityGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Visibility");
        group.setLayout(new GridLayout(4, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        group.setLayoutData(gd);

        AccessModifier currentVisibility = AccessModifier.PUBLIC;

        publicButton = new Button(group, SWT.RADIO);
        publicButton.setText("Public");
        publicButton.setSelection(currentVisibility == AccessModifier.PUBLIC);

        packageButton = new Button(group, SWT.RADIO);
        packageButton.setText("Package");
        packageButton.setSelection(currentVisibility == AccessModifier.PACKAGE);

        protectedButton = new Button(group, SWT.RADIO);
        protectedButton.setText("Protected");
        protectedButton.setSelection(currentVisibility == AccessModifier.PROTECTED);

        privateButton = new Button(group, SWT.RADIO);
        privateButton.setText("Private");
        privateButton.setSelection(currentVisibility == AccessModifier.PRIVATE);
    }

    /**
     * Creates the modifiers group (static and abstract)
     */
    private void createModifiersGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Modifiers");
        group.setLayout(new GridLayout(2, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        group.setLayoutData(gd);

        staticButton = new Button(group, SWT.CHECK);
        staticButton.setText("Static");
        staticButton.setSelection(false);

        abstractButton = new Button(group, SWT.CHECK);
        abstractButton.setText("Abstract");
        abstractButton.setSelection(false);
    }
    
    /**
     * Creates the modifiers group (static and abstract)
     */
    private void createReturnTypeGroup(Composite parent) {
    	Group group = new Group(parent, SWT.NONE);
        group.setText("Return Type");
        group.setLayout(new GridLayout(2, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        group.setLayoutData(gd);

        Label label = new Label(group, SWT.NONE);
        label.setText("Return Type:");

        methodReturnType = new Text(group, SWT.BORDER);
        methodReturnType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        methodReturnType.setText("void");
    }

    /**
     * Creates the parameters group (currently disabled due to decisions)
     */
    private void createParametersGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Parameters");
        group.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        group.setLayoutData(gd);

        Label label = new Label(group, SWT.NONE);
        label.setText("(NA)");
        label.setEnabled(false);

        // Create a disabled composite to show parameters would go here
        Composite parameterComposite = new Composite(group, SWT.NONE);
        parameterComposite.setLayout(new GridLayout(1, false));
        parameterComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        parameterComposite.setEnabled(false);

        List<String> parameterStrings = new ArrayList<>();
        for (var param : parameters) {
            parameterStrings.add(param.getType() + " " + param.getName());
        }

        for (String paramStr : parameterStrings) {
            Text paramText = new Text(parameterComposite, SWT.BORDER | SWT.READ_ONLY);
            paramText.setText(paramStr);
            paramText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
            paramText.setEnabled(false);
        }
    }

    @Override
    protected void okPressed() {
        
        AccessModifier visibility;
        if (publicButton.getSelection()) {
            visibility = AccessModifier.PUBLIC;
        } else if (packageButton.getSelection()) {
            visibility = AccessModifier.PACKAGE;
        } else if (protectedButton.getSelection()) {
            visibility = AccessModifier.PROTECTED;
        } else {
            visibility = AccessModifier.PRIVATE;
        }

        // Create a new MethodSpecification with the configured values
        resultMethodSpecification = new MethodSpecification(
            methodNameText.getText(),
            parameters,
            visibility,
            staticButton.getSelection(),
            abstractButton.getSelection(),
            methodReturnType.getText(),
            enclosingClass
        );

        super.okPressed();
    }

    /**
     * Gets the configured method specification after the dialog is closed
     * 
     * @return the MethodSpecification with user-configured values
     */
    public MethodSpecification getMethodSpecification() {
        return resultMethodSpecification;
    }
}
