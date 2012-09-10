package cellHTS.pages;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Retain;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.ComponentResources;

import data.HTSAnalyzerParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 25.08.2010
 * Time: 12:07:15
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureHTSAnalyzer {
 
	private String convertSpecies;
	
    @Persist
    private HTSAnalyzerParameter htsanalyzeParameters;

     @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages prop;

    @Inject
    private ComponentResources resources;

    @Persist
    private TreeMap<String,String> orgCode2SpeciesName;
    @Persist
    private TreeMap<String,String> speciesName2OrgCode;
    
    @InjectPage
    private CellHTS2 cellHTS2;

    public void setupRender() {  
    	if( htsanalyzeParameters == null  || htsanalyzeParameters.getSpecies() ==null ) {
    		htsanalyzeParameters = new HTSAnalyzerParameter(prop);
    	}
    	generateOrgCode2SpeciesRelationships(prop.get("htsanalyzer-organism"));
     }
    
    public String getSpeciesSetModel() {
    	Set<String> vals = speciesName2OrgCode.keySet();
    	//build up
    	String species = null;
    	for(String val: vals) {
    		if(species == null) {
    			species = val;
    		}
    		else {
    			species += ","+val;
    		}
    	}
    	return species;
    }
    public String getInitalIDsSetModel() {
    	return prop.get("htsanalyzer-initalIDs");
    }
    public String getDuplicateRemoverMethodSetModel() {
    	return  prop.get("htsanalyzer-duplicateRemoverMethod");
    }
    
    public void setSpeciesSetModel(String species) {
    	
    }
    public void setDuplicateRemoverMethodSetModel(String remover) {
    	
    }
    
    
   
    
    public void generateOrgCode2SpeciesRelationships(String speciesString) {
        orgCode2SpeciesName=new TreeMap<String,String>();
        speciesName2OrgCode=new TreeMap<String,String>();

        String[] idsWithRCode = speciesString.split(",");
        for(String idWithRCode : idsWithRCode) {
            String[]res = idWithRCode.split("\\|");
            orgCode2SpeciesName.put(res[0],res[1]);
            speciesName2OrgCode.put(res[1],res[0]);
        }
        
    }
    //this species is for converting the species and put it into the htsanalyzer obj correctly
    public void setConvertSpecies(String value) {
    	//get species code
    	String speciesCode = speciesName2OrgCode.get(value);
    	//put the species correctly
    	htsanalyzeParameters.setSpecies(speciesCode);    	
    }
    public String getConvertSpecies() {
    	return convertSpecies;
    }

	public HTSAnalyzerParameter getHtsanalyzeParameters() {
		return htsanalyzeParameters;
	}

	public void setHtsanalyzeParameters(HTSAnalyzerParameter htsanalyzeParameters) {
		this.htsanalyzeParameters = htsanalyzeParameters;
	}
	public Object onSuccess() {
		return cellHTS2;
    }
    
   
}
