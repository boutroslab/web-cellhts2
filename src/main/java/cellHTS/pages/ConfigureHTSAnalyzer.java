package cellHTS.pages;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.ComponentResources;
import data.HTSAnalyzerParameter;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 25.08.2010
 * Time: 12:07:15
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureHTSAnalyzer {
    private String speciesSet;
    private String initalIDsSet;
    private String duplicateRemoverMethodSet;

    @Persist
    private BeanModel myBeanEditModel;

    @InjectPage
    private CellHTS2 cellHTS2;
    
    @Persist
    private HTSAnalyzerParameter htsanalyzeParameters;

     @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages prop;

    @Inject
    private ComponentResources resources;

    private String speciesUpdater;


    public void onActivate() {
        speciesSet=prop.get("htsanalyzer-organism");
        initalIDsSet =prop.get("htsanalyzer-initalIDs");
        duplicateRemoverMethodSet=prop.get("htsanalyzer-duplicateRemoverMethod");        
         //init the species selection drop down with the first value
        if(htsanalyzeParameters!=null  && htsanalyzeParameters.getSpecies()!=null) {
            HashMap<String,String> id2OrgWRCode = speciesSetToOrgSpeciesMap();
           
            speciesUpdater=id2OrgWRCode.get(htsanalyzeParameters.getSpecies());
            System.out.println(speciesUpdater);
        }
        else {
            speciesUpdater=speciesSet.split(",")[0];
        }


     }


    public HTSAnalyzerParameter getHtsanalyzeParameters() {
        return htsanalyzeParameters;
    }

    public void setHtsanalyzeParameters(HTSAnalyzerParameter htsanalyzeParameters) {
        this.htsanalyzeParameters = htsanalyzeParameters;
    }

    public Object onSuccessFromHtsanalyzeParameters() {
        String extractedRSpeciesName = speciesUpdater.split("\\|")[0];
        htsanalyzeParameters.setSpecies(extractedRSpeciesName);




        return cellHTS2;
    }


    public String getSpeciesSet() {
        return speciesSet;
    }

    public void setSpeciesSet(String speciesSet) {
        this.speciesSet = speciesSet;
    }

    public String getInitalIDsSet() {
        return initalIDsSet;
    }

    public void setInitalIDsSet(String initalIDsSet) {
        this.initalIDsSet = initalIDsSet;
    }

    public String getDuplicateRemoverMethodSet() {
        return duplicateRemoverMethodSet;
    }

    public void setDuplicateRemoverMethodSet(String duplicateRemoverMethodSet) {
        this.duplicateRemoverMethodSet = duplicateRemoverMethodSet;
    }

    public BeanModel getMyBeanEditModel() {
        BeanModel model = beanModelSource.createEditModel(HTSAnalyzerParameter.class, resources.getMessages());
        // Make other changes to model here.
        model.add("speciesSet", null);
        model.add("initalIDsSet", null);
        model.add("duplicateRemoverMethodSet", null);
        
        return model;
    }

    public String getSpeciesUpdater() {
        return speciesUpdater;
    }

    public void setSpeciesUpdater(String speciesUpdater) {
        this.speciesUpdater = speciesUpdater;
        
    }
    public HashMap<String,String> speciesSetToOrgSpeciesMap() {
        HashMap<String,String> returnMe=new HashMap<String,String>();
        String[] idsWithRCode = speciesSet.split(",");
        for(String idWithRCode : idsWithRCode) {
            String[]res = idWithRCode.split("\\|");
            returnMe.put(res[0],idWithRCode);
        }
        return returnMe;
    }
}
