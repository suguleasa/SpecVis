/*
 * HorizontalAxisFisheye.java
 * Author: Elena Caraba
 * Create Date: Summer 2007
 *
 */

package gui.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.JFrame;

import models.SpecTable;

import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.PFrame;
import gui.graph.HorizontalAxisFisheye.NumberNode;
import gui.graph.VerticalAxisFisheye.NameNode;

public class HorizontalAxisFisheye extends PNode {

    PNode layoutNode = new PNode();

    private LineNode lineNode;

    private GridNode grid;

    private boolean focused = false;

    private boolean rightOfFocus = false;

    private boolean leftOfFocus = false;

    private boolean isNeighbour = false;

    private boolean specialCase = false;

    private boolean neighbourFocused = false;

    private boolean itIsANeighbour = false;

    private boolean flagLeft = false;
    
    private boolean flagRight = false;

    private boolean colorRed = false;

    private boolean focusColorRed = false;

    private boolean neighbourRed = false;
    
    private boolean exited = false;

    public static final double INITIAL_SIZE =.79074;

    public static final double FOCUS_SIZE = 3.2715574;

    public static final double OTHERS_SIZE =0.73;

    public static final double NEIGHBOUR_SIZE = 1.6364;

    public static final double SPECIAL_SIZE = 1.2;
    public static final double MIN_SIZE = 0.573;

    public static final float BOX_WIDTH = 21;
    
    String stringName;

    public double length = 0;
    public double totalNewLength = 0;
    public int potentialMin = (int) Double.POSITIVE_INFINITY;
    public int  minIndex = (int)Double.POSITIVE_INFINITY;
    public int potentialMax = (int) Double.NEGATIVE_INFINITY;
    public int maxIndex = (int) Double.NEGATIVE_INFINITY;
    
    
    public double halfFocusLength = 0;
    public double firstNeighbLength = 0;
    public double secondNeighbLength = 0;
    
    
    public int numNodes ;
    public int numNodesLeft;
    
    public double partialLength;
    public double partialLength2;
    public double partialLength3;
    public double partialLength4;
    public double newSmallWidthRight;
    public double newSmallWidthLeft;
    public double newWidth;

	public boolean overNeighb1 = false;

	public double widthN11;

	public double widthN12;

	public boolean overFocus = false;

	public double newSpecialWidth =1;

	public double initialWidth;

    final private SpecCanvas parent;

    /*
     * Note: These are the most important terms I've used when coding:
     * 
     * focused case = the case when one spectrum is selected (clicked)
     * unfocused case = the initial state, before the fisheye/highlighting/zooming is applied
     * 
     * focus = the number/name/spectrum that was selected; it has the biggest size
     * neighbour = the node before or after the focus number; seconf highest size
     * others = all the other numbers that have neither the focus size, nor the
     * 			 neighbour size, when one node is selected 
     * 
     * specialCase = true ==> synonym for the focused case
     * specialCase = false ==> synonym for the unfocused case 
     * 
     * **** For the layout methods: we split the layout method in half
     * firstPart = the first part of the layout that goes from index 0 to (last index)/2
     * secondPart = 			
     */
    
    public class LineNode extends PNode {

        double difference = 0;

        private NumberNode focusNode;


//        private NumberNode clickedNode;
        
        private int lastNodeIndex = -1;

        
        public double length1 = 0;
        public double length2 = 0;
        public int indexN1 = -1;
        public int indexN2 = -1;
        public boolean firstPart = false;
        public boolean secondPart = false;

		protected int indexFocus;
		public double othersDim;

		protected double neighbDim;

		protected double focusDim;
		 
