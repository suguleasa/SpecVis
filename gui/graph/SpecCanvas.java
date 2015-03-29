/*
 * SpecCanvas.java
 * Author: Elena Caraba
 * Create Date: Jun 8, 2007
 * 
 */
package gui.graph;


import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.handles.PBoundsHandle;


/**
 * @author ecaraba
 * This will create the grid and layout the spec in the table
 */
public class SpecCanvas extends PCanvas {
    
    private int maxPhase = 45;
    private HorizontalAxisFisheye xAxis;
    private VerticalAxisFisheye yAxis;
    private GridNode grid;
    private LowerCanvas lower;
    
    public SpecCanvas() {
        
        lower = new LowerCanvas(this);
        this.xAxis = lower.getXAxis();
        
       
        //grid = new GridNode(parent);
        grid = new GridNode(this);
        
        xAxis.setGridNode(grid);

        removeInputEventListener(getPanEventHandler());
        //addInputEventListener(new PDragEventHandler());

        grid.translate(114,0);
        //xAxis = new HorizontalAxisFisheye();
        //xAxis.translate(150,610);
        
        PBoundsHandle.addBoundsHandlesTo(xAxis);
        
        yAxis = new VerticalAxisFisheye(this, grid.getTargetNames());
        yAxis.translate(-15,7.5);
        //yAxis.setGridNode(grid);
        
        getLayer().addChild(grid);
        getLayer().addChild(yAxis);

    }
    

    public void callGridUnfocus() {
    	grid.unfocusGrid();
    }
    
    public void callAxisUnFocusX() {
        xAxis.callUnFocus();
    }
    
    public void callAxisUnFocusY() {
        yAxis.callUnFocus();
    }
    
    public void callAxisUpdateY(int indexY) {
        yAxis.callUpdateAxis(indexY);
    }
    
    public void callAxisUpdateX(int indexX) {
        xAxis.callUpdateAxis(indexX);

    }
    public void callGridColorUpdateX(int indexColumn) {
    	grid.callUpdateGridColorX(indexColumn);
    }
    
    public void callGridUncolorUpdateX(int indexColumn) {
    	grid.callUpdateGridUncolorX(indexColumn);
    }
    
    public void callGridColorUpdateY(int indexRow) {
    	grid.callUpdateGridColorY(indexRow);
    }
    
    public void callGridUncolorUpdateY(int indexRow) {
    	grid.callUpdateGridUncolorY(indexRow);
    }
    
    public LowerCanvas getLowerCanvas() {
        return lower;
    }
    
    public void callGridUpdate(int index) {
    	//System.out.println("Buna");
    //	grid.callUpdateGrid(index);
    	yAxis.callUpdateAxis(index);
    	xAxis.callUpdateAxis(index);
    }
    /**
     * @return Returns the grid.
     */
    public GridNode getGrid() {
        return grid;
    }
}
