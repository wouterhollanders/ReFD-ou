package nl.ou.refd.plugin;

import org.eclipse.ui.IStartup;

public class StartUp implements IStartup {

	@Override
	public void earlyStartup() {
		try {
			System.out.println("Start clearing markers...");
			MarkerRemover markerRemover = new MarkerRemover();
			markerRemover.removeReFDDangerMarkers();

		} catch (Exception e) {
			// Handle any other exceptions
			e.printStackTrace();
		}
	}

}