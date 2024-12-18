package nl.ou.refd.plugin.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.ensoftcorp.open.commons.ui.utilities.DisplayUtils;

import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.graph.SelectionUtil;

public class CodeComparisonSelectionDialog extends Dialog {
    private SourceViewer sourceViewer1;
    private SourceViewer sourceViewer2;
    private IDocument document1;
    private IDocument document2;

    public CodeComparisonSelectionDialog(Shell parentShell, String code1, String code2) {
        super(parentShell);
        this.document1 = new Document(code1);
        this.document2 = new Document(code2);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Create a SashForm to divide the space proportionally
        SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Editor 1
        sourceViewer1 = createSourceViewer(sashForm, document1);

        // Editor 2
        sourceViewer2 = createSourceViewer(sashForm, document2);

        // Set the weights for the sash form: 40% for each editor
        sashForm.setWeights(new int[] { 40, 40 });

        return container;
    }

    private SourceViewer createSourceViewer(Composite parent, IDocument document) {
        SourceViewer sourceViewer = new SourceViewer(parent, null, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        // Set layout data to fill available space in the SashForm
        sourceViewer.getTextWidget().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Set the document content
        sourceViewer.setDocument(document);

        // Make the text field read-only
        sourceViewer.getTextWidget().setEditable(false);

        return sourceViewer;
    }

    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE; // Add resizable style
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Code Comparison");
        newShell.setSize(800, 600); // Initial size
        newShell.setMinimumSize(600, 400); // Optional: Minimum size
    }

    @Override
    protected void okPressed() {
        // Capture selections from both viewers
        TextSelection selection1 = (TextSelection) sourceViewer1.getSelectionProvider().getSelection();
        TextSelection selection2 = (TextSelection) sourceViewer2.getSelectionProvider().getSelection();

        // Perform your refactoring logic using selection1 and selection2
        System.out.println("Selection from Viewer 1: " + selection1.getText());
        System.out.println("Selection from Viewer 2: " + selection2.getText());
        
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

        super.okPressed();
    }
}
