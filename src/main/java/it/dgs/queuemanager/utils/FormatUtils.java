package it.dgs.queuemanager.utils;

import java.io.File;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FormatUtils {

	public static String viewObject(Object obj) {
		String json = "";
		try {
			ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().createXmlMapper(false).build();
			objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
			json = objectMapper.writeValueAsString(obj);
		} catch (Exception err) {
			log.error("Error", err);
		}
		return json;
	}

	public static String[] splitObjectName(String objectName) {
		return objectName.split("/");
	}

	public static String findObjectName(String objectName, int start) {
		String[] components = splitObjectName(objectName);
		String find = "";
		if (components != null) {
			for (int j = start; j < components.length; j++) {
				find += File.separator + components[j];
			}
		}
		return find;
	}

	public static String formatPath(String... paths) {
		StringBuilder str = new StringBuilder("");
		for (String path : paths) {
			if (!path.isEmpty()) {
				if (path.endsWith("/")) {
					str.append(path);
				} else {
					str.append(path + "/");
				}
			}
		}
		return str.toString();
	}

}
