/*
 * GridNode.java
 * Author: Elena Caraba
 * Create Date: Jun 8, 2007
 *
 */
package gui.graph;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JFrame;

import models.SpecTable;
import models.Spectrum;
import models.Target;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import gui.DetailsPanel;
import gui.graph.HorizontalAxisFisheye.NumberNode;

/**
 * @author ecaraba
 * Created on Jun 8, 2007
 * GridNode is the container node which lays out the Spectra nodes
 * in a grid layout.
 */
public class GridNode extends PPath {

  // public static final double midWidth = 95.0;
  // public static final double midHeight = 74.0;
   //public static final double focusWidth = 180.0;
   //public static final double focusHeight = 173.0;
   public static final double midWidth = 65.25;
   public static final double midHeight = 52.0;
   public static final double focusWidth = 120.5;
   public static final double focusHeight = 122.2;
   public static final double xMargin = 12.5;
   public static final double yMargin = 5.35;
   public static final double xFocusMargin = 15;
   public static final double yFocusMargin = 9.66;
   public static final int xGridMargin = 10;
   public static final int yGridMargin = 0;
   public static int DEFAULT_ANIMATION_MILLIS = 50;

   public ArrayList targetNames;

   private int numRows = 4;
   private int numCols = 3;
   private int lowPhaseValue = 15;
   private Integer focusX;
   private Integer focusY;

   private int totalNumberOfTargets=0;

   private boolean aboveNFocus = false;
   private boolean belowNFocus = false;
   private boolean rightNFocus = false;
   private boolean leftNFocus = false;
   private SpecNode[][] matrix;
   public SpecNode focusNode;
   final private SpecCanvas parent;
   private SpecTable specTable;

   private Integer oldX = null;
   private Integer oldY = null;
protected boolean specClicked = false;



   public void callUpdateAxis(int indexX, int indexY) {
       parent.callAxisUpdateY( indexY);
       parent.callAxisUpdateX(indexX);
   }

   public GridNode(final SpecCanvas parent) {
       this.parent = parent;

       this.setStrokePaint(null);
       setPathToRectangle(GridNode.xGridMargin, GridNode.yGridMargin, 900, 670);

       processFile();


       this.addInputEventListener(new PBasicInputEventHandler() {

           public void mouseClicked(PInputEvent event) {
               PNode node = event.getPickedNode();
               int index = indexOfChild(node);
               mouseClickAction(node, index, event);
               specClicked  = true;
           }

           public void mouseMoved(PInputEvent event) {
               SpecNode node = (SpecNode) event.getPickedNode();

               mouseColorActionX(node.getXIndex());
               mouseColorActionY(node.getYIndex());

           }

           public void mouseExited(PInputEvent event) {
               SpecNode node = (SpecNode) event.getPickedNode();
               mouseUncolorActionX(node.getXIndex());
               mouseUncolorActionY(node.getYIndex());
           }
       });
   }

