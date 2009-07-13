/*
 * //
 * // Copyright (C) 2009 Boutros-Labs(German cancer research center) b110-it@dkfz.de
 * //
 * //
 * //    This program is free software: you can redistribute it and/or modify
 * //    it under the terms of the GNU General Public License as published by
 * //    the Free Software Foundation, either version 3 of the License, or
 * //    (at your option) any later version.
 * //
 * //    This program is distributed in the hope that it will be useful,
 * //    but WITHOUT ANY WARRANTY; without even the implied warranty of
 * //    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * //
 * //    You should have received a copy of the GNU General Public License
 * //    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package data;

import data.*;

import java.io.Serializable;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.ArrayList;

import org.apache.tapestry5.annotations.Persist;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 23.01.2009
 * Time: 12:54:03
 * To change this template use File | Settings | File Templates.
 */
//this class will store all the information/members of cellHTS2 which is needed to store a whole session
//this comes in handy if you want to analyse lots of data with all the same plateConfigs and stuff
public class PersistentCellHTS2 implements Serializable {

    //all the obj ref variables we want to make persistent
    private Experiment experiment;
    private HashMap<Integer, Boolean> activatedPages;
    private Map<String, String> descriptionMap;
    private int posWellAmount;
    private int negWellAmount;
    private String descriptionFile;
    private String annotFile;
    private String plateListFile;
    private String plateConfFile;
    private boolean isDualChannel;
    private String channelLabel1;
    private String channelLabel2;
    private Integer plateFormat;
    private ChannelTypes channel;
    private PlateTypes plate;
    private NormalizationTypes normalTypes;
    private LogTransform logTransform;
    private NormalScalingTypes normalScaling;
    private ResultsScalingTypes resultsScaling;
    private SummerizeReplicates sumRep;
    private boolean noErrorUploadFile;
    private boolean noErrorPlateConfFile;
    private boolean noErrorAnnotFile;
    private boolean noErrorDescriptionFile;
    private boolean validateErrorFileView;

    private String errorDatafileMsg;
    private String errorPlateconfFileMsg;
    private String errorAnnotFileMsg;
    private String errorDescriptionFileMsg;
    
    private String enableWizardsNewPage;
    private boolean parseFileParams;
    private boolean backDisable;
    private boolean nextDisable;
    private int currentPagePointer;
    private boolean errorNextLink;
    private String nextLinkErrorMsg;
//    private String uploadDir;
//    private String downloadDir;
//    private String jobName;
    private File jobNameDir;
    private HashMap<String, String> sampleWellMap;
    private HashMap<String, DataFile> dataFileList;
    private HashSet<String> excludeFilesFromParsing;
    private int plateAmount;
    private HashMap<String,HashMap<String,String>> contaminatedWellMap;
    private String screenLogFile;
    private ArrayList<String> contaminatedPlatesAssoc;
    private int repAmount;
    private ArrayList<Plate> clickedWellsAndPlates;
    private ViabilityChannel viabilityChannel;
    private String  emailAddress;
    private boolean isEmailMandantory;
    private String fixRegExp;
    private String viabilityFunction;

