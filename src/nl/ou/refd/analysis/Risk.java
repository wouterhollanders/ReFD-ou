package nl.ou.refd.analysis;

/**
 * Represents a category of risk that a detector can identify in the program.
 * Each concrete risk type defines its identity and a human-readable description.
 */
public abstract class Risk {

	/**
	 * Returns the unique identifier for this risk type.
	 */
	public abstract String getRiskId();

	/**
	 * Returns a human-readable description of this risk.
	 */
	public abstract String getDescription();
}
