/*
 * LowerCanvas.java
 * Author: Elena Caraba
 * Create Date: Jul 2, 2007
 * 
 */
package gui.graph;

import java.awt.Font;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * @author ecaraba
 * Created on Jul 2, 2007
 *
 */
public class LowerCanvas extends PCanvas {

    private HorizontalAxisFisheye xAxis;
    private SpecCanvas parent;
    
    public LowerCanvas(SpecCanvas parent) {
        
        this.parent = parent;
        
        PNode node = new PNode();
       // node.setPathToRectangle(0, 0, 1, 1);
    //    node.setStrokePaint(null);
      //  
        PText phaseLabel = new PText("Phase");
        Font font = new Font("SansSerif", Font.PLAIN, 16);
        phaseLabel.setFont(font);
        
        phaseLabel.translate(450,100);
        
        xAxis = new HorizontalAxisFisheye(this.parent);
        xAxis.translate(125,-70);
        
        node.addChild(xAxis);
        node.addChild(phaseLabel);
        PBounds b = node.getUnionOfChildrenBounds(null);
        //b.inset(-3.5, -3.5);
        b.inset(-3,-3);
        node.setBounds(b);
  
        getCamera().addChild(node);
        
    }
    
    /**
     * @return Returns the xAxis.
     */
    public HorizontalAxisFisheye getXAxis() {
        return xAxis;
    }
}