   //TODO:somethings messed up with the constructor..plateconfig is plateannot
    public PersistentCellHTS2(
                              Experiment experiment,
                              HashMap<Integer, Boolean> activatedPages,
                              Map<String, String> descriptionMap,
                              int posWellAmount,
                              int negWellAmount,
                              String descriptionFile,
                              String plateListFile,
                              String annotFile,
                              String plateConfFile,
                              boolean dualChannel,
                              String channelLabel1,
                              String channelLabel2,
                              Integer plateFormat,
                              ChannelTypes channel,
                              PlateTypes plate,
                              NormalizationTypes normalTypes,
                              LogTransform logTransform,
                              NormalScalingTypes normalScaling,
                              ResultsScalingTypes resultsScaling,
                              SummerizeReplicates sumRep,
                              boolean noErrorUploadFile,
                              boolean noErrorPlateConfFile,
                              boolean noErrorAnnotFile,
                              boolean noErrorDescriptionFile,
                              boolean validateErrorFileView,
                              String errorDatafileMsg,
                              String errorPlateconfFileMsg,
                              String errorAnnotFileMsg,
                              String errorDescriptionFileMsg,
                              String enableWizardsNewPage,
                              boolean parseFileParams,
                              boolean backDisable,
                              boolean nextDisable,
                              int currentPagePointer,
                              boolean errorNextLink,
                              String nextLinkErrorMsg,
                            //  String uploadDir,
                            //  String downloadDir,
                            //  String jobName,
                              File jobNameDir,
                              HashMap<String, DataFile> dataFileList,
                              HashSet<String> excludeFilesFromParsing,
                              String screenLogFile,
                              ArrayList<Plate> clickedWellsAndPlates,
                              ViabilityChannel viabilityChannel,
                              String emailAddress,
                              boolean isEmailMandantory,
                              String fixRegExp,
                              String viabilityFunction
                              ) {

        this.experiment = experiment;
        this.activatedPages = activatedPages;
        this.descriptionMap = descriptionMap;
        this.posWellAmount = posWellAmount;
        this.negWellAmount = negWellAmount;
        this.descriptionFile = descriptionFile;
        this.plateListFile = plateListFile;
        this.plateConfFile = plateConfFile;
        isDualChannel = dualChannel;
        this.channelLabel1 = channelLabel1;
        this.channelLabel2 = channelLabel2;

        this.plateFormat = plateFormat;
        this.channel = channel;
        this.plate = plate;
        this.normalTypes = normalTypes;
        this.logTransform = logTransform;
        this.normalScaling = normalScaling;
        this.resultsScaling = resultsScaling;
        this.sumRep = sumRep;
        this.noErrorUploadFile = noErrorUploadFile;
        this.noErrorPlateConfFile = noErrorPlateConfFile;
        this.noErrorAnnotFile = noErrorAnnotFile;
        this.noErrorDescriptionFile = noErrorDescriptionFile;
        this.validateErrorFileView = validateErrorFileView;

        this.errorDatafileMsg = errorDatafileMsg;
        this.errorPlateconfFileMsg = errorPlateconfFileMsg;
        this.errorAnnotFileMsg = errorAnnotFileMsg;
        this.errorDescriptionFileMsg = errorDescriptionFileMsg;

        this.enableWizardsNewPage = enableWizardsNewPage;
        this.parseFileParams = parseFileParams;
        this.backDisable = backDisable;
        this.nextDisable = nextDisable;
        this.currentPagePointer = currentPagePointer;
        this.errorNextLink = errorNextLink;
        this.nextLinkErrorMsg = nextLinkErrorMsg;
//        this.uploadDir = uploadDir;
//        this.downloadDir = downloadDir;
//        this.jobName = jobName;
        this.jobNameDir=jobNameDir;
        this.sampleWellMap = sampleWellMap;
        this.dataFileList=dataFileList;
        this.excludeFilesFromParsing=excludeFilesFromParsing;
        this.plateAmount=plateAmount;
        this.contaminatedWellMap=contaminatedWellMap;
        this.screenLogFile=screenLogFile;
        this.contaminatedPlatesAssoc=contaminatedPlatesAssoc;
        this.repAmount = repAmount;
        this.annotFile=annotFile;
        this.clickedWellsAndPlates=clickedWellsAndPlates;
        this.viabilityChannel = viabilityChannel;
        this.emailAddress=emailAddress;
        this.isEmailMandantory=isEmailMandantory;
        this.fixRegExp=fixRegExp;
        this.viabilityFunction=viabilityFunction;
    }
    public PersistentCellHTS2(){
        
    }

    public Experiment getExperiment() {
        return experiment;
    }

    public void setExperiment(Experiment experiment) {
        this.experiment = experiment;
    }

    public HashMap<Integer, Boolean> getActivatedPages() {
        return activatedPages;
    }

    public void setActivatedPages(HashMap<Integer, Boolean> activatedPages) {
        this.activatedPages = activatedPages;
    }

    public Map<String, String> getDescriptionMap() {
        return descriptionMap;
    }

    public void setDescriptionMap(Map<String, String> descriptionMap) {
        this.descriptionMap = descriptionMap;
    }

    public int getPosWellAmount() {
        return posWellAmount;
    }

    public void setPosWellAmount(int posWellAmount) {
        this.posWellAmount = posWellAmount;
    }

    public int getNegWellAmount() {
        return negWellAmount;
    }

    public void setNegWellAmount(int negWellAmount) {
        this.negWellAmount = negWellAmount;
    }

    public String getDescriptionFile() {
        return descriptionFile;
    }

    public void setDescriptionFile(String descriptionFile) {
        this.descriptionFile = descriptionFile;
    }

    public String getPlateListFile() {
        return plateListFile;
    }

    public void setPlateListFile(String plateListFile) {
        this.plateListFile = plateListFile;
    }

