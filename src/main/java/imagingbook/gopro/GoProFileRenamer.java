package imagingbook.gopro;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
 * thereby preserving the original names.
 * All associated GoPro file types are renamed, i.e.,
 * {@code .MP4},
 * {@code .THM} and 
 * {@code .LRV} files.
 *
 * @author wilbur@ieee.org
 * @version 2024/01/16
 */
public class GoProFileRenamer {

	private static boolean DRY_RUN = false;
	private static boolean RECURSIVE = true;
	private static boolean VERBOSE = true;

	private static String DEFAULT_DIR = Paths.get("").toAbsolutePath().toString();

	public static void main(String[] args) throws Exception {
		// choose the root directory containing the java files
		File rootDir = selectDirectory(DEFAULT_DIR);
		if (rootDir == null) {
			log("Cancelled.");
			return;
		}

		if (DRY_RUN) {
			System.out.println("This is a DRY RUN only - no files will be renamed!");
		}

		log("Root directory: " + rootDir.getAbsolutePath());
		validateDirectory(rootDir);

		new GoProFileRenamer().run(rootDir);
		log("Done.");
	}

	// -------------------------------------------------------------------

	private int checkedCount = 0;
	private int renamedCount = 0;

	void run(File rootDir) throws IOException {
		checkedCount = 0;
		renamedCount = 0;
		log("Renaming files ...");
		processDirectory(rootDir);
		if (checkedCount == 0) {
			System.out.println("Found no GoPro files to rename!");
		}
		else {
			log("Files checked: " + checkedCount);
			log("Files renamed: " + renamedCount);
		}
	}

	/**
	 * Recursively walk a directory tree and rename all GoPro files found.
	 * @throws IOException
	 */
	private void processDirectory(File dir) throws IOException {
		log("processing directory: " + dir.getName());
		File[] allfiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return !file.isHidden();	// skip hidden files and directories
			}
		});

		if (allfiles == null)	// this happens if 'startDir' is not a directory
			return;

		List<File> subdirs = new ArrayList<>();

		// process all files in current directory:
		for (File file : allfiles) {
			checkedCount++;
			if (file.isDirectory()) {
				subdirs.add(file);
			}
			else {
				if (isGoProFile(file)) {
					renameGoproFile(file);
				} else {
					log("    ignored: " + file.getName());
				}
			}
		}

		if (RECURSIVE) {
			for (File sdir : subdirs) {
				processDirectory(sdir);
			}
		}
	}

	/**
	 * Checks if the given file is a valid GoPro file. That is, its name has
	 * the form 'GHcctttt.xxx' or 'GLcctttt.xxx', where 'tttt' is a 4-digit
	 * 'take' number and 'cc' is a 2-digit 'clip' number. The file extension
	 * 'xxx' is not taken into account.
	 * @param f the file to be tested.
	 * @return {@code true} if the file is a GoPro file, {@code false} otherwise.
	 */
	private boolean isGoProFile(File f) {
		String fName = f.getName();
		if (!fName.startsWith("GH") && !fName.startsWith("GL")) {
			return false;
		}
		String rawName = getFileRawName(fName);
		if (rawName.length() != 8) {
			return false;
		}
		if (!Character.isDigit(rawName.charAt(rawName.length() - 1))) {
			return false;	
		}
		return true;
	}
	
	private String mapGoproName(String oldName) {	// GH010446.MP4
		String rawName = getFileRawName(oldName);	// GH010446
		String takeNo = rawName.substring(4);		// 0446
		String clipNo = rawName.substring(2, 4);	// 01
		return takeNo + clipNo + "-" + oldName;		// 044601-GH010446.MP4
	}

	/**
	 * Tries to rename the given GoPro file according to our conventions.
	 * @param f a file for which {@link #isGoProFile(File)} returns {@code true}.
	 * @return {@code true} if the file was properly renamed, {@code false} otherwise.
	 */
	private boolean renameGoproFile(File f) {
		String oldname = f.getName();
		String newname = mapGoproName(oldname);

		// now rename that file:
		Path source = Paths.get(f.getAbsolutePath());
		try {
			log("   renaming " + oldname + " -> " +  newname);
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

	@SuppressWarnings("unused")
	private String getFileExtension(String fileName) {
		int lastIndex = fileName.lastIndexOf('.');
		if (lastIndex == -1) {
			return null;
		}
		return fileName.substring(lastIndex);
	}

	private String getFileRawName(String fileName) {
		int lastIndex = fileName.lastIndexOf('.');
		if (lastIndex == -1) {
			return null;
		}
		return fileName.substring(0, lastIndex);
	}

	private static File selectDirectory(String defaultDir) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // use native look and feel
		} catch (ClassNotFoundException |
				 InstantiationException | IllegalAccessException |
				 UnsupportedLookAndFeelException e) { }
		JComponent.setDefaultLocale(Locale.US);

		JFileChooser chooser = new JFileChooser(defaultDir);

		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Select the root directory");
		chooser.setApproveButtonText("Select");

		// set up checkboxes for options:
		OptionsPanel cbp = new OptionsPanel("Options");
		cbp.addCheckBox("Dry run only", DRY_RUN);
		cbp.addCheckBox("Recursive", RECURSIVE);
		cbp.addCheckBox("Verbose", VERBOSE);
		chooser.setAccessory(cbp);

		int returnVal = chooser.showOpenDialog(null);
		if (returnVal != JFileChooser.APPROVE_OPTION) {
			return null;	// cancelled
		}

		DRY_RUN 	= cbp.getNextState();
		RECURSIVE 	= cbp.getNextState();
		VERBOSE 	= cbp.getNextState();
		return chooser.getSelectedFile();
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

	private static void log(String msg) {
		if(VERBOSE) {
			System.out.println(msg);
		}
	}

	// ----------------------------------------------------------------------------------

	/**
	 * Collects a list of checkboxes to set/reset various boolean options.
	 */
	private static class OptionsPanel extends JComponent {

		final List<JCheckBox> checkboxes = new ArrayList<>();
		final int wdth = 150;	// width of each checkbox entry
		final int hgt = 20;		// height of each checkbox entry
		final int lftX = 5;		// indentation from left border
		final int gapY = 5;		// vertical gap between successive checkboxes

		int posY = 0;	// vertical position
		int cntr = 0;	// counter for retrieving state values

		OptionsPanel() {
			this((String) null);
		}

		OptionsPanel(String title) {
			if (title != null) {
				JLabel lbl = new JLabel(title);
				this.add(lbl).setBounds(lftX, posY, wdth, hgt);
				posY += hgt + gapY;
			}
		}

		JCheckBox addCheckBox( String text, boolean initVal) {
			JCheckBox cb = new JCheckBox(text, initVal);
			cb.setBounds(lftX, posY, wdth, hgt);
			posY += hgt + gapY;
			checkboxes.add(cb);
			// add checkbox to this component:
			this.add(cb);
			this.setPreferredSize(new Dimension(wdth, posY));
			return cb;
		}

		// will throw an exception if no next checkbox exists
		boolean getNextState() {
			boolean state = checkboxes.get(cntr).isSelected();
			cntr = cntr + 1;
			return state;
		}

	}

}
