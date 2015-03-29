/*
 * Spectrum.java
 * Author: Elena Caraba
 * Create Date: Jun 8, 2007
 * 
 */
package models;

/**
 * Spectrum class
 *
 */
public class Spectrum {

    private String runNumber;
    private String imageLocation;
    private Integer phase;
    private Target target;
    
    public Spectrum(Target target, String runNumber, Integer phase) {
        
        this.target = target;
        this.runNumber = runNumber;
        this.phase = phase;
        
    }
    
    
    /**
     * @return Returns the phase.
     */
    public Integer getPhase() {
        return phase;
    }
    /**
     * @param phase The phase to set.
     */
    public void setPhase(Integer phase) {
        this.phase = phase;
    }
    /**
     * @return Returns the runNumber.
     */
    public String getRunNumber() {
        return runNumber;
    }
    /**
     * @param runNumber The runNumber to set.
     */
    public void setRunNumber(String runNumber) {
        this.runNumber = runNumber;
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
