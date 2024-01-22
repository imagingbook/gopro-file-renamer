package imagingbook.demos.console1;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.io.PrintStream;

// https://stackoverflow.com/a/343084/15716872

public class Main{

    public static void main( String [] args ) throws InterruptedException  {
        JFrame frame = new JFrame();
        frame.add( new JLabel(" Outout" ), BorderLayout.NORTH );

        JTextArea ta = new JTextArea();
        TextAreaOutputStream taos = new TextAreaOutputStream( ta, 60 );
        PrintStream ps = new PrintStream( taos );
        System.setOut( ps );
        System.setErr( ps );

        frame.add( new JScrollPane( ta )  );
        frame.pack();
        frame.setVisible( true );
        frame.setSize(800,600);

        for( int i = 0 ; i < 10 ; i++ ) {
            System.out.println("logging output line" +  i );
            Thread.sleep( 200 );
        }
        System.out.println("Done.");

        Thread.sleep( 1000 );
        System.exit(0);
    }
}
