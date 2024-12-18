package nl.ou.refd.plugin.ui.topbarmenu;

import org.eclipse.core.commands.ExecutionEvent;
import com.ensoftcorp.open.commons.utilities.MappingUtils;

import nl.ou.refd.plugin.Controller;

/**
 * Class representing the menu button for the Rename Method refactoring
 * option. The presence of this button can be configured in plugin.xml.
 */
/**
 * Class representing the menu button for the Rename Method refactoring option.
 * The presence of this button can be configured in plugin.xml.
 */
public class ResetAnalysisButton extends MenuButtonHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(ExecutionEvent event) {
		try {
			MappingUtils.mapWorkspace();
			Thread.sleep(1000);
			Controller.getController().resetAnalysis();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
