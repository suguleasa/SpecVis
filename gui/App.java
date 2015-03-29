/*
 * App.java
 * Modified by: Elena Caraba
 * Create Date: Summer 2007
 * 
 */
package gui;
import java.awt.Color;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.swing.*;
import java.awt.Image;
import java.awt.Dimension;

/**
 * Created on Jun 13, 2006
 * This is a helper class for the application to run.
 * It primarily holds static functions for use throughout the app
 */
public class  App {
    static private JPanel statusArea = new JPanel();
    static private JLabel status = new JLabel("");
    static private String propFile = "Warehouse.properties";
    static private String baseDir = "/opt/snf/www/snfactory/sunfall/java/warehouse";
    static private String snfactoryURL = "http://snfactory.lbl.gov/sunfall/";
    static private String imageURL = snfactoryURL + "images/warehouse_images/";
    static private String snfactoryUser = "hizsn";
    static private String snfactoryPass = "hizsn";

    static private Properties props;
    static private long imageCacheTimeHint = 0;
    static private Dimension screenSize = null;

    private App() {}  // defeat instantiation

    static {
        props = new Properties();
        FileInputStream fis;
        
        // Look for propFile in the current directory first, followed by
        // our base directory (set above in baseDir or via -D setting):
        baseDir = System.getProperty("snwarehouse.basedir", baseDir);
        File test = new File(propFile);
        if (! test.exists()) {
                propFile = baseDir + "/" + propFile;
        }
        
        try {
                fis = new FileInputStream(propFile);
                props.load(fis);

        } catch (FileNotFoundException e) {
                System.out.println("Cant find file " + propFile);
                e.printStackTrace();
        } catch (IOException e) {
                System.out.println("IO Exception loading inputstream");
                e.printStackTrace();
        }

	props.setProperty("snfactoryURL.path", snfactoryURL);
	props.setProperty("snfactoryURL.user", snfactoryUser);
	props.setProperty("snfactoryURL.pass", snfactoryPass);
	props.setProperty("imageURL", imageURL);
    };

    static public JPanel getStatusArea() {
       return statusArea;
    }
    static public void showStatus(String s) {
       status.setText(s);
    } 
    static public String getResource(String key) {
       
        String value = props.getProperty(key);
        if (value == null) {
            return "";
            
        }
        else {
            return value;
        }
    }
    
    static public String[] getResourceList(String key) {
        String value = props.getProperty(key);
        String[] values = value.split(";");
        return values;
    }
    
    static public Color getColor(String c) {
        int bColorNum = Integer.parseInt(c, 16);
        Color color = new Color(bColorNum);
        return color;
    }
    
    static public Color getBackgroundColor() {
        return App.getColor(App.getResource("backgroundColor"));
        
    }
    
    static public Color getButtonColor() {
        return App.getColor(App.getResource("buttonColor"));
    }
    
    /**
     * Creates a JLabel with an image from a URL
     * @param image The image to grab from the image directory
     * @return The JLabel containg the image, will return null if 
     * image is not found
     */
    static public JLabel createImageLabel(String image, boolean flush) {
        
        JLabel imageLabel = null;
        try {
            URL imageURL = new URL(App.getResource("imageURL") + image);
            
            
            if (flush) {
		Toolkit.getDefaultToolkit().getImage(imageURL).flush();
            }
            ImageIcon imageI = new ImageIcon(imageURL);
            
            if (imageI.getImageLoadStatus() == MediaTracker.COMPLETE) {
                imageLabel = new JLabel(imageI);
            }
            
            
        
        } catch (MalformedURLException e) {
           
            e.printStackTrace();
        }
        
        return imageLabel;
        
    }

    static private void resizeIfTooBig(ImageIcon imageI) {
	
	if (screenSize == null) {
	    	screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	}
	if (screenSize == null) {
		return;
	}
	int w = screenSize.width  - 50;
	int h = screenSize.height - 50;
	boolean debug = false;

	if (debug) {
		w = 1024 - 50;
		h = 768 - 50;
	}

	Image img = imageI.getImage();
	int w2 = img.getWidth(null);
	int h2 = img.getHeight(null);
	if (w2 > w || h2 > h) {
		if (debug) {
			System.out.println("shrinking: " + imageI.toString());
		}
		Image smaller = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		imageI.setImage(smaller);
	}
    }
    
    /**
     * Creates a JLabel with an image from a URL
     * @param image The image to grab from the image directory
     * @return The JLabel containg the image, will return null if 
     * image is not found
     */
    static public JLabel createImageLabelUrl(String url, boolean flush) {
        
        JLabel imageLabel = null;
        try {
            URL imageURL = new URL(url);
            
            if (flush) {
		Toolkit.getDefaultToolkit().getImage(imageURL).flush();
            }
            ImageIcon imageI = new ImageIcon(imageURL);

            //ImageIO.setUseCache(false);
            //ImageIcon imageI = new ImageIcon(ImageIO.read(imageURL));

            if (imageI.getImageLoadStatus() == MediaTracker.COMPLETE) {
                imageLabel = new JLabel(imageI);
            }
            
            resizeIfTooBig(imageI);
            
        
        } catch (MalformedURLException e) {
           
            e.printStackTrace();
        } 
        
        return imageLabel;
        
    }
    
    static public String encodeHtml(String text) {
        String escapedStr = text.replaceAll("<","&lt;");
        escapedStr = escapedStr.replaceAll(">", "&gt;");
        return escapedStr;
    }
    
    static public Color getStateColor(String state) {
        
        String color = "000000"; //pending, scheduled, etc
        if (state != null && state.equals("success") ) {
            color = "158F0A"; // green
        }
        else if (state.equals("cancelled") || state.equals("failure") || state.equals("incomplete") ) {
            color = "B40406"; //red
        }
        else if (state.equals("marginal")) {
            //color = "F9621C"; //orange
            color = "F86A17";
        } 

        return App.getColor(color);
    }

    static public void setImageCacheTimeHint() {
	imageCacheTimeHint = System.currentTimeMillis();
    }
    static public long getImageCacheTimeHint() {
	return imageCacheTimeHint;
    }
}