        public LineNode() {

           
        	//creating the nodes
            for (int i = -15; i <= 40; i++) {

                NumberNode textNode = new NumberNode(i);
                textNode.setScale(INITIAL_SIZE);

                addChild(textNode);
                
                length += textNode.getFullBoundsReference().getWidth(); //initial total length   
            }
            
            numNodes = getChildrenCount();
                 
            addInputEventListener(new PBasicInputEventHandler() {

                public void mouseClicked(PInputEvent event) {
                
                	unfocusAll();
                	
                	//get back to the initial state - the unfocused case - when 
                	//the you click on any number, target name, or right-click on
                	// a spectrum node
                	parent.callAxisUnFocusX();
                    parent.callAxisUnFocusY();
                    parent.callGridUnfocus();
                    
                    PNode node = event.getPickedNode();
                    
                    mouseClickAction(node);
                    
                    
                    //uncomment this to expand the grid when clicking on a node
                  
                    /*
                    clickedNode = (NumberNode) node;

                    parent.callGridUpdate(indexOfChild(clickedNode));*/
                }
                

                public void mouseReleased(PInputEvent event) {

                	changeOthersSize(INITIAL_SIZE);
                	PNode node = event.getPickedNode();
                    mouseClickAction(node);

                    uncolorAll((NumberNode)node);
                    unfocusAll();
                    uncolorAll((NumberNode)node);
                    
                   /* clickedNode = (NumberNode) node;
                    
                    parent.callGridUpdate(indexOfChild(clickedNode));*/
                }

                
                public void mouseMoved(PInputEvent event) {

                    NumberNode node = (NumberNode) event.getPickedNode();
      
                    
                    //mouseMoved should be called only once when the mouse is moved over the same number
                    if(indexOfChild(node) == lastNodeIndex){
                        return;
                    }
                    lastNodeIndex = indexOfChild(node);
                    
                    //reset everything
                    if(((float)node.getScale() == (float) NEIGHBOUR_SIZE|| (float)node.getScale() == (float)FOCUS_SIZE) && specialCase == true ){
        	          
        	            setInitial();
        	         }
                    
                    
                    difference = NEIGHBOUR_SIZE - INITIAL_SIZE;
                    
                    double increment = 0.4; //hack
                    double decrement = difference / 3;
                    
                    parent.callGridColorUpdateX(indexOfChild(node));

                    
                    //apply the fisheye effect for the unfocused case
                    if (specialCase == false) {
                    	 if(indexOfChild(node)< (numNodes/2)) {
                         	flagLeft = true;
                         	flagRight = false;
                         }
                         else {
                         	flagLeft = false;
                         	flagRight = true;
                         }
                    		 
                        node.setScale(NEIGHBOUR_SIZE);
                        node.setNumberPaint(Color.RED);
                        
                        //in the fisheye effect color the previous and the following two numbers
                        colorNextNumbers(node, NEIGHBOUR_SIZE, Color.blue, NEIGHBOUR_SIZE - decrement, 0, decrement,0);
                        colorPreviousNumbers(node, NEIGHBOUR_SIZE, Color.blue, NEIGHBOUR_SIZE - decrement, 0, decrement,0);
                     
                        //applying the layout formula
                        specialFormulaPartOne(node, length, decrement,numNodes, 0);
                      
                    }
                   
                    else {
                        
                        double othersLength1 = 0;
                        double othersLength2 = 0;
                      
                        //get the values necessary for the layout formula in the focused case
                        //indexN1 = index of Neighbour 1; othersLength1 = length of the other small numbers
                        //indexN2 = index of the seconf big  neighbour;
                        for(int i=0; i< getChildrenCount(); i++) {
                            
                            NumberNode local =  (NumberNode) getChild(i);
                            
                            if((i<numNodes -1) && ((float) local.getScale() == (float)NEIGHBOUR_SIZE && (float) getChild(i+1).getScale() == (float) FOCUS_SIZE)) {
                                indexN1 = indexOfChild(local);
                                if(indexN1 != 0) {
                                    othersLength1 = getChild(indexN1-1).getFullBounds().getWidth();
                                }
                            }
                            
                            if(i!=0 && (float) local.getScale() == (float) NEIGHBOUR_SIZE && (float) getChild(i-1).getScale() == (float) FOCUS_SIZE) {
                                indexN2 = indexOfChild(local);
                                if(indexN2 < getChildrenCount()-1) {
                                    othersLength2 =getChild(indexN2+1).getFullBounds().getWidth();
                                }
                            }
                            
                            if((float) local.getScale() == (float) NEIGHBOUR_SIZE) {
                                neighbDim = local.getFullBounds().getWidth();
                            }
                        
                            if((float) local.getScale() == (float) FOCUS_SIZE) {
                                focusDim = local.getFullBounds().getWidth();
                                indexFocus = indexOfChild(local);
                            }
                            
                           
                        }
                        
                        if(indexFocus == 0){
                        	indexN1 = -1;
                        }
                        if(indexFocus == (numNodes-1)) {
                        	indexN1 = numNodes -2;
                        }
                        
                        length1 = (indexN1) * othersLength1;
                        
                        if(othersLength1 != 0) {
                            othersDim = othersLength1;
                        }
                        else {
                            if(othersLength2 != 0) {
                                othersDim = othersLength2;
                            }
                        }
                      
                        
                      if(indexFocus == 0 || indexFocus == (numNodes-1)){ // if we are with the focused node at the ends
                    	  totalNewLength = focusDim +  neighbDim + (numNodes -2)*othersDim;
                      }
                      else {
                    	  totalNewLength = focusDim + 2* neighbDim + (numNodes -3)*othersDim;  
                      }
                       
                        if(indexOfChild(node)<indexN1) {
                            firstPart = true;
                            secondPart = false;
                            decideWhichHalf(node,indexN1);
                            specialFormulaPartOne(node, length1, decrement, indexN1,0);
                     
                        }
                        
                        length2 = (numNodes - indexN2-1) * othersLength2;
                        
                        int middle = numNodes-indexN2;
                        middle /= 2;
                        middle += indexN2;
                        
                        int numberOfNodes = numNodes -indexN2-1;
                        
                        if(indexOfChild(node)>indexN2) {
                            secondPart = true;
                            firstPart = false;
                            decideWhichHalf(node,middle);
                            specialFormulaPartTwo(node, length2, decrement,numberOfNodes, indexN2+1);
                        }
                        
                        //rolling over other numbers
                        colorOtherNumbers(node,increment);

                        //rolling over the focused number
                        colorFocusNumber(node);

                        //rolling over neighbour
                        colorNeighbourNumber(node);
                     
                        
                        // fixing the flickering when transit from last other before first neighbour and 1st neighb
                        if(indexOfChild(node) != indexN1){
                        	overNeighb1 = false;
                        	//node.setNumberX(0);
                        }
                        else {
                        	overNeighb1 = true;
                        }
                        
                        
                        //fixing the flickering when transit from first neighb to focus
                        if(indexOfChild(node) != indexFocus) {
                        	overFocus = false;
                        }
                        else {
                        	overFocus = true;
                        }
                    }
                    
                } // end of mouseMoved

                
                public void mouseExited(PInputEvent event) {
                    
                    NumberNode node = (NumberNode) event.getPickedNode();
                    
                    repaintToInitial(node);

                    parent.callGridUncolorUpdateX(indexOfChild(node));
                                
                    if(overFocus == true) {
                    	overFocus = false;
                    }
                    
                    if(overNeighb1 == true) {
                    	overNeighb1 = false;
                    }
                    
                    
                    uncolorAll(node);
                    
                    if(specialCase == false) {
                        uncolorAll(node);
                        
                        repaintToInitial(node);
                        
                        newWidth = node.getWidth();
                        
                    }
                    
                    exited = true;
                    
                }
                
                public void mouseEntered(PInputEvent event) {
                	exited = false;
                }
                
                
            });// end of the addInputEventListener

        } // end of the LineNode constructor

        public void printWidth() {
        	for(int i=0; i<numNodes;  i++){
            	System.out.println("Size of "+i+"  == "+ getChild(i).getFullBoundsReference().getWidth());
            }
        }
        
        public void setInitial() {
        	firstPart = false;
            secondPart = false;
            flagLeft = false;
            flagRight = false;
        }
        
