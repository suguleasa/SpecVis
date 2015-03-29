/*
 * VerticalAxisFisheye.java
 * Author: Elena Caraba
 * Create Date: Jun 29, 2007
 *
 */
package gui.graph;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Component;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent ;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.PFrame;
import gui.graph.HorizontalAxisFisheye.LineNode;
import gui.graph.HorizontalAxisFisheye.NumberNode;

public class VerticalAxisFisheye extends PNode{

  PNode layoutNode = new PNode();

  private boolean focused = false;
  private boolean rightOfFocus = false;
  private boolean leftOfFocus = false;
  private boolean isNeighbour = false;
  private boolean specialCase = false;
  private boolean neighbourFocused = false;
  private boolean itIsANeighbour = false;
  private boolean focusLock = false;
  private boolean colorBlue = false;
  private boolean colorRed = false;
  private boolean over = false;
  private boolean focusColorRed = false;
  private boolean colorNeighbRed = false;
 
  public static final double INITIAL_SIZE = 0.495;//0.485;//3.5319434;//1.44967831;
  public static final double FOCUS_SIZE = 1.2;//0.94;
  public static final double OTHERS_SIZE = 0.46;//0.92253;
  public static final double NEIGHBOUR_SIZE = 0.75;//3.953674;
  public static final double SPECIAL_SIZE = 0.7;
 
  public static final double ROLLOVER_SIZE = 1;
 
  public static final double MIN_SIZE = 0.4584; // below this size, the target name is not visible anymore
  public static final float BOX_HEIGHT = 20;

  public static final float BOX_WIDTH = 201; //box width including the boundaries
  public static final float SPECIAL_BOX_WIDTH = 160;
 
  public double length = 0;
 
  public ArrayList targetNames;
  private GridNode grid;
 
  public int focusBoxOffset = -80;
  public int nameOffset = -20;
  public int neighbBoxOffset = -60;
  public int boxOffset = -20;
 
  public int counter = 0;
  public int index = 0;
 
  String stringName;

  private NameNode focusNode;
  private LineNode lineNode;
 
  final private SpecCanvas parent;

  public boolean flagLeft = false;

  public boolean flagRight = false;
   
  public boolean firstPart = false;

  public boolean secondPart = false;

  public double initialHeight = 0;
  public int numNodes =0 ;
  public int numNodesLeft;


  public double secondNeighbLength;

  public double firstNeighbLength;

  public int potentialMin = (int) Double.POSITIVE_INFINITY;
  public int  minIndex = (int)Double.POSITIVE_INFINITY;
  public int potentialMax = (int) Double.NEGATIVE_INFINITY;
  public int maxIndex = (int) Double.NEGATIVE_INFINITY;

 
  public double partialLength;
  public double partialLength2;
  public double partialLength3;
  public double partialLength4;
  public double newSmallHeightRight;
  public double newSmallHeightLeft;
  public double newHeight;

  public double initialWidth;

  public double newSmallWidthLeft;
 
  public double newSpecialHeight =1;

  public double heightN11;
   
  public double heightN12;
   
  public void callUpdateAxis(int index) {
      lineNode.callMouseClick(index);
  }

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
   */
  
  public class LineNode extends PNode {

        ArrayList lines;
     
        double difference=0;
        NameNode clickedNode = null;

        private double halfFocusLength;
        private int lastNodeIndex = -1;

        protected int indexN1;

        protected int indexN2;

        protected double length1;

        protected double totalNewLength;

        protected double length2;

        private double newSmallWidthRight;

        protected boolean overFocus = false;

        protected int indexFocus;

        protected boolean overNeighb1 = false;

        protected double neighbDim;

        protected double focusDim;

        protected double othersDim;

        protected double newHeight;
        
        
      public LineNode() {

          Iterator i = targetNames.iterator();
          
          while(i.hasNext()) {
          
        	  String name = (String) i.next();
              NameNode textNode = new NameNode(name);
         
              textNode.setScale(INITIAL_SIZE);

              addChild(textNode);
              counter ++;
              length += textNode.getFullBoundsReference().getHeight();  
        }
       
          numNodes = counter;
         
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
                
            }
           
           
            public void mouseReleased(PInputEvent event) {
                 
            	changeOthersSize(ROLLOVER_SIZE);
            	
                PNode node = event.getPickedNode();
                mouseClickAction(node);
                clickedNode = (NameNode) node;
              
                uncolorAll((NameNode)node);
                unfocusAll();
                uncolorAll((NameNode)node);

              //  parent.callGridUpdate(indexOfChild(clickedNode));
            }
           
