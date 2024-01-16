package imagingbook.gopro;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;

// https://stackoverflow.com/questions/70959722/jfilechooser-with-additional-jcheckbox-for-setting-the-file-opening-method
public class CheckboxPanel extends JComponent {

    JCheckBox cbDryRun;
    JCheckBox cbRecursive;
    // boolean checkBoxInit = false;

    int preferredWidth = 150;
    int preferredHeight = 100;//Mostly ignored as it is
    int checkBoxPosX = 5;
    int checkBoxPosY = 20;
    int checkBoxWidth = preferredWidth;
    int checkBoxHeight = 20;

    public CheckboxPanel(boolean initVal) {
        this.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        JLabel lbl = new JLabel("Options:");
        lbl.setBounds(5, 0, 150, 20);
        this.add(lbl);


        this.cbDryRun = new JCheckBox("Dry run only?", initVal);
        // cbDryRun.setBounds(checkBoxPosX, checkBoxPosY, checkBoxWidth, checkBoxHeight);
        cbDryRun.setBounds(5, 25, 150, 20);
        this.add(cbDryRun);

        this.cbRecursive = new JCheckBox("Recursive", true);
        cbRecursive.setBounds(5, 50, 150, 20);
        this.add(cbRecursive);
    }

    public boolean getDryRun() {
        return cbDryRun.isSelected();
    }

    public boolean getRecursive() {
        return cbRecursive.isSelected();
    }

}