        public void decideWhichHalf(NumberNode node, int totalIndex) {
            
        	flagLeft = false;
            flagRight = false;
            
            if(firstPart == true) {
                if(indexOfChild(node)<(totalIndex/2)) {
                    flagLeft = true;
                    flagRight = false;
                }
                else {
                    flagRight = true;
                    flagLeft = false;
                }
            }
            
            if(secondPart == true) {
                if(indexOfChild(node)<(totalIndex)) {
                    flagLeft = true;
                    flagRight = false;
                }
                else {
                    flagRight = true;
                    flagLeft = false;
                }
            }
        }
        
                
    public void specialFormulaPartTwo(NumberNode node, double localLength,double decrement, int NumNodes, int startingIndex) {
            
        NumberNode firstNeighb = null; 
        NumberNode secondNeighb = null;
        
        int firstIndex=0;
        int secondIndex=0;
        int focusIndex=0;
        
        initialWidth = node.getWidth();
        focusIndex = indexOfChild(node);
                    
        //set the indices for the first and second neighbour
        if(hasPrevious(node)) {
            firstNeighb = setPreviousNumber(focusIndex);
            firstIndex = indexOfChild(firstNeighb);
            if(hasPrevious(firstNeighb)) {
                secondNeighb = setPreviousNumber(firstIndex);
                secondIndex = indexOfChild(secondNeighb);
                
            }
            else {
                secondIndex =0;
                secondNeighb= null;
            }
        }
        
        else{
            firstIndex = 0;
            secondIndex = 0;
        }
        
        secondNeighbLength = (NEIGHBOUR_SIZE- 2*decrement)*initialWidth;
        firstNeighbLength = (NEIGHBOUR_SIZE-decrement)*initialWidth;
        halfFocusLength = node.getFullBounds().getWidth()/2;

        potentialMin = Math.min(firstIndex, secondIndex);
        minIndex = Math.min(potentialMin, focusIndex);
        potentialMax = Math.max(firstIndex,secondIndex);
        maxIndex = Math.max(potentialMax, focusIndex);
        
       
        if((float)node.getScale() == (float) NEIGHBOUR_SIZE || (float)node.getScale() == (float)FOCUS_SIZE) {
        	minIndex = 0;
        }
        
            //taking care of the layout from the left
            partialLength = (localLength/NumNodes)*(maxIndex-indexN2);
            
            if(partialLength!=0) {
	            partialLength2 = partialLength - secondNeighbLength -firstNeighbLength - halfFocusLength;
	            int numberNodes = maxIndex-indexN2-3;
	           
	            if (numberNodes >0) {
	            	newSmallWidthLeft = partialLength2/numberNodes;
	            }
	            else {
	            	newSmallWidthLeft = 0;
	            }
            }
            else {
                partialLength2=0;
            }
        
            double incrementSize1 = newSmallWidthLeft/initialWidth;
            
            for(int i=startingIndex; i<minIndex; i++) {
                if(incrementSize1 < MIN_SIZE) {
                    NumberNode localNode = (NumberNode) getChild(i);
                    localNode.setScale(MIN_SIZE);
                }
                else {
                    getChild(i).setScale(incrementSize1);
                }
            }
            
         
            //taking care of the layout from the right
            partialLength3 = localLength - partialLength - (halfFocusLength+firstNeighbLength+secondNeighbLength);
            numNodesLeft = numNodes - (maxIndex+2)-1;
            if(numNodesLeft >0){
            	newSmallWidthRight = partialLength3/numNodesLeft;
            }
            else {
            	newSmallWidthRight=0;
            }

            double incrementSize2 = newSmallWidthRight/initialWidth;
            
            for(int i=(maxIndex+3); i<numNodes; i++) {
                if(incrementSize2 <= MIN_SIZE) {
                    NumberNode localNode = (NumberNode) getChild(i);
                    localNode.setScale(MIN_SIZE);
                }
                else {
                    getChild(i).setScale(incrementSize2);
                }
            }
    }
        
        
                
