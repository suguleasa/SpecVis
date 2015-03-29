/*
 * SpecNode.java
 * Author: Elena Caraba
 * Create Date: Jun 8, 2007
 * 
 */
package gui.graph;

import java.awt.Color;
import java.awt.Font;
import java.net.MalformedURLException;
import java.net.URL;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import gui.WarehouseAuthenticator;
import models.Spectrum;
import models.Target;

/**
 * @author ecaraba
 * Created on Jun 8, 2007
 *
 */
public class SpecNode extends PPath {
    
    private Spectrum spec;
    private Target target;
    private int scale;
    private PNode imageNode;
    private PText nameNode;
    private PText phaseNode;
    private boolean focused = false;
    private boolean neighborFocused = false;
    private boolean empty = false;
    private int xIndex;
    private int yIndex;
    
    public SpecNode(int xIndex, int yIndex) {
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        empty = true;
        this.setStrokePaint(null);
        this.setStroke(null);
        setPathToRectangle(0, 0, 5, 5);
        this.setPaint(getColor("f3f3f3"));
        //this.setVisible(false);
    }
    
    public SpecNode(Target target, Spectrum spec, int x, int y) {
       
        Font font = new Font("SansSerif", Font.PLAIN, 9);
        
        this.target = target;
        this.spec = spec;
        
        this.xIndex = x;
        this.yIndex = y;
        
        WarehouseAuthenticator.init();
        
        //create the composite of the image and the name
        setPathToRectangle(0, 0, 1, 1);
    
        
        //create the target name 
        //PNode nameNode = new PText(target.getName());
        nameNode = new PText(target.getTargetName());
        nameNode.translate(5,3);
        nameNode.setFont(font);
        phaseNode = new PText("Phase: " + spec.getPhase().toString());
        phaseNode.translate(5,15);
        phaseNode.setFont(font);
        //create the image 
        imageNode = null;


        
        PBounds b = getUnionOfChildrenBounds(null);
        //b.inset(-3.5, -3.5);
        b.inset(-3,-3);
        setBounds(b);
        
        setPaint(Color.gray);
        setStroke(null);
        setChildrenPickable(false);
        
    }
    
    public Color getColor(String c) {
        int bColorNum = Integer.parseInt(c, 16);
        Color color = new Color(bColorNum);
        return color;
    }
    
