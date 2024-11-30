package nl.ou.refd.plugin.ui;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class PopupUtil {

	private static final String REFACTORING_ACTION_COMPLETED = "Refactoring Analysis Completed";

	public static void showSafeRefactoringPopup(String refactoringName) {
		Display.getDefault().asyncExec(() -> {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openInformation(shell, REFACTORING_ACTION_COMPLETED,
					"Refactoring Action " + refactoringName + " is safe to use.");
		});
	}
}