package data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 25.08.2010
 * Time: 11:06:33
 * To change this template use File | Settings | File Templates.
 */
public class HTSAnalyzerParameter implements Serializable {
    private String species;
    //general parameters
    private String annotationColumn;
    private String initalIDs;
   
    private String duplicateRemoverMethod;
    private Boolean orderAbsValue;
    private Boolean useGEneListCollectionDM; //gsc.list<-list(Dm.GO.CC=Dm.GO.CC,kegg.droso=kegg.droso)"
    private Boolean useGEneListCollectionKegg;
    private Boolean useGEneListCollectionGO;
    private Integer cutoffHitsEnrichment;
    private Integer nPermutations;
    private Integer exponent;
    private Integer minGeneSetSize;
    //parameters for network analysis
    private Integer nGseaPlots;
    

    public HTSAnalyzerParameter() {
        species="Drosophila_melanogaster";
        annotationColumn="GeneID";
        initalIDs="FlybaseCG";
        duplicateRemoverMethod="max";
        orderAbsValue=false;
        useGEneListCollectionDM=true;
        useGEneListCollectionKegg=true;
        useGEneListCollectionGO=false;
        cutoffHitsEnrichment=2;
        nPermutations=1000;
        exponent=1;
        minGeneSetSize=5;
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

    public Boolean getUseGEneListCollectionDM() {
        return useGEneListCollectionDM;
    }

    public void setUseGEneListCollectionDM(Boolean useGEneListCollectionDM) {
        this.useGEneListCollectionDM = useGEneListCollectionDM;
    }

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
        return                      
                "  species=c(\"" + species + "\")" +
                ", annotationColumn=\"" + annotationColumn + "\"" +
                ", initialIDs=\"" + initalIDs + "\"" +
                ", duplicateRemoverMethod=\"" + duplicateRemoverMethod + "\"" +
                ", orderAbsValue=" + orderAbsValue.toString().toUpperCase() +
                ", cutoffHitsEnrichment=" + cutoffHitsEnrichment +
                ", nPermutations=" + nPermutations +
                ", exponent=" + exponent +
                ", minGeneSetSize=" + minGeneSetSize +
                ", nGseaPlots=" + nGseaPlots;


    }

    public String generateGeneCollectionParameters() {
//setup gene collection prework
        String geneCollectionSetup="";


        //build up gene collection list
        ArrayList<String> geneCollTempList=new ArrayList<String>();
        if(getUseGEneListCollectionDM()) {
            if(getSpecies().contains("Drosophila"))  {
               geneCollectionSetup+="Dm.GO.CC<-GOGeneSets(species=\"Drosophila_melanogaster\",ontologies=c(\"CC\"));";
               geneCollTempList.add("Dm.GO.CC=Dm.GO.CC");
            }


        }
        if(getUseGEneListCollectionGO()) {
           // setupGeneCollection+="
           //geneCollTempList.add();
        }
        if(getUseGEneListCollectionKegg()) {
           if(getSpecies().contains("Drosophila"))  {
               geneCollectionSetup+="kegg.droso<-KeggGeneSets(species=\"Drosophila_melanogaster\");";
               geneCollTempList.add("kegg.droso=kegg.droso");
           }
        }
        if(geneCollectionSetup.equals("")) {
            return "";
        }
        //join
        String geneCollTempListString="";
        for(String tmp : geneCollTempList) {
            if(geneCollTempListString.equals("")) {
               geneCollTempListString=tmp;
            }
            else {
                geneCollTempListString+=","+tmp;
            }
        }

        String collList = "";
        collList = String.format("gsc.list<-list(%s)",geneCollTempListString);


        return geneCollectionSetup+collList;

    }
}
