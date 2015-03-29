/*
 * Target.java
 * Author: Elena Caraba
 * Create Date: Jul 3, 2007
 * 
 */
package models;

import java.util.ArrayList;

/**
 * Target class
 *
 */
public class Target {
    
    private String targetName;
    private ArrayList specs;
    private Double redshift;
    
    public Target(String targetName) {
        this.targetName = targetName;
        specs = new ArrayList();
    }
    
    
    
    public void addSpec(Spectrum spec) {
        specs.add(spec);
    }
    
   

    /**
     * @return Returns the specs.
     */
    public ArrayList getSpecs() {
        return specs;
    }
    /**
     * @param specs The specs to set.
     */
    public void setSpecs(ArrayList specs) {
        this.specs = specs;
    }
    /**
     * @return Returns the targetName.
     */
    public String getTargetName() {
        return targetName;
    }
    /**
     * @param targetName The targetName to set.
     */
    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
    /**
     * @return Returns the redshift.
     */
    public Double getRedshift() {
        return redshift;
    }
    /**
     * @param redshift The redshift to set.
     */
    public void setRedshift(Double redshift) {
        this.redshift = redshift;
    }
}
