package nl.ou.refd.analysis;

import java.util.ArrayList;
import java.util.List;

import nl.ou.refd.analysis.detectors.BrokenLocalReferences;
import nl.ou.refd.analysis.detectors.BrokenSubTyping;
import nl.ou.refd.analysis.detectors.CorrespondingSubclassSpecification;
import nl.ou.refd.analysis.detectors.Detector;
import nl.ou.refd.analysis.detectors.DoubleDefinition;
import nl.ou.refd.analysis.detectors.LostSpecification;
import nl.ou.refd.analysis.detectors.LostVariableMutation;
import nl.ou.refd.analysis.detectors.MissingAbstractImplementation;
import nl.ou.refd.analysis.detectors.MissingDefinition;
import nl.ou.refd.analysis.detectors.MissingLocalReferences;
import nl.ou.refd.analysis.detectors.MissingLocalReferences.Body;
import nl.ou.refd.analysis.detectors.MissingSuperImplementation;
import nl.ou.refd.analysis.detectors.OverloadParameterConversion;
import nl.ou.refd.analysis.detectors.RemovedConcreteOverride;
import nl.ou.refd.analysis.microsteps.AddClass;
import nl.ou.refd.analysis.microsteps.AddMethod;
import nl.ou.refd.analysis.microsteps.AddMethodBody;
import nl.ou.refd.analysis.microsteps.AddParameter;
import nl.ou.refd.analysis.microsteps.Microstep;
import nl.ou.refd.analysis.microsteps.MoveMethod;
import nl.ou.refd.analysis.microsteps.RemoveMethod;
import nl.ou.refd.analysis.refactorings.Refactoring;
import nl.ou.refd.locations.collections.LabeledLocationSet;
import nl.ou.refd.locations.graph.Graph;

/**
 * Class representing a danger analyser, which is a visitor which walks the
 * model of microsteps and detectors.
 */
public class DangerAnalyser implements ModelVisitor, DangerAggregator {

	private final Refactoring refactoring;
	private final VerdictFunction verdictFunction;
	
	//This was only added to validate the complexity of working with the POST-phase. This is not a real addition to the tool.
	private boolean simulateMicrosteps;

	private List<LabeledLocationSet> dangers;

	public DangerAnalyser(Refactoring refactoring) {
		this.refactoring = refactoring;
		this.verdictFunction = refactoring.verdictFunction(this);
		this.simulateMicrosteps = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void aggregateDangers(LabeledLocationSet dangers) {
		this.dangers.add(dangers);
	}

	/**
	 * Analyzes the refactoring contained within this analyzer.
	 * 
	 * @return a list of LabeledLocationSet objects representing the dangers found
	 */
	public List<LabeledLocationSet> analyse() {
		this.dangers = new ArrayList<LabeledLocationSet>();
		this.refactoring.getMicrosteps().forEach(microstep -> {
			microstep.accept(this);
		});

		return new ArrayList<LabeledLocationSet>(this.dangers);
	}

	//This was only added to validate the complexity of working with the POST-phase. This is not a real addition to the tool.
	public void SetMicrostepSimulation(boolean bool) {
		this.simulateMicrosteps = bool;
	}

	/**
	 * Convenience method to handle a microstep.
	 * 
	 * @param microstep the microstep to handle
	 */
	private void handleMicrostep(Microstep microstep) {
		microstep.getDetectors().forEach(detector -> detector.accept(this));
		if (this.simulateMicrosteps) {
			microstep.executeOnGraph(Graph.getInstance());
		}
	}

	/**
	 * Convenience method to handle a MoveMethod microstep.
	 * 
	 * @param microstep the MoveMethod microstep to handle
	 */
	private void handleMoveMethodMicrostep(MoveMethod microstep) {
		microstep.getDetectors().forEach(detector -> detector.accept(this));
		microstep.getComponentMicrosteps().forEach(componentMicrostep -> componentMicrostep.accept(this));
	}

	private void handleAddParameterMicrostep(AddParameter microstep) {
		microstep.getDetectors().forEach(detector -> detector.accept(this));
		microstep.getComponentMicrosteps().forEach(componentMicrostep -> componentMicrostep.accept(this));
	}

	/**
	 * Convenience method to handle a detector.
	 * 
	 * @param detector the detector to handle
	 */
	private void handleDetector(Detector<?> detector) {
		detector.accept(this.verdictFunction);
	}

	// Microsteps
	@Override
	public void visit(AddMethod microstep) {
		handleMicrostep(microstep);
	}

	@Override
	public void visit(RemoveMethod microstep) {
		handleMicrostep(microstep);
	}

	@Override
	public void visit(MoveMethod microstep) {
		handleMoveMethodMicrostep(microstep);
	}

	@Override
	public void visit(AddClass addClass) {
		handleMicrostep(addClass);
	}

	@Override
	public void visit(AddMethodBody microstep) {
		handleMicrostep(microstep);
	}

	@Override
	public void visit(AddParameter microstep) {
		handleAddParameterMicrostep(microstep);
	}

	// Risks
	@Override
	public void visit(BrokenLocalReferences.Body detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(BrokenSubTyping.Method detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(CorrespondingSubclassSpecification.Method detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(DoubleDefinition.Method detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(LostSpecification.Method detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(MissingAbstractImplementation.Method detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(MissingDefinition.Method detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(MissingSuperImplementation.Method detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(OverloadParameterConversion.Method detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(RemovedConcreteOverride.Method detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(DoubleDefinition.Class detector) {
		handleDetector(detector);
	}

	@Override
	public void visit(MissingLocalReferences.Body detector) {
		handleDetector(detector);

	}

	@Override
	public void visit(LostVariableMutation.Body detector) {
		handleDetector(detector);
	}

}
