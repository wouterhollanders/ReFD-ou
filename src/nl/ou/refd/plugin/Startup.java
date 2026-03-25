package nl.ou.refd.plugin;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

	@Override
	public void earlyStartup() {
		try {
			System.out.println("Start clearing markers...");
			MarkerRemover markerRemover = new MarkerRemover();
			markerRemover.removeDangers();

		} catch (Exception e) {
			// Handle any other exceptions
			e.printStackTrace();
		}
	}

}
