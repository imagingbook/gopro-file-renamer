package imagingbook.demos.console2;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// https://stackoverflow.com/questions/70126038/how-to-put-the-output-from-the-console-to-a-swing-gui-in-java
// https://stackoverflow.com/a/70127858/15716872

public class XmlSwing {
    private JTextArea textArea;

    private void println(String s) {
        textArea.append(s + "\n");
    }

    private void createAndDisplayGui() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(createTextArea(), BorderLayout.CENTER);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

        // demo:
        for( int i = 0 ; i < 10 ; i++ ) {
            this.println("logging output line " +  i );
            try {
                Thread.sleep( 500 );
            } catch (InterruptedException e) {  }
        }
        this.println("Done.");
    }

    private JScrollPane createTextArea() {
        textArea = new JTextArea(20, 40);
        createTextAreaText();
        JScrollPane scrollPane = new JScrollPane(textArea);
        return scrollPane;
    }

    private void createTextAreaText() {
        try {
            File file = new File("dum.xml");
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            textArea.append("Root element: " + document.getDocumentElement().getNodeName() + "\n");
            if (document.hasChildNodes()) {
                printNodeList(document.getChildNodes());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void printNodeList(NodeList nodeList) {
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node elemNode = nodeList.item(count);
            if (elemNode.getNodeType() == Node.ELEMENT_NODE) {
                // get node name and value
                textArea.append("\nZeilenname =" + elemNode.getNodeName() + " [Anfang]\n");
                textArea.append("Zeileninhalt =" + elemNode.getTextContent() + "\n");
                if (elemNode.hasAttributes()) {
                    NamedNodeMap nodeMap = elemNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        textArea.append("Attributname : " + node.getNodeName() + "\n");
                        textArea.append("Attributwert : " + node.getNodeValue() + "\n");
                    }
                }
                if (elemNode.hasChildNodes()) {
                    // recursive call if the node has child nodes
                    printNodeList(elemNode.getChildNodes());
                }
                textArea.append("Zeilenname =" + elemNode.getNodeName() + " [Ende]\n");
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new XmlSwing().createAndDisplayGui());
    }
}
