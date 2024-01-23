package imagingbook.gopro;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

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
public class GoProFileRenamer extends JFrame {

    private static String appTitle = "GoPro File Renamer";

    private String startDir = System.getProperty("user.dir"); //Paths.get("").toAbsolutePath().toString();
    private boolean RECURSIVE = true;
    private boolean VERBOSE = true;
    private boolean DRY_RUN = true;

    private final JLabel startDirLabel;
    private final JTextField startDirField;
    private final JCheckBox checkDryRun, checkRecursive, checkVerbose;
    private final JButton buttonFind, buttonRun, buttonClear, buttonQuit;
    private final JTextArea outputArea;
    private final JScrollPane scrollPane;

    public GoProFileRenamer() {
        super(appTitle);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }
        JFrame.setDefaultLookAndFeelDecorated(true);

        startDirLabel = new JLabel("Start directory:");
        startDirField = new JTextField(startDir);

        checkRecursive  = new JCheckBox("Recursive", RECURSIVE);
        checkVerbose    = new JCheckBox("Verbose", VERBOSE);
        checkDryRun     = new JCheckBox("Dry run only", DRY_RUN);

        // cbRecursive.setBorder(createEmptyBorder());
        // cbVerbose.setBorder(createEmptyBorder());
        // cbDryRun.setBorder(createEmptyBorder());

        buttonFind  = new JButton("Find");
        buttonRun   = new JButton("Run");
        buttonClear = new JButton("Clear");
        buttonQuit  = new JButton("Quit");

        outputArea  = new JTextArea("", 20, 80);
        outputArea.setEditable(false);
        scrollPane  = new JScrollPane(outputArea);

        // --------------------------------------------------------------

        buttonFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("chooseButton action event " + e);
                JFileChooser chooser = new JFileChooser(startDir);

                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Select the root directory");
                chooser.setApproveButtonText("Select");

                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    startDirField.setText(f.getAbsolutePath());
                }
            }
        });

        buttonRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRename();
            }
        });

        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearOutput();
            }
        });

       buttonQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // System.out.println("cancelButton action event " + e);
                System.exit(0);
            }
        });

        // --------------------------------------------------------------

        makeLayout();
        pack();
        setLocationRelativeTo(null);
    }

    private void makeLayout() {
        GroupLayout layout = new GroupLayout(this.getContentPane());
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(startDirLabel)
                                        .addGroup(layout.createParallelGroup(LEADING)
                                                .addComponent(startDirField)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(checkRecursive)
                                                        .addComponent(checkVerbose)
                                                        .addComponent(checkDryRun))
                                        )
                                        .addGroup(layout.createParallelGroup(LEADING)
                                                .addComponent(buttonFind)
                                                .addComponent(buttonRun)
                                                .addComponent(buttonClear)
                                                .addComponent(buttonQuit)
                                        )
                        )
                        .addComponent(scrollPane)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(startDirLabel)
                        .addComponent(startDirField)
                        .addComponent(buttonFind))
                .addGroup(layout.createParallelGroup(LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(BASELINE)
                                        .addComponent(checkRecursive)
                                        .addComponent(checkVerbose)
                                        .addComponent(checkDryRun))
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonRun)
                                .addComponent(buttonClear)
                                .addComponent(buttonQuit))
                )
                .addComponent(scrollPane)
        );

        layout.linkSize(SwingConstants.HORIZONTAL, buttonFind, buttonRun, buttonQuit);
        this.getContentPane().setLayout(layout);
    }

    private void clearOutput() {
        outputArea.setText(null);
    }

    private void updateSettings() {
        startDir = startDirField.getText();
        RECURSIVE = checkRecursive.isSelected();
        VERBOSE = checkVerbose.isSelected();
        DRY_RUN = checkDryRun.isSelected();
    }

    private void log(String msg) {
        this.outputArea.append(msg + "\n");
    }

    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GoProFileRenamer().setVisible(true);
            }
        });
    }

    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    // Admissible raw file names are GHzzxxxx, GLzzxxxx and GXzzxxxx,
    // where zz and xxxx are all decimal digits.
    // This is the associated regular expression pattern:
    private final Pattern p = Pattern.compile("G[HLX]\\d{6}");

    private int checkedCount = 0;
    private int renamedCount = 0;
    private int errorCount = 0;

    void doRename() {
        updateSettings();
        File dir = new File(startDir);

        if (!dir.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Path is not a directory:\n" + startDir);
            return;
        }

        String dlgTitle =  appTitle + (DRY_RUN ? " (Dry Run)" : "");
        int result = JOptionPane.showConfirmDialog(null,
                (DRY_RUN ?
                        "DRY RUN ONLY, no files will be renamed." :
                        "About to rename files.")
                    + "\nProceed?",
                dlgTitle,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        // 0=OK
        if (result != 0) {
            return;
        }

        checkedCount = 0;
        renamedCount = 0;
        errorCount = 0;

        log("Renaming GoPro files " + (DRY_RUN ? "(DRY RUN) ..." : "..."));
        processDirectory(dir);
        if (checkedCount == 0) {
            System.out.println("Found no files to check!");
        }
        else {
            log("------------------------------");
            log("Files checked: " + checkedCount);
            log("Files renamed: " + renamedCount);
            log("File errors:   " + errorCount);
        }
    }

    /**
     * Recursively walk a directory tree and rename all GoPro files found.
     */
    private void processDirectory(File dir) {
        log("Directory: " + dir.getName());
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
            if (file.isDirectory()) {
                subdirs.add(file);
            }
            else {
                checkedCount++;
                if (isGoProFileName(file)) {
                    renameGoproFile(file);
                } else {
                    if (VERBOSE) log("    ignored " + file.getName());
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
        if (VERBOSE) log("   renaming " + oldname + " -> " +  newname);
        if (!DRY_RUN) {
            try {
                Files.move(source, source.resolveSibling(newname));
                renamedCount++;
            } catch (IOException e) {
                log("ERROR: could not rename file " + oldname);
                errorCount++;
                return false;
            }
        }
        return true;
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

}
