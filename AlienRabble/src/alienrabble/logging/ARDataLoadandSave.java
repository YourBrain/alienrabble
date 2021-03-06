package alienrabble.logging;

import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import alienrabble.model.ARXMLModelData;

/**
 * this class handles all our logging of information to DOM XML files
 * 
 * @author monkey
 *
 */
public class ARDataLoadandSave {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ARDataLoadandSave.class
            .getName());

	//there will only be a single instance of this class in the project
	private static ARDataLoadandSave instance;
	
	private String initfile = null;  //the name of the file that gives use all the initial information
	private String outputfile = null; //the name of the data save file. 
	
	//separate sets of grab and sort exemplars
	private ARXMLModelData myXmlModelData_Grab;
	private ARXMLModelData myXmlModelData_Sort;
	private ARXMLExperimentData myXmlParticipantData;
	private ARXMLGrabData myXmlGrabData; // data from the grab phase
	private ARXMLSortData myXmlSortData; // data from the sort phase
	
	private ARDataLoadandSave(){
	}

	public void setUpLoadandSaveDocs(String initfile){
		this.initfile = initfile;
		//create the participant data object
		myXmlParticipantData = new ARXMLExperimentData(this.initfile);
		myXmlModelData_Sort = new ARXMLModelData(this.initfile);
		myXmlModelData_Grab = new ARXMLModelData(this.initfile);
		myXmlGrabData = new ARXMLGrabData();
		myXmlSortData = new ARXMLSortData();		
	}
	
	/**
	 * retrieves the singleton instance of the CollisionTreeManager.
	 * @return the singleton instance of the manager.
	 */
	public static ARDataLoadandSave getInstance() {
		if (instance == null) {
			instance = new ARDataLoadandSave();
		}
		return instance;
	}
	
	
	public boolean saveAllData(){
		
		if (outputfile  == null){
			if (!generateOutputFileName()) return false;
		}
		
		DocumentBuilderFactory factory
		 = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.severe(e.getMessage());
			return false;
		}
		DOMImplementation impl = builder.getDOMImplementation();
		
		Document doc = impl.createDocument(null,null,null);
		
		doc = myXmlParticipantData.addParticipantData(doc);
		//TODO ought to track grab and sort model sets separately 
		doc = myXmlModelData_Grab.addModelData(doc);
		doc = myXmlGrabData.writeGrabData(doc);
		doc = myXmlSortData.writeSortData(doc);
		
		
		// transform the Document into a String
		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.severe(e.getMessage());
			return false;
		}
		//transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		java.io.FileWriter fw= null;
		try {
			fw = new java.io.FileWriter(outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.severe(e.getMessage());
			return false;
		}
		
		StreamResult sr = new StreamResult(fw);
		try {
			transformer.transform(domSource, sr);
			fw.close();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.severe(e.getMessage());
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.severe(e.getMessage());
			return false;
		}
		//if we get this far we've probably succeeded!
		return true;
	}
	
	
	
	/**
	 * generates the 
	 * @return
	 */
	private boolean generateOutputFileName(){
		this.outputfile = myXmlParticipantData.getCurrentDate() + "_ID_" + myXmlParticipantData.getID() + ".xml";
		return true;
	}
	
	public ARXMLExperimentData getXmlExperimentData(){
		return myXmlParticipantData;
	}
	public ARXMLModelData getXmlModelData_Grab(){
		return myXmlModelData_Grab;
	}	
	public ARXMLModelData getXmlModelData_Sort(){
		return myXmlModelData_Sort;
	}
	public ARXMLGrabData getXmlGrabData(){
		return myXmlGrabData;
	}
	public ARXMLSortData getXmlSortData(){
		return myXmlSortData;
	}

}
