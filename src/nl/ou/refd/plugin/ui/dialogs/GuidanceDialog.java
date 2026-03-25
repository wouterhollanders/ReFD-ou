package nl.ou.refd.plugin.ui.dialogs;

import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import nl.ou.refd.analysis.decisions.RefactoringDecisionPoint;
import nl.ou.refd.analysis.decisions.MissingMethodSignatureDecisionPoint;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.locations.specifications.LocationSpecification.AccessModifier;

/**
 * Dialog that presents guidance for creating a missing method signature.
 * It renders a concrete example based on the provided MethodSpecification.
 */
public class GuidanceDialog extends TitleAreaDialog {

    private final RefactoringDecisionPoint guidancePoint;

    public GuidanceDialog(Shell parentShell, RefactoringDecisionPoint guidancePoint) {
        super(parentShell);
        this.guidancePoint = guidancePoint;
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Refactoring Guidance");
        newShell.setSize(700, 450);
    }

    @Override
    public void create() {
        super.create();
        if (guidancePoint instanceof MissingMethodSignatureDecisionPoint) {
            setTitle("Create Missing Method Signature");
            setMessage("Add the following method to the indicated class, then re-run validation.");
        } else {
            setTitle("Refactoring Guidance");
            setMessage("Review the decision point and apply the suggested action.");
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        Composite container = new Composite(area, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Dispatch guidance rendering based on the decision point type
        if (guidancePoint instanceof MissingMethodSignatureDecisionPoint) {
            renderMissingMethodSignatureGuidance(container, (MissingMethodSignatureDecisionPoint) guidancePoint);
        } else {
            renderGenericGuidance(container, guidancePoint);
        }

        return area;
    }

    private void renderGenericGuidance(Composite container, RefactoringDecisionPoint point) {
        Group group = new Group(container, SWT.NONE);
        group.setText("Guidance");
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Label idLabel = new Label(group, SWT.NONE);
        idLabel.setText("Decision Point:");
        Label idValue = new Label(group, SWT.NONE);
        idValue.setText(point.getRiskIdentifier());

        Label descLabel = new Label(group, SWT.NONE);
        descLabel.setText("Description:");
        Label descValue = new Label(group, SWT.WRAP);
        descValue.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        descValue.setText(point.getRiskDescription());
    }

    private void renderMissingMethodSignatureGuidance(Composite container, MissingMethodSignatureDecisionPoint mmsdp) {
        MethodSpecification spec = mmsdp.getTargetMethod();

        // Context group: where to add and short description
        Group contextGroup = new Group(container, SWT.NONE);
        contextGroup.setText("Context");
        contextGroup.setLayout(new GridLayout(2, false));
        contextGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Label classLabel = new Label(contextGroup, SWT.NONE);
        classLabel.setText("Enclosing class:");
        Label classValue = new Label(contextGroup, SWT.NONE);
        classValue.setText(spec.getEnclosingClass().getClassName());

        Label signatureLabel = new Label(contextGroup, SWT.NONE);
        signatureLabel.setText("Target signature:");
        Label signatureValue = new Label(contextGroup, SWT.NONE);
        signatureValue.setText(spec.getMethodName() + "(" +
            spec.getParameters().stream()
                .map(p -> p.getType() + " " + p.getName())
                .collect(Collectors.joining(", ")) + ")");

        // Example group: show a ready-to-copy code snippet
        Group exampleGroup = new Group(container, SWT.NONE);
        exampleGroup.setText("Example Method:");
        exampleGroup.setLayout(new GridLayout(1, false));
        exampleGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Text codeToAdd = new Text(exampleGroup, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData ctaGd = new GridData(SWT.FILL, SWT.FILL, true, true);
        ctaGd.heightHint = 240;
        ctaGd.widthHint = 640;
        codeToAdd.setLayoutData(ctaGd);
        codeToAdd.setText(buildMethodToAdd(spec));
    }

    private static String buildMethodToAdd(MethodSpecification spec) {
        StringBuilder sb = new StringBuilder();

        // Optional: show comment indicating placement
        sb.append("// Add this method to class ")
          .append(spec.getEnclosingClass().getClassName())
          .append("\n\n");

        // Visibility
        String visibility = accessModifierToString(spec.getVisibility());
        if (!visibility.isEmpty()) {
            sb.append(visibility).append(' ');
        }

        // Modifiers
        if (spec.isAbstract()) {
            sb.append("abstract ");
        }
        if (spec.isStatic()) {
            sb.append("static ");
        }

        // Return type and name
        sb.append(spec.getReturnType()).append(' ').append(spec.getMethodName()).append('(');

        // Parameters
        String params = spec.getParameters().stream()
            .map(p -> p.getType() + " " + p.getName())
            .collect(Collectors.joining(", "));
        sb.append(params).append(')');

        // Body vs abstract terminator
        if (spec.isAbstract()) {
            sb.append(";");
        } else {
            sb.append(" {\n\n");
            String returnType = spec.getReturnType();
            // Provide a minimal return statement for non-void methods
            if (returnType != null && !returnType.equals("void")) {
                sb.append("    ");
                sb.append(minimalReturnForType(returnType)).append("\n");
            }
            sb.append("}");
        }

        return sb.toString();
    }

    private static String accessModifierToString(AccessModifier am) {
        if (am == null) return ""; // default/package
        switch (am) {
            case PUBLIC: return "public";
            case PROTECTED: return "protected";
            case PRIVATE: return "private";
            case PACKAGE: default: return ""; // default visibility
        }
    }

    private static String minimalReturnForType(String type) {
        // Very lightweight hints; not exhaustive, but enough for guidance dialogs
        switch (type) {
            case "int":
            case "short":
            case "byte":
            case "long":
            case "float":
            case "double":
                return "return 0;";
            case "boolean":
                return "return false;";
            case "char":
                return "return '\\0';";
            default:
                return "return null;"; // reference types
        }
    }
}
