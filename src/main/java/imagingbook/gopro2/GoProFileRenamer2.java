package imagingbook.gopro2;

// https://www.javacodex.com/Swing/GroupLayout

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.LEADING;

public class GoProFileRenamer2 {

    private static String appTitle = "GoPro File Renamer";

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame(appTitle);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel label = new JLabel("Root directory:");
        JTextField textField = new JTextField();

        JCheckBox cbDryRun = new JCheckBox("Dry run only");
        JCheckBox cbRecursive = new JCheckBox("Recursive");
        JCheckBox cbVerbose = new JCheckBox("Verbose");

        JButton chooseButton = new JButton("Select");
        JButton runButton = new JButton("Run");
        JButton cancelButton = new JButton("Cancel");

        JTextArea outputTextArea = new JTextArea("", 20, 50);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        outputTextArea.append("Some output\n");
        for (int i=0; i < 50; i++) {
            outputTextArea.append("Some more output "+ i + "\n");
        }

        // --------------------------------------------------------------

        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 0);
        cbDryRun.setBorder(emptyBorder);
        cbRecursive.setBorder(emptyBorder);
        cbVerbose.setBorder(emptyBorder);
        // checkBox4.setBorder(emptyBorder);

        GroupLayout layout = new GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);

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
                                                .addComponent(chooseButton)
                                                .addComponent(runButton)
                                                .addComponent(cancelButton))
                        )
                        .addComponent(scrollPane)
        );

       layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(BASELINE)
                        .addComponent(label)
                        .addComponent(textField)
                        .addComponent(chooseButton))
                .addGroup(layout.createParallelGroup(LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(BASELINE)
                                        .addComponent(cbRecursive)
                                        .addComponent(cbVerbose)
                                        .addComponent(cbDryRun))
                        )
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(runButton)
                                .addComponent(cancelButton))
                )
                .addComponent(scrollPane)
        );

        layout.linkSize(SwingConstants.HORIZONTAL, chooseButton, runButton, cancelButton);

        frame.pack();
        // Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        // Rectangle r = frame.getBounds();
        // frame.setBounds(center.x - r.width / 2, center.y - r.height / 2, r.width, r.height);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