    public void specialFormulaPartOne(NumberNode node, double localLength, double decrement, int NumNodes, int startingIndex) {
        
            
            NumberNode firstNeighb = null;
            NumberNode secondNeighb = null;
            
            int firstIndex=0;
            int secondIndex=0;
            int focusIndex=0;
            
            initialWidth = node.getWidth();
            focusIndex = indexOfChild(node);
               
            //set the indices for the first and second neighbour
            if(hasPrevious(node)) {
                firstNeighb = setPreviousNumber(focusIndex);
                firstIndex = indexOfChild(firstNeighb);
                if(hasPrevious(firstNeighb)) {
                    secondNeighb = setPreviousNumber(firstIndex);
                    secondIndex = indexOfChild(secondNeighb);
                    
                }
                else {
                    secondIndex =0;
                    secondNeighb= null;
                }
            }
            
            else{
                firstIndex = 0;
                secondIndex = 0;
            }
            
            secondNeighbLength = (NEIGHBOUR_SIZE- 2*decrement)*initialWidth;
            firstNeighbLength = (NEIGHBOUR_SIZE-decrement)*initialWidth;
            halfFocusLength = node.getFullBounds().getWidth()/2;
    
            potentialMin = Math.min(firstIndex, secondIndex);
            minIndex = Math.min(potentialMin, focusIndex);
            potentialMax = Math.max(firstIndex,secondIndex);
            maxIndex = Math.max(potentialMax, focusIndex);
            
                //taking care of the layout from the left
                partialLength = (localLength/NumNodes)*maxIndex;
                
                if(partialLength!=0) {
                partialLength2 = partialLength - secondNeighbLength - firstNeighbLength - halfFocusLength;
                newSmallWidthLeft = partialLength2/minIndex;
                }
                else {
                    partialLength2=0;
                }
                
                double incrementSize1 = newSmallWidthLeft/initialWidth;
                
                for(int i=startingIndex; i<minIndex; i++) {
                    if(incrementSize1 < MIN_SIZE) {
                        NumberNode localNode = (NumberNode) getChild(i);
                        localNode.setScale(MIN_SIZE);
                    }
                    else {
                        getChild(i).setScale(incrementSize1);
                    }
                }
                
                
                //taking care of the layout from the right
                partialLength3 = localLength - partialLength - (halfFocusLength+firstNeighbLength+secondNeighbLength);
                numNodesLeft = NumNodes - (maxIndex+2)-1;
             
               if(numNodesLeft >0){
            	   newSmallWidthRight = partialLength3/numNodesLeft;
                }
               else {
            	   newSmallWidthRight = 0;
               }
                
                double incrementSize2 = newSmallWidthRight/initialWidth;
                
                for(int i=(maxIndex+3); i<NumNodes; i++) {
                    if(incrementSize2 <= MIN_SIZE) {
                        NumberNode localNode = (NumberNode) getChild(i);
                        localNode.setScale(MIN_SIZE);
                    }
                    else {
                        getChild(i).setScale(incrementSize2);
                    }
                }

        }
	    
	    
        public void layoutChildren() {
                	
        	int startIndex = ((numNodes-1 - indexN2)/2 +indexN2);
        	
        	if(specialCase == false) {    
            	
            	if(flagLeft == true) {
                    leftLayoutChildren(0,0, numNodes);       
                }
                else if(flagRight == true) {
                     rightLayoutChildren(length, 0,numNodes-1);
                   
               } else {
                       leftLayoutChildren(0, 0,numNodes);
                       
                       //when coming from the focused case to unfocused, without rolling the mouse over the axis
                       if (grid != null) {
                    	   if(grid.getFocusState() == false) {
                              changeSizeAll(INITIAL_SIZE);
                              leftLayoutChildren(0,0,numNodes);
                 	      }
                       }
               }
            }
            else {
            	
            
            	
            	if(firstPart == true) {
                    
                    if(flagLeft == true) {
                        leftLayoutChildren(0, 0, indexN1-2); // indexN1-1 = last index that is nailed
                    }
                    else if(flagRight == true) {
                    	 rightLayoutChildren(length1,0,indexN1-1);
                    }
                    else {
                    	leftLayoutChildren(0,0,numNodes);
                    }
               
                }
                else {
                	
                    if(secondPart == true) {
                        if(flagLeft == true) {
                              
                        	leftLayoutChildren((totalNewLength-length2),indexN2+1,numNodes);
                            }
                            else if(flagRight == true) {
                            	leftLayoutChildren(0,0,numNodes);
                            
                            	//rightLayoutChildren(totalNewLength,indexN2,numNodes-1);
                          
                           }
                     
                    }
                    else {
                        
                        leftLayoutChildren(0,0,numNodes);
                    }
                }
    
            }
        	
        		// when we exit the axis, redraw anything.
        	   if(exited == true) {
        		   leftLayoutChildren(0,0,numNodes);
               }
               
        	   
        }
        
        public void rightLayoutChildren(double endingOffset, int startingIndex, int endingIndex) {
         
        	//starting index is actually the last index of all the nodes (i.e the 55th index is now 0)
        	//ending index is the index where the right part ends
        	
            double xOffset = endingOffset;
            double yOffset = 70;
           
            NumberNode each = null;
            
            boolean first = true;

            for(int i=(endingIndex); i>=(startingIndex); i--) {
                
                each = (NumberNode) getChild(i);
                
                //subtract from the end the width of the last node
                if (first) {
                    xOffset -= each.getFullBounds().getWidth();
                    first = false;
                }
                
                each.setOffset(xOffset, yOffset);
                   
                    if(specialCase == true && flagRight == true && secondPart == true) {
                    	if(i>1) {
                            NumberNode localeach = (NumberNode) (getChild(i-1));
                            xOffset -= localeach.getFullBounds().getWidth();
                            }
                        else {
                            xOffset -= each.getFullBounds().getWidth();
                       }
                    	
                    }
                    else {
                        if(i>0) {
                        	NumberNode localeach = (NumberNode) (getChild(indexOfChild(each)-1));
                        	xOffset -= localeach.getFullBounds().getWidth();
                        }
                        else {
                        	xOffset -= each.getFullBounds().getWidth();
                        }
                    }
            }
        }


        public void leftLayoutChildren(double startingOffset, int startingIndex, int endingIndex) {
            
            double xOffset = startingOffset;
            double yOffset = 70;
            
            // fixed where should the initial x offset be when rolling over the focus, first neighbour or others
            if(overNeighb1 == true) {
            	if(indexN1 == 0){
            		xOffset = startingOffset;
            		System.out.println("Click");
            	}
            	else {
            		if(indexN1 == 1){
            			xOffset -= othersDim* 3/4;	//hack for 3/4
            		}
            		else {
            			xOffset -= othersDim* 4/3; // hack for 4/3
            		}
            	}
        	}
            else {
            	if(overFocus == true) {
            		if(indexFocus == 0 || indexFocus == 1){
            			xOffset = startingOffset;
            			
            		}
            		else{
            			xOffset -= othersDim* 3/5; //hack fo 3/5
            		}
            	}
            	else {
            		xOffset = startingOffset;	
            	}
            }
           
         
            NumberNode each = null;
            
            for(int i=startingIndex; i<endingIndex; i++) {
                each = (NumberNode) getChild(i);
            
                each.setOffset(xOffset, yOffset);
                xOffset += each.getFullBounds().getWidth();
            }
        }
        
        
        public NumberNode getNode(int index) {

            String val = (new Integer(index)).toString();
            NumberNode each = null;
            Iterator k = getChildrenIterator();
            while (k.hasNext()) {
                NumberNode cur = (NumberNode) k.next();
                if (cur.getNumberText().equals(val)) {
                    return cur;
                }
            }
            return each;
        }

        public void repaintToInitial(PNode node) {
        
            NumberNode localNode = (NumberNode) node;
        
            if (specialCase == false) {
        
            	uncolorAll(localNode);
           
            } else {
                
                decolorOtherNumbers(localNode);
        
                decolorFocusNumber(localNode);
        
                decolorNeighbourNumber(localNode);
        
            } 
        
        }

