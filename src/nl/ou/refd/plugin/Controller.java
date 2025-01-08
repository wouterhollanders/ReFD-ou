package nl.ou.refd.plugin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import nl.ou.refd.analysis.DangerAnalyser;
import nl.ou.refd.analysis.refactorings.CombineMethodsIntoClass;
import nl.ou.refd.analysis.refactorings.FormTemplateMethod;
import nl.ou.refd.analysis.refactorings.PullUpMethod;
import nl.ou.refd.analysis.refactorings.Refactoring;
import nl.ou.refd.analysis.refactorings.RenameMethod;
import nl.ou.refd.exceptions.NoActiveProjectException;
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

	public void resetAnalysis() {
		var markerRemover = new MarkerRemover();
		try {
			markerRemover.removeReFDDangerMarkers();
		} catch (CoreException e) {
			e.printStackTrace();
		}
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
				var dangers = new DangerAnalyser(refactoring).analyse();

				if (dangers == null || dangers.isEmpty()) {
					// Use the new safe refactor marker
					PopupUtil.showSafeRefactoringPopup(refactoring.getName());

				} else {
					// Create markers for each danger
					dangers.forEach(danger -> danger.mark(new MarkerCreator(project)::defaultMarker));
				}
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
				var dangers = new DangerAnalyser(refactoring).analyse();

				if (dangers == null || dangers.isEmpty()) {
					// Use the new safe refactor marker
					PopupUtil.showSafeRefactoringPopup(refactoring.getName());

				} else {
					// Create markers for each danger
					dangers.forEach(danger -> danger.mark(new MarkerCreator(project)::defaultMarker));
				}
			}
		}).start();
	}

	/**
	 * Start a refactoring analysis for the Pull Up Method refactoring. This method
	 * starts a new thread to not block the program during analysis.
	 * 
	 * @param target      the method to pull up
	 * @param destination the class to pull target up to
	 * @throws NoActiveProjectException
	 */
	public void renameMethod(MethodSpecification target, String newMethodName) throws NoActiveProjectException {
		final IProject project = EclipseUtil.currentProject();

		new Thread(new Runnable() {

			@Override
			public void run() {
				RenameMethod refactoring = new RenameMethod(target, newMethodName);
				var dangers = new DangerAnalyser(refactoring).analyse();

				if (dangers == null || dangers.isEmpty()) {
					// Use the new safe refactor marker
					PopupUtil.showSafeRefactoringPopup(refactoring.getName());

				} else {
					// Create markers for each danger
					dangers.forEach(danger -> danger.mark(new MarkerCreator(project)::defaultMarker));
				}
			}
		}).start();
	}

	public void formTemplateMethod(ClassSpecification destinationSuper, List<MethodSpecification> methodsToPull,
			List<ClassSpecification> subClasses, List<MethodSpecification> methodsToCreate)
			throws NoActiveProjectException {
		final IProject project = EclipseUtil.currentProject();

		new Thread(new Runnable() {

			@Override
			public void run() {
				var dangers = new ArrayList<LabeledLocationSet>();
				FormTemplateMethod refactoring = new FormTemplateMethod(destinationSuper, methodsToPull, subClasses,
						methodsToCreate);

				dangers.addAll(new DangerAnalyser(refactoring).analyse());

				for (Refactoring sub : refactoring.getSubRefactorings()) {
					dangers.addAll(new DangerAnalyser(sub).analyse());
				}

				if (dangers == null || dangers.isEmpty()) {
					PopupUtil.showSafeRefactoringPopup(refactoring.getName());
				} else {
					dangers.forEach(danger -> danger.mark(new MarkerCreator(project)::defaultMarker));
				}
			}
		}).start();
	}
}