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
    //private Boolean useGEneListCollectionDM; //gsc.list<-list(Dm.GO.CC=Dm.GO.CC,kegg.droso=kegg.droso)"
    private Boolean useGEneListCollectionKegg;
    private Boolean useGEneListCollectionGO;
    private Integer cutoffHitsEnrichment;
    private Integer nPermutations;
    private Integer exponent;
    private Integer minGeneSetSize;
    //parameters for network analysis
    private Integer nGseaPlots;
    

    public HTSAnalyzerParameter(String species,String annotationColumn, String initalIDs,String duplicateRemoverMethod) {
        this.species=species;
        this.annotationColumn=annotationColumn;
        this.initalIDs=initalIDs;
        this.duplicateRemoverMethod=duplicateRemoverMethod;
        /*species="Dm";
        annotationColumn="GeneID";
        initalIDs="FlybaseCG";
        duplicateRemoverMethod="max"; */
        orderAbsValue=false;
        //useGEneListCollectionDM=false;
        useGEneListCollectionKegg=false;
        useGEneListCollectionGO=true;
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
        return                      
                "  species=c(\"" + species + "\")" +
                ", annotationColumn=\"" + annotationColumn + "\"" +
                ", initialIDs=\"" + initalIDs + "\"" +
                ", duplicateRemoverMethod=\"" + duplicateRemoverMethod + "\"" +
                ", orderAbsValue=" + orderAbsValue.toString().toUpperCase() +
                ", cutoffHitsEnrichment=" + cutoffHitsEnrichment +
                ", nPermutations=" + nPermutations +
                ", exponent=" + exponent +
                ", minGeneSetSize=" + minGeneSetSize +//+
                ", goGSCs=c(\"GO.MF\",\"GO.BP\",\"GO.CC\")"
                //", nGseaPlots=" + nGseaPlots;
                ;

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
        
           geneCollectionSetup+="GO.MF <- GOGeneSets(species = \""+species+"\",ontologies = c(\"MF\"));";
           geneCollectionSetup+="GO.BP <- GOGeneSets(species = \""+species+"\",ontologies = c(\"BP\"));";
           geneCollectionSetup+="GO.CC <- GOGeneSets(species = \""+species+"\",ontologies = c(\"CC\"));";
            if(listString.equals("")) {
                listString="GO.MF = GO.MF, GO.BP = GO.BP, GO.CC = GO.CC";
            }
            else {
                listString+=",GO.MF = GO.MF, GO.BP = GO.BP, GO.CC = GO.CC";
            }
        }
        if(getUseGEneListCollectionKegg()) {
            geneCollectionSetup+="PW.KEGG <- KeggGeneSets(species = \""+species+"\");";
           /*if(getSpecies().contains("Dm"))  {
               geneCollectionSetup+="kegg.droso<-KeggGeneSets(species=\"Dm\");";
               geneCollTempList.add("kegg.droso=kegg.droso");
           } */
            if(listString.equals("")) {
                listString="PW.KEGG = PW.KEGG";
            }
            else {
                listString+=",PW.KEGG = PW.KEGG";
            }
        }


        String collList = "";
        collList = String.format("gsc.list<-list(%s);",listString);

        return geneCollectionSetup+collList;

    }
}
