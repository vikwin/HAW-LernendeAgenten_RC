package agents;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public abstract class FileHandler {

	public static synchronized void saveZipFile(File zipFile,
			String entryFilename, Double[] data) {
		try {
			ZipOutputStream zos;
			ZipEntry entry;

			if (zipFile.exists()) {
				// get a temp file
				File tempFile = File.createTempFile(zipFile.getName(), ".zip");
				// delete it, otherwise you cannot rename your existing zip to
				// it.
				tempFile.delete();

				boolean renameOk = zipFile.renameTo(tempFile);
				if (!renameOk) {
					System.err
							.printf("### Fehler beim Umbenennen der Datei von %s zu %s ###\n",
									zipFile.getAbsolutePath(),
									tempFile.getAbsolutePath());
				}
				byte[] buf = new byte[1024];

				ZipInputStream zin = new ZipInputStream(new FileInputStream(
						tempFile));
				zos = new ZipOutputStream(new FileOutputStream(zipFile));

				entry = zin.getNextEntry();
				while (entry != null) {
					String name = entry.getName();
					if (!name.equals(entryFilename + ".json")) {
						// Add ZIP entry to output stream.
						zos.putNextEntry(new ZipEntry(name));
						// Transfer bytes from the ZIP file to the output file
						int len;
						while ((len = zin.read(buf)) > 0) {
							zos.write(buf, 0, len);
						}
					}
					entry = zin.getNextEntry();
				}

				// Close the streams
				zin.close();
				tempFile.delete();
			} else {
				zos = new ZipOutputStream(new FileOutputStream(zipFile));
			}

			// add new entry
			entry = new ZipEntry(entryFilename + ".json");
			zos.putNextEntry(entry);

			StringBuilder actionListString = new StringBuilder("["); // JSONValue.toJSONString(actionList);
			// Double[] actionList = getActionList();
			for (double d : data) {
				actionListString.append(d + ",");
			}
			actionListString.delete(actionListString.length() - 1,
					actionListString.length());
			actionListString.append("]");

			zos.write(actionListString.toString().getBytes());

			// Complete the ZIP file
			zos.close();
		} catch (IOException e) {
			System.err.printf("### Fehler beim Speichern des Agents: %s ###\n",
					e.getMessage());
		}
	}
}
