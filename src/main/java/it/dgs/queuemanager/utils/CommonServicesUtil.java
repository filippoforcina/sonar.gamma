package it.dgs.queuemanager.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class CommonServicesUtil {
	
   	public static InputStream convertFileToInputStream(File file) throws IOException {
        return  new FileInputStream(file);
    }
	
	public static InputStream convertByteArrayToInputStream(byte[] byteArray) throws IOException {
        return  new ByteArrayInputStream(byteArray);
    }
	
	
	public static File creaFileTemp(String path, String fileName) throws IOException {
        String filePath = path + Long.toString(System.currentTimeMillis()) + fileName; 
        File file = new File(filePath);
        // Verifica se il file esiste già, in caso contrario crea un nuovo file
        if (file.createNewFile()) {
            log.debug("Il file è stato creato con successo! [{}]", file.getAbsolutePath());
        } else {
            log.debug("Il file esiste già. [{}]", file.getAbsolutePath());
        }

        // Scrivi del contenuto nel file (opzionale)
        FileWriter writer = new FileWriter(file);
        writer.write("Questo è il contenuto del file.");
        writer.close();

		return file;
    }
	
	
	public static String createURI (String filePath) throws Exception{
		   URI uri = new URI("file", filePath, null);

	       return uri.toString();
	}
	
	public static InputStream cloneInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        baos.flush();
        return new ByteArrayInputStream(baos.toByteArray());
    }
	
	
	public static File createFileFromInputStream(InputStream inputStream, String outputPath) throws IOException {
        File outputFile = new File(outputPath);
        
        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        }

        return outputFile;
    }
  
	
}
