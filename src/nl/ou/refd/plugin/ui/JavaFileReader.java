package nl.ou.refd.plugin.ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class JavaFileReader {
	
	public static String readAndFormatJavaFile(String filePath) {
		StringBuilder rawContent = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				rawContent.append(line).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		StringBuilder formatted = new StringBuilder();
	    String[] lines = rawContent.toString().split("\n");
	    int indentLevel = 0;

	    for (String line : lines) {
	        line = line.trim();

	        if (line.endsWith("}") || line.startsWith("}")) {
	            indentLevel--;
	        }

	        for (int i = 0; i < indentLevel; i++) {
	            formatted.append("    "); // Add 4 spaces per indent level
	        }

	        formatted.append(line).append("\n");

	        if (line.endsWith("{")) {
	            indentLevel++;
	        }
	    }

	    return formatted.toString();
	}
}
