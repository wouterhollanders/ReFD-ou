package nl.ou.refd.plugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IStartup;

public class MarkerRemover implements IStartup {

	private final static String MARKER_TYPE = "nl.ou.refd.markers.dangerMarker";

	@Override
	public void earlyStartup() {
		try {
			System.out.println("Start clearing markers...");
			IMarker[] markers = ResourcesPlugin.getWorkspace().getRoot().findMarkers(IMarker.PROBLEM, true,
					IFile.DEPTH_INFINITE);

			for (IMarker marker : markers) {
				try {
					String markerType = marker.getType();
					if (MARKER_TYPE.equals(markerType)) {
						// Retrieve attributes before deleting the marker
						String message = (String) marker.getAttribute(IMarker.MESSAGE);
						System.out.println("Deleting marker: " + message);

						// Delete marker
						marker.delete();
					}
				} catch (Exception e) {
					// Handle individual marker exceptions to continue processing others
					System.err.println("Error processing marker: " + e.getMessage());
				}
			}

			// Refresh the workspace after deleting markers
			ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IFile.DEPTH_INFINITE, null);

		} catch (Exception e) {
			// Handle any other exceptions
			e.printStackTrace();
		}
	}
}