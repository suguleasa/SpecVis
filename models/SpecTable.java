/*
 * SpecTable.java
 * Author: Elena Caraba
 * Create Date: Jun 8, 2007
 * 
 */
package models;


import java.util.*;

import javax.swing.table.AbstractTableModel;

import edu.umd.cs.piccolo.PNode;
/**
 * This is the data structure class
 */
public class SpecTable extends AbstractTableModel {
    
    
    private ArrayList columnNames;
    
    /** each row contains a HashMap of column values */
    private ArrayList rows;
    
    
    public SpecTable() {
        
        rows = new ArrayList();
        initializeColumnNames();
        
    }
    
    public void addSpec(Spectrum spec, PNode node) {
        
        HashMap row = new HashMap();
        row.put("spec", spec);
        row.put("target", spec.getTarget());
        row.put("pnode", node);
        row.put("runNumber", spec.getRunNumber());
        row.put("targetName", spec.getTarget().getTargetName());
        row.put("phase", spec.getPhase());
        row.put("redshift", spec.getTarget().getRedshift());
        
        rows.add(row);
    
    }
    
    
    public void initializeColumnNames() {
               
        columnNames = new ArrayList();
        columnNames.add("spec");
        columnNames.add("target");
        columnNames.add("pnode");
        columnNames.add("runNumber");
        columnNames.add("targetName");
        columnNames.add("phase");
        columnNames.add("redshift");

    }
    
    public Object getValueAt(int rowIndex, int colIndex) {
        
        HashMap row = (HashMap) rows.get(rowIndex);
        Object val = row.get(getColumnName(colIndex));
        return val;
    }
    
    public void setValueAt(Object val, int rowIndex, int colIndex) {
        
        HashMap row = (HashMap) rows.remove(rowIndex);
        row.put(getColumnName(colIndex), val);
        rows.add(rowIndex, row);
        
    }
    
    public String getColumnName(int colIndex) {
        
        String name = (String) columnNames.get(colIndex);
        return name;
        
    }
    
    public int getColumnIndex(String colName) {
        
        return columnNames.indexOf(colName);
        
    }
    
    public int getRowCount() {
        
        return rows.size();
    }
   
    
    public int getColumnCount() {
        return columnNames.size();
    }
    
    public Class getColumnClass(int colIndex) {
        
        return getValueAt(0, colIndex).getClass();
        
    }
    
    public boolean isCellEditable(int colIndex, int rowIndex) {
        return true;
    }
 
    /**
     * @return Returns the columnNames.
     */
    public ArrayList getColumnNames() {
        return columnNames;
    }
  
}
