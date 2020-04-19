package de.bright_side.brightmarkdown.base;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class TestingUtil {
	private static final String PROPERTIES_FILE_NAME = "config.properties";
	private static final String DEBUG_OUTPUT_DIR_PROPERTY_NAME = "debugOutputDir";

	private static File getPropertiesFile() {
		File file = new File(System.getProperty("user.home"), ".BrightMarkdown-test-config");
		file = new File(file, PROPERTIES_FILE_NAME);
		return file;
	}
	
	private static Properties getConfigProperties() throws Exception {
		File file = getPropertiesFile();
		if (!file.exists()) {
			throw new Exception(createConfigFileInfoText("Config file not found"));
		}
		Properties result = new Properties();
		result.load(new FileReader(file));
		return result;
	}

	private static String getDebugOutputDir() throws Exception {
		Properties properties = getConfigProperties();
		Object result = properties.get(DEBUG_OUTPUT_DIR_PROPERTY_NAME);
		if (result == null) {
			throw new Exception(createConfigFileInfoText("Missing property '" + DEBUG_OUTPUT_DIR_PROPERTY_NAME + "'"));
		}
		return result.toString();
	}
	
	private static String createConfigFileInfoText(String message) {
		StringBuilder result = new StringBuilder("Error: " + message + "\n");
		result.append("The tests use configuration settings from a properties file located at '" + getPropertiesFile() + "'.\n");
		result.append("Example content:>>\n");
		result.append("debugOutputDir=C:/BrightMarkdownTest\n");
		result.append("<<\n");
		return result.toString();
	}

	public static void writeFile(File file, String data) throws Exception {
		try (FileWriter myWriter = new FileWriter(file)) {
			myWriter.write(data);
		} catch (IOException e) {
			throw e;
		}
	}

	public static void writeDebugFileAndResources(String fileNameWithoutEnding, String fileData) throws Exception {
		if (!TestingConstants.WRITING_DEBUG_FILES_ENABLED) {
			return;
		}
		File dir = new File(getDebugOutputDir());
		writeTestResource(dir, "test.jpg");
		writeTestResource(dir, "img1.jpg");
		writeFile(new File(dir, fileNameWithoutEnding + ".html"), fileData);
	}

	private static void writeTestResource(File dir, String resourceFileName) throws Exception {
		File outputFile = new File(dir, resourceFileName);
		try(InputStream inputStream = new TestingUtil().getClass().getClassLoader().getResourceAsStream(resourceFileName)){
			if (inputStream == null) {
				throw new Exception("Could not get resource '" + resourceFileName + "'");
			}
			Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			throw e;
		}
	}
}
