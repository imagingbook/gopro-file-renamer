package com.imagingbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.UIManager;

/**
 * GoPro files are named in a weird way, e.g., the continuous segments of
 * the some composite video sequence ("0527") are named
 * <pre>
 * GH010527.MP4
 * GH020527.MP4
 * GH030527.MP4
 * ...</pre>
 * Thus if listed by title, clips belonging to the same video do not show in succession!
 * This program renames these files to
 * <pre>
 * 052701-GH010527.MP4
 * 052702-GH020527.MP4
 * 052703-GH030527.MP4
 * ...</pre>
 * thereby preserving the original titles.
 * All associated GoPro file types are renamed, i.e. 
 * {@code .MP4},
 * {@code .THM} and 
 * {@code .LRV} files.
 *
 * @author wilbur
 * @version 2022/12/12
 */
public class GoProFileRenamer {

	private static boolean DRY_RUN = true;

	private static String DEFAULT_DIR = "D:\\video\\2023-Vogesen-Schwarzwald";
// 	private static String DEFAULT_DIR = Paths.get("").toAbsolutePath().toString();
//	private static int MAX_NAME_LENGTH = 128;

	public static void main(String[] args) throws Exception {
		System.out.println("Default directory: " + DEFAULT_DIR);
		if (DRY_RUN) {
			System.out.println("This is a DRY RUN only!");
		}
		// choose the root directory containing the java files
		String rootDir = selectDirectory(DEFAULT_DIR);
		if (rootDir == null)
			return;

		System.out.println("Root path: " + rootDir);
		File fRootDir = new File(rootDir);
		validateDirectory(fRootDir);

		GoProFileRenamer gfrn = new GoProFileRenamer(fRootDir);
		gfrn.run();
		Thread.sleep(100);
		System.out.println("Done.");
	}

	// -------------------------------------------------------------------

	private final File fRootDir;
	List<File> fileList = null;
	private int fileCount = 0;
	private int renamedCount = 0;
//	MessageDigest md5Digest;

	GoProFileRenamer(File rootDir) throws NoSuchAlgorithmException {
		fRootDir = rootDir;
//		md5Digest = MessageDigest.getInstance("MD5");
	}

	void run() throws IOException {
		System.out.println("Renaming files ...");
		fileList = new ArrayList<File>();
		processFiles(fRootDir);
		if (fileCount == 0) {
			System.out.println("Found no GoPro files to rename!");
		}
		else {
			System.out.println("Files checked: " + fileCount);
			System.out.println("Files renamed: " + renamedCount);
		}
	}

	/**
	 * Recursively walk a directory tree and rename all GoPro files found.
	 * @throws IOException 
	 * @throws NoSuchAlgorithmException 
	 */
	private void processFiles(File startDir) throws IOException { // throws FileNotFoundException {
		fileCount = 0;
		List<File> filesDirs = Arrays.asList(startDir.listFiles());
		Iterator<File> filesIter = filesDirs.iterator();

		while (filesIter.hasNext()) {
			File file = filesIter.next();
			fileCount++;
			String fName = file.getName();
			//String newName = newName(fName);
			if (isGoProFile(fName)) {
				renameFile(file);
			}
			else {
				System.out.println("ignored: " + fName);
			}
		}
	}
	
	private boolean isGoProFile(String fName) {
		if (!fName.startsWith("GH") && !fName.startsWith("GL")) {
			//System.out.println("1 "  + fName);
			return false;
		}
		String rawName = getRawName(fName);
		if (rawName.length() != 8) {
			//System.out.println("32 "  + fName);
			return false;
		}
		if (!Character.isDigit(rawName.charAt(rawName.length() - 1))) {
			//System.out.println("3 "  + fName + " - " + rawName.charAt(rawName.length() - 1));
			return false;	
		}
		return true;
	}
	
	private String newName(String oldName) {
		String rawName = getRawName(oldName);
		String number = rawName.substring(4);
		String sequence = rawName.substring(2, 4);	// GH010527
		return number + sequence + "-" + oldName;
	}

	private boolean renameFile(File file) throws IOException {
		String oldname = file.getName();
		String newname = newName(oldname);

		// now rename that file:
		Path source = Paths.get(file.getAbsolutePath());
		try {
			// rename a file in the same directory
			//System.out.println("renaming " + oldname.substring(0, 100) + "...");
			System.out.println(oldname + " -> " +  newname);
			if (!DRY_RUN) {
				Files.move(source, source.resolveSibling(newname));
				renamedCount++;
			}

		} catch (IOException e) {
			System.out.println("Error: could not rename file " + oldname);
			return false;
		}
		return true;
	}

	// -------------------------------------------------------------------

	// https://howtodoinjava.com/java/io/sha-md5-file-checksum-hash/
	@SuppressWarnings("unused")
	private String getFileChecksum(MessageDigest digest, File file) throws IOException {
		// Get file input stream for reading the file content
		try (FileInputStream fis = new FileInputStream(file)) {
			// Create byte array to read data in chunks
			byte[] byteArray = new byte[1024];
			int bytesCount = 0;

			// Read file data and update in message digest
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			}
			// close the stream; We don't need it now.
			//fis.close();
		}

		// Get the hash's bytes
		byte[] bytes = digest.digest();

		// This bytes[] has bytes in decimal format;
		// Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		// return complete hash
		return sb.toString();
	}

	@SuppressWarnings("unused")
	private String getExtension(String fileName) {
		int lastIndex = fileName.lastIndexOf('.');
		if (lastIndex == -1) {
			return null;
		}
		return fileName.substring(lastIndex);
	}

	private String getRawName(String fileName) {
		int lastIndex = fileName.lastIndexOf('.');
		if (lastIndex == -1) {
			return null;
		}
		return fileName.substring(0, lastIndex);
	}

	private static String selectDirectory(String defaultDir) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {	}	// use native look and feel
		JComponent.setDefaultLocale(Locale.US);
		

		JFileChooser chooser = new JFileChooser(defaultDir);
		//			FileNameExtensionFilter filter = new FileNameExtensionFilter("LaTeX files", "tex");
		//			chooser.setFileFilter(filter);

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Select a directory");
		chooser.setApproveButtonText("Select");
		
		//chooser.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		int returnVal = chooser.showOpenDialog(null);

		if (returnVal != JFileChooser.APPROVE_OPTION) {
			System.out.println("canceled.");
			return null;
		}
		String path = chooser.getSelectedFile().getAbsolutePath();

		if (path == null) {
			System.out.println("cancelled.");
			return null;
		}

		return path;
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 */
	private static void validateDirectory(File aDirectory) throws FileNotFoundException {
		if (aDirectory == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists()) {
			throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		}
		if (!aDirectory.isDirectory()) {
			throw new IllegalArgumentException("This is not a directory: " + aDirectory);
		}
		if (!aDirectory.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
		}
	}

}
