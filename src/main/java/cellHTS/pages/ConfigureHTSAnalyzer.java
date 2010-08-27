package cellHTS.pages;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.ComponentResources;
import data.HTSAnalyzerParameter;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 25.08.2010
 * Time: 12:07:15
 * To change this template use File | Settings | File Templates.
 */
public class ConfigureHTSAnalyzer {
    private String speciesSet="Drosophila_melanogaster,Homo_sapiens,Rattus_norvegicus,Mus_musculus,Caenorhabditis_elegans";
    private String initalIDsSet="Ensembl.transcript,Ensembl.prot,Ensembl.gene,Entrez.gene,RefSeq,Symbol,GenBank,Flybase,FlybaseCG,FlybaseProt";
     //parameters for gene set analysis
    private String duplicateRemoverMethodSet="max,min,average";

    @Persist
    private BeanModel myBeanEditModel;

    @InjectPage
    private CellHTS2 cellHTS2;
    
    @Persist
    private HTSAnalyzerParameter htsanalyzeParameters;

     @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private ComponentResources resources;

    public HTSAnalyzerParameter getHtsanalyzeParameters() {
        return htsanalyzeParameters;
    }

    public void setHtsanalyzeParameters(HTSAnalyzerParameter htsanalyzeParameters) {
        this.htsanalyzeParameters = htsanalyzeParameters;
    }

    public Object onSuccessFromHtsanalyzeParameters() {
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
    
}
