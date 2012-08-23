package data;

import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.Persist;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 10.12.2009
 * Time: 14:38:42
 * To change this template use File | Settings | File Templates.
 */

//this class receives all the R version etc stuff
    
public class RInformation {

    private String rVersion;

    private String cellHTS2Version;
    
    private String rServeVersion;

    //this boolean shows if R was compiled with zip functionality
    private boolean rWithZipFunction;

    public RInformation(String rVersion, String cellHTS2Version, String rServeVersion, boolean RWithZipFunction) {
        this.rVersion = rVersion;
        this.cellHTS2Version = cellHTS2Version;
        this.rWithZipFunction = RWithZipFunction;
        this.rServeVersion = rServeVersion;
    }

    public String getRVersion() {
        return rVersion;
    }

    public void setRVersion(String rVersion) {
        this.rVersion = rVersion;
    }

    public String getCellHTS2Version() {
        return cellHTS2Version;
    }

    public void setCellHTS2Version(String cellHTS2Version) {
        this.cellHTS2Version = cellHTS2Version;
    }

    public boolean isRWithZipFunction() {
        return rWithZipFunction;
    }

    public void setRWithZipFunction(boolean rWithZipFunction) {
        this.rWithZipFunction = rWithZipFunction;
    }

	public String getrServeVersion() {
		return rServeVersion;
	}

	public void setrServeVersion(String rServeVersion) {
		this.rServeVersion = rServeVersion;
	} 
    
}