        public void changeSizeAll(double size) {
		
        	for (int i = 0; i < getChildrenCount(); i++) {
		        
		        NumberNode node3 = (NumberNode) getChild(i);
		        node3.setRectWidth(BOX_WIDTH);
		        node3.setScale(size);
		        
		    }
		    
		}

        
        public void changeOthersSize(double size) {
		    for (int i = 0; i <= getChildrenCount() - 1; i++) {
		        
		        NumberNode node3 = (NumberNode) getChild(i);
		    
		        node3.setRectWidth(BOX_WIDTH-1);
		        
		      
		        getChild(i).setScale(size);
		        
		        
		        //when we unfocus, set the size of the boxes.
		        if (grid != null) {
	                 if(grid.getFocusState()== false) {
	                	 node3.setWidth(newWidth);
	                	
	                }
	             }
		        
		        
		    }
		    
		    specialCase = true;
		}


		public void colorPreviousNumbers(NumberNode node, double checkSize, Color color, 
        		double specialSize, int xMargin, double increment, double value) {
            if (hasPrevious(node)) {
                if ((float) getChild(indexOfChild(node) - 1).getScale() != (float) checkSize) {
                    NumberNode previousNeighbour = (NumberNode) getChild(indexOfChild(node) - 1);
                    previousNeighbour.setNumberPaint(color);
                    previousNeighbour.setScale(specialSize);
                    previousNeighbour.setX(xMargin);
                    previousNeighbour.moveBox(value);
                    if (hasPrevious(previousNeighbour)) {
                        if ((float) getChild(
                                indexOfChild(previousNeighbour) - 1).getScale() != (float) checkSize) {
                            NumberNode previousOther = (NumberNode) getChild(indexOfChild(previousNeighbour) - 1);
                            previousOther.setNumberPaint(color);
                            previousOther.setScale(specialSize - increment);
                            previousOther.setX(xMargin);
                            previousOther.moveBox(value);
                        }
                    }
                }
            }
        }

        public void colorNextNumbers(NumberNode node, double checkSize,
                Color color, double specialSize, int xMargin, double increment, double value) {
        
            if (nextExists(node)) {
                if ((float) getChild(indexOfChild(node) + 1).getScale() != (float) checkSize) {
                    NumberNode nextNeighbour = (NumberNode) getChild(indexOfChild(node) + 1);
                    nextNeighbour.setNumberPaint(color);
                    nextNeighbour.setScale(specialSize);
                    nextNeighbour.setX(xMargin);
                    nextNeighbour.moveBox(value);
                    if (nextExists(nextNeighbour)) {
                        if ((float) getChild(indexOfChild(nextNeighbour) + 1).getScale() != (float) checkSize) {
                            NumberNode nextOther = (NumberNode) getChild(indexOfChild(nextNeighbour) + 1);
                            nextOther.setNumberPaint(color);
                            nextOther.setScale(specialSize - increment);
                            nextOther.setX(xMargin);
                            nextOther.moveBox(value);
                        }
                    }
                }
            }
        }

        public boolean nextExists(NumberNode node) {
            PNode newNode = (PNode) node;
            if ((indexOfChild(newNode) + 1) < getChildrenCount()) {
                return true;
            }
            else {
                return false;
            }
        }

        public void colorFocusNumber(NumberNode node) {
            if ((float) node.getScale() == (float) FOCUS_SIZE) {
                node.setNumberPaint(Color.RED);
                focusColorRed = true;
              
                colorSpecialNumbers(node,Color.blue,NEIGHBOUR_SIZE,OTHERS_SIZE,NEIGHBOUR_SIZE,SPECIAL_SIZE);
            }
        }

        public void decolorFocusNumber(NumberNode localNode) {
            if (focusColorRed == true) {
                localNode.setNumberPaint(Color.BLACK);
                localNode.setScale(FOCUS_SIZE);
                focusColorRed = false;
              
                colorSpecialNumbers(localNode,Color.black,NEIGHBOUR_SIZE,SPECIAL_SIZE,NEIGHBOUR_SIZE,OTHERS_SIZE);}

        }

        public void uncolorAll(NumberNode localNode){
            
            if(specialCase == false) {
                for(int i = 0; i<getChildrenCount(); i++) {
                    localNode = (NumberNode) getChild(i);
                    localNode.setNumberPaint(Color.BLACK);
                    localNode.setNumberX(0);
                    localNode.setScale(INITIAL_SIZE);
                    
                }
            }
            else {
                for(int i = 0; i<getChildrenCount(); i++) {
                    localNode = (NumberNode) getChild(i);
                    if((float) localNode.getScale() == (float) FOCUS_SIZE) {
                        for(int j=0; j<i-1; j++) {
                            NumberNode node = (NumberNode)  getChild(j);
                            node.setNumberPaint(Color.BLACK);
                            node.setScale(OTHERS_SIZE);
                        }
                        for(int j=i+2; j< getChildrenCount(); j++) {
                            NumberNode node = (NumberNode)  getChild(j);
                            node.setNumberPaint(Color.BLACK);
                            node.setScale(OTHERS_SIZE);
                        }
                    }
                }
            }
            
            
        }
        
        
        public void colorOtherNumbers(NumberNode node, double increment) {
            if ((float) node.getScale() == (float) OTHERS_SIZE) {

                node.setNumberPaint(Color.red);
              
                node.setScale(SPECIAL_SIZE + increment);
                repaint();
                colorRed = true;

              
                colorNextNumbers(node,NEIGHBOUR_SIZE,Color.blue,SPECIAL_SIZE,0,increment,0);
              
                colorPreviousNumbers(node,NEIGHBOUR_SIZE,Color.blue,SPECIAL_SIZE,0,increment,0);
                
                //coloring the neighbour and the focus number in blue if that's the case
                if(hasPrevious(node)){
                    NumberNode previous = (NumberNode) getChild(indexOfChild(node)-1);
                    if((float)previous.getScale() == (float)NEIGHBOUR_SIZE) {
                        previous.setNumberPaint(Color.blue);
                        //color the focus
                        NumberNode previousPrevious = (NumberNode) getChild(indexOfChild(previous)-1);
                        previousPrevious.setNumberPaint(Color.blue);
                    }
                    if(hasPrevious(previous)){
                        NumberNode previousPrevious = (NumberNode) getChild(indexOfChild(previous)-1);
                        if((float) previousPrevious.getScale() == (float) NEIGHBOUR_SIZE) {
                            previousPrevious.setNumberPaint(Color.BLUE);
                        }
                    }
                }
                
                if(nextExists(node)) {
                    NumberNode next = (NumberNode)getChild(indexOfChild(node)+1); 
                    if((float)getChild(indexOfChild(node)+1).getScale() == (float)NEIGHBOUR_SIZE) {
                        next.setNumberPaint(Color.blue);
                        //color the focus
                        NumberNode nextNext = (NumberNode)getChild(indexOfChild(next)+1);
                        nextNext.setNumberPaint(Color.blue);          
         
                    }
                    if(nextExists(next)){
                        NumberNode nextNext = (NumberNode)getChild(indexOfChild(next)+1);
                        if((float) nextNext.getScale() == (float)NEIGHBOUR_SIZE) {
                            nextNext.setNumberPaint(Color.blue);
                        }
                    }
                }
            }
        }