       public void mouseClickAction(PNode node,  int z, PInputEvent event) {


           SpecNode newSpec = null;


           //check if clicked on same event. If so, open the details
           if (focusNode != null) {
               if ((event != null) && ((event.getModifiers() & InputEvent.BUTTON3_MASK)
                       != InputEvent.BUTTON3_MASK)) {
                   if (node.getClass().equals(SpecNode.class)) {
                       SpecNode spec = (SpecNode) node;
                       if (focusNode == spec) {
                           JFrame details = new DetailsPanel(spec.getTarget().getTargetName(),spec.getSpec().getRunNumber(), spec.getSpec().getPhase());
                           details.setLocation(parent.getX() + (parent.getWidth()/2 - details.getWidth()/2), parent.getY());
                           return;
                       }
                   }
               }
           }

                       
           deFocusGrid();

               if ((event == null) || ((event.getModifiers() & InputEvent.BUTTON3_MASK)
                       != InputEvent.BUTTON3_MASK)) {


                   //focus the new node
                   if (node.getClass().equals(SpecNode.class)) {
                       SpecNode spec = (SpecNode) node;
                       newSpec = spec;

                       if (newSpec != null) {
                           if (newSpec == focusNode) {
                               focusX = null;
                               focusY = null;
                               focusNode = null;
                           }
                           else {

                               focusNode = newSpec;

                               //System.out.println("focus node is " + newSpec.getTarget().getTargetName() + " : " + newSpec.getSpec().getPhase().intValue());

                               int x = spec.getXIndex();
                               int y = spec.getYIndex();

                               spec.focusImage();
                               focusX = new Integer(x);
                               focusY = new Integer(y);

                               //get the neighbors to be focused
                               SpecNode neighbor = null;
                               SpecNode neighbor1 = null;
                               SpecNode neighbor2 = null;
                               SpecNode neighbor3 = null;

                               if ((x+1) < numCols) {
                                   neighbor = matrix[x+1][y];
                               }
                               if ((x-1) >= 0) {
                                   neighbor1 = matrix[x-1][y];
                               }
                               if ((y+1) < numRows) {
                                   neighbor2 = matrix[x][y+1];
                               }
                               if ((y-1) >= 0) {
                                   neighbor3 = matrix[x][y-1];
                               }


                               //focuses the neighbors
                               //these flags are a bit deprecated now, but will leave them
                               //just in case
                               if ((neighbor != null)) {
                                   neighbor.focusNeighborImage();
                                   rightNFocus = true;
                               }

                               if ((neighbor1 != null)) {
                                   leftNFocus = true;
                                   neighbor1.focusNeighborImage();

                               }
                               if ((neighbor2 != null)) {
                                   belowNFocus = true;
                                   neighbor2.focusNeighborImage();
                               }
                               if ((neighbor3 != null)) {
                                   aboveNFocus = true;
                                   neighbor3.focusNeighborImage();
                               }




                               if (oldX !=null) {
                                   if ((oldX.intValue()-lowPhaseValue) != (x-lowPhaseValue)) {

                                       parent.callAxisUpdateX(x-lowPhaseValue);
                                   }
                               }
                               else {
                                   parent.callAxisUpdateX(x-lowPhaseValue);
                               }
                               if (oldY !=null) {
                                   if (oldY.intValue() != (y)) {

                                        parent.callAxisUpdateY(y);
                                   }
                               }
                               else {
                                   
                                   parent.callAxisUpdateY(y);
                               }


                           }
                       }
                   }
               } else { //right clicked
                   parent.callAxisUnFocusX();
                   parent.callAxisUnFocusY();
               }
       }

       public void deFocusGrid(){

               //defocus the old node
               if (focusNode != null) {
                   int x = focusNode.getXIndex();
                   int y = focusNode.getYIndex();

                   focusNode.deFocusImage();

                   //get the neighbors to be focused
                   SpecNode neighbor = null;
                   SpecNode neighbor1 = null;
                   SpecNode neighbor2 = null;
                   SpecNode neighbor3 = null;

                   aboveNFocus = false;
                   belowNFocus = false;
                   rightNFocus = false;
                   leftNFocus = false;

                   if ((x+1) < numCols) {
                       neighbor = matrix[x+1][y];
                   }
                   if ((x-1) >= 0) {
                       neighbor1 = matrix[x-1][y];
                   }
                   if ((y+1) < numRows) {
                       neighbor2 = matrix[x][y+1];
                   }
                   if ((y-1) >= 0) {
                       neighbor3 = matrix[x][y-1];
                   }

                   if (neighbor != null) {
                       neighbor.deFocusNeighborImage();
                   }
                   if (neighbor1 != null) {
                       neighbor1.deFocusNeighborImage();
                   }
                   if (neighbor2 != null) {
                       neighbor2.deFocusNeighborImage();
                   }
                   if (neighbor3 != null) {
                       neighbor3.deFocusNeighborImage();
                   }


                   oldX = focusX;
                   oldY = focusY;


                   focusX = null;
                   focusY = null;
                   focusNode = null;


               }
}

	  public void unfocusGrid() {
		  if(focusNode != null){
			  deFocusGrid();
	      }
	  }

	
	  /*
	   * color the corresponding column when rolling over the phase
	   */
       public void mouseColorActionX(int indexColumn) {
               for(int i=0; i<numRows; i++){
                       SpecNode localNode = matrix[indexColumn][i];
                       if (!localNode.isEmpty()) {
                           if (localNode.isFocused() || localNode.isNeighborFocused()) {
                              
                                       localNode.setPaint(Color.lightGray);

                           }
                           else {
                                         localNode.setPaint(Color.red);
                           }
                       }
               }
       }


 	  /*
 	   * uncolor the corresponding column when the roll-over the phase is done
 	   */
       public void mouseUncolorActionX(int indexColumn) {
               for(int i=0; i<numRows; i++){
                       SpecNode localNode = matrix[indexColumn][i];
                       if (!localNode.isEmpty()) {
                    	   localNode.setPaint(Color.gray);
                       }
               }
       }


 	  /*
 	   * color the corresponding row when rolling over the target names
 	   */
       public void mouseColorActionY(int indexRow) {
               for(int i=0; i<numCols; i++){
                       SpecNode localNode = matrix[i][indexRow];
                       if (!localNode.isEmpty()) {
                               if (localNode.isFocused() || localNode.isNeighborFocused()) {
                            	  localNode.setPaint(Color.lightGray);
                               }
                           else {
                                  localNode.setPaint(Color.red);
                           }
                       }
               }
       }


