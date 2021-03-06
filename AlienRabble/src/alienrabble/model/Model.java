package alienrabble.model;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

import alienrabble.dynamicalien.DynamicExemplar;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.export.binary.BinaryImporter;


/**
 * Holds data about the individual objects; their name, the binaryLocation
 * of the jbin file (if there is one), the objects dimensions and other 
 * categorical properties.
 * 
 * @author Caspar Addyman
 *
 */
public class Model extends Node {
	
	private static final Logger logger 
	= Logger.getLogger(ARXMLModelData.class.getName());
	private static final long serialVersionUID = 1L;
	private String ID;
	private String name;
	private String binaryLocation;
	private boolean hasmodel;
	private Spatial model;
	private Hashtable<String, String> properties; //categorical properties as set of name value pairs
	private Hashtable<String, Float> dimensions; //scalar dimensional properties as name value pairs
	private Quaternion initialRotation;
	private Vector3f initialScale;
	private boolean isDynamicAlien;
	private DynamicExemplar dynamicAlien;
	private int whichCategory; 
	
	public Model(){
		properties = new Hashtable<String, String>();
		dimensions = new Hashtable<String, Float>();
		binaryLocation = null;
		hasmodel = false;
		whichCategory = 0;
		initialRotation = new Quaternion(0,0,0,0);
		initialScale = new Vector3f(1,1,1);
	}
	
	public Model(String name)
	{
		this();
		this.name = name;
		this.ID = name;
	}
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}

	/*
	 * the value for the category that this object is assigned to 
	 * read from the dimensions in the xml file.
	 */
	public int Category(){
		return whichCategory;
	}
	
	public boolean IsDynamicAlien(){
		return isDynamicAlien;
	}
	public void setIsDynamicAlien(boolean isDynamicAlien){
		this.isDynamicAlien = isDynamicAlien;
	}
	
	public String getID(){
		return ID;
	}
	public void setID(String ID){
		this.ID = ID;
	}
	
	public Quaternion getInitialRotation(){
		return initialRotation;
	}
	public void setInitialRotation(Quaternion q){
		this.initialRotation = q;
	}
	
	public Vector3f getInitialScale(){
		return initialScale;
	}
	public void setInitialScale(Vector3f s){
		this.initialScale = s;
	}
	
	public String getBinaryLocation(){
		return binaryLocation;
	}
	public void setBinaryLocation(String binaryLocation){
		this.binaryLocation = binaryLocation;
	}
	
	
	public int numProperties(){
		return properties.size();
	}
	public int numDimensions(){
		return dimensions.size();
	}
	
	public void setProperty(String name, String value){
		properties.put(name, value);
	}
	public void setDimension(String name, Float value){
		dimensions.put(name, value);
	}
	public String getProperty(String name){
		return properties.get(name);
	}
	
	public Float getDimension(String name){
		return dimensions.get(name);
	}	
	
	public Enumeration<String> getAllProperties(){
		return properties.keys();
	}

	public Enumeration<String> getAllDimensions(){
		return dimensions.keys();
	}
	
	public boolean loadModelBinary(){
		if (binaryLocation == null) return false;
		if (hasmodel == true) return true;
		
		URL alienURL = Model.class.getClassLoader().getResource(binaryLocation);
		BinaryImporter BI = new BinaryImporter();
		try {
			model = (Spatial)BI.load(alienURL.openStream());
			} 
		catch (IOException e) {
			logger.severe("failed to load model binary" + e.getMessage());
			return false;
		}
		this.attachChild(model);
		this.setLocalRotation(initialRotation);
		this.setLocalScale(initialScale);
		whichCategory = getDimension("Category").intValue();
		hasmodel = true;
		return true;
	}		

	public boolean createDynamicExemplar(){
		if (!isDynamicAlien) return false;
		if (hasmodel == true) return true;
		
		//set up the details for this alien
		dynamicAlien = new DynamicExemplar(this.getName());
		
		//body
		dynamicAlien.setBodyHeight(getDimension("BodyHeight"));
		dynamicAlien.setBodyWidth(getDimension("BodyWidth"));
		dynamicAlien.setBodyType(getDimension("BodyType").intValue());
		
		//legs
		dynamicAlien.setLegType(getDimension("LegType").intValue());
		dynamicAlien.setLegCount(getDimension("LegCount").intValue());
		
		//arms
		dynamicAlien.setArmType(getDimension("ArmType").intValue());
		dynamicAlien.setArmSize(getDimension("ArmSize"));
		
		//Stripes
		dynamicAlien.setStripeColours(getDimension("StripeColours").intValue());
		dynamicAlien.setStripeAngle(getDimension("StripeAngle").intValue());
		dynamicAlien.setStripeFreq(getDimension("StripeFreq").intValue());

		//eyes
		dynamicAlien.setEyeCount(getDimension("EyeCount").intValue());
		dynamicAlien.setEyeSize(getDimension("EyeSize"));

		dynamicAlien.setUpExemplar();
		this.attachChild(dynamicAlien);
		this.setLocalRotation(initialRotation);
		this.setLocalScale(initialScale);
		
		whichCategory = getDimension("Category").intValue();
		
		hasmodel = true;
		return true;
	}
	
	public void reset(){
		this.detachAllChildren();
		hasmodel = false;
		binaryLocation = null;
	}
}
	