        public void decolorOtherNumbers(NumberNode node) {
            if (colorRed == true) {
        
                node.setNumberPaint(Color.black);
                node.setScale(OTHERS_SIZE);
        
                colorPreviousNumbers(node, NEIGHBOUR_SIZE,
                        Color.black, OTHERS_SIZE, 0, 0, 0);
        
                colorNextNumbers(node, NEIGHBOUR_SIZE, Color.black,
                        OTHERS_SIZE, 0, 0,0);
        
                colorRed = false;
                
                //coloring the neighbour and the focus number in blue if that's the case
                if(hasPrevious(node)){
                    NumberNode previous = (NumberNode) getChild(indexOfChild(node)-1);
                    if((float)getChild(indexOfChild(node)-1).getScale() == (float)NEIGHBOUR_SIZE) {
                        previous.setNumberPaint(Color.black);
                        //color the focus
                        if(hasPrevious(previous)) {
                        	NumberNode previousPrevious = (NumberNode) getChild(indexOfChild(previous)-1);
                        	previousPrevious.setNumberPaint(Color.black);
                        }
                    }
                    if(hasPrevious(previous)){
                        NumberNode previousPrevious = (NumberNode) getChild(indexOfChild(previous)-1);
                        if((float) previousPrevious.getScale() == (float) NEIGHBOUR_SIZE) {
                            previousPrevious.setNumberPaint(Color.black);
                        }
                    }
                }
                
                if(nextExists(node)) {
                    NumberNode next = (NumberNode) getChild(indexOfChild(node)+1);
                    if((float)getChild(indexOfChild(node)+1).getScale() == (float)NEIGHBOUR_SIZE) {
                        next.setNumberPaint(Color.black);
                        //color the focus
                        if(nextExists(next)){
                        	NumberNode nextNext = (NumberNode) getChild(indexOfChild(next)+1);
                        	nextNext.setNumberPaint(Color.black);
                        }
                    }
                    if(nextExists(next)){
                        NumberNode nextNext = (NumberNode) getChild(indexOfChild(next)+1);
                        if((float) nextNext.getScale() == (float) NEIGHBOUR_SIZE) {
                            nextNext.setNumberPaint(Color.black);
                        }
                    }
                }
        
            }
        }