    public void focusImage() {
        if (empty == false) {
            
            if (imageNode == null) {
                try {
                    String[] dateParts = spec.getRunNumber().split("_");
                    String yr = dateParts[0];
                    String doy = dateParts[1];
                    String url = "http://snfactory.lbl.gov/sunfall/images/snifs_images/" + yr + "/" + doy + "/thumb_spec_" + spec.getRunNumber() + ".png";
                    imageNode = new PImage(new URL(url));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                if (imageNode != null) {
                    //addChild(imageNode);
                    
                    imageNode.translate(5,32);
                    imageNode.scale(.65);
                    
                }
            }
            
            if (neighborFocused == true) {
                neighborFocused = false;
                imageNode.scale(2);
                removeChild(0);
                
               
            }
            if (focused == false) {
                addChild(0,nameNode);
                addChild(1,phaseNode);
                addChild(2,imageNode);
                focused = true;
                if (imageNode != null) {
                    //imageNode.scale(2.0);
                    //imageNode.scale(100.0);
                    PBounds b = getUnionOfChildrenBounds(null);
                    b.inset(-5, -5);
                    setBounds(b);
                    
                 //   System.out.println(this.getFullBoundsReference().getWidth()  + " : " + this.getFullBoundsReference().getHeight());
                }
                
            }
        }

    }
    

    public void deFocusImage() {
        if (empty == false) {
            
            if (focused == true) {
                removeChild(0);
                removeChild(0);
                removeChild(0);
                focused = false;
                if (imageNode != null) {
                 
                    PBounds b = getUnionOfChildrenBounds(null);
                    b.inset(-2.5, -2.5);
                    setBounds(b);
                    
                }   
            }
        }
    }
    
    
    public void focusNeighborImage() {
        if (empty == false) {
            
            if (imageNode == null) {
                try {
                    String[] dateParts = spec.getRunNumber().split("_");
                    String yr = dateParts[0];
                    String doy = dateParts[1];
                    String url = "http://snfactory.lbl.gov/sunfall/images/snifs_images/" + yr + "/" + doy + "/thumb_spec_" + spec.getRunNumber() + ".png";
                    imageNode = new PImage(new URL(url));
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                
                if (imageNode != null) {
                    //addChild(imageNode);
                    
                    imageNode.translate(5,32);
                    imageNode.scale(.65);
                   
                }
            }
            
            if (focused == true) {
                focused = false;
                removeChild(0);
                removeChild(0);
                removeChild(0);
               
            }
            if (neighborFocused == false) {
                
                neighborFocused = true;
                if (imageNode != null) {
                    //imageNode.scale(2.0);
                    addChild(imageNode);
                    imageNode.scale(.5);
                    PBounds b = getUnionOfChildrenBounds(null);
                    b.inset(-5, -5);
                    setBounds(b);
                    
                 //   System.out.println(this.getFullBoundsReference().getWidth()  + " : " + this.getFullBoundsReference().getHeight());
                    
                }
                
            }
        } else {
          
            if (neighborFocused == false) {
                neighborFocused = true;
                this.setWidth(GridNode.midWidth);
                this.setHeight(GridNode.midHeight);
            }
        }
    }
    
    
    public void deFocusNeighborImage() {
        if (empty == false) {
            if (neighborFocused == true) {
                neighborFocused = false;
                if (imageNode != null) {
                    //imageNode.scale(2.0);
                    imageNode.scale(2.0);
                    removeChild(0);
                    PBounds b = getUnionOfChildrenBounds(null);
                    b.inset(-2.5, -2.5);
                    setBounds(b);
                    
                }
                
            }
        }
        else {
            if (neighborFocused == true) {
                neighborFocused = false;
                this.setWidth(5);
                this.setHeight(5);
            }
        }
    }
    
    /** dynamic queries specify that this node be "removed" from the grid view */
    public void filteredOut() {
        this.empty = true;
        this.setPaint(getColor("f3f3f3"));
    }
    
    /** dynamic queries specify that this node be "added" to the grid view */
    public void filteredIn() {
        this.empty = false;
        this.setPaint(Color.gray);
    }

    /**
     * @return Returns the focused.
     */
    public boolean isFocused() {
        return focused;
    }
    /**
     * @param focused The focused to set.
     */
    public void setFocused(boolean focused) {
        this.focused = focused;
    }
    /**
     * @return Returns the xIndex.
     */
    public int getXIndex() {
        return xIndex;
    }
    /**
     * @param index The xIndex to set.
     */
    public void setXIndex(int index) {
        xIndex = index;
    }
    /**
     * @return Returns the yIndex.
     */
    public int getYIndex() {
        return yIndex;
    }
    /**
     * @param index The yIndex to set.
     */
    public void setYIndex(int index) {
        yIndex = index;
    }
    /**
     * @return Returns the neighborFocused.
     */
    public boolean isNeighborFocused() {
        return neighborFocused;
    }
    /**
     * @param neighborFocused The neighborFocused to set.
     */
    public void setNeighborFocused(boolean neighborFocused) {
        this.neighborFocused = neighborFocused;
    }
    /**
     * @return Returns the empty.
     */
    public boolean isEmpty() {
        return empty;
    }
    /**
     * @param empty The empty to set.
     */
    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    /**
     * @return Returns the spec.
     */
    public Spectrum getSpec() {
        return spec;
    }
    /**
     * @param spec The spec to set.
     */
    public void setSpec(Spectrum spec) {
        this.spec = spec;
    }
    /**
     * @return Returns the target.
     */
    public Target getTarget() {
        return target;
    }
    /**
     * @param target The target to set.
     */
    public void setTarget(Target target) {
        this.target = target;
    }
}
