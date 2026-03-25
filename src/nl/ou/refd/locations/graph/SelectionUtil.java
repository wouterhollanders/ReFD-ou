package nl.ou.refd.locations.graph;

import java.util.stream.Collectors;

import com.ensoftcorp.atlas.core.index.common.SourceCorrespondence;

import nl.ou.refd.locations.graph.Tags.Attributes;

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
	
	//copied from student of Brinska, but modified to so it can be stored here in the seperate file.
	public static GraphQuery selectFromSource(String sourceFile, int begin, int end) {
		GraphQuery g = Graph.query().universe();
		
		return new GraphQuery(g.locations().stream().filter(location->{
				SourceCorrespondence sc = location.getAttribute(Attributes.SOURCE_CORRESPONDENCE);

				if (sc == null || !sc.sourceFile.toString().contains(sourceFile) || !(sc.offset >= begin && sc.offset <= end && (sc.offset+sc.length) >= begin && (sc.offset+sc.length) <= end)) {

					return false;
				}
				else {
					return true;
				}
				
			}).collect(Collectors.toSet())
		);
	}
	
	
}