        public void colorNeighbourNumber(NumberNode node) {
            if((float) node.getScale() == (float) NEIGHBOUR_SIZE) {
                
                neighbourRed = true;
                
                double difference = (NEIGHBOUR_SIZE - INITIAL_SIZE)/3;
                
                node.setNumberPaint(Color.RED);
                
                //if it's the left neighbour
                if((indexOfChild(node)+1 < getChildrenCount())&& ((float) getChild(indexOfChild(node)+1).getScale() == (float) FOCUS_SIZE)) {
                  
                	colorSpecialNumbers(node,Color.blue,OTHERS_SIZE,OTHERS_SIZE,NEIGHBOUR_SIZE-difference,NEIGHBOUR_SIZE-2*difference);
                	
                	if(hasPrevious(node)) {
            			NumberNode neighb11 = (NumberNode) getChild(indexOfChild(node)-1);
            			widthN11 = neighb11.getFullBoundsReference().getWidth();
            			if(hasPrevious(neighb11)) {
            				NumberNode neighb12 = (NumberNode) getChild(indexOfChild(node)-2);
            				widthN12 = neighb12.getFullBoundsReference().getWidth();
            			}
            		}
                	
                	
                    NumberNode  focus = (NumberNode) getChild(indexOfChild(node)+1);
                    focus.setNumberPaint(Color.blue);
                    if(nextExists(focus)) {
                        NumberNode  rightNeighb = (NumberNode) getChild(indexOfChild(node)+2);
                        rightNeighb.setNumberPaint(Color.blue);
                    }
                }
                
                //if it's the right neighbour
                if((indexOfChild(node)-1>=0)&&((float) getChild(indexOfChild(node)-1).getScale() == (float) FOCUS_SIZE)) {
                  
                	colorSpecialNumbers(node,Color.blue,OTHERS_SIZE,OTHERS_SIZE,NEIGHBOUR_SIZE-difference,NEIGHBOUR_SIZE-2*difference);
                    NumberNode focus = (NumberNode) getChild(indexOfChild(node)-1);
                    focus.setNumberPaint(Color.blue);
                    if(hasPrevious(focus)){
                        NumberNode leftNeighb = (NumberNode) getChild(indexOfChild(node)-2);
                        leftNeighb.setNumberPaint(Color.blue);
                    }
                }    
            }
        }

        
        public void decolorNeighbourNumber(NumberNode node) {
            if (neighbourRed == true) {
                node.setNumberPaint(Color.black);
                node.setScale(NEIGHBOUR_SIZE);
                neighbourRed = false;
                
                double difference = (NEIGHBOUR_SIZE - INITIAL_SIZE)/3;
                
                //if it's the left neighbour
                if((indexOfChild(node)+1 < getChildrenCount())&&(float) getChild(indexOfChild(node)+1).getScale() == (float) FOCUS_SIZE) {
                  
                	colorSpecialNumbers(node,Color.black,NEIGHBOUR_SIZE-difference,NEIGHBOUR_SIZE-2*difference,OTHERS_SIZE,OTHERS_SIZE);
                    NumberNode  focus = (NumberNode)
                    getChild(indexOfChild(node)+1);
                    focus.setNumberPaint(Color.black);
                    if(nextExists(focus)) {
                        NumberNode  rightNeighb = (NumberNode)
                        getChild(indexOfChild(node)+2);
                        rightNeighb.setNumberPaint(Color.black);
                    }
                    
                    
                /*    if(hasPrevious(node)) {
                        NumberNode previousNumber = (NumberNode)
getChild(indexOfChild(node) - 1);
                        previousNumber.setNumberPaint(Color.BLACK);
                        previousNumber.setScale(OTHERS_SIZE);
                        if(hasPrevious(previousNumber)) {
                            NumberNode otherPrevious = (NumberNode)
getChild(indexOfChild(previousNumber)-1);
                            otherPrevious.setNumberPaint(Color.BLACK);
                            otherPrevious.setScale(OTHERS_SIZE);
                        }
                    }*/
                }
                    
                //if it's the right neighbour
                if((indexOfChild(node)-1 >0)&&(float) getChild(indexOfChild(node)-1).getScale() == (float) FOCUS_SIZE) {
                  
                	colorSpecialNumbers(node,Color.black,NEIGHBOUR_SIZE-difference,NEIGHBOUR_SIZE-2*difference,OTHERS_SIZE,OTHERS_SIZE);
                    NumberNode focus = (NumberNode)
                    getChild(indexOfChild(node)-1);
                    focus.setNumberPaint(Color.black);
                    if(hasPrevious(focus)){
                        NumberNode leftNeighb = (NumberNode)
                        getChild(indexOfChild(node)-2);
                        leftNeighb.setNumberPaint(Color.black);
                    }
                }        
            }
        }
        
        
        public void colorSpecialNumbers(NumberNode node, Color color, double checkSize1, double checkSize2, double size1, double size2) {
            if (nextExists(node)) {
                if ((float) getChild(indexOfChild(node) + 1).getScale() == (float) checkSize1) {
                    NumberNode nextNeighbour = (NumberNode) getChild(indexOfChild(node) + 1);
                    nextNeighbour.setNumberPaint(color);
                    nextNeighbour.setScale(size1);
                    if (nextExists(nextNeighbour)) {
                        if ((float) getChild(indexOfChild(nextNeighbour)+1).getScale() == (float) checkSize2) {
                            NumberNode nextOther = (NumberNode) getChild(indexOfChild(nextNeighbour) + 1);
                            nextOther.setNumberPaint(color);
                            nextOther.setScale(size2);
                        }
                    }
                }
            }
            if (hasPrevious(node)) {
                if ((float) getChild(indexOfChild(node) - 1).getScale() == (float) checkSize1) {
                    NumberNode previousNeighbour = (NumberNode) getChild(indexOfChild(node) - 1);
                    previousNeighbour.setNumberPaint(color);
                    previousNeighbour.setScale(size1);
                    if (hasPrevious(previousNeighbour)) {
                        if ((float) getChild(indexOfChild(previousNeighbour) - 1).getScale() == (float) checkSize2) {
                            NumberNode previousOther = (NumberNode) getChild(indexOfChild(previousNeighbour) - 1);
                            previousOther.setNumberPaint(color);
                            previousOther.setScale(size2);
                        }
                    }
                }
            }
        }
        
        
        
        public void callClickAction(int index) {
            
                mouseClickAction(getNode(index));
            
        }

        public void mouseClickAction(PNode node) {
            NumberNode newNode = null;

       
            changeOthersSize(INITIAL_SIZE);

            defocusingNumbers(focusNode);
            specialCase = false;

            if (node.getClass().equals(NumberNode.class)) {

                NumberNode number = (NumberNode) node;
                newNode = number;

                if (newNode != null) {

                    if (newNode == focusNode) {
                        focusNode = null;
                        
                    } else {

                       /* for(int i=0; i<getChildrenCount(); i++) {
                        	if((float) getChild(i).getScale() == (float) NEIGHBOUR_SIZE) {
                        		NumberNode localNode = (NumberNode) getChild(i);
                        		localNode.translateNumber(100);
                        	}
                        }*/
                    	
                        changeOthersSize(OTHERS_SIZE);

                        focusNode = newNode;

                        focusTheNeighbours(focusNode);

                        focusNumber(number);

                        //specialCase = true;

                    }
                }
            }

        }

        public void unfocusAll() {

            if (focusNode != null) {
                defocusingNumbers(focusNode);
                changeOthersSize(INITIAL_SIZE);
                specialCase = false;
            }
        }

        public void defocusingNumbers(NumberNode focusNode) {
            if (focusNode != null) {

                focused = true;
                deFocusNumber(focusNode);

                NumberNode previousNumber = null;
                NumberNode nextNumber = null;

                int pickedIndex = indexOfChild(focusNode);

                previousNumber = setPreviousNumber(pickedIndex);
                nextNumber = setNextNumber(pickedIndex);

                neighbourFocused = true;
                if (hasPrevious(focusNode) == true) {
                    deFocusNeighbour(previousNumber);
                }
                neighbourFocused = true;
                if (hasNext(focusNode) == true) {
                    deFocusNeighbour(nextNumber);
                }

            }
            
            setInitial();

        }

        public void focusTheNeighbours(NumberNode focusNode) {

            NumberNode previousNumber = null;
            NumberNode nextNumber = null;

            int pickedIndex = indexOfChild(focusNode);

            previousNumber = setPreviousNumber(pickedIndex);
            nextNumber = setNextNumber(pickedIndex);

            if (hasPrevious(focusNode) == true) {
                focusNeighbour(previousNumber);
                leftOfFocus = true;
            } else {
                leftOfFocus = false;
            }

            if (hasNext(focusNode) == true) {
                focusNeighbour(nextNumber);
                rightOfFocus = true;
            } else {
                rightOfFocus = false;
            }

        }

