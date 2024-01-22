package imagingbook.demos.console4_incomplete;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import net.miginfocom.swing.MigLayout;

public class Main extends JFrame {

    private void initGUI()
    {
        setPreferredSize(new Dimension(800, 600));
        final MigLayout migLayout = new MigLayout();
        setLayout(migLayout);
        final JTextArea jTextArea = new JTextArea(5, 100);
        add(new JScrollPane(jTextArea));
        final MessageConsole messageConsole = new MessageConsole(jTextArea);

        jTextArea.setVisible(true);
        messageConsole.redirectOut();
        messageConsole.setMessageLines(100);
        System.out.println("Println");
        System.err.println("Hi");

        // {
        //     final Layout layout = new TTCCLayout("yyyy-MM-dd HH:mm:ss");
        //     final ConsoleAppender appender = new ConsoleAppender();
        //     appender.setName(ConsoleAppender.class.getSimpleName());
        //     appender.setLayout(layout);
        //     appender.activateOptions();
        //     Logger.getRootLogger().addAppender(appender);
        // }
        //
        // final Logger logger = Logger.getRootLogger();
        // logger.info("Hello, World!");
    }

    /**
     * Runs the program
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().initGUI();

            }
        });
    }
}
