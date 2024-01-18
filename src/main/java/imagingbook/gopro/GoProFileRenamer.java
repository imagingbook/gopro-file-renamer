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
import java.util.regex.Pattern;

/**
 * GoPro files are named in a weird way, e.g., the successive clips of
 * a "chaptered" video sequence ("0527") are named
 * <pre>
 * GH010527.MP4
 * GH020527.MP4
 * GH030527.MP4
 * ...</pre>
 * Thus if listed by file name, clips belonging to the same video do not show in succession!
 * This program renames these files to
 * <pre>
 * 052701-GH010527.MP4
 * 052702-GH020527.MP4
 * 052703-GH030527.MP4
 * ...</pre>
 * thereby preserving the original names.
 * All associated GoPro file types are renamed, such as
 * {@code .MP4},
 * {@code .THM},
 * {@code .LRV} files.
 *
 * Note that this only works for files produced with GoPro Hero6 to Hero12 cams.
 * Earlier models with different file naming conventions are not supported
 * (see https://community.gopro.com/s/article/GoPro-Camera-File-Naming-Convention
 * for details).
 *
 * @author wilbur@ieee.org
 * @version 2024/01/16
 */
public final class GoProFileRenamer {

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
				if (isGoProFileName(file)) {
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

	// Admissible raw file names are GHzzxxxx, GLzzxxxx and GHzzxxxx,
	// where zz and xxxx are all decimal digits:
	final Pattern p = Pattern.compile("G[HLX]\\d{6}");

	/**
	 * Checks if the given file is a valid GoPro file. That is, its name has
	 * the form 'GHzzxxxx.ext' or 'GLzzxxxx.ext', where 'xxx' is a 4-digit
	 * 'video' number and 'zz' is a 2-digit 'chapter' number. The file extension
	 * 'ext' is irrelevant.
	 * @param f the file to be tested.
	 * @return {@code true} if the file is a GoPro file, {@code false} otherwise.
	 */
	boolean isGoProFileName(File f) {
		String fName = f.getName();				// e.g., "GH010527.MP4"
		String rName = getFileRawName(fName);	// e.g., "GH010527"
		return p.matcher(rName).matches();
	}

	/**
	 * Maps an existing GoPro file name to its new name by prepending the
	 * video and chapter numbers to the original file name, for example,
	 * "GH010446.MP4" becomes "044601-GH010446.MP4".
	 * @param fName the orinal file name
	 * @return the modified file name
	 */
	private String mapGoproFileName(String fName) {	// "GH010446.MP4"
		String rName = getFileRawName(fName);		// "GH010446"
		String videoNo = rName.substring(4);		// "0446"
		String chapNo = rName.substring(2, 4);		// "01"
		return videoNo + chapNo + "-" + fName;		// "044601-GH010446.MP4"
	}

	/**
	 * Tries to rename the given GoPro file according to our conventions.
	 * @param f a file for which {@link #isGoProFileName(File)} returns {@code true}.
	 * @return {@code true} if the file was properly renamed, {@code false} otherwise.
	 */
	private boolean renameGoproFile(File f) {
		String oldname = f.getName();
		String newname = mapGoproFileName(oldname);

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
	static String getFileExtension(String fileName) {
		int lastIndex = fileName.lastIndexOf('.');
		if (lastIndex == -1) {
			return null;
		}
		return fileName.substring(lastIndex);
	}

	/**
	 * Strips the file extension from the given file name, for example,
	 * "GH010446.MP4" yields "GH010446".
	 * @param fileName the file name
	 * @return the raw file name without extension
	 */
	static String getFileRawName(String fileName) {
		int lastIndex = fileName.lastIndexOf('.');
		if (lastIndex == -1) {	// fileName has no extension
			return fileName;
		}
		return fileName.substring(0, lastIndex);
	}

	static File selectDirectory(String defaultDir) {
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

		DRY_RUN 	= cbp.getNextCheckboxState();
		RECURSIVE 	= cbp.getNextCheckboxState();
		VERBOSE 	= cbp.getNextCheckboxState();
		return chooser.getSelectedFile();
	}

	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 */
	private static void validateDirectory(File dir) throws FileNotFoundException {
		if (dir == null) {
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!dir.exists()) {
			throw new FileNotFoundException("Directory does not exist: " + dir);
		}
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("This is not a directory: " + dir);
		}
		if (!dir.canRead()) {
			throw new IllegalArgumentException("Directory cannot be read: " + dir);
		}
	}

	private static void log(String msg) {
		if(VERBOSE) {
			System.out.println(msg);
		}
	}

	// ----------------------------------------------------------------------------------

	/**
	 * Shows a sequence of checkboxes to set/reset various boolean options.
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
		boolean getNextCheckboxState() {
			boolean state = checkboxes.get(cntr).isSelected();
			cntr = cntr + 1;
			return state;
		}

	}

}
