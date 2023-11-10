package it.dgs.queuemanager.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

public class FileUtil {

	private final static String FILE_PROP = "application.properties";

	public Properties getAppProperties() {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = ClassLoader.getSystemClassLoader().getResourceAsStream(FILE_PROP);
			prop.load(input);
		} catch (IOException io) {
			io.printStackTrace();
		}
		return prop;
	}

	public InputStream getResource(String fileName) {
		InputStream is = null;
		ClassLoader classloader = getClass().getClassLoader();
		if (classloader == null) {
			is = getClass().getResourceAsStream(fileName);
		} else {
			is = getClass().getClassLoader().getResourceAsStream(fileName);
			if (is == null) {
				is = getClass().getResourceAsStream(fileName);
			}
		}
		return is;
	}

	public String readResource(InputStream inputStream) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		return result.toString(StandardCharsets.UTF_8.name());
	}

	public <T> T loadObject(String res, Class<T> cls) throws Exception {
		InputStream is = getResource(res);
		String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		ObjectMapper mapper = new ObjectMapper();
		T search = mapper.readValue(json, cls);
		return search;
	}

	public <T> List<T> loadObjectList(String res, Class<T> cls) throws Exception {
		InputStream is = getResource(res);
		String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
		ObjectMapper mapper = new ObjectMapper();
		CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, Class.forName(cls.getName()));
		List<T> search = mapper.readValue(json, collectionType);
		return search;
	}

}
