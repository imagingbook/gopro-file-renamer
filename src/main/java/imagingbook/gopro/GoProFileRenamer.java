package imagingbook.gopro;

import javax.imageio.ImageIO;
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
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static imagingbook.gopro.JarUtils.getImplementationVersion;
import static imagingbook.gopro.JarUtils.getManifest;
import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.CENTER;
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
 * @version 2024/01/31
 */
public class GoProFileRenamer extends JFrame {

    enum ProcessMode {
        Rename, Revert;
    }

    private static final String appTitle = "GoPro File Renamer";
    private static final String helpUrl = "https://github.com/imagingbook/gopro-file-renamer?tab=readme-ov-file#gopro-file-renamer";
    private static final String implVersion = getImplementationVersion(getManifest(GoProFileRenamer.class));
    private static final Color renameButtonColor = Color.red.darker();
    private static final Color revertButtonColor = Color.green.darker();

    private String startDir = System.getProperty("user.dir"); //Paths.get("").toAbsolutePath().toString();
    private boolean RECURSIVE = true;
    private boolean VERBOSE   = true;
    private boolean DRYRUN    = true;
    private boolean ABSDIRS   = false;

    private ProcessMode mode = ProcessMode.Rename;
    private FileNameFormat renamer = null;

    private int checkedCount = 0;
    private int matchedCount = 0;
    private int renamedCount = 0;
    private int errorCount   = 0;

    private final JLabel startDirLabel;
    private final JTextField startDirField;
    private final JCheckBox checkDryRun, checkRecursive, checkVerbose, checkAbsDirs;
    private final JButton buttonFind, buttonRename, buttonRevert, buttonClear, buttonQuit, buttonHelp;
    private final JTextArea outputArea;
    private final JScrollPane scrollPane;

    public GoProFileRenamer() {
        // version number only shows when run from JAR:
        super(appTitle + " (" + (implVersion != null ? implVersion : "no version") + ")");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }
        JFrame.setDefaultLookAndFeelDecorated(true);

        try {
            this.setIconImage(ImageIO.read(getClass().getResource("camera-gopro-icon.png")));
        } catch (IOException ex) { }

        startDirLabel = new JLabel("Start directory:");
        startDirField = new JTextField(startDir);

        checkRecursive  = new JCheckBox("Recursive", RECURSIVE);
        checkVerbose    = new JCheckBox("Verbose", VERBOSE);
        checkDryRun     = new JCheckBox("Dry run only", DRYRUN);
        checkAbsDirs    = new JCheckBox("Show absolute paths", ABSDIRS);

        buttonFind  = new JButton("Find");

        buttonRename = new JButton("Rename Files");
        buttonRename.setOpaque(true);
        buttonRename.setForeground(renameButtonColor);
        buttonRename.setFont(buttonRename.getFont().deriveFont(Font.BOLD));

        buttonRevert = new JButton("Revert Files");
        buttonRevert.setOpaque(true);
        buttonRevert.setForeground(revertButtonColor);
        buttonRevert.setFont(buttonRename.getFont().deriveFont(Font.BOLD));

        buttonClear = new JButton("Clear Output");
        buttonQuit  = new JButton("Quit");

        // https://docs.oracle.com/javase//7/docs/api/javax/swing/plaf/synth/doc-files/componentProperties.html
        buttonHelp  = new JButton(UIManager.getIcon("OptionPane.informationIcon"));
        buttonHelp.setBorder(null);
        buttonHelp.setBorderPainted(false);

        outputArea  = new JTextArea("", 20, 80);
        outputArea.setEditable(false);
        scrollPane  = new JScrollPane(outputArea);

        // --------------------------------------------------------------

        buttonFind.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

        buttonRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = ProcessMode.Rename;
                processFiles();
            }
        });

        buttonRevert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mode = ProcessMode.Revert;
                processFiles();
            }
        });

        buttonClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTextOutput();
            }
        });

       buttonQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Desktop.isDesktopSupported()) {
                    Desktop desktop = Desktop.getDesktop();
                    if (desktop.isSupported(Desktop.Action.BROWSE)) {
                        try {
                            desktop.browse(URI.create(helpUrl));
                        } catch (IOException ex) { }
                    }
                }
            }
        });

        // --------------------------------------------------------------

        makeGuiLayout();
        pack();
        setLocationRelativeTo(null);
    }

    private void makeGuiLayout() {
        GroupLayout layout = new GroupLayout(this.getContentPane());
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(CENTER)
                                    .addComponent(startDirLabel)
                                    .addComponent(buttonHelp)
                                )
                                .addGroup(layout.createParallelGroup(LEADING)
                                        .addComponent(startDirField)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(checkRecursive)
                                                .addComponent(checkVerbose)
                                                .addComponent(checkDryRun)
                                                .addComponent(checkAbsDirs)
                                        )
                                )
                                .addGroup(layout.createParallelGroup(CENTER)
                                        .addComponent(buttonFind)
                                        .addComponent(buttonRename)
                                        .addComponent(buttonRevert)
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
                        .addComponent(buttonFind)
                )
                .addGroup(layout.createParallelGroup(LEADING)
                        .addComponent(buttonHelp)
                            .addGroup(layout.createParallelGroup(BASELINE)
                                    .addComponent(checkRecursive)
                                    .addComponent(checkVerbose)
                                    .addComponent(checkDryRun)
                                    .addComponent(checkAbsDirs)
                            )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(buttonRename)
                                .addComponent(buttonRevert)
                                .addComponent(buttonClear)
                                .addComponent(buttonQuit)
                        )
                )
                .addComponent(scrollPane)
        );

        layout.linkSize(SwingConstants.HORIZONTAL, buttonFind, buttonRename, buttonQuit);
        this.getContentPane().setLayout(layout);
    }

    private void clearTextOutput() {
        outputArea.setText(null);
    }

    private void updateSettings() {
        startDir = startDirField.getText();
        RECURSIVE = checkRecursive.isSelected();
        VERBOSE = checkVerbose.isSelected();
        DRYRUN = checkDryRun.isSelected();
        ABSDIRS = checkAbsDirs.isSelected();
        checkedCount = 0;
        matchedCount = 0;
        renamedCount = 0;
        errorCount = 0;
    }

    private void log(String msg) {
        if (msg == null)
            this.outputArea.append("null\n");
        else
            this.outputArea.append(msg + "\n");
    }

    // -------------------------------------------------------------------------

    void processFiles() {
        updateSettings();
        File dir = new File(startDir);
        this.renamer = (mode.equals(ProcessMode.Rename)) ?
                new FileNameFormat.OriginalGoproFormat() :
                new FileNameFormat.RenamedGoproFormat();

        if (!dir.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Path is not a directory:\n" + startDir);
            return;
        }

        String dlgTitle =  appTitle + (DRYRUN ? " (Dry Run)" : "");
        int result = JOptionPane.showConfirmDialog(null,
                (DRYRUN ?
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

        log("Renaming GoPro files " + (DRYRUN ? "(DRY RUN) ..." : "..."));
        processDirectory(dir);
        if (checkedCount == 0) {
            log("Found no files to check!");
        }
        else {
            log("------------------------------");
            log("Files checked: " + checkedCount);
            log("Files matched: " + matchedCount);
            log("Files renamed: " + renamedCount);
            log("File errors:   " + errorCount);
        }
    }

    /**
     * Recursively walk a directory tree and rename all GoPro files found.
     */
    private void processDirectory(File dir) {
        log("Directory: " + (ABSDIRS ? dir.getAbsolutePath() : dir.getName()));
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
                if (renamer.matchFileName(file.getName())) {
                    renameFile(file);
                } else {
                    if (VERBOSE) log("    ignoring " + file.getName());
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
     * Tries to rename the given GoPro file according to our conventions.
     * @param f a file with matching name.
     * @return {@code true} if the file was properly renamed, {@code false} otherwise.
     */
    private boolean renameFile(File f) {
        String oldname = f.getName();
        String newname = renamer.mapFileName(oldname);

        // now rename that file:
        Path source = Paths.get(f.getAbsolutePath());
        matchedCount++;
        if (VERBOSE) log("   renaming " + oldname + " -> " +  newname);
        if (!DRYRUN) {
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

// -------------------------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GoProFileRenamer().setVisible(true);
            }
        });
    }
    
}
