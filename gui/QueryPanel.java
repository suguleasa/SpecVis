/*
 * QueryPanel.java
 * Author: Elena Caraba
 * Create Date: Jun 29, 2007
 *
 * 
 */
package gui;

import gui.graph.GridNode;
import gui.graph.SpecNode;
import gui.swingext.DefaultDoubleBoundedRangeModel;
import gui.swingext.DoubleRangeSlider;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;

import models.SpecTable;
import models.Spectrum;
import models.Target;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author ecaraba
 * Created on Jun 29, 2007
 *
 */
public class QueryPanel extends JPanel {
    
	private SpecTable specTable;
	private GridNode gridNode;

    public QueryPanel(SpecTable specTable, GridNode gridNode) { 
    	
    	this.specTable = specTable;
    	this.gridNode = gridNode;
        
        
        JLabel labelQueries = new JLabel("Queries ", JLabel.CENTER);
        labelQueries.setFont(new java.awt.Font("Times New Roman", 1, 18));
        labelQueries.setForeground(Color.blue);

        MyRangeSlider phaseSlider = new MyRangeSlider(-15,40,-15,40);
        JLabel labelPhase = new JLabel("Phase (days) ", JLabel.CENTER);
        Container box1 = Box.createVerticalBox();
        labelPhase.setFont(new java.awt.Font("Times New Roman", 1, 12));
        box1.add(labelPhase);
        box1.add(phaseSlider);
       
        MyRangeSlider specSlider = new MyRangeSlider(0,60,0,60);
        JLabel labelSpec = new JLabel("Number of Spectra ", JLabel.CENTER);
        Container box2 = Box.createVerticalBox();
        labelSpec.setFont(new java.awt.Font("Times New Roman", 1, 12));
        box2.add(labelSpec);
        box2.add(specSlider);
        
        JButton testButton = new JButton("redshift test");
        
        DefaultFormBuilder builder = new DefaultFormBuilder(new FormLayout(""));
        builder.appendColumn("left:pref");
        builder.appendRow("35dlu");
        builder.appendRow("35dlu");
        builder.appendRow("35dlu");
        builder.appendRow("35dlu");
        builder.appendRow("35dlu");
        

        builder.add(box1);
        builder.nextLine();
        builder.add(box2);
        builder.nextLine();
        //builder.add(testButton);
        testButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                test();
            }
        });
        
        JPanel panel = builder.getPanel();
        panel.setBackground(Color.white);
        JPanel blankPanel = new JPanel();
        blankPanel.setBackground(Color.white);
        
        
        this.setBackground(Color.white);
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.NORTH, panel);
        this.add(BorderLayout.CENTER, blankPanel);
        this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Queries"));

    }
    
    public void test() {
        gridNode.testQuery();
    }
    
	public void testTable() {
	    
	    
	    //list the target name and run number of the spec with phase 0
	    int colIndex = specTable.getColumnIndex("phase");
	    
	    int numRows = specTable.getRowCount();
	    for (int i = 0; i < numRows; i++) {
	        
	        Integer curPhase = (Integer) specTable.getValueAt(i, colIndex);
	        if (curPhase.intValue() == 0) {
	            Target curTarget = (Target) specTable.getValueAt(i, specTable.getColumnIndex("target"));
	            Spectrum spec = (Spectrum) specTable.getValueAt(i, specTable.getColumnIndex("spec"));
	            SpecNode specNode = (SpecNode) specTable.getValueAt(i, specTable.getColumnIndex("pnode"));
	            System.out.println(curTarget.getTargetName() + " run:" + spec.getRunNumber() + " phase:" + spec.getPhase() + " x:" + specNode.getXIndex() + " y:" + specNode.getYIndex());
	            specNode.filteredOut();
	        }
	        
	    }
	    
	}
	
	public void removeNode(int phaseNumber) {
		int colIndex = specTable.getColumnIndex("phase");
	    
	    int numRows = specTable.getRowCount();
	    for (int i = 0; i < numRows; i++) {
	        
	        Integer curPhase = (Integer) specTable.getValueAt(i, colIndex);
	        if (curPhase.intValue() == phaseNumber) {
	            Target curTarget = (Target) specTable.getValueAt(i, specTable.getColumnIndex("target"));
	            Spectrum spec = (Spectrum) specTable.getValueAt(i, specTable.getColumnIndex("spec"));
	            SpecNode specNode = (SpecNode) specTable.getValueAt(i, specTable.getColumnIndex("pnode"));
	            specNode.filteredOut();
	        }
	        
	    }
	    
	}
	
	public void addNode(int phaseNumber) {
		int colIndex = specTable.getColumnIndex("phase");
	    
	    int numRows = specTable.getRowCount();
	    for (int i = 0; i < numRows; i++) {
	        
	        Integer curPhase = (Integer) specTable.getValueAt(i, colIndex);
	        if (curPhase.intValue() == phaseNumber) {
	            Target curTarget = (Target) specTable.getValueAt(i, specTable.getColumnIndex("target"));
	            Spectrum spec = (Spectrum) specTable.getValueAt(i, specTable.getColumnIndex("spec"));
	            SpecNode specNode = (SpecNode) specTable.getValueAt(i, specTable.getColumnIndex("pnode"));
	            specNode.filteredIn();
	        }
	        
	    }
	}
	
	/*
	 * Algorithm for adding or removing spec nodes from the grid
	 */
			
	public void updateRange(double beginningOfSlider,double newLength, double oldValue, double oldExtent) {

		int newValue = (int) Math.round(beginningOfSlider);
		int newExtent = (int) Math.round(newLength);

		int value = (int) Math.round(oldValue);
		int extent =(int) Math.round(oldExtent);
		int oldHigh = value + extent;
		int newHigh = newValue + newExtent;

		    if(value!=newValue){
				
				if(value < newValue){
					for(int j=value; j < newValue; j++){
						removeNode(j);
					}
				}
				else { 
					if(value > newValue) {
						for(int j=newValue; j< value; j++){
							addNode(j);
						}
					}
				}
			}
			
						
			if(oldHigh!=newHigh) {				
				if(oldHigh > newHigh){
					for(int j = newHigh; j <= oldHigh; j++){
						removeNode(j);
					}
				}
				else {
					if(oldHigh < newHigh){
						for(int j=oldHigh; j<= newHigh; j++){
							addNode(j);
						}
					}
				}
			}
		}
       
	
    private class MyRangeSlider extends JComponent {

                   JFormattedTextField textFieldLeft;
                   DoubleRangeSlider slider;
                   JFormattedTextField textFieldRight;

                   public MyRangeSlider(int min, int max, int value, int extent){
                    
                       slider = new DoubleRangeSlider(min, max, value, extent) {


                           double oldValue; 
                           double oldExtent; 
                           double newValue;
                           double newExtent; 
                           
                           
                           /*
                            * Update the range when the mouse is released.
                            */
                           public void mouseReleased(MouseEvent e) {
                         
                               super.model.setValueIsAdjusting(true);

                               newValue = super.model.getValue();
                               newExtent = super.model.getExtent();
                               double newLength =  newExtent;
                               double oldLength =  oldExtent;
                               
                               updateRange(newValue, newLength, oldValue, oldLength);
                               oldValue = newValue;
                               oldExtent = super.model.getExtent();
                               
                           }

                    
                           
                      /*     public void mousePressed(MouseEvent e) {
                           	super.mousePressed(e);

                            oldValue = getLowValue();
                            oldExtent = Math.abs(getHighValue())+Math.abs(oldValue);
                            
                            newValue = super.model.getValue();
                            newExtent = super.model.getExtent();
                            //updateRange(newValue, newExtent, oldValue, oldExtent);
                            
                           
                        } */
                           
                           public void stateChanged(ChangeEvent e) {

                           		DefaultDoubleBoundedRangeModel source = (DefaultDoubleBoundedRangeModel)e.getSource();


                               double initialLeft = source.getValue();
                               double initialRight = source.getExtent() + initialLeft;
                               
                               /*
                                * updating the text fields if the slider was used.
                                */
                               if (!source.getValueIsAdjusting()) { 
                                   textFieldLeft.setValue(new Integer((int)initialLeft));
                                   textFieldRight.setValue(new Integer((int)initialRight));
                               }

                               else { 

                                   textFieldLeft.setText(String.valueOf((int)initialLeft));

                                   textFieldRight.setText(String.valueOf((int)initialRight));
                               }
                               
                               
                               double newLength = initialRight - initialLeft;
                               double oldLength =  oldExtent -oldValue;
                               repaint();
                           }
                        
                       };

                       textFieldLeft = new JFormattedTextField();
                       textFieldRight = new JFormattedTextField();

                       textFieldLeft.setValue(new Integer(min));
                       textFieldLeft.setColumns(3); //get some space
                       textFieldRight.setValue(new Integer(max));
                       textFieldRight.setColumns(3); //get some space

                       // enter the value of the new interval of the slider and press return or enter
                       textFieldLeft.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
                       textFieldLeft.getActionMap().put("check", new AbstractAction() {


                        /*
                        * update the slider when the left text field is used
                        */
            	   		public void actionPerformed(ActionEvent e) {
            	   			
            	   			double oldValue = slider.model.getValue();            
                	   		double oldExtent = slider.model.getExtent() - oldValue;	
                        	
                	   		String val = (String) textFieldLeft.getText();
                        	
                	   		try {
                        		double newValue = Double.parseDouble(val);
                              	double newExtent = slider.model.getExtent();
                            	
                        		slider.setLowValue(newValue);
                        
                        		updateRange(newValue, newExtent, oldValue, oldExtent-oldValue);	
                        		
          	                  oldValue = newValue;
        	                  oldExtent = newExtent-oldValue;
        	                  
        	                 
                        	} catch (NumberFormatException nfe) {
                                System.out.println("NumberFormatException: " + nfe.getMessage());
                        	}
                        	
                        	if (!textFieldLeft.isEditValid()) { 
                                Toolkit.getDefaultToolkit().beep();
                                textFieldLeft.selectAll();
                            } else { try {                    
                                textFieldLeft.commitEdit();     
                            	} catch (java.text.ParseException exc) { }
                            }
                        }
                        
                       });


                       // enter the value of the new interval of the slider and press return or enter
                       textFieldRight.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
                       textFieldRight.getActionMap().put("check", new AbstractAction() {
                    	   
                    	   /*
                            * update the slider when the right text field is used
                            */
                           public void actionPerformed(ActionEvent e) {

                               double oldValue = slider.model.getValue();
                               double oldExtent = slider.model.getExtent();

                               String val = (String)
                               textFieldRight.getText();
                               try {
                                   		   double newValue = slider.model.getValue();
                                   		   double newExtent = Double.parseDouble(val);
                                   
                                  
                                           slider.setLowValue(newValue);
                                           slider.setHighValue(newExtent);
                                           double newLength = newExtent - newValue;
                                           
                                           updateRange(newValue, newLength, oldValue, oldExtent);	
                                           
                                           oldValue = newValue;
                                           oldExtent = newExtent;
                                           
                               } catch (NumberFormatException nfe) {

                                   System.out.println("NumberFormatException: " + nfe.getMessage());
                               }

                               if (!textFieldRight.isEditValid()) {
                                   Toolkit.getDefaultToolkit().beep();
                                   textFieldRight.selectAll();
                               } else try {
                                   textFieldRight.commitEdit();
                               } catch (java.text.ParseException exc) { }
                           }
                       });

                       
                       //put the text fields and the ends of the slider and create the final double range slider
                       JLabel sliderLabel = new JLabel("START: ", JLabel.CENTER);
                       sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                       this.setLayout(new BorderLayout());
                       this.add(BorderLayout.LINE_START, sliderLabel);

                       slider.setEnabled(true);
                       textFieldLeft.setEnabled(true);
                       textFieldRight.setEnabled(true);

                       this.add(BorderLayout.LINE_START, textFieldLeft);
                       this.add(BorderLayout.LINE_END, textFieldRight);
                       this.add(BorderLayout.CENTER, slider);

                       
                   }


                   // create the final slider as one entity
                   public JFrame createSlider() {
                      
                	   JFrame sliderFrame = new JFrame();

                       slider.setEnabled(true);
                       textFieldLeft.setEnabled(true);
                       textFieldRight.setEnabled(true);


                       sliderFrame.getContentPane().add(BorderLayout.LINE_START, textFieldLeft);
                       sliderFrame.getContentPane().add(BorderLayout.LINE_END, textFieldRight);
                       sliderFrame.getContentPane().add(BorderLayout.CENTER, slider);

                       sliderFrame.setVisible(true);
                       sliderFrame.pack();

                       return sliderFrame;
                   }

    }

   

}
