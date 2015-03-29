/*
 * SpecVis.java
 * Author: Elena Caraba
 * Create Date: Jun 8, 2007
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import edu.umd.cs.piccolox.swing.PScrollPane;
import gui.graph.HorizontalAxisFisheye;
import gui.graph.LowerCanvas;
import gui.graph.SpecCanvas;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author ecaraba
 * This is the starter class
 */
public class SpecVis extends JFrame {

    public SpecVis() {
        setTitle("SpecVis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        SpecCanvas vis = new SpecCanvas();
        QueryPanel query = new QueryPanel(vis.getGrid().getSpecTable(), vis.getGrid());
        HorizontalAxisFisheye xAxis = new HorizontalAxisFisheye(vis);
        
        vis.setPreferredSize(new Dimension(900,670));
        PScrollPane scroll = new PScrollPane(vis);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        //HorizontalAxisFisheye.HorizontalBox hBox = new HorizontalAxisFisheye.HorizontalBox();
        
        
        Container box = Box.createVerticalBox();
        box.add(scroll);
        box.add(vis.getLowerCanvas());
        
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(BorderLayout.CENTER, box);
        panel.setBorder(BorderFactory.createEtchedBorder());
        
       
        getContentPane().add(BorderLayout.CENTER, panel);
        getContentPane().add(BorderLayout.EAST, query);
        pack();
        setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setSize(new Dimension(1000,800));
        
} 

public static void main(String args[]) {
        new SpecVis();
}
    
}