              public void mouseMoved(PInputEvent event) {
                
                   NameNode node = (NameNode) event.getPickedNode();
                 
                   difference = FOCUS_SIZE - INITIAL_SIZE;
                  
                   double increment = difference/3;;
                   double diff = ROLLOVER_SIZE - INITIAL_SIZE;
                   double decrement = diff/3;

                  
                   index = indexOfChild(node);
                   
                   //mouseMoved should be called only once when the mouse is moved over the same number
                   if(index == lastNodeIndex){
                       return;
                   }
                   lastNodeIndex = indexOfChild(node);
                  
                   //reset everything
                   if(((float)node.getScale() == (float) NEIGHBOUR_SIZE|| (float)node.getScale() == (float)FOCUS_SIZE) && specialCase == true ){
                       setInitial();
                    }
                              
                   parent.callGridColorUpdateY(indexOfChild(node));
                  
                   //apply the fisheye effect for the unfocused case
                   if (specialCase == false){
                      
                       node.setScale(ROLLOVER_SIZE);
                       node.setNamePaint(Color.RED);
                       node.setX(-10);
                       node.setNameX(nameOffset);
                       node.setWidth(150);
                      
                       //in the fisheye effect color the previous and the following two numbers
                       colorNextNames(node, NEIGHBOUR_SIZE, Color.blue, ROLLOVER_SIZE - decrement, 0,-10, decrement); 
                       colorPreviousNames(node, NEIGHBOUR_SIZE,Color.blue, ROLLOVER_SIZE - decrement, 0,-10, decrement);
                      
                       
                       //setting the names in their boxes
                       // the values are the ones that work the best
                       if(hasPrevious(node)){
                           NameNode prevName = setPreviousName(index);
                           prevName.setX(0);
                           prevName.setWidth(170);
                           prevName.setNameX(-15);
                       }
                      
                       if(nextExists(node)) {
                           NameNode nextName = setNextName(index);
                           nextName.setWidth(170);
                           nextName.setX(0);
                           nextName.setNameX(-15);
                       }
                      
                       //applying the layout formula
                       specialLayoutPartOne(node, length,decrement,numNodes, 0, ROLLOVER_SIZE);
                      
                   }
                   else {
                      
                       double othersLength1 = 0;
                       double othersLength2 = 0;
                      
                       //get the values necessary for the layout formula in the focused case
                       //indexN1 = index of Neighbour 1; othersLength1 = length of the other small numbers
                       for(int i=0; i< getChildrenCount(); i++) {
                          
                           NameNode local =  (NameNode) getChild(i);
                          
                           if((i<numNodes -1) && ((float)local.getScale() == (float)NEIGHBOUR_SIZE && (float) getChild(i+1).getScale() == (float) FOCUS_SIZE)) {
                               indexN1 = indexOfChild(local);
                               if(indexN1 != 0) {
                                   othersLength1 = getChild(indexN1-1).getFullBounds().getHeight();
                               }
                           }
                          
                           if(i!=0 && (float) local.getScale() == (float) NEIGHBOUR_SIZE && (float) getChild(i-1).getScale() == (float) FOCUS_SIZE) {
                               indexN2 = indexOfChild(local);
                               if(indexN2 < getChildrenCount()-1) {
                                   othersLength2 =getChild(indexN2+1).getFullBounds().getHeight();
                               }
                              
                           }
                          
                           if((float) local.getScale() == (float) NEIGHBOUR_SIZE) {
                               neighbDim = local.getFullBounds().getHeight();
                           }
                      
                           if((float) local.getScale() == (float) FOCUS_SIZE) {
                               focusDim = local.getFullBounds().getHeight();
                              
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
                       
                       if(indexFocus == 0 || indexFocus == (numNodes-1)){ // if the focused node is the first or last node
                     	  totalNewLength = focusDim +  neighbDim + (numNodes -2)*othersDim;
                       }
                       else {
                     	  totalNewLength = focusDim + 2* neighbDim + (numNodes -3)*othersDim;  
                       }
                      
                     
                   
                       if(indexOfChild(node)<indexN1) {
                           firstPart = true;
                           secondPart = false;
                           decideWhichHalf(node,indexN1);
                       }
                      
                       length2 = (numNodes - indexN2-1) * othersLength2;
                      
                       int middle = numNodes-indexN2;
                       middle /= 2;
                       middle += indexN2;
                      
                       if(indexOfChild(node)>indexN2) {
                           secondPart = true;
                           firstPart = false;
                           decideWhichHalf(node,middle);
                    
                       }
                      
                        //rolling over other numbers
                        colorOtherNames(node,increment);

                        //rolling over the focused number
                        colorFocusName(node);

                        //rolling over neighbour
                        colorNeighbourName(node);
                         
                        //fixing the flickering when transit from first neighb to focus
                        if(indexOfChild(node) != indexFocus) {
                            overFocus  = false;
                        }
                        else {
                            overFocus = true;
                        }
                       
                       
                        // fixing the flickering when transit from last other before first neighbour and 1st neighb
                        if(indexOfChild(node) != indexN1){
                            overNeighb1  = false;
                        }
                        else {
                            overNeighb1 = true;
                        }
                   }
              }
            
              public void mouseExited(PInputEvent event) {
                
                  NameNode node = (NameNode) event.getPickedNode();
                 
                  repaintToInitial(node);          
                 
                  parent.callGridUncolorUpdateY(indexOfChild(node));
                  
                   if(overFocus == true) {
                       overFocus = false;
                   }
                  
                   if(overNeighb1 == true) {
                       overNeighb1 = false;
                   }
                
                   if(specialCase == false) {
                       uncolorAll(node);
                       repaintToInitial(node);
                   }
                  
              }
            
          });// end of the addInputEventListener

      } // end of the LineNode constructor
     
      public void printHeight() {
          for(int i=0; i<numNodes;  i++){
              System.out.println("Size of "+i+"  == "+ getChild(i).getFullBoundsReference().getHeight());
          }
      }
     