    public String getPlateConfFile() {
        return plateConfFile;
    }

    public void setPlateConfFile(String plateConfFile) {
        this.plateConfFile = plateConfFile;
    }

    public boolean isDualChannel() {
        return isDualChannel;
    }

    public void setDualChannel(boolean dualChannel) {
        isDualChannel = dualChannel;
    }

    public String getChannelLabel1() {
        return channelLabel1;
    }

    public void setChannelLabel1(String channelLabel1) {
        this.channelLabel1 = channelLabel1;
    }

    public String getChannelLabel2() {
        return channelLabel2;
    }

    public void setChannelLabel2(String channelLabel2) {
        this.channelLabel2 = channelLabel2;
    }

    

    public Integer getPlateFormat() {
        return plateFormat;
    }

    public void setPlateFormat(Integer plateFormat) {
        this.plateFormat = plateFormat;
    }

    public ChannelTypes getChannel() {
        return channel;
    }

    public void setChannel(ChannelTypes channel) {
        this.channel = channel;
    }

    public PlateTypes getPlate() {
        return plate;
    }

    public void setPlate(PlateTypes plate) {
        this.plate = plate;
    }

    public NormalizationTypes getNormalTypes() {
        return normalTypes;
    }

    public void setNormalTypes(NormalizationTypes normalTypes) {
        this.normalTypes = normalTypes;
    }

    public LogTransform getLogTransform() {
        return logTransform;
    }

    public void setLogTransform(LogTransform logTransform) {
        this.logTransform = logTransform;
    }

    public NormalScalingTypes getNormalScaling() {
        return normalScaling;
    }

    public void setNormalScaling(NormalScalingTypes normalScaling) {
        this.normalScaling = normalScaling;
    }

    public ResultsScalingTypes getResultsScaling() {
        return resultsScaling;
    }

    public void setResultsScaling(ResultsScalingTypes resultsScaling) {
        this.resultsScaling = resultsScaling;
    }

    public SummerizeReplicates getSumRep() {
        return sumRep;
    }

    public void setSumRep(SummerizeReplicates sumRep) {
        this.sumRep = sumRep;
    }

    public boolean isNoErrorUploadFile() {
        return noErrorUploadFile;
    }

    public void setNoErrorUploadFile(boolean noErrorUploadFile) {
        this.noErrorUploadFile = noErrorUploadFile;
    }

    public boolean isNoErrorPlateConfFile() {
        return noErrorPlateConfFile;
    }

    public void setNoErrorPlateConfFile(boolean noErrorPlateConfFile) {
        this.noErrorPlateConfFile = noErrorPlateConfFile;
    }

    public boolean isNoErrorAnnotFile() {
        return noErrorAnnotFile;
    }

    public void setNoErrorAnnotFile(boolean noErrorAnnotFile) {
        this.noErrorAnnotFile = noErrorAnnotFile;
    }

    public boolean isNoErrorDescriptionFile() {
        return noErrorDescriptionFile;
    }

    public void setNoErrorDescriptionFile(boolean noErrorDescriptionFile) {
        this.noErrorDescriptionFile = noErrorDescriptionFile;
    }

    public boolean isValidateErrorFileView() {
        return validateErrorFileView;
    }

    public void setValidateErrorFileView(boolean validateErrorFileView) {
        this.validateErrorFileView = validateErrorFileView;
    }


    public String getErrorDatafileMsg() {
        return errorDatafileMsg;
    }

    public void setErrorDatafileMsg(String errorDatafileMsg) {
        this.errorDatafileMsg = errorDatafileMsg;
    }

    public String getErrorPlateconfFileMsg() {
        return errorPlateconfFileMsg;
    }

    public void setErrorPlateconfFileMsg(String errorPlateconfFileMsg) {
        this.errorPlateconfFileMsg = errorPlateconfFileMsg;
    }

    public String getErrorAnnotFileMsg() {
        return errorAnnotFileMsg;
    }

    public void setErrorAnnotFileMsg(String errorAnnotFileMsg) {
        this.errorAnnotFileMsg = errorAnnotFileMsg;
    }

    public String getErrorDescriptionFileMsg() {
        return errorDescriptionFileMsg;
    }

    public void setErrorDescriptionFileMsg(String errorDescriptionFileMsg) {
        this.errorDescriptionFileMsg = errorDescriptionFileMsg;
    }

    public String getEnableWizardsNewPage() {
        return enableWizardsNewPage;
    }

