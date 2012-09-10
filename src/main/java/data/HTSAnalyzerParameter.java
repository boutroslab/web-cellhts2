package data;


import java.io.Serializable;
import java.util.ArrayList;

import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 25.08.2010
 * Time: 11:06:33
 * To change this template use File | Settings | File Templates.
 */
public class HTSAnalyzerParameter implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String species;   //species will be a small single code
    //general parameters
    private String annotationColumn;
    private String initalIDs;
   
    private String duplicateRemoverMethod;
    private Boolean orderAbsValue;
    //private Boolean useGEneListCollectionDM; //gsc.list<-list(Dm.GO.CC=Dm.GO.CC,kegg.droso=kegg.droso)"
    private Boolean useGEneListCollectionKegg;
    private Boolean useGEneListCollectionGO;
    private Integer cutoffHitsEnrichment;
    private Integer nPermutations;
    private Integer exponent;
    private Integer minGeneSetSize;
    //parameters for network analysis
    private Integer nGseaPlots;
    
   
    
    @Inject
    public HTSAnalyzerParameter(Messages msg) {
    	species = msg.get("htsanalyzer-organism").split(",")[0].split("\\|")[0];  
    	annotationColumn = "GeneID";
    	initalIDs= msg.get("htsanalyzer-initalIDs").split(",")[0];
    	duplicateRemoverMethod = msg.get("htsanalyzer-duplicateRemoverMethod").split(",")[0];
        orderAbsValue=false;
        //useGEneListCollectionDM=false;
        useGEneListCollectionKegg=false;
        useGEneListCollectionGO=true;
        cutoffHitsEnrichment=2;
        nPermutations=1000;
        exponent=1;
        minGeneSetSize=20;
        nGseaPlots=10;
    }
 
    public String getAnnotationColumn() {
        return annotationColumn;
    }

    public void setAnnotationColumn(String annotationColumn) {
        this.annotationColumn = annotationColumn;
    }

    public String getInitalIDs() {
        return initalIDs;
    }

    public void setInitalIDs(String initalIDs) {
        this.initalIDs = initalIDs;
    }

    public String getDuplicateRemoverMethod() {
        return duplicateRemoverMethod;
    }

    public void setDuplicateRemoverMethod(String duplicateRemoverMethod) {
        this.duplicateRemoverMethod = duplicateRemoverMethod;
    }

    public Boolean getOrderAbsValue() {
        return orderAbsValue;
    }

    public void setOrderAbsValue(Boolean orderAbsValue) {
        this.orderAbsValue = orderAbsValue;
    }
    /*
    public Boolean getUseGEneListCollectionDM() {
        return useGEneListCollectionDM;
    }

    public void setUseGEneListCollectionDM(Boolean useGEneListCollectionDM) {
        this.useGEneListCollectionDM = useGEneListCollectionDM;
    }
    */
    public Boolean getUseGEneListCollectionKegg() {
        return useGEneListCollectionKegg;
    }

    public void setUseGEneListCollectionKegg(Boolean useGEneListCollectionKegg) {
        this.useGEneListCollectionKegg = useGEneListCollectionKegg;
    }

    public Boolean getUseGEneListCollectionGO() {
        return useGEneListCollectionGO;
    }

    public void setUseGEneListCollectionGO(Boolean useGEneListCollectionGO) {
        this.useGEneListCollectionGO = useGEneListCollectionGO;
    }

    public Integer getCutoffHitsEnrichment() {
        return cutoffHitsEnrichment;
    }

    public void setCutoffHitsEnrichment(Integer cutoffHitsEnrichment) {
        this.cutoffHitsEnrichment = cutoffHitsEnrichment;
    }

    public Integer getNPermutations() {
        return nPermutations;
    }

    public void setNPermutations(Integer nPermutations) {
        this.nPermutations = nPermutations;
    }

    public Integer getExponent() {
        return exponent;
    }

    public void setExponent(Integer exponent) {
        this.exponent = exponent;
    }

    public Integer getMinGeneSetSize() {
        return minGeneSetSize;
    }

    public void setMinGeneSetSize(Integer minGeneSetSize) {
        this.minGeneSetSize = minGeneSetSize;
    }

    public Integer getNGseaPlots() {
        return nGseaPlots;
    }

    public void setNGseaPlots(Integer nGseaPlots) {
        this.nGseaPlots = nGseaPlots;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }


    public String toHTSanalyzeRParameterString() {
        String returnString =
                "  species=c(\"" + species + "\")" +
                ", annotationColumn=\"" + annotationColumn + "\"" +
                ", initialIDs=\"" + initalIDs + "\"" +
                //", duplicateRemoverMethod=\"" + duplicateRemoverMethod + "\"" +
                //", orderAbsValue=" + orderAbsValue.toString().toUpperCase() +
                ", cutoffHitsEnrichment=" + cutoffHitsEnrichment +
                //", nPermutations=" + nPermutations +
                //", exponent=" + exponent +
                ", minGeneSetSize=" + minGeneSetSize;

                if(getUseGEneListCollectionGO()) {
                    returnString += ", goGSCs = c(\"GO_CC\")";
                }
                else {
                    returnString += ", goGSCs = c()";
                }
                if(getUseGEneListCollectionKegg()) {
                    returnString += ", keggGSCs = c(\"PW_KEGG\")";
                }
                else {
                    returnString += ", keggGSCs = c()";                
                }
                //", nGseaPlots=" + nGseaPlots;
                ;
        return returnString;

    }

    public String generateGeneCollectionParameters() {
//setup gene collection prework



        //build up gene collection list
        ArrayList<String> geneCollTempList=new ArrayList<String>();
        /*if(getUseGEneListCollectionDM()) {
            if(getSpecies().contains("Dm"))  {
               geneCollectionSetup+="Dm.GO.CC<-GOGeneSets(species=\"Dm\",ontologies=c(\"CC\"));";
               geneCollTempList.add("Dm.GO.CC=Dm.GO.CC");
            }
        }*/
        String geneCollectionSetup="";
        String listString= "";

        if(getUseGEneListCollectionGO()) {
        
          // geneCollectionSetup+="GO.MF <- GOGeneSets(species = \""+species+"\",ontologies = c(\"MF\"));";
          // geneCollectionSetup+="GO.BP <- GOGeneSets(species = \""+species+"\",ontologies = c(\"BP\"));";
           geneCollectionSetup+="GO_CC <- GOGeneSets(species = \""+species+"\",ontologies = c(\"CC\"));";
            if(listString.equals("")) {
               listString="GO_CC = GO_CC";
            }
            else {
                listString+=", GO_CC = GO_CC";
            }


           /* if(listString.equals("")) {
                listString="GO.MF = GO.MF, GO.BP = GO.BP, GO.CC = GO.CC";
            }
            else {
                listString+=",GO.MF = GO.MF, GO.BP = GO.BP, GO.CC = GO.CC";
            } */
        }
        if(getUseGEneListCollectionKegg()) {
            geneCollectionSetup+="PW_KEGG <- KeggGeneSets(species = \""+species+"\");";
           /*if(getSpecies().contains("Dm"))  {
               geneCollectionSetup+="kegg.droso<-KeggGeneSets(species=\"Dm\");";
               geneCollTempList.add("kegg.droso=kegg.droso");
           } */
            if(listString.equals("")) {
                listString="PW_KEGG = PW_KEGG";
            }
            else {
                listString+=",PW_KEGG = PW_KEGG";
            }


        }


        String collList = "";
        collList = String.format("ListGSC<-list(%s);",listString);

        return geneCollectionSetup+collList;

    }
}
