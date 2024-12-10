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
	
	
	public static String getClassContent(String className) {
        // Query all nodes tagged as "Class"
        AtlasSet<Node> nodes = Query.universe()
                .nodesTaggedWithAny("Class") // Find all class nodes
                .eval().nodes(); // Evaluate and retrieve nodes

        // Filter by name attribute
        for (Node node : nodes) {
            if (node.hasAttr("name") && node.getAttr("name").equals(className)) {
                // Retrieve the "source" attribute or another attribute containing the code
                if (node.hasAttr("source")) {
                    return node.getAttr("source").toString();
                }
            }
        }

        return ""; // Return empty string if no match is found
    }
}
