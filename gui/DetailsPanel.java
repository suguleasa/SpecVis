/*
 * DetailsPanel.java
 * Author: Elena Caraba
 * Create Date: Jul 2, 2007
 * Installation Location:
 *
 */
package gui;

import java.awt.Color;

import gui.swingext.TextPaneExt;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author ecaraba
 *
 */
public class DetailsPanel extends JFrame {
    
    private String targetName;
    private String runNumber;
    private Integer phase;
    
    public DetailsPanel(String targetName, String runNumber, Integer phase) {
        this.targetName = targetName;
        this.runNumber = runNumber;
        this.phase = phase;
        
        JPanel panel = createPanel();
        panel.setBackground(Color.white);
        
        this.getContentPane().add(panel);
        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        
    }
    
    public JPanel createPanel() {
        
        
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(""));
        builder.appendColumn("left:pref");
        builder.appendRow("pref");
        builder.appendRow("pref");
        builder.appendRow("pref");
        builder.appendRow("pref");
        builder.appendRow("pref");
        
        String[] dateParts = runNumber.split("_");
        String yr = dateParts[0];
        String doy = dateParts[1];
        String url = "http://snfactory.lbl.gov/sunfall/images/snifs_images/" + yr + "/" + doy + "/spec_" + runNumber + ".png";
        
        
        TextPaneExt nameL = new TextPaneExt(targetName);
        TextPaneExt phaseL = new TextPaneExt("Phase: " + phase.toString());
        TextPaneExt runL = new TextPaneExt("Run Number: " + runNumber);
        TextPaneExt filesL = new TextPaneExt("File Location: " + url);
        
        builder.add(nameL);
        builder.nextLine();
        builder.add(phaseL);
        builder.nextLine();
        builder.add(runL);
        builder.nextLine();
        builder.add(filesL);
        builder.nextLine();
        
        
        JLabel image = App.createImageLabelUrl(url, false);
        builder.add(image);
        
        return builder.getPanel();
        
    }

}
