/*
 * TabularFisheye.java
 * Author: Elena Caraba
 * Create Date: Jun 1, 2007
 * 
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.Toolkit;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPaintContext;
import java.lang.Math.*;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

//import com.sun.media.sou.Toolkit;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;

public class TabularFisheye extends PCanvas {
			
	static CalendarNode calendarNode;
	static ArrayList list;
	static int maxIndex ;
	
	public TabularFisheye() {
		calendarNode = new CalendarNode();
		getLayer().addChild(calendarNode);
		setZoomEventHandler(null);
		setPanEventHandler(null);
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent arg0) {
				calendarNode.setBounds(getX(), getY(), getWidth() - 1, getHeight() - 1);
				calendarNode.layoutChildren(false);
			}
		});		
	}

	
	public void removeNode(int week, int day) {
		calendarNode.removeDay(week, day);
	}
	
	public void addNode(int week, int day) {
		calendarNode.addDay(week, day);
	}
	
	
	static boolean containsNode(DayNode dayNode){
		boolean bool=false;
		
		if (dayNode.isDescendentOf(calendarNode))
			bool = true;
		
		return bool;
	}
	
	static DayNode getNode(int week, int day) {
		Iterator i =  list.iterator();
		while(i.hasNext()) {
			DayNode dayNode = (DayNode) i.next();
			if((dayNode.getWeek() == week) && (dayNode.getDay() == day)) {
				return dayNode;
			}
		}
		return null;
	}

	public Dimension getPreferredSize() {
		return new Dimension(300, 400);
	}

	static class CalendarNode extends PNode {
		static int DEFAULT_NUM_DAYS = 7;
		static int DEFAULT_NUM_WEEKS = 7;
		static int TEXT_X_OFFSET = 1;
		static int TEXT_Y_OFFSET = 10;
		static int DEFAULT_ANIMATION_MILLIS = 250;
		static float FOCUS_SIZE_PERCENT = 0.65f;
		static Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 10);
	
		int numDays = DEFAULT_NUM_DAYS;
		int numWeeks = DEFAULT_NUM_WEEKS;
		int daysExpanded = 0;
		int weeksExpanded = 0;
		int arrayLength;
		

		public CalendarNode() {		
			list = new ArrayList();
			for (int week = 0; week < numWeeks; week++) {
				for (int day = 0; day < numDays; day++) {
					DayNode dayNode = new DayNode(week, day); 
					addChild(dayNode);
					list.add(dayNode);
				}
				
			}			
            maxIndex = list.size() - 1;
           
		/*	CalendarNode.this.addInputEventListener(new PBasicInputEventHandler() {
				public void mouseReleased(PInputEvent event) {
					DayNode pickedDay = (DayNode) event.getPickedNode();
					if (pickedDay.hasWidthFocus() && pickedDay.hasHeightFocus()) {
						setFocusDay(null, true);
					} else {
						setFocusDay(pickedDay, true);
					}
				}
			}); */
			
		}

	       public DayNode removeDay(int week,int day) {
	           DayNode dayNode = getNode(week,day);

	           boolean bool= containsNode(dayNode);
	           if(dayNode ==  null || bool ==  false) {
	               System.out.println("The day node is null ");
	               return null;
	           } 
	           else {
	        	   	return (DayNode) removeChild(dayNode);
	           }
	       }
		
		public void addDay(int week, int day) {
			DayNode dayNode = getNode(week,day);
			if(dayNode !=  null || indexOfChild(dayNode) == -1) {
				addChild(dayNode);
			}
			else {
				System.out.println("Node has to be removed first before you can add it again.");
			}
		}	
		
		public DayNode getDay(int week, int day) {
			return (DayNode) getChild((week * numDays) + day);
		}
	
		
		public void setFocusDay(DayNode focusDay, boolean animate) {
			for (int i = 0; i < getChildrenCount(); i++) {
				DayNode each = (DayNode) getChild(i);
				each.setHasWidthFocus(false);
				each.setHasHeightFocus(false);
			}

			if (focusDay == null) {
				daysExpanded = 0;
				weeksExpanded = 0;
			} else {
				focusDay.setHasWidthFocus(true);					
				daysExpanded = 1;
				weeksExpanded = 1;		
		
				for (int i = 0; i < numDays; i++) {
					getDay(focusDay.getWeek(), i).setHasHeightFocus(true);
				}			
		
				for (int i = 0; i < numWeeks; i++) {
					getDay(i, focusDay.getDay()).setHasWidthFocus(true);
				}			
			}
						
			layoutChildren(animate);
		}

		protected void layoutChildren(boolean animate) {
			double focusWidth = 0;
			double focusHeight = 0;
			
			if (daysExpanded != 0 && weeksExpanded != 0) {
				focusWidth = (getWidth() * FOCUS_SIZE_PERCENT) / daysExpanded;
				focusHeight = (getHeight() * FOCUS_SIZE_PERCENT) / weeksExpanded;
			}
			
			double collapsedWidth = (getWidth() - (focusWidth * daysExpanded)) / (numDays - daysExpanded);
			double collapsedHeight = (getHeight() - (focusHeight * weeksExpanded)) / (numWeeks - weeksExpanded);

			double xOffset = 0;
			double yOffset = 0;
			double rowHeight = 0;
			DayNode each = null;

			for (int week = 0; week < numWeeks; week++) {
				for (int day = 0; day < numDays; day++) {
					each = getDay(week, day);
					double width = collapsedWidth;
					double height = collapsedHeight;
					
					if (each.hasWidthFocus()) width = focusWidth;
					if (each.hasHeightFocus()) height = focusHeight;
					
					if (animate) {
						each.animateToBounds(xOffset, yOffset, width, height, DEFAULT_ANIMATION_MILLIS).setStepRate(0);
					} else {
						each.setBounds(xOffset, yOffset, width, height);
					}
					
					xOffset += width;
					rowHeight = height;
				}
				xOffset = 0;
				yOffset += rowHeight;
			}
		}
	}
	
		
	static class DayNode extends PNode {
		boolean hasWidthFocus;
		boolean hasHeightFocus;
		ArrayList lines;
		int week;
		int day;
		String dayOfMonthString;
		
		public DayNode(int week, int day) {	
			lines = new ArrayList();
			lines.add("7:00 AM Walk the dog.");
			lines.add("9:30 AM Meet John for Breakfast.");
			lines.add("12:00 PM Lunch with Peter.");
			lines.add("3:00 PM Research Demo.");
			lines.add("6:00 PM Pickup Sarah from gymnastics.");
			lines.add("7:00 PM Pickup Tommy from karate.");
			this.week = week;
			this.day = day;
			this.dayOfMonthString = Integer.toString((week * 7) + day);
			setPaint(Color.BLACK);
		}

		public int getWeek() {
			return week;
		}

		public int getDay() {
			return day;
		}

		public boolean hasHeightFocus() {
			return hasHeightFocus;
		}

		public void setHasHeightFocus(boolean hasHeightFocus) {
			this.hasHeightFocus = hasHeightFocus;
		}

		public boolean hasWidthFocus() {
			return hasWidthFocus;
		}

		public void setHasWidthFocus(boolean hasWidthFocus) {
			this.hasWidthFocus = hasWidthFocus;
		}
		
		protected void paint(PPaintContext paintContext) {
			Graphics2D g2 = paintContext.getGraphics();
			g2.setPaint(getPaint());
			g2.draw(getBoundsReference());
			g2.setFont(CalendarNode.DEFAULT_FONT);
	
			float y = (float) getY() + CalendarNode.TEXT_Y_OFFSET;
			paintContext.getGraphics().drawString(dayOfMonthString, (float) getX() + CalendarNode.TEXT_X_OFFSET, y);
			if (hasWidthFocus && hasHeightFocus) {
				paintContext.pushClip(getBoundsReference());
				for (int i = 0; i < lines.size(); i++) {
					y += 10;
					g2.drawString((String)lines.get(i), (float) getX() + CalendarNode.TEXT_X_OFFSET, y);				
				}
				paintContext.popClip(getBoundsReference());
			}
		}

	}	
	
	public void updateRange(double beginningOfSlider,double endOfSlider, double oldValue, double oldExtent){
		
		int newValue = (int) Math.round(beginningOfSlider);
		int newExtent = (int) Math.round(endOfSlider) + newValue;

		int value = (int) Math.round(oldValue);
		int extent =(int) Math.round(oldExtent) + value;
		
		if(value < newValue){
			for(int j=value; j < newValue; j++){
				removeNode(j/7,j%7);
			}
		}
		else {
			for(int j=newValue; j< value; j++){
				addNode(j/7,j%7);
			}
		}
		
		if(extent > newExtent){
			for(int j = newExtent; j <= extent; j++){
				removeNode(j/7,j%7);
			}
		}
		else {
			for(int j=extent; j<= newExtent; j++){
				addNode(j/7,j%7);
			}
		}
			
	}
	
   public class MyRangeSlider extends JComponent {
	   
	   		JFormattedTextField textFieldLeft; 
	   		DoubleRangeSlider slider;
	   		JFormattedTextField textFieldRight;
	   		
            public MyRangeSlider(int min, int max, int value, int extent){
           
              
        	   	  slider = new DoubleRangeSlider(min, max, value, extent) {
        	   		
        	   		
        	   		double oldValue = super.model.getValue();            
        	   		double oldExtent = super.model.getExtent();
                    
        	            public void mouseReleased(MouseEvent e) {

        	                  super.model.setValueIsAdjusting(true);
        	          
        	                  double newValue = super.model.getValue();
        	                  double newExtent = super.model.getExtent();
        	                                  
        	                  updateRange(newValue, newExtent, oldValue, oldExtent);
        	                  
        	                  oldValue = newValue;
        	                  oldExtent = newExtent;
        	            }

        	            public void stateChanged(ChangeEvent e) {
        	            
        	            	DefaultDoubleBoundedRangeModel source = (DefaultDoubleBoundedRangeModel)e.getSource();
        	            	
        	            	
        	            	int initialLeft = (int)source.getValue();
        	            	int initialRight = (int)source.getExtent() + initialLeft;
        	            	
        	            	if (!source.getValueIsAdjusting()) { //done adjusting
        	                    textFieldLeft.setValue(new Integer(initialLeft));
        	                    textFieldRight.setValue(new Integer(initialRight));
        	                } 
        	                
        	                else { //value is adjusting; just set the text
        	                    textFieldLeft.setText(String.valueOf(initialLeft));
        	                    textFieldRight.setText(String.valueOf(initialRight));
        	                }   
  	
        	            	repaint(); 
        	            }	            

        	   	  };
            
            			textFieldLeft = new JFormattedTextField();
                    	textFieldRight = new JFormattedTextField();
       
                    	textFieldLeft.setValue(new Integer(0));
                    	textFieldLeft.setColumns(5); //get some space
                    	textFieldRight.setValue(new Integer(48));
                    	textFieldRight.setColumns(5); //get some space
                    	

                    	textFieldLeft.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
                        textFieldLeft.getActionMap().put("check", new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                    	   		
                            	double oldValue = slider.model.getValue();            
                    	   		double oldExtent = slider.model.getExtent();
                            	
                    	   		String val = (String) textFieldLeft.getText();
                            	
                    	   		try {
                            		int newValue = Integer.parseInt(val);
                                  	double newExtent = slider.model.getExtent();
                                	System.out.println("text field left " + newValue);
                                	
                            		slider.setLowValue(newValue);
                            		
                                 	
                                	updateRange(newValue, newExtent+ (double) newValue, oldValue, oldExtent);
 
            	                  
                            	} catch (NumberFormatException nfe) {
                                    System.out.println("NumberFormatException: " + nfe.getMessage());
                            	}
                            	
                            	if (!textFieldLeft.isEditValid()) { 
                                    Toolkit.getDefaultToolkit().beep();
                                    textFieldLeft.selectAll();
                                } else try {                    
                                    textFieldLeft.commitEdit();     
                                } catch (java.text.ParseException exc) { }
                            }
                        });
                        
                        
                        textFieldRight.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");                        
                        textFieldRight.getActionMap().put("check", new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                            	
                            	double oldValue = slider.model.getValue();            
                    	   		double oldExtent = slider.model.getExtent();
                    	   		
                            	String val = (String) textFieldRight.getText();
                            	try {
                            		double newValue = slider.model.getValue();
                            		int newExtent = Integer.parseInt(val);
                            		System.out.println("text field right " + newExtent);
                            		slider.model.setExtent(newExtent);/////
                            		slider.setLowValue(newValue);
                            		slider.setHighValue(newExtent);

                                	updateRange(newValue, newExtent, oldValue, oldExtent);
                                	                                	
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
                        
                    	JFrame sliderFrame = new JFrame();
                    	slider.setEnabled(true);
                    	textFieldLeft.setEnabled(true);
                    	textFieldRight.setEnabled(true);
                    	sliderFrame.getContentPane().add(BorderLayout.LINE_START, textFieldLeft);
                    	sliderFrame.getContentPane().add(BorderLayout.LINE_END, textFieldRight);
                    	sliderFrame.getContentPane().add(BorderLayout.CENTER, slider);
                   
                    	sliderFrame.setVisible(true);
                    	sliderFrame.pack(); 
                    	
            }
            
   }
 
