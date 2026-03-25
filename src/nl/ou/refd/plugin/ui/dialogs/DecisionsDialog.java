package nl.ou.refd.plugin.ui.dialogs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.custom.ScrolledComposite;

import nl.ou.refd.analysis.decisions.RefactoringDecision;
import nl.ou.refd.analysis.decisions.RefactoringDecisionPoint;
import nl.ou.refd.analysis.decisions.DecisionMethodSpecification;

/**
 * Dialog for presenting and handling dynamic design decisions during refactoring.
 * Allows actors to choose how to handle specific risks (for now missing references only as example)
 * on a per-risk basis.
 */
public class DecisionsDialog extends TitleAreaDialog {

    private final List<RefactoringDecisionPoint> decisionPoints;
    private final DecisionMethodSpecification refactoringSpec;
    private Map<String, RefactoringDecision> userDecisions;
    
    // Map to track combo boxes for each decision point: riskId -> combo
    private Map<String, Combo> decisionCombos;

    /**
     * Creates a new DecisionsDialog
     * 
     * @param parentShell the parent shell
     * @param decisionPoints the list of decision points to present
     * @param refactoringSpec the refactoring specification to populate with decisions
     */
    public DecisionsDialog(Shell parentShell, 
                           List<RefactoringDecisionPoint> decisionPoints,
                           DecisionMethodSpecification refactoringSpec) {
        super(parentShell);
        this.decisionPoints = decisionPoints;
        this.refactoringSpec = refactoringSpec;
        this.userDecisions = new HashMap<>();
        this.decisionCombos = new HashMap<>();
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Decision Point Model");
        newShell.setSize(600, 400);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Missing References Configuration");
        setMessage("Configure how to handle missing references in the extracted method.\n"
                + "For each reference, choose whether to add it as a parameter or access it as a field.");
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        
        // Create scrolled composite to handle many decision points
        ScrolledComposite scrolledComposite = new ScrolledComposite(area, SWT.V_SCROLL | SWT.BORDER);
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        Composite container = new Composite(scrolledComposite, SWT.NONE);
        container.setLayout(new GridLayout(1, false));
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Create a section for each decision point
        for (RefactoringDecisionPoint decisionPoint : decisionPoints) {
            createDecisionPointGroup(container, decisionPoint);
        }

        scrolledComposite.setContent(container);
        scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        return area;
    }

    /**
     * Creates a group UI for a single decision point
     * 
     * @param parent the parent composite
     * @param decisionPoint the decision point to create UI for
     */
    private void createDecisionPointGroup(Composite parent, RefactoringDecisionPoint decisionPoint) {
        Group group = new Group(parent, SWT.NONE);
        group.setText("Reference: " + decisionPoint.getRiskIdentifier());
        group.setLayout(new GridLayout(2, false));
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        group.setLayoutData(gd);

        // Description
        Label descriptionLabel = new Label(group, SWT.WRAP);
        descriptionLabel.setText(decisionPoint.getRiskDescription());
        GridData descGd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        descGd.horizontalSpan = 2;
        descGd.widthHint = 500;
        descriptionLabel.setLayoutData(descGd);

        // Decision selector
        Label choiceLabel = new Label(group, SWT.NONE);
        choiceLabel.setText("How to handle:");
        choiceLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

        Combo decisionCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
        decisionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        // Populate combo with possible decisions
        List<RefactoringDecision> decisions = decisionPoint.getPossibleDecisions();
        String[] decisionLabels = new String[decisions.size()];
        
        for (int i = 0; i < decisions.size(); i++) {
            RefactoringDecision decision = decisions.get(i);
            decisionLabels[i] = decision.getLabel();
        }
        
        decisionCombo.setItems(decisionLabels);
        decisionCombo.select(0); // Select first option by default

        // Store reference for later retrieval
        decisionCombos.put(decisionPoint.getRiskIdentifier(), decisionCombo);

        // Add listener to track selection (optional - for immediate feedback)
        decisionCombo.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            @Override
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                int selectedIndex = decisionCombo.getSelectionIndex();
                if (selectedIndex >= 0 && selectedIndex < decisions.size()) {
                    RefactoringDecision selectedDecision = decisions.get(selectedIndex);
                    userDecisions.put(decisionPoint.getRiskIdentifier(), selectedDecision);
                }
            }
        });

        // Pre-select first decision
        if (!decisions.isEmpty()) {
            userDecisions.put(decisionPoint.getRiskIdentifier(), decisions.get(0));
        }

        // Add visual separator for better readability
        Label separator = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sepGd = new GridData(SWT.FILL, SWT.CENTER, true, false);
        sepGd.horizontalSpan = 2;
        separator.setLayoutData(sepGd);
    }

    /**
     * Collects all user decisions and applies them to the refactoring specification
     * when OK is pressed
     */
    @Override
    protected void okPressed() {
        // Collect all decisions from combo boxes
        for (RefactoringDecisionPoint decisionPoint : decisionPoints) {
            String riskId = decisionPoint.getRiskIdentifier();
            Combo combo = decisionCombos.get(riskId);
            
            if (combo != null) {
                int selectedIndex = combo.getSelectionIndex();
                if (selectedIndex >= 0) {
                    List<RefactoringDecision> decisions = decisionPoint.getPossibleDecisions();
                    if (selectedIndex < decisions.size()) {
                        RefactoringDecision selectedDecision = decisions.get(selectedIndex);
                        
                        // Record in user decisions map
                        userDecisions.put(riskId, selectedDecision);
                        
                        // Apply decision to the refactoring specification
                        refactoringSpec.recordDecision(riskId, selectedDecision);
                        
                        // Apply the decision to the method specification
                        // This may modify parameters, fields, etc.
                        decisionPoint.applyDecision(selectedDecision, 
                            refactoringSpec.getMethodSpecification());
                    }
                }
            }
        }

        super.okPressed();
    }

    /**
     * Gets the map of decisions made by the user
     * 
     * @return map of riskId -> RefactoringDecision
     */
    public Map<String, RefactoringDecision> getUserDecisions() {
        return new HashMap<>(userDecisions);
    }

    /**
     * Gets the refactoring specification with decisions applied
     * 
     * @return the DecisionBasedRefactoringSpecification
     */
    public DecisionMethodSpecification getRefactoringSpecification() {
        return refactoringSpec;
    }
}