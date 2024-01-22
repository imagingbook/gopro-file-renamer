package imagingbook.gopro2;

// https://www.javacodex.com/Swing/GroupLayout

import imagingbook.demos.console3_good.TextAreaLogProgram;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

public class GoProFileRenamer2 extends JFrame {

    private static String appTitle = "GoPro File Renamer";
    private static String DEFAULT_DIR = Paths.get("").toAbsolutePath().toString();

    private final JLabel label;
    private final JTextField textField;
    private final JCheckBox cbDryRun, cbRecursive, cbVerbose;
    private final JButton btnChoose, btnRun, btnQuit;
    private final JTextArea outputArea;
    private final JScrollPane scrollPane;

    JFileChooser chooser;

    public GoProFileRenamer2() {
        super(appTitle);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }
        JFrame.setDefaultLookAndFeelDecorated(true);

        label = new JLabel("Root directory:");
        textField = new JTextField();

        cbDryRun    = new JCheckBox("Dry run only", false);
        cbRecursive = new JCheckBox("Recursive", true);
        cbVerbose   = new JCheckBox("Verbose", true);

        btnChoose   = new JButton("Select");
        btnRun      = new JButton("Run");
        btnQuit     = new JButton("Quit");

        outputArea  = new JTextArea("", 20, 50);
        scrollPane  = new JScrollPane(outputArea);
        // outputTextArea.append("Some output\n");
        // for (int i=0; i < 50; i++) {
        //     outputTextArea.append("Some more output "+ i + "\n");
        // }


        // --------------------------------------------------------------

        btnChoose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("chooseButton action event " + e);
                JFileChooser chooser = new JFileChooser((String) null);   // DEFAULT_DIR

                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Select the root directory");
                chooser.setApproveButtonText("Select");

                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File f = chooser.getSelectedFile();
                    System.out.println("Selected dir = " + f.getAbsolutePath());
                    textField.setText(f.getAbsolutePath());
                }
            }
        });

        btnRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("runButton action event " + e);
                System.out.println("cbRecursive  = " + cbRecursive.isSelected());
                System.out.println("cbVerbose    = " + cbVerbose.isSelected());
                System.out.println("cbDryRun     = " + cbDryRun.isSelected());
            }
        });

       btnQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // System.out.println("cancelButton action event " + e);
                System.exit(0);
            }
        });

        // --------------------------------------------------------------

        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        cbDryRun.setBorder(emptyBorder);
        cbRecursive.setBorder(emptyBorder);
        cbVerbose.setBorder(emptyBorder);
        // checkBox4.setBorder(emptyBorder);

        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(label)
                                        .addGroup(layout.createParallelGroup(LEADING)
                                                .addComponent(textField)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addComponent(cbRecursive)
                                                        .addComponent(cbVerbose)
                                                        .addComponent(cbDryRun))
                                                )
                                        .addGroup(layout.createParallelGroup(LEADING)
                                                .addComponent(btnChoose)
                                                .addComponent(btnRun)
                                                .addComponent(btnQuit))
                        )
                        .addComponent(scrollPane)
        );

       layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(label)
                        .addComponent(textField)
                        .addComponent(btnChoose))
                .addGroup(layout.createParallelGroup(LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(BASELINE)
                                        .addComponent(cbRecursive)
                                        .addComponent(cbVerbose)
                                        .addComponent(cbDryRun))
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(btnRun)
                                .addComponent(btnQuit))
                )
                .addComponent(scrollPane)
        );

        layout.linkSize(SwingConstants.HORIZONTAL, btnChoose, btnRun, btnQuit);

        pack();
        setLocationRelativeTo(null);
        // setVisible(true);
    }

    private


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GoProFileRenamer2().setVisible(true);
            }
        });
    }
}