      public void specialLayoutPartOne(NameNode node, double localLength, double decrement, int NumNodes, int startingIndex, double specialSize) {
         
          NameNode firstNeighb = null;
          NameNode secondNeighb = null;
         
          int firstIndex=0;
          int secondIndex=0;
          int focusIndex=0;
         
          initialHeight = node.getHeight();
          focusIndex = indexOfChild(node);
                     
          if(hasPrevious(node)) {
              firstNeighb = setPreviousName(focusIndex);
              firstIndex = indexOfChild(firstNeighb);
              if(hasPrevious(firstNeighb)) {
                  secondNeighb = setPreviousName(firstIndex);
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
          
          secondNeighbLength = (specialSize- 2*decrement)*initialHeight;
          firstNeighbLength = (specialSize-decrement)*initialHeight;
          halfFocusLength = node.getFullBounds().getHeight()/2;
 
          potentialMin = Math.min(firstIndex, secondIndex);
          minIndex = Math.min(potentialMin, focusIndex);
          potentialMax = Math.max(firstIndex,secondIndex);
          maxIndex = Math.max(potentialMax, focusIndex);
 
         
          //taking care of the layout from the left
          partialLength = (localLength/NumNodes)*(maxIndex+1);
         
          if(partialLength!=0) {
                  partialLength2 = partialLength - secondNeighbLength - firstNeighbLength - halfFocusLength;
                  newSmallHeightLeft = partialLength2/minIndex;
          } else {
                  partialLength2=0;
            }
             
          double incrementSize1 = newSmallHeightLeft/initialHeight;
         
          //set the new size of the numbers; if the new size is
          //less than the minimum visible size set that size to the smallest visible size
              for(int i=startingIndex; i<minIndex; i++) {
                  if(incrementSize1 < MIN_SIZE) {
                      NameNode localNode = (NameNode) getChild(i);
                      localNode.setScale(MIN_SIZE);
                  }
                  else {
                      getChild(i).setScale(incrementSize1);
                 }
              }
              
              //taking care of the layout from the right
              partialLength3 = localLength - partialLength -(halfFocusLength+firstNeighbLength+secondNeighbLength);
              numNodesLeft = NumNodes - (maxIndex+2)-1;
          
             if(numNodesLeft >0){
                 newSmallHeightRight = partialLength3/numNodesLeft;
              }
             else {
                 newSmallHeightRight = 0;
             }
             
              double incrementSize2 = newSmallHeightRight/initialHeight;
             
              for(int i=(maxIndex+3); i<NumNodes; i++) {
                  if(incrementSize2 <= MIN_SIZE) {
                      NameNode localNode = (NameNode) getChild(i);
                      localNode.setScale(MIN_SIZE);
                  }
                  else {
                      getChild(i).setScale(incrementSize2);
                     }
              }

      }
     
        //this is the layout method for the left part of either first or second part
        public void specialLayoutLeft() {
           
            if(overNeighb1 ==true) {
                if(indexN1-2>0) {
                    newSpecialHeight = (length1 - heightN11 - heightN12)/(indexN1 -2);
                   
                    for(int i=0; i< indexN1-2; i++){
                        if(newSpecialHeight/initialHeight == 0) {
                            break;
                        }
                        else {
                               if((newSpecialHeight/initialHeight) < MIN_SIZE) {
                                   getChild(i).setScale(MIN_SIZE);
                               }
                               else {                        
                            	   getChild(i).setScale(newSpecialHeight/initialHeight);
                               }
                        }
                    }
                }
            }
        }
       
     
        public void layoutChildren() {
        	
        //	System.out.println("flagLEFT = "+flagLeft+" FIRSTPart ="+firstPart+" flagRIGHT = "+flagRight+" SECONDPart = "+ secondPart+ " index: "+ indexOfChild(focusNode));
           
            if(specialCase == false) {   
                  
                     if(flagLeft == true) {
                         leftLayoutChildren(0,0, numNodes);
                     }
                    
                     else if(flagRight == true) {
                          rightLayoutChildren(length, 0,numNodes-1);
                    }
                     else {
                        leftLayoutChildren(0, 0,numNodes);
                          
                           //when coming from the focused case to unfocused, without rolling the mouse over the axis
                           if (grid != null) {
                               if(grid.getFocusState() == false) {
                                  changeOthersSize(INITIAL_SIZE);
                                  leftLayoutChildren(0,0,numNodes);
                               }
                           }    
                    }
                 }
                 else {
                    
                     if(firstPart == true) {
                        
                         if(flagLeft == true) {
                             leftLayoutChildren(0, 0, indexN1-1); //indexN1-1 = last index that is nailed
                         }
                         else if(flagRight == true) {
                             rightLayoutChildren(length1,0,indexN1-1);
                             //rightLayoutChildren(length1,indexN1-1,0);
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
                                    // leftLayoutChildren(0,0,numNodes);
                                    
                                     rightLayoutChildren(totalNewLength,indexN2+3,numNodes-1);
                              
                                }
                         }
                         else {
                            
                             leftLayoutChildren(0,0,numNodes);
                         }
                     }
        
                 }
         
         } // end layoutChildren;
       
        public void leftLayoutChildren(double startingOffset, int startingIndex, int endingIndex) {
           
           
            double yOffset = startingOffset;
            double xOffset = 30;
           
            if(overNeighb1 == true) {
                 yOffset -= othersDim* 3/4;
            }
            else {
                if(overFocus == true) {
                    yOffset -= othersDim* 3/5;   
                }
                else {
                    yOffset = startingOffset;   
                }
            }
           
            NameNode each = null;
            for(int i=startingIndex; i<endingIndex; i++) {
                each = (NameNode) getChild(i);
           
                each.setOffset(xOffset, yOffset);
                
                yOffset += each.getFullBounds().getHeight();
                
            }
        }
       
       public void rightLayoutChildren(double endingOffset, int startingIndex, int endingIndex) {
          
    	   	//starting index is actually the last index of all the nodes (i.e the 55th index is now 0)
    	   	//ending index is the index where the right part ends
       	   
           	double yOffset =endingOffset;
           	double xOffset = 30;
          
           	NameNode each = null;
          
           	boolean first = true;
           	for(int i=(endingIndex); i>=(startingIndex); i--) {
              
               each = (NameNode) getChild(i);
               if (first) {
                   yOffset -= each.getFullBounds().getHeight();
                   first = false;
               }
              
               each.setOffset(xOffset, yOffset);
              
               if(specialCase == true && flagRight == true && secondPart == true) {
               	if(i>1) {
                       NameNode localeach = (NameNode) (getChild(i-1));
                       yOffset -= localeach.getFullBounds().getHeight();
                       }
                   else {
                       yOffset -= each.getFullBounds().getHeight();
                  }
               	
               }
               else {
                   if(i>0) {
                   	NameNode localeach = (NameNode) (getChild(i-1));
                   	yOffset -= localeach.getFullBounds().getHeight();
                   }
                   else {
                   	yOffset -= each.getFullBounds().getHeight();
                   }
               }
      
             }
       }
      
    public void decideWhichHalf(NameNode node, int totalIndex) {
          flagLeft  = false;
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
         
          if(secondPart  == true) {
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
 
    public void setInitial() {
        firstPart = false;
        secondPart = false;
        flagLeft = false;
        flagRight = false;
    }

    public void repaintToInitial(PNode node) {
       
        NameNode localNode = (NameNode) node;
       
           if(specialCase == false) {
            colorNextNames(localNode, INITIAL_SIZE, Color.black,
                    INITIAL_SIZE, 0,0, 0);
            colorPreviousNames(localNode, INITIAL_SIZE, Color.black,
                    INITIAL_SIZE, 0,0, 0);

            localNode.setScale(INITIAL_SIZE);
            localNode.setNamePaint(Color.BLACK);
            localNode.setX(0);
           
            localNode.setHeight(BOX_HEIGHT);
            localNode.setWidth(BOX_WIDTH);
            if(hasPrevious(localNode)){
                NameNode prevName = setPreviousName(indexOfChild(node));
                prevName.setPathToRectangle(0,0,200,BOX_HEIGHT);
            }
           
            if(nextExists(localNode)) {
                NameNode nextName = setNextName(indexOfChild(node));
                 nextName.setPathToRectangle(0,0,200,BOX_HEIGHT);
            }
           
           }
           else {
             
            //repaint the OTHERS and its neighbours
            decolorOthers(localNode);
       
            //repaint the FOCUS and its neighbours
            decolorFocusName(localNode);
             
            // repaint the NEIGHBOURS
            decolorNeighbourName(localNode);               
             
          }
      }

    public void colorSpecialNames(NameNode node, Color color, double checkSize1, double checkSize2, double size1, double size2, int nameMargin) {
        if (nextExists(node)) {
            if ((float) getChild(indexOfChild(node) + 1).getScale() == (float) checkSize1) {
                NameNode nextNeighbour = (NameNode) getChild(indexOfChild(node) + 1);
                nextNeighbour.setNamePaint(color);
                nextNeighbour.setScale(size1);
                nextNeighbour.setNameX(nameMargin);
                if (nextExists(nextNeighbour)) {
                    if ((float) getChild(indexOfChild(nextNeighbour) + 1).getScale() == (float) checkSize2) {
                        NameNode nextOther = (NameNode) getChild(indexOfChild(nextNeighbour) + 1);
                        nextOther.setNamePaint(color);
                        nextOther.setScale(size2);
                        nextOther.setNameX(nameMargin);
                    }
                }
            }
        }
        if (hasPrevious(node)) {
            if ((float) getChild(indexOfChild(node) - 1).getScale() == (float) checkSize1) {
                NameNode previousNeighbour = (NameNode) getChild(indexOfChild(node) - 1);
                previousNeighbour.setNamePaint(color);
                previousNeighbour.setScale(size1);
                previousNeighbour.setNameX(nameMargin);
                if (hasPrevious(previousNeighbour)) {
                    if ((float) getChild(indexOfChild(previousNeighbour) - 1).getScale() == (float) checkSize2) {
                        NameNode prevOther = (NameNode) getChild(indexOfChild(previousNeighbour) - 1);
                        prevOther.setNamePaint(color);
                        prevOther.setScale(size2);
                        prevOther.setNameX(nameMargin);
                    }
                }
            }
        }
    }

    public void colorNextNames(NameNode node, double checkSize,
            Color color, double specialSize, int xMargin, int nameMargin, double increment) {
   
        if (nextExists(node)) {
            if ((float) getChild(indexOfChild(node) + 1).getScale() != (float) checkSize) {
                NameNode nextNeighbour = (NameNode) getChild(indexOfChild(node) + 1);
                nextNeighbour.setNamePaint(color);
                nextNeighbour.setScale(specialSize);
                nextNeighbour.setX(xMargin);
                nextNeighbour.setNameX(nameMargin);
                if (nextExists(nextNeighbour)) {
                    if ((float) getChild(indexOfChild(nextNeighbour) + 1).getScale() != (float) checkSize) {
                        NameNode nextOther = (NameNode) getChild(indexOfChild(nextNeighbour) + 1);
                        nextOther.setNamePaint(color);
                        nextOther.setScale(specialSize - increment);
                        nextOther.setX(xMargin);
                        nextOther.setNameX(nameMargin);
                    }
                }
            }
        }
    }

    public void colorPreviousNames(NameNode node, double checkSize,
            Color color, double specialSize, int xMargin, int nameMargin, double increment) {
        if (hasPrevious(node)) {
            if ((float) getChild(indexOfChild(node) - 1).getScale() != (float) checkSize) {
                NameNode previousNeighbour = (NameNode) getChild(indexOfChild(node) - 1);
                previousNeighbour.setNamePaint(color);
                previousNeighbour.setScale(specialSize);
                previousNeighbour.setX(xMargin);
                previousNeighbour.setNameX(nameMargin);
                if (hasPrevious(previousNeighbour)) {
                    if ((float) getChild(
                            indexOfChild(previousNeighbour) - 1).getScale() != (float) checkSize) {
                        NameNode previousOther = (NameNode) getChild(indexOfChild(previousNeighbour) - 1);
                        previousOther.setNamePaint(color);
                        previousOther.setScale(specialSize - increment);
                        previousOther.setX(xMargin);
                        previousOther.setNameX(nameMargin);
   
                    }
                }
            }
        }
    }

    public void colorOtherNames(NameNode node, double increment) {
        if ((float) node.getScale() == (float) OTHERS_SIZE) {
   
            node.setNamePaint(Color.red);
            
            node.setScale(SPECIAL_SIZE + increment);
            node.setNameX(-15);
            node.setWidth(120);
           
            repaint();
            colorRed = true;
   
           
            colorNextNames(node,NEIGHBOUR_SIZE,Color.blue,SPECIAL_SIZE,0,0,increment);
           
            colorPreviousNames(node,NEIGHBOUR_SIZE,Color.blue,SPECIAL_SIZE,0,0,increment);
           
            if(hasPrevious(node)){
                NameNode prevName = setPreviousName(indexOfChild(node));
                prevName.setWidth(SPECIAL_BOX_WIDTH);
                prevName.setNameX(-10);
            }
           
           
            if(nextExists(node)) {
                NameNode nextName = setNextName(indexOfChild(node));
                 nextName.setWidth(SPECIAL_BOX_WIDTH);
                 nextName.setNameX(-10);
            }
           
            //coloring the neighbour and the focus Name in blue if that's the case
            if(hasPrevious(node)){
                NameNode previous = (NameNode) getChild(indexOfChild(node)-1);
               
                if((float)getChild(indexOfChild(node)-1).getScale() == (float)NEIGHBOUR_SIZE) {
                    previous.setNamePaint(Color.blue);
                    previous.setWidth(165);
                    previous.setNameX(nameOffset);
                   
                    //color the focus
                    NameNode previousPrevious = (NameNode) getChild(indexOfChild(previous)-1);
                    previousPrevious.setNamePaint(Color.blue);   
                }
               
                if(hasPrevious(previous)){
                    NameNode previousPrevious = (NameNode) getChild(indexOfChild(previous)-1);
                    if((float) previousPrevious.getScale() == (float) NEIGHBOUR_SIZE) {
                        previousPrevious.setNamePaint(Color.BLUE);
                    }
                }
            }
           
            if(nextExists(node)) {
                NameNode next = (NameNode) getChild(indexOfChild(node)+1);
                if((float)getChild(indexOfChild(node)+1).getScale() == (float)NEIGHBOUR_SIZE) {
                    next.setNamePaint(Color.blue);
                    next.setWidth(165);
                    next.setNameX(nameOffset);
                   
                    //color the focus
                    NameNode nextNext = (NameNode) getChild(indexOfChild(next)+1);
                    nextNext.setNamePaint(Color.blue);
                }
                if(nextExists(next)){
                    NameNode nextNext = (NameNode) getChild(indexOfChild(next)+1);
                    if((float) nextNext.getScale() == (float) NEIGHBOUR_SIZE) {
                        nextNext.setNamePaint(Color.blue);
                    }
                }
            }   
        }
    }

   
    public void  decolorOthers(NameNode node) {
       
           if (colorRed == true) {

            node.setNamePaint(Color.black);
            node.setScale(OTHERS_SIZE);

            colorPreviousNames(node, NEIGHBOUR_SIZE,Color.black, OTHERS_SIZE, 0,0, 0);

            colorNextNames(node, NEIGHBOUR_SIZE, Color.black, OTHERS_SIZE, 0, 0,0);

            colorRed = false;
           
	        node.setWidth(BOX_WIDTH);
	        node.setNameX(0);
           
            if(hasPrevious(node)){
                NameNode prevName = setPreviousName(indexOfChild(node));
                prevName.setWidth(BOX_WIDTH);
               
            }
           
           
            if(nextExists(node)) {
                NameNode nextName = setNextName(indexOfChild(node));
                 nextName.setWidth(BOX_WIDTH);
            }

        }
       
        //coloring the neighbour and the focus Name in blue if that's the case
        if(hasPrevious(node)){
            NameNode previous = (NameNode) getChild(indexOfChild(node)-1);
            if((float)getChild(indexOfChild(node)-1).getScale() == (float)NEIGHBOUR_SIZE) {
                previous.setNamePaint(Color.black);
                previous.setWidth(165);
                               
                //color the focus
                if(hasPrevious(previous)){
                	NameNode previousPrevious = (NameNode) getChild(indexOfChild(previous)-1);
                	previousPrevious.setNamePaint(Color.black);
                }
            }
            if(hasPrevious(previous)){
                NameNode previousPrevious = (NameNode) getChild(indexOfChild(previous)-1);
                if((float) previousPrevious.getScale() == (float) NEIGHBOUR_SIZE) {
                    previousPrevious.setNamePaint(Color.black);
                }
            }
        }
       
        if(nextExists(node)) {
            NameNode next = (NameNode) getChild(indexOfChild(node)+1);
            if((float)getChild(indexOfChild(node)+1).getScale() == (float)NEIGHBOUR_SIZE) {
                next.setNamePaint(Color.black);
                next.setWidth(165);
               
                //color the focus
                if(nextExists(next)){
                	NameNode nextNext = (NameNode) getChild(indexOfChild(next)+1);
                	nextNext.setNamePaint(Color.black);
                }
            }
            if(nextExists(next)){
                NameNode nextNext = (NameNode) getChild(indexOfChild(next)+1);
                if((float) nextNext.getScale() == (float) NEIGHBOUR_SIZE) {
                    nextNext.setNamePaint(Color.black);
                }
            }
        }
    }
    public void uncolorAll(NameNode localNode){
       
        if(specialCase == false) {
            for(int i = 0; i<getChildrenCount(); i++) {
                localNode = (NameNode) getChild(i);
                localNode.setNamePaint(Color.BLACK);
                localNode.setScale(INITIAL_SIZE);
                localNode.setNameX(0);
            }
        }
        else {
            for(int i = 0; i<getChildrenCount(); i++) {
                localNode = (NameNode) getChild(i);
                if((float) localNode.getScale() == (float) FOCUS_SIZE) {
                    for(int j=0; j<i-1; j++) {
                        NameNode node = (NameNode)  getChild(j);
                        node.setNamePaint(Color.BLACK);
                        node.setScale(OTHERS_SIZE);
                    }
                    for(int j=i+2; j< getChildrenCount(); j++) {
                        NameNode node = (NameNode)  getChild(j);
                        node.setNamePaint(Color.BLACK);
                        node.setScale(OTHERS_SIZE);
                    }
                }
            }
        }

    }

    public void colorNeighbourName(NameNode node) {
        if((float) node.getScale() == (float) NEIGHBOUR_SIZE) {
           
            colorNeighbRed = true;
           
            node.setNamePaint(Color.RED);
            node.setWidth(165);
           
            double difference = (NEIGHBOUR_SIZE - INITIAL_SIZE)/3;
            int index = indexOfChild(node);
           
            if(hasPrevious(node)){
                NameNode prevName = setPreviousName(indexOfChild(node));
                prevName.setNameX(nameOffset);
               
            }
           
            if(nextExists(node)) {
                NameNode nextName = setNextName(indexOfChild(node));
                 nextName.setNameX(nameOffset);
            }
           
            //if it's the neighbour above
            if((index<getChildrenCount()-1)&&((float) getChild(index+1).getScale() == (float) FOCUS_SIZE)) {
               
            	colorSpecialNames(node,Color.blue,OTHERS_SIZE,OTHERS_SIZE,NEIGHBOUR_SIZE-difference,NEIGHBOUR_SIZE-2*difference,-10);

                if(hasPrevious(node)) { 
                	NameNode neighb11 = (NameNode) getChild(indexOfChild(node)-1);
                    heightN11 = neighb11.getFullBoundsReference().getHeight();
                    if(hasPrevious(neighb11)) {
                        NameNode neighb12 = (NameNode) getChild(indexOfChild(node)-2);
                        heightN12 = neighb12.getFullBoundsReference().getHeight();
                    }
                }

                NameNode  focus = (NameNode) getChild(index+1);
                focus.setNamePaint(Color.blue);
                if(nextExists(focus)) {
                    NameNode  rightNeighb = (NameNode) getChild(index+2);
                    rightNeighb.setNamePaint(Color.blue);
                    
                }
            }
           
            //if it's the neighbour below
            if((index-1 >=0)&&(float) getChild(index-1).getScale() == (float) FOCUS_SIZE) {
               
            	colorSpecialNames(node,Color.blue,OTHERS_SIZE,OTHERS_SIZE,NEIGHBOUR_SIZE-difference,NEIGHBOUR_SIZE-2*difference,-10);
           
                NameNode focus = (NameNode) getChild(index-1);
                focus.setNamePaint(Color.blue);
                if(hasPrevious(focus)){
                    NameNode leftNeighb = (NameNode) getChild(index-2);
                    leftNeighb.setNamePaint(Color.blue);
                }
            }
           
           
        }
    }
   
    public void decolorNeighbourName(NameNode node) {
        if (colorNeighbRed == true) {
            node.setNamePaint(Color.black);
            node.setScale(NEIGHBOUR_SIZE);
            colorNeighbRed = false;
           
            double difference = (NEIGHBOUR_SIZE - INITIAL_SIZE)/3;
           
            //if it's the left neighbour
            if((indexOfChild(node)+1 <getChildrenCount())&& ((float) getChild(indexOfChild(node)+1).getScale() == (float) FOCUS_SIZE)) {
               
            	colorSpecialNames(node,Color.black,NEIGHBOUR_SIZE-difference,NEIGHBOUR_SIZE-2*difference,OTHERS_SIZE,OTHERS_SIZE,0);
                NameNode  focus = (NameNode) getChild(indexOfChild(node)+1);
                focus.setNamePaint(Color.black);
                if(nextExists(focus)) {
                    NameNode  rightNeighb = (NameNode) getChild(indexOfChild(node)+2);
                    rightNeighb.setNamePaint(Color.black);
                }
            }
               
            //if it's the right neighbour
            if((indexOfChild(node)-1>0)&&((float) getChild(indexOfChild(node)-1).getScale() == (float) FOCUS_SIZE)) {
               
            	colorSpecialNames(node,Color.black,NEIGHBOUR_SIZE-difference,NEIGHBOUR_SIZE-2*difference,OTHERS_SIZE,OTHERS_SIZE,0);
                NameNode focus = (NameNode) getChild(indexOfChild(node)-1);
                focus.setNamePaint(Color.black);
                if(hasPrevious(focus)){
                    NameNode leftNeighb = (NameNode) getChild(indexOfChild(node)-2);
                    leftNeighb.setNamePaint(Color.black);
                }
            }       
        }
    }

    public void colorFocusName(NameNode node) {
        if ((float) node.getScale() == (float) FOCUS_SIZE) {
            node.setNamePaint(Color.RED);
           
            focusColorRed = true;
           
            colorSpecialNames(node,Color.blue,NEIGHBOUR_SIZE,OTHERS_SIZE,NEIGHBOUR_SIZE,SPECIAL_SIZE,-10);
           
            //set the position of the previous names in their boxes
            if(hasPrevious(node)){
                NameNode prevName = setPreviousName(indexOfChild(node));
                prevName.setNameX(nameOffset);
                if(hasPrevious(prevName)){
                	setPreviousName(indexOfChild(prevName)).setWidth(SPECIAL_BOX_WIDTH);
                }
               
            }
           
            //set the position of the previous names in their boxes
            if(nextExists(node)) {
                NameNode nextName = setNextName(indexOfChild(node));
                 nextName.setNameX(nameOffset);
                 nextName.setWidth(SPECIAL_BOX_WIDTH);
                 if(nextExists(nextName)) {
                	 setNextName(indexOfChild(nextName)).setWidth(SPECIAL_BOX_WIDTH);
                 }
            }
       
        }
    }
   
   
    public void decolorFocusName(NameNode localNode) {
        if (focusColorRed == true) {
            localNode.setNamePaint(Color.BLACK);
            localNode.setScale(FOCUS_SIZE);
            focusColorRed = false;
           
            colorSpecialNames(localNode,Color.black,NEIGHBOUR_SIZE,SPECIAL_SIZE,NEIGHBOUR_SIZE,OTHERS_SIZE,0);
           
            if(hasPrevious(localNode)){
                NameNode prevName = setPreviousName(indexOfChild(localNode));
                prevName.setNameX(nameOffset);
                if(hasPrevious(prevName)){
                	setPreviousName(indexOfChild(prevName)).setWidth(BOX_WIDTH);
                }
            }
           
           
            if(nextExists(localNode)) {
                NameNode nextName = setNextName(indexOfChild(localNode));
                 nextName.setNameX(nameOffset);
                 if(nextExists(nextName)) {
                	 setNextName(indexOfChild(nextName)).setWidth(BOX_WIDTH);
                 }
            }
           }
    }
   
     public void decolorUnfocusedCase(PNode node) {
         NameNode newNode = (NameNode) node;
       
         if(nextExists(newNode)) {
                NameNode  nextNeighb = (NameNode) getChild(indexOfChild(node)+1);
                nextNeighb.setNamePaint(Color.black);
                nextNeighb.setScale(INITIAL_SIZE);
                if(nextExists(nextNeighb)) {
                    NameNode nextOther = (NameNode) getChild(indexOfChild(nextNeighb)+1);
                    nextOther.setNamePaint (Color.black);
                    nextOther.setScale(INITIAL_SIZE);
               }
         }
         else {
                 if(hasPrevious(newNode)) {
                     NameNode  previousNeighb = (NameNode) getChild(indexOfChild(node)-1);
                     previousNeighb.setNamePaint(Color.black);
                     previousNeighb.setScale(INITIAL_SIZE);
                     if(hasPrevious(previousNeighb)) {
                         NameNode previousOther = (NameNode) getChild(indexOfChild(previousNeighb)-1);
                         previousOther.setNamePaint(Color.black);
                         previousOther.setScale(INITIAL_SIZE);
                }
                 }
      }
      
      
      if(hasPrevious(newNode)) {
              NameNode  previousNeighb = (NameNode) getChild(indexOfChild(node)-1);
              previousNeighb.setNamePaint(Color.black);
              previousNeighb.setScale(INITIAL_SIZE);
              if(hasPrevious(previousNeighb)) {
                   NameNode previousOther = (NameNode) getChild(indexOfChild(previousNeighb)-1);
                   previousOther.setNamePaint(Color.black);
                   previousOther.setScale(INITIAL_SIZE);
               }
      }
      else {
               if(nextExists(newNode)) {
                   NameNode  nextNeighb = (NameNode) getChild(indexOfChild(node)+1);
                   nextNeighb.setNamePaint(Color.black);
                   nextNeighb.setScale(INITIAL_SIZE);
                   if(nextExists(nextNeighb)) {
                       NameNode nextOther = (NameNode) getChild(indexOfChild(nextNeighb)+1);
                       nextOther.setNamePaint(Color.black);
                       nextOther.setScale(INITIAL_SIZE);
                   }
               }
      }
    
          newNode.setScale(INITIAL_SIZE);
          newNode.setNamePaint(Color.BLACK );
       
      
     }
     
     
      public void focusTheNeighbours(NameNode focusNode) {

                      NameNode previousName = null;
                      NameNode nextName = null;

                      int pickedIndex = indexOfChild(focusNode);

                      previousName = setPreviousName(pickedIndex);
                      nextName = setNextName(pickedIndex);

                      if (hasPrevious(focusNode) == true) {
                          focusNeighbour(previousName);
                          leftOfFocus = true;
                       }
                      else {
                          leftOfFocus = false;
                      }

                      if (nextExists(focusNode) == true) {
                          focusNeighbour(nextName);
                          rightOfFocus = true;
                      }
                      else {
                          rightOfFocus = false;
                      }

      }

      
    public boolean nextExists(NameNode node) {
          PNode newNode = (PNode) node;
          if((indexOfChild(newNode)+1)  < getChildrenCount()) {
              return true;
          }
          else {
              return false;
          }
        
      }
   
    public void changeOthersSize(double size) {
              for(int i=0; i<=getChildrenCount()-1; i++) {
                  getChild(i).setScale(size);
              }
             specialCase = true;
    }

    public void callMouseClick(int index) {
            PNode node = this.getChild(index);
            mouseClickAction(node);
    }

    public void mouseClickAction(PNode node) {
          
    	   NameNode newNode = null;
   
           changeOthersSize(INITIAL_SIZE);
   
           defocusingNames(focusNode);
           specialCase = false;
          
           if(node.getClass().equals(NameNode.class)) {
   
               NameNode  Name = (NameNode) node;
               newNode = Name;
   
               if(newNode != null) {
   
                   if(newNode == focusNode) {
                       focusNode = null;
                    
                   }
                   else {
   
                       changeOthersSize(OTHERS_SIZE);
                     
                       focusNode = newNode;
   
                       focusTheNeighbours(focusNode);
   
                       focusName(Name);
   
                   }
               }
           }
   
      }
     public void unfocusAll() {
    	 if (focusNode != null) {
              defocusingNames(focusNode);
              changeOthersSize(INITIAL_SIZE);
              specialCase = false;
           }
        }

       public void defocusingNames(NameNode focusNode) {
           if(focusNode != null) {

               focused = true;
               deFocusName(focusNode);

               NameNode previousName = null;
               NameNode nextName = null;

               int pickedIndex = indexOfChild(focusNode);

               previousName = setPreviousName(pickedIndex);
               nextName = setNextName(pickedIndex);

               neighbourFocused = true;
               if (hasPrevious(focusNode) == true) {
                   deFocusNeighbour(previousName);
               }
               neighbourFocused = true;
               if (nextExists(focusNode) == true) {
                   deFocusNeighbour(nextName);
               }

           }
           setInitial();
       }

    public NameNode theNext(NameNode focusNode) {

              int pickedIndex = indexOfChild(focusNode);

              if( getChildrenCount()> pickedIndex + 1) {
                  focusNode = (NameNode) getChild(pickedIndex +1);
              }
              return focusNode;
          }


      public NameNode thePrevious(NameNode focusNode) {
         
              int pickedIndex = indexOfChild(focusNode);
             
              if(0 <= pickedIndex-1 ) {
                  focusNode = (NameNode) getChild(pickedIndex -1);
              }
             
              return focusNode;
      }

      public NameNode setNextName(int index) {
           NameNode nextName;

           if(getChildrenCount()> index + 1) {
                   nextName = (NameNode) getChild(index + 1);
           }
           else {
                nextName =null;
           }
          
           return nextName;
      }

      public NameNode setPreviousName(int index) {
          NameNode previousName;

          if (0 <= index -1 ) {
               previousName = (NameNode) getChild(index -1);
                   itIsANeighbour = true;
           }
           else {
               previousName = null;
               itIsANeighbour = false;
           }
          return previousName;
      }

      public void focusName(NameNode focusName) {

              focused = true;
                  if(focusName != null) {
                      focusName.setScale(FOCUS_SIZE);
                  }
                 
                  // hack! arrange the name and the box width
                  focusName.setX(-80);
                  focusName.setNameX(nameOffset);
                  focusName.setRectHeight(100);
                  focusName.setWidth(105);
                  if(specialCase == true) {
                      focusName.setNameY(40);
                  }
      }

      public void deFocusName(NameNode focusName) {

              focused = false;
              if (focusName != null) {
            	  focusName.setScale(INITIAL_SIZE);
              }
             
              // hack! arrange the name and the box width
              focusName.setX(nameOffset);
              focusName.setNameX(0);
              focusName.setRectHeight(BOX_HEIGHT);
              focusName.setWidth(BOX_WIDTH);
           
              focusName.setNameY(0);
             
      }


      public void focusNeighbour(NameNode focusName) {

             neighbourFocused = true;
             if (focusName != null) {
                  focusName.setScale(NEIGHBOUR_SIZE);
             }
             
             //hack! arrange the name and the box width
             focusName.setNameX(nameOffset);
             focusName.setRectHeight(88);
             focusName.setWidth(165);
             focusName.setNameY(40);
            
      }


      public void deFocusNeighbour (NameNode focusName) {
              neighbourFocused = false;
              if(focusName != null) {
                  focusName.setScale(INITIAL_SIZE);
              }
              //hack! arrange the name and the box width
              focusName.setNameX(0);
              focusName.setRectHeight(BOX_HEIGHT);
              focusName.setWidth(BOX_WIDTH);
              focusName.setNameY(0);
             
       }


      public boolean hasPrevious(NameNode focusName) {

          int pickedIndex = indexOfChild(focusName);

          if (pickedIndex != 0) {
              return true;
          }

          else {
              return false;
          }
      }

  } // end of the LineNode class



  public class NameNode extends PPath {

          PText name;
         
      public NameNode(String i) {
       
        super();
       
        //setStrokePaint(Color.white);
      
        setPathToRectangle(0,0,BOX_WIDTH,BOX_HEIGHT);
       
        name = new PText();
      
    
       stringName = String.valueOf(i);
       name.setText(stringName);
 
       //set the name inside the box
       name.translate(20,0);
      
       PBounds b = getFullBounds();
       b.inset(0,0);
      
       //set the x coordinate for the box; set some space
       setX(-20);
      
       setBounds(b);
          
       addChild(name);
      
       setPaint(Color.white);
    		
       setChildrenPickable(false);
  
      }//end of the NameNode constructor

     
      public String getNameText() {
          return name.getText();
      }
      
      public void setBoxHeight(double size) {
         
      }
      
      public void setNamePaint(Color c) {
           name.setTextPaint(c);
      }
    
      public void setNameX(int size) {
          name.setX(size);
      }
     
      public void setNameY(int size) {
            name.setY(size);
        }
      public void setRectHeight(float height) {
          setPathToRectangle(0,0,200,height);
      }
     
     
  } // end of the NameNode class
 
  public VerticalAxisFisheye(SpecCanvas parent, ArrayList targetNames) {
      this.parent = parent;

      layoutNode = new PNode();
      this.targetNames = targetNames;
      lineNode = new LineNode();
      addChild(lineNode);

  }

      public void callUnFocus() {
          lineNode.unfocusAll();
      }
 
      public void setGridNode(GridNode grid) {
          this.grid = grid;
      }

}
