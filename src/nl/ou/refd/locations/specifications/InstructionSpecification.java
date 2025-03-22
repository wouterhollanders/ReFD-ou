package nl.ou.refd.locations.specifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;

import nl.ou.refd.exceptions.IncompatibleProgramLocationException;
import nl.ou.refd.locations.collections.ClassSet;
import nl.ou.refd.locations.collections.MethodSet;
import nl.ou.refd.locations.generators.ProgramComponentsGenerator;
import nl.ou.refd.locations.graph.Graph;
import nl.ou.refd.locations.graph.GraphQuery;
import nl.ou.refd.locations.graph.ProgramLocation;
import nl.ou.refd.locations.graph.Relation;
import nl.ou.refd.locations.graph.Tags;
import nl.ou.refd.locations.specifications.LocationSpecification.AccessModifier;
import nl.ou.refd.locations.streams.InstructionStream;

/**
 * Class representing a specification of a single instruction location in a
 * codebase. The specification can be of either a location that already exists
 * within the codebase, or that does not exists (yet).
 */
public class InstructionSpecification extends LocationSpecification {

	private String instructionName;
	private MethodSpecification enclosingMethod;

	/**
	 * Creates a field location specification from a field name and a specification
	 * of a class the field belongs to.
	 * 
	 * @param fieldName      the name of the field
	 * @param enclosingClass the class the field belongs to
	 */
	public InstructionSpecification(String fieldName, MethodSpecification enclosingMethod) {
		this.instructionName = fieldName;
		this.enclosingMethod = enclosingMethod;
	}
	
	public InstructionSpecification(ProgramLocation pl) {
			
		GraphQuery nQ = Graph.query(pl);
		
		this.instructionName = pl.<String>getAttribute(Tags.Attributes.NAME);
		this.enclosingMethod = new MethodSpecification(Graph.query(pl).parent().singleLocation());
	}
	
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public InstructionSpecification copy() {
//		return new InstructionSpecification(fieldName, enclosingClass);
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	@Override
//	public String toString() {
//		return this.fieldName;
//	}
//	
	/**
	 * Returns an immutable String representing the field name.
	 * @return the name of the class as a string
	 */
	public String getInstructionName() {
		return instructionName;
	}
//
//	/**
//	 * Sets the field's name.
//	 * @param fieldName the new name of the field
//	 */
//	public void setFieldName(String fieldName) {
//		this.fieldName = fieldName;
//	}
//
	/**
	 * Returns the enclosing class as an object. This object is mutable.
	 * 
	 * @return the enclosing package as ClassSpecification
	 */
	public MethodSpecification getEnclosingMethod() {
		return enclosingMethod;
	}
//
//	/**
//	 * Sets the enclosing class.
//	 * @param enclosingClass the new enclosing class
//	 */
//	public void setEnclosingClass(ClassSpecification enclosingClass) {
//		this.enclosingClass = enclosingClass;
//	}

	@Override
	public LocationSpecification copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProgramLocation construct(Graph graph) {
		ProgramLocation parentMethod = new MethodSet(this.getEnclosingMethod()).singleLocation();
		ProgramLocation nInstruction = createInstruction(graph);
		nInstruction.putAttribute(Tags.Attributes.NAME, this.getInstructionName());
		
		Relation contains = graph.createRelation(parentMethod, nInstruction);
		contains.tag(Tags.Relation.CONTAINS);
		contains.tag(Tags.Relation.HAS_CONTROL_FLOW);

		return nInstruction;
	}

	private static ProgramLocation createInstruction(Graph graph) {
		ProgramLocation rNode = graph.createProgramLocation();
		
		rNode.tag(Tags.ProgramLocation.CONTROLFLOW);
		rNode.tag(Tags.ProgramLocation.DATAFLOW);
		rNode.tag(Tags.ProgramLocation.REFACTOR_CREATED_INSTRUCTION);
		rNode.tag(Tags.ProgramLocation.VARIABLE);
		rNode.tag(Tags.ProgramLocation.PACKAGE_VISIBILITY); //Strange, but the example has this
		

		return rNode;
	}

}
