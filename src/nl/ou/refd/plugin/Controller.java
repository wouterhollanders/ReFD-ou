package nl.ou.refd.plugin;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.ensoftcorp.open.commons.utilities.MappingUtils;

import nl.ou.refd.analysis.DangerAnalyser;
import nl.ou.refd.analysis.refactorings.CombineMethodsIntoClass;
import nl.ou.refd.analysis.refactorings.ExtractMethod;
import nl.ou.refd.analysis.refactorings.PullUpMethod;
import nl.ou.refd.analysis.refactorings.RefactorRepair;
import nl.ou.refd.analysis.refactorings.Refactoring;
import nl.ou.refd.exceptions.NoActiveProjectException;
import nl.ou.refd.locations.collections.InstructionSet;
import nl.ou.refd.locations.collections.LabeledLocationSet;
import nl.ou.refd.locations.specifications.ClassSpecification;
import nl.ou.refd.locations.specifications.MethodSpecification;
import nl.ou.refd.plugin.ui.EclipseUtil;
import nl.ou.refd.plugin.ui.PopupUtil;

/**
 * The controller class which is the central point of the plugin. It is also the
 * activator class (Eclipse-specific) which controls the plug-in life cycle. The
 * controller contains a singleton to access its functionality.
 */
public class Controller extends AbstractUIPlugin {

	private static Controller controller;

	/**
	 * Singleton of controller.
	 * 
	 * @return
	 */
	public static Controller getController() {
		return controller;
	}

	/**
	 * Standard method to start Eclipse plugin. This gets called before internal
	 * methods of the plugin, so it initialized the singleton of controller as well.
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		controller = this;
	}

	/**
	 * Standard method to stop Eclipse plugin.
	 */
	public void stop(BundleContext context) throws Exception {
		controller = null;
		super.stop(context);
	}

	/**
	 * Start a refactoring analysis for the Pull Up Method refactoring. This method
	 * starts a new thread to not block the program during analysis.
	 * 
	 * @param target      the method to pull up
	 * @param destination the class to pull target up to
	 * @throws NoActiveProjectException
	 */
	public void pullUpMethod(MethodSpecification target, ClassSpecification destination)
			throws NoActiveProjectException {
		final IProject project = EclipseUtil.currentProject();
		new Thread(new Runnable() {

			@Override
			public void run() {
				PullUpMethod refactoring = new PullUpMethod(target, destination);
				DisplayDangers(new DangerAnalyser(refactoring).analyse(), refactoring.getName(), project);
			}
		}).start();
	}

	/**
	 * Start a refactoring analysis for the Combine Methods into Class refactoring.
	 * This method starts a new thread to not block the program during analysis.
	 * 
	 * @param targets     the methods to move to the new class
	 * @param destination the new class to combine the targets into
	 * @throws NoActiveProjectException
	 */
	public void combineMethodsIntoClass(List<MethodSpecification> targets, ClassSpecification destination)
			throws NoActiveProjectException {
		final IProject project = EclipseUtil.currentProject();
		new Thread(new Runnable() {

			@Override
			public void run() {
				CombineMethodsIntoClass refactoring = new CombineMethodsIntoClass(destination, targets);
				DisplayDangers(new DangerAnalyser(refactoring).analyse(), refactoring.getName(), project);
			}
		}).start();
	}

	public void extractMethod(InstructionSet target, MethodSpecification destinationMethod) throws NoActiveProjectException {
		final IProject project = EclipseUtil.currentProject();

		new Thread(new Runnable() {

			@Override
			public void run() {
				ExtractMethod refactoring = new ExtractMethod(target, destinationMethod);
				DisplayDangers(new DangerAnalyser(refactoring).analyse(), refactoring.getName(), project);
			}
		}).start();
	}
	
	public void extractMethodPost(InstructionSet target, MethodSpecification destinationMethod) throws NoActiveProjectException {
		final IProject project = EclipseUtil.currentProject();

		new Thread(new Runnable() {

			@Override
			public void run() {
				ExtractMethod refactoring = new ExtractMethod(target, destinationMethod);
				// 1) Analyse and simulate the original refactoring (executes microsteps once)
				DangerAnalyser initial = new DangerAnalyser(refactoring);
		        List<LabeledLocationSet> dangers = initial.analyse();
				DisplayDangers(dangers, refactoring.getName(), project);
				// 2) If dangers exist, apply only repair steps on top of current graph
				if (dangers != null && !dangers.isEmpty()) {
		        	RefactorRepair orchestrator = new RefactorRepair();
		        	Refactoring repairsOnly = orchestrator.repairsOnlyRefactoring(refactoring, dangers);
		        	// Apply repairs (executes repair microsteps)
		        	var test = new DangerAnalyser(repairsOnly).analyse();
		        	// 3) Verify by re-running detectors without executing microsteps again
		        	DangerAnalyser verify = new DangerAnalyser(refactoring);
		        	verify.SetMicrostepSimulation(false);
		        	List<LabeledLocationSet> after = verify.analyse();
		        	DisplayDangers(after, refactoring.getName(), project);
				}
			}
		}).start();
	}

	public void resetAnalysis() {
		var markerRemover = new MarkerRemover();
		try {
			markerRemover.removeDangers();
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	private void DisplayDangers(List<LabeledLocationSet> dangers, String name, IProject project) {
		if (dangers == null || dangers.isEmpty()) {
			// Use the new safe refactor marker
			PopupUtil.showSafeRefactoringPopup(name);

		} else {
			// Create markers for each danger
			dangers.forEach(danger -> danger.mark(new MarkerCreator(project)::defaultMarker));
		}
	}
}
