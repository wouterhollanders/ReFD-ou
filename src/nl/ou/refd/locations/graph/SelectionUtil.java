package nl.ou.refd.locations.graph;

import com.ensoftcorp.atlas.core.db.graph.Node;
import com.ensoftcorp.atlas.core.db.set.AtlasSet;
import com.ensoftcorp.atlas.core.query.Query;

/**
 * Class which helps with selecting elements through utility methods.
 */
public class SelectionUtil {
	private SelectionUtil(){}
	
	/**
	 * Gets the current selection in the editor of Eclipse as GraphQuery.
	 * @return the current selection in the editor of Eclipse as GraphQuery
	 */
	public static GraphQuery getSelection() {
		return new GraphQuery(com.ensoftcorp.atlas.ui.selection.SelectionUtil.getLastSelectionEvent().getSelection());
	}
}
