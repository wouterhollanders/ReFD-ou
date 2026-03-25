package nl.ou.refd.plugin.ui.topbarmenu;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import com.ensoftcorp.open.commons.ui.utilities.DisplayUtils;
import nl.ou.refd.exceptions.NoActiveProjectException;
import nl.ou.refd.locations.generators.ProgramComponentsGenerator;
import nl.ou.refd.locations.graph.Graph;
import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.graph.Tags;
import nl.ou.refd.plugin.ui.EclipseUtil;
import nl.ou.refd.locations.collections.MethodSet;

/**
 * Class representing the menu button for the Combine Methods into Class
 * refactoring option. The presence of this button can be configured in
 * plugin.xml.
 */
public class ControlFlowCheckButton extends MenuButtonHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(ExecutionEvent event) {

		IProject currentProject;

		try {
			currentProject = EclipseUtil.currentProject();
		} catch (NoActiveProjectException e) {
			DisplayUtils.showMessage("Error: No active project");
			return;
		}

		var destinationMethod = new ProgramComponentsGenerator().stream().classes().classesByName("BillingPrinterA")
				.methods().filterByName("printOwing").collect().toLocationSpecifications().get(0);

		var methodDest = new ProgramComponentsGenerator().stream().classes()
				.classesByName(destinationMethod.getEnclosingClass().getClassName()).methods()
				.filterByName("MyNewMethod").collect().toLocationSpecifications().get(0);
		var body = methodDest.getBody().collect();
		var bodyLocations = body.locations();
		System.out.print(bodyLocations);

		// Validate whether the parameter 'orderCount' flows into the method body
		ProgramLocation methodLoc = new MethodSet(methodDest).singleLocation();

		// Find the parameter ProgramLocation named 'orderCount'
		ProgramLocation orderCountParam = null;
		String targetParamName = "orderCount";
		for (var edge : methodLoc.out(Tags.Relation.HAS_PARAMETER)) {
			ProgramLocation p = edge.to();
			String name = p.getAttribute(Tags.Attributes.NAME);
			if (targetParamName.equals(name)) {
				orderCountParam = p;
				break;
			}
		}

		if (orderCountParam == null) {
			DisplayUtils.showMessage("Parameter 'orderCount' not found in MyNewMethod");
			return;
		}

		// Compute dataflow from the parameter and intersect with the method body
		GraphQuery paramFlows = Graph.query(orderCountParam)
				.successorsOn(Graph.query().universe().relations(Tags.Relation.DATAFLOW));
		GraphQuery methodBody = Graph.query(methodLoc).contained();

		boolean flowsIntoBody = methodBody.intersection(paramFlows).locationCount() > 0;

//		// Reverse validation: check if method body declares a variable named after the
//		// parameter
//		ProgramLocation matchingLocalVar = null;
//		for (var edge : methodLoc.out(Tags.Relation.HAS_VARIABLE)) {
//			ProgramLocation v = edge.to();
//			String vName = v.getAttribute(Tags.Attributes.NAME);
//			if (targetParamName.equals(vName)) {
//				matchingLocalVar = v;
//				break;
//			}
//		}
//
//		GraphQuery paramFlows2 = Graph.query(orderCountParam)
//				.successorsOn(Graph.query().universe().relations(Tags.Relation.DATAFLOW));
//		boolean relatedByDataflow = paramFlows2.intersection(Graph.query(matchingLocalVar)).locationCount() > 0;
		DisplayUtils.showMessage("orderCount flows into body: " + flowsIntoBody);
	}

}