        public NumberNode theNext(NumberNode focusNode) {

            int pickedIndex = indexOfChild(focusNode);

            if (getChildrenCount() > pickedIndex + 1) {
                focusNode = (NumberNode) getChild(pickedIndex + 1);
            }
            return focusNode;
        }

        public NumberNode setNextNumber(int index) {
            NumberNode nextNumber;

            if (getChildrenCount() > index + 1) {
                nextNumber = (NumberNode) getChild(index + 1);

            } else {
                nextNumber = null;
            }
            return nextNumber;
        }

        public NumberNode setPreviousNumber(int index) {
            NumberNode previousNumber;

            if (0 <= index - 1) {
                previousNumber = (NumberNode) getChild(index - 1);
                itIsANeighbour = true;
            } else {
                previousNumber = null;
                itIsANeighbour = false;
            }
            return previousNumber;
        }

        public void focusNumber(NumberNode focusNumber) {

            if (focusNumber != null) {

                focusNumber.setScale(FOCUS_SIZE);
                focusNumber.setRectWidth(45);
                focusNumber.setNumberX(15);
          
            }
        }

        public void deFocusNumber(NumberNode focusNumber) {

            focused = false;
            if (focusNumber != null) {

                focusNumber.setScale(INITIAL_SIZE);
                focusNumber.setRectWidth(20);
                focusNumber.setNumberX(0);
            
            }
        }

        public void focusNeighbour(NumberNode focusNumber) {

            neighbourFocused = true;
            if (focusNumber != null) {
                focusNumber.setScale(NEIGHBOUR_SIZE);
                focusNumber.setRectWidth(40);
                focusNumber.setNumberX(15);
            }
        }

        public void deFocusNeighbour(NumberNode focusNumber) {

            neighbourFocused = false;
            if (focusNumber != null) {
                focusNumber.setScale(INITIAL_SIZE);
                focusNumber.setRectWidth(20); 
                focusNumber.setNumberX(0);
            }
        }

        public boolean hasNext(NumberNode focusNumber) {

            int pickedIndex = indexOfChild(focusNumber);

            if (pickedIndex <= getChildrenCount() - 1) {
                return true;
            } else {
                return false;
            }
        }

        public boolean hasPrevious(NumberNode focusNumber) {

            int pickedIndex = indexOfChild(focusNumber);

            if (pickedIndex != 0) {
                return true;
            }

            else {
                return false;
            }
        }

    } // end of the LineNode class

    public class NumberNode extends PPath {

        boolean isFocus = false;

        boolean hasWidthFocus;

        boolean empty = false;

        PText number;

        public NumberNode(int i) {

            super();
            //setStrokePaint(Color.white);
            setPathToRectangle(0, 0,  BOX_WIDTH, 100);

            number = new PText();
           
            stringName = String.valueOf(i);
            number.setText(stringName);
            number.setScale(0.8); // value chosen to fit the current graph

            PBounds b = getFullBounds();
            b.inset(-0.1, 0); 

            // center the numbers in their box
            if (i >= 0 && i <= 9) {
               number.translate(10,0);
            }
            if(i>=10 ||(i>-10 && i<0)) {
                number.translate(5,0);
            }
            
            setBounds(b);
            
            addChild(number);
            
            setPaint(Color.white);
            setChildrenPickable(false);

        }//end of the NumberNode constructor

        public void setNumberX(int size) {
        	number.setX(size);
        }
        
        public void moveBox(double value) {
              setX(value);
              number.setX(value);
          }
        
        public boolean isFocused() {
            return focused;
        }
        
        public double initialTranslate() {
            return number.getX();
            
        }

        public void translateNumber(double value) {
                number.translate(value,0);
        }
        public void setRectWidth(float width) {
            setPathToRectangle(0,0,width,100);
        }
 
        
        public double getNumberScale() {
            return number.getScale();
        }
        public String getNumberText() {
            return number.getText();
        }

        public void setNumberPaint(Color c) {
            number.setTextPaint(c);
        }
    } // end of the NumberNode class

 
    

    public HorizontalAxisFisheye(final SpecCanvas parent) {

        this.parent = parent;
        lineNode = new LineNode();
        addChild(lineNode);

    }

    public void callUpdateAxis(int index) {
        lineNode.callClickAction(index);
    }

    public void callUnFocus() {
        lineNode.unfocusAll();
    }

    public void setGridNode(GridNode grid) {
        this.grid = grid;
    }

    public class HorizontalBox extends PFrame {

        //private SpecTable specTable;

        public SpecTable specTable = (SpecTable) grid.getSpecTable();

        public void initialize() {

            System.out.println("spec table is: " + specTable);

            getCanvas().setPanEventHandler(null);
            getCanvas().addInputEventListener(new PDragEventHandler());

            PLayer layer = getCanvas().getLayer();

            PNode myCompositeBox = PPath.createRectangle(30, 30, 100, 80);

            //SpecNode graphNode = new

            /*
             * int colIndex = specTable.getColumnIndex("phase");
             * System.out.println("Phase index is: " + colIndex);
             */
            /*
             * int numRows = specTable.getRowCount(); for (int i = 0; i <
             * numRows; i++) {
             *
             * Integer curPhase = (Integer)
specTable.getValueAt(i,colIndex);
             * //curPhase.intValue();
             *
             * Target curTarget = (Target)
             * specTable.getValueAt(i,specTable.getColumnIndex("target"));
             * Spectrum spec = (Spectrum)
             * specTable.getValueAt(i,specTable.getColumnIndex("spec"));
             * SpecNode specNode = (SpecNode)
             * specTable.getValueAt(i,specTable.getColumnIndex("pnode"));
             * myCompositeBox.addChild(specNode); }
             */

            myCompositeBox.setChildrenPickable(false);

            myCompositeBox.scale(1.5);

            JFrame boxFrame = new JFrame();

            //boxFrame.getContentPane().add(BorderLayout.CENTER, );
            boxFrame.setVisible    (true);
            boxFrame.pack();

            layer.addChild(myCompositeBox);

        }

    }

}