 	  /*
 	   * uncolor the corresponding row when roll-over the target names is done
 	   */
       public void mouseUncolorActionY(int indexRow) {
               for(int i=0; i<numCols; i++){
                       SpecNode localNode = matrix[i][indexRow];
                       if (!localNode.isEmpty()) {
                           localNode.setPaint(Color.gray);
                       }
               }
       }

       public void callClickAction(int index) {
           mouseClickAction(getChild(index), 0, null);
       }

       public void callColorActionX(int indexColumn) {
               mouseColorActionX(indexColumn);
       }

       public void callUncolorActionX(int indexColumn) {
               mouseUncolorActionX(indexColumn);
       }

       public void callColorActionY(int indexRow) {
               mouseColorActionY(indexRow);
       }

       public void callUncolorActionY(int indexRow) {
    	   mouseUncolorActionY(indexRow);
       }
/*
 * Lays out the pnode in a grid layout
 * Before actually laying out each node, it firsts computes
 * the offset to the left and above the node. After laying out the
 * node, it computes the offset below and to the right of the node.
 * The rightNFocus, leftNFocus, belowNFocus, aboveNFocus flags are
decprecated now, but I will
 * leave them in case. They were used to treat cases where the focus
node didn't have a neighbor
 * differently from when it did have the neighbor
 *
 * (non-Javadoc)
 * @see edu.umd.cs.piccolo.PNode#layoutChildren()
 */
       public void layoutChildren() {

           int cols = numCols;
           int rows = numRows;
           int colCount = 0;
           int rowCount = 0;
           int nodeCount = 0;

               double xOffset = GridNode.xGridMargin + 10;

               double[] yOffsets = new double[cols];

               int j = 0;

               Iterator i = getChildrenIterator();
               while (i.hasNext()) {

                   j++;

                   SpecNode each = (SpecNode) i.next();
                   PBounds bounds = each.getFullBoundsReference();

                   //compute the offset to the left of the node
                   //compute the X offset
                   if (focusX != null) {
                       if (colCount == focusX.intValue()) { //this takes care of the focused col and row

                           if (!each.isFocused()) {
                               if (each.isNeighborFocused()) {
                                   xOffset += (GridNode.focusWidth/4);
                               }
                               else {
                                   xOffset += (GridNode.focusWidth/2);
                               }
                           }
                       }  else {
                           if ((colCount == (focusX.intValue() + 1))) {

                               if (!each.isNeighborFocused() && rightNFocus) {
                                   xOffset += (GridNode.midWidth/2);
                               }
                           }
                           if (colCount == (focusX.intValue() - 1)) { //this takes care of others in the neighbor cols
                                   if (!each.isNeighborFocused() && leftNFocus) {
                                   xOffset += (GridNode.midWidth/2);
                               }
                               }
                       }

                   }

                   //compute the offset above the node
                   //compute the Y offset
                   if (focusY != null) {
                       if (rowCount == focusY.intValue()) {
                           if (!each.isFocused()) {
                               if (each.isNeighborFocused()) {
                                   yOffsets[colCount] += (GridNode.focusHeight/4);
                               }
                               else {
                                   yOffsets[colCount] += (GridNode.focusHeight/2);
                               }
                           }
                       } else {
                           if ((rowCount == (focusY.intValue() + 1))) {
                               if (!each.isNeighborFocused() && belowNFocus) {
                                   yOffsets[colCount] += (GridNode.midHeight/2);
                               }
                           }
                       if (rowCount == (focusY.intValue() - 1)) { //neighbor row
                           if (!each.isNeighborFocused() && aboveNFocus) {
                               yOffsets[colCount] += (GridNode.midHeight/2);
                           }
                       }
                       }
                   }


                   //System.out.println("xOffset " + xOffset + " width " + each.getFullBounds().getWidth());

                   //compute the node size
                   double width = 5;
                   double height = 5;
                   if (each.isFocused()) {
                       width = GridNode.focusWidth;
                       height = GridNode.focusHeight;
                   }
                   else if (each.isNeighborFocused()) {
                       width = GridNode.midWidth;
                       height = GridNode.midHeight;
                   }

                   //set the node offset
                   if (focusX != null) {
                       if (rowCount == 4) {
                           double temp = xOffset + (colCount * xFocusMargin) - each.getX();
                       //    System.out.println("xOffset " + temp + " width " + each.getFullBounds().getWidth());
                       }
                       each.setOffset(xOffset + (colCount * xFocusMargin) - each.getX(), yOffsets[colCount] + (rowCount * yFocusMargin) - each.getY() + 10);
                   }
                   else {
                       if (rowCount == 4) {
                           double temp = xOffset + (colCount * xMargin) - each.getX();
                       //    System.out.println("xOffset " + temp + " width " + each.getFullBounds().getWidth());
                       }
                         each.setOffset(xOffset + (colCount * xMargin) - each.getX(), yOffsets[colCount] + (rowCount * yMargin) - each.getY() + 10);
                   }

                   //compute the offset below the node
                   //compute the Y offset
                   if (focusY != null) {

                       if (rowCount == focusY.intValue()) {
                           if (each.isFocused()) {
                               yOffsets[colCount] += GridNode.focusHeight;
                           }
                           else if (each.isNeighborFocused()){
                               yOffsets[colCount] += 3*GridNode.focusHeight/4;

                           }
                           else {
                               yOffsets[colCount] += GridNode.focusHeight/2;

                           }
                       }
                       else {
                           if ((rowCount == (focusY.intValue() + 1))) {

                               if (each.isNeighborFocused()) {
                                       yOffsets[colCount] += GridNode.midHeight;
                                   }
                                   else {

                                       if (belowNFocus == true) {
                                           yOffsets[colCount] += GridNode.midHeight/2;
                                       }
                                       else {
                                           yOffsets[colCount] += each.getFullBoundsReference().getHeight();
                                       }
                                   }


                           }
                           if ((rowCount == (focusY.intValue() - 1))) {

                               if (each.isNeighborFocused()) {
                                       yOffsets[colCount] += GridNode.midHeight;
                                   }
                                   else {

                                       if (aboveNFocus == true) {
                                           yOffsets[colCount] += GridNode.midHeight/2;
                                       }
                                       else {
                                           yOffsets[colCount] += each.getFullBoundsReference().getHeight();
                                       }
                                   }


                           }

                       }


                   }
                   else {
                       yOffsets[colCount] += each.getFullBoundsReference().getHeight();
                   }

//                compute the offset to the left of the node
//                compute the X offset
                   if (colCount >= (numCols-1)) {
                       xOffset = GridNode.xGridMargin + 10;
                       colCount = 0;
                       rowCount++;
                   }
                   else {

                       if (focusX != null) {

                           //the focus column
                           if (colCount == focusX.intValue()) {
                               if (each.isFocused()) {
                                   xOffset += GridNode.focusWidth;
                               }
                               else if (each.isNeighborFocused()){
                                   xOffset += 3*(GridNode.focusWidth/4);
                               }
                               else {
                                   xOffset += (GridNode.focusWidth/2);
                               }
                           }
                           else {
                               if ((colCount == (focusX.intValue() + 1))) {
                                   if (each.isNeighborFocused()) {
                                           xOffset += GridNode.midWidth;
                                       }
                                       else {
                                           if (rightNFocus == true) {
                                               xOffset += GridNode.midWidth/2;
                                           }
                                           else {
                                               xOffset += each.getFullBoundsReference().getWidth();
                                           }
                                       }
                               }
                               if ((colCount == (focusX.intValue() - 1))) {
                                   if (each.isNeighborFocused()) {
                                           xOffset += GridNode.midWidth;
                                       }
                                       else {
                                           if (leftNFocus == true) {

                                               xOffset += GridNode.midWidth/2;
                                           }
                                           else {
                                               xOffset += each.getFullBoundsReference().getWidth();
                                           }
                                       }
                               }

                           }

                       }
                       else {
                          xOffset += each.getFullBoundsReference().getWidth();
                       }

                       colCount++;

                   }
               }

               //System.out.println("done laying out "  + j);
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

       public void testQuery() {
               int colIndex = specTable.getColumnIndex("redshift");

           int numRows = specTable.getRowCount();
           for (int i = 0; i < numRows; i++) {

               Double curZ = (Double) specTable.getValueAt(i, colIndex);
               if (curZ.intValue() < 0.03) {

                   SpecNode specNode = (SpecNode) specTable.getValueAt(i, specTable.getColumnIndex("pnode"));
                   this.removeChild(specNode);
               }

           }

       }

          /**
    * Opens and parses the data file
    * @param fileName The fullpath name of the data file
    */
   public void processFile() {

       specTable = new SpecTable();
       targetNames = new ArrayList();

       HashMap targetList = new HashMap();


       int numTargets = 0;
       int numPhases = 41 + lowPhaseValue;
       LinkedHashMap spectra = new LinkedHashMap();
       String url = "http://snfactory.lbl.gov/~sspoon/specvis_input3.txt";
       URL source = null;
       System.out.println(url);
       try {
           source = new URL(url);
       } catch (MalformedURLException e1) {

           System.out.println("could not find " + url);
           System.exit(0);

       }

       try {

               BufferedReader in =
                  new BufferedReader(new InputStreamReader(
                          source.openStream()));

               String line;

               while ((line = in.readLine()) != null){

                   String[] parts = line.split(",");
                   String target = parts[0];
                   String run = parts[1];
                   String phaseStr = parts[2];
                   Integer phase = new Integer(Integer.parseInt(phaseStr));
                   String redshiftStr = parts[4];
                   Double redshift = new Double(Double.parseDouble(phaseStr));

                   if (spectra.containsKey(target)) {
                       Map phases = (Map) spectra.get(target);
                       phases.put(phase, run);
                       spectra.put(target, phases);
                   }
                   else {

                       Target t = null;
                       if (targetList.containsKey(target)) {
                           t = (Target) targetList.get(target);
                       }
                       else {
                           t = new Target(target);
                           t.setRedshift(redshift);
                           targetList.put(target, t);
                       }

                       if (numTargets < 60) {
                       numTargets++;

                       targetNames.add(target);
                     //  System.out.println(numTargets + " " + target);

                       Map phases = new HashMap();
                       phases.put(phase, run);
                       spectra.put(target, phases);

                       }
                       else {
                           break;
                       }

                   }

               }

              in.close();

       } catch (Exception e) {

               System.out.println("Could not open and read file: " + source);
               e.printStackTrace();
               //System.exit(0);
       }

       //System.out.println("done parsing file");

       Set targets = spectra.keySet();
       Iterator i = targets.iterator();
       int row = 0;

       matrix = new SpecNode[numPhases][numTargets]; //x,y axis

       while (i.hasNext()) {
           String target = (String) i.next();
           //System.out.println("target " + target + " " + row);
           Map unordered = (Map) spectra.get(target);
           Map ordered = new TreeMap(unordered);

           Set phases = ordered.keySet();
           Iterator j = phases.iterator();
           while (j.hasNext()) {

               Integer phase = (Integer) j.next();
               String run = (String) ordered.get(phase);

               Target t = null;
               if (targetList.containsKey(target)) {
                   t = (Target) targetList.get(target);
               }
               else {
                   t = new Target(target);
                   targetList.put(target, t);
               }

               Spectrum spec = new Spectrum(t, run, phase);
               t.addSpec(spec);

               SpecNode node = new SpecNode(t, spec , phase.intValue()+lowPhaseValue, row);
               if (((phase.intValue()+lowPhaseValue) >= 0) && ((phase.intValue()+lowPhaseValue) < numPhases)) {

                   //System.out.println(row + " : " + phase.intValue());
                   matrix[(phase.intValue()+lowPhaseValue)][row] = node;
                   specTable.addSpec(spec,node);
               }

           }

           row++;

       }

       numRows = row;
       numCols = numPhases;
       totalNumberOfTargets = numTargets;
      // System.out.println("num targets " + numTargets);

       //add the children, by rows. 1 Target = 1 row
       // Target = y axis, Phase = x axis
       for (int a = 0; a < numTargets; a++) {
           for (int b = 0; b < numPhases; b++) {
               SpecNode node = matrix[b][a];
               if (node == null) {
                   node = new SpecNode(b, a);
                   matrix[b][a] = node;

               }

               addChild(node);
           }
       }

       //System.out.println("done processing file");

   }

   /**
    * @return Returns the targetNames.
    */
   public ArrayList getTargetNames() {
       return targetNames;
   }
   /**
    * @return Returns the specTable.
    */
   public SpecTable getSpecTable() {
       return specTable;
   }

   public void callUpdateGrid(int index) {
       //System.out.println("calling update");
       callClickAction(index);
   }

   public void callUpdateGridColorX(int indexColumn) {
       //System.out.println("Color update on the grid");
       callColorActionX(indexColumn);
   }
   public void callUpdateGridUncolorX(int indexColumn) {
       //System.out.println("Color update on the grid");
       callUncolorActionX(indexColumn);
   }


   public void callUpdateGridColorY(int indexRow) {
       callColorActionY(indexRow);
   }

   public void callUpdateGridUncolorY(int indexRow) {
       callUncolorActionY(indexRow);
   }

   public boolean getFocusState() {
       if (focusNode == null) {
           return false;
       }
       else {
           return true;
       }

   }
   
   public boolean isItClicked(){
	   return specClicked;
   }

 }
