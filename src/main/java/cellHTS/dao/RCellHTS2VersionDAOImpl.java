package cellHTS.dao;


import java.io.File;
import java.io.FileInputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationGlobals;

import cellHTS.classes.RInterface;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 10.12.2009
 * Time: 14:38:42
 * To change this template use File | Settings | File Templates.
 */

//this class receives all the R version etc stuff
    
public class RCellHTS2VersionDAOImpl implements RCellHTS2VersionDAO{
	
	@Inject
	private Messages msg;
	@Inject
    private ApplicationGlobals applicationGlobals;
	
	private RCellHTS2VersionDAOImpl rCellHTS2Version;
	
    private String buildCellHTS2Version;

    private String versionCellHTS2Version;

    private String cellHTS2Version;
    
    

    public RCellHTS2VersionDAOImpl() {
    	RInterface rInterface = new RInterface();
        String cellHTS2Version = rInterface.getCellHTS2Version();
        init();
    }
    public void init() {    	
            //check the sessionstate variables 
    			try {
    				String path = applicationGlobals.getServletContext().getRealPath(File.separator);
                //this will actually start and end a rserver instance
                
                    File manifestFile = new File(path, "META-INF/MANIFEST.MF");
                    Manifest mf = new Manifest();
                    mf.read(new FileInputStream(manifestFile));
                    Attributes atts = mf.getMainAttributes();
                    buildCellHTS2Version = atts.getValue("Implementation-Build");
                    versionCellHTS2Version = atts.getValue("Implementation-Version");

                } catch (Exception e) {
                	 //when we are running through a jetty we cant get the MANIFEST so we will
                    //output not available
                	buildCellHTS2Version = "<not available>";
                	versionCellHTS2Version = "<not available>";

                }              
                RInterface rInterface = new RInterface();
                cellHTS2Version = rInterface.getCellHTS2Version();     
    }
    
    
    public String getBuildCellHTS2Version() {
        return buildCellHTS2Version;
    }
    public String getVersionCellHTS2Version() {
        return versionCellHTS2Version;
    }
    public String getCellHTS2Version() {
        return cellHTS2Version;
    }
}
