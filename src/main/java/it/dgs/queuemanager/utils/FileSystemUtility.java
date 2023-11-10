package it.dgs.queuemanager.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
public class FileSystemUtility {

	public static String createTempDirectory(String prefix) throws Exception {
		File tempDirectory = Files.createTempDirectory(prefix).toFile();
		String tempPath = tempDirectory.getAbsolutePath();
		log.debug("Cartella temporanea creata: " + tempPath);
		return tempPath;
	}

	public static void createDirectory(String directoryPath) {
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			boolean success = directory.mkdirs();
			if (success) {
				log.debug("Cartella creata con successo.");
			} else {
				log.debug("Impossibile creare la cartella.");
			}
		} else {
			log.debug("La cartella esiste già.");
		}
	}

	public static void deleteDirectory(String directoryPath) {
		Path path = Paths.get(directoryPath);
		try {
			if (Files.exists(path)) {
				deleteDirectory(path);
				log.debug("Cartella eliminata con successo: " + path);
			} else {
				log.debug("La cartella non esiste: " + path + "; " + directoryPath);
			}
		} catch (IOException e) {
			log.debug("Impossibile eliminare la cartella: " + directoryPath + "; " + e.getMessage());
		}
	}

	private static void deleteDirectory(Path directory) throws IOException {
		List<Path> paths = Files.list(directory).collect(Collectors.toList());
		for (Path path : paths) {
			if (Files.isDirectory(path)) {
				deleteDirectory(path);
			} else {
				Files.delete(path);
				log.debug("Files.delete: " + path);
			}
		}
		Files.delete(directory);
	}

	public static void createZipFromDirectory(String sourceDirectoryPath, String zipFilePath) {
		try (FileOutputStream fos = new FileOutputStream(zipFilePath); ZipOutputStream zos = new ZipOutputStream(fos)) {
			File sourceDirectory = new File(sourceDirectoryPath);
			addToZip(sourceDirectory, sourceDirectory.getName(), zos, false);
			log.debug("File ZIP creato con successo.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void addToZip(File file, String entryName, ZipOutputStream zos, boolean subFolder) throws IOException {
		if (file.isDirectory()) {
			for (File childFile : file.listFiles()) {
				String subEntry = childFile.getName();
				if (subFolder) {
					subEntry = entryName + File.separator + childFile.getName();
				}
				addToZip(childFile, subEntry, zos, true);
			}
		} else {
			ZipEntry zipEntry = new ZipEntry(entryName);
			zos.putNextEntry(zipEntry);
			try (FileInputStream fis = new FileInputStream(file)) {
				byte[] buffer = new byte[1024];
				int length;
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
			}
			zos.closeEntry();
		}
	}

	public static void unzipZipFile(String zipFilePath, String outputDirectoryPath) {
		try (FileInputStream fis = new FileInputStream(zipFilePath); ZipInputStream zis = new ZipInputStream(fis)) {
			byte[] buffer = new byte[1024];
			ZipEntry zipEntry;
			while ((zipEntry = zis.getNextEntry()) != null) {
				String entryName = zipEntry.getName();
				Path entryPath = Paths.get(outputDirectoryPath, entryName);
				if (zipEntry.isDirectory()) {
					Files.createDirectories(entryPath);
				} else {
					try (FileOutputStream fos = new FileOutputStream(entryPath.toFile())) {
						int length;
						while ((length = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, length);
						}
					}
				}
				zis.closeEntry();
			}
			log.debug("File ZIP scompattato con successo.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