    public void setEnableWizardsNewPage(String enableWizardsNewPage) {
        this.enableWizardsNewPage = enableWizardsNewPage;
    }

    public boolean isParseFileParams() {
        return parseFileParams;
    }

    public void setParseFileParams(boolean parseFileParams) {
        this.parseFileParams = parseFileParams;
    }

    public boolean isBackDisable() {
        return backDisable;
    }

    public void setBackDisable(boolean backDisable) {
        this.backDisable = backDisable;
    }

    public boolean isNextDisable() {
        return nextDisable;
    }

    public void setNextDisable(boolean nextDisable) {
        this.nextDisable = nextDisable;
    }

    public int getCurrentPagePointer() {
        return currentPagePointer;
    }

    public void setCurrentPagePointer(int currentPagePointer) {
        this.currentPagePointer = currentPagePointer;
    }

    public boolean isErrorNextLink() {
        return errorNextLink;
    }

    public void setErrorNextLink(boolean errorNextLink) {
        this.errorNextLink = errorNextLink;
    }

    public String getNextLinkErrorMsg() {
        return nextLinkErrorMsg;
    }

    public void setNextLinkErrorMsg(String nextLinkErrorMsg) {
        this.nextLinkErrorMsg = nextLinkErrorMsg;
    }

    public File getJobNameDir() {
        return jobNameDir;
    }

    public void setJobNameDir(File jobNameDir) {
        this.jobNameDir = jobNameDir;
    }

    public HashMap<String, String> getSampleWellMap() {
        return sampleWellMap;
    }

    public void setSampleWellMap(HashMap<String, String> sampleWellMap) {
        this.sampleWellMap = sampleWellMap;
    }

    public HashMap<String, DataFile> getDataFileList() {
        return dataFileList;
    }

    public void setDataFileList(HashMap<String, DataFile> dataFileList) {
        this.dataFileList = dataFileList;
    }

    public HashSet<String> getExcludeFilesFromParsing() {
        return excludeFilesFromParsing;
    }

    public void setExcludeFilesFromParsing(HashSet<String> excludeFilesFromParsing) {
        this.excludeFilesFromParsing = excludeFilesFromParsing;
    }

    public int getPlateAmount() {
        return plateAmount;
    }

    public void setPlateAmount(int plateAmount) {
        this.plateAmount = plateAmount;
    }

    public HashMap<String, HashMap<String, String>> getContaminatedWellMap() {
        return contaminatedWellMap;
    }

    public void setContaminatedWellMap(HashMap<String, HashMap<String, String>> contaminatedWellMap) {
        this.contaminatedWellMap = contaminatedWellMap;
    }

    public String getScreenLogFile() {
        return screenLogFile;
    }

    public void setScreenLogFile(String screenLogFile) {
        this.screenLogFile = screenLogFile;
    }

    public ArrayList<String> getContaminatedPlatesAssoc() {
        return contaminatedPlatesAssoc;
    }

    public void setContaminatedPlatesAssoc(ArrayList<String> contaminatedPlatesAssoc) {
        this.contaminatedPlatesAssoc = contaminatedPlatesAssoc;
    }

    public int getRepAmount() {
        return repAmount;
    }

    public void setRepAmount(int repAmount) {
        this.repAmount = repAmount;
    }

    public String getAnnotFile() {
        return annotFile;
    }

    public void setAnnotFile(String annotFile) {
        this.annotFile = annotFile;
    }

    public ArrayList<Plate> getClickedWellsAndPlates() {
        return clickedWellsAndPlates;
    }

    public void setClickedWellsAndPlates(ArrayList<Plate> clickedWellsAndPlates) {
        this.clickedWellsAndPlates = clickedWellsAndPlates;
    }
    public ViabilityChannel getViabilityChannel() {
        return viabilityChannel;
    }

    public void setViabilityChannel(ViabilityChannel viabilityChannel) {
        this.viabilityChannel = viabilityChannel;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public boolean isEmailMandantory() {
        return isEmailMandantory;
    }

    public void setEmailMandantory(boolean emailMandantory) {
        isEmailMandantory = emailMandantory;
    }

    public String getFixRegExp() {
        return fixRegExp;
    }

    public void setFixRegExp(String fixRegExp) {
        this.fixRegExp = fixRegExp;
    }

    public String getViabilityFunction() {
        return viabilityFunction;
    }

    public void setViabilityFunction(String viabilityFunction) {
        this.viabilityFunction = viabilityFunction;
    }
}