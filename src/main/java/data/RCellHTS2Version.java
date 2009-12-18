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
    
public class RCellHTS2Version {

    private String buildCellHTS2Version;

    private String versionCellHTS2Version;

    private String cellHTS2Version;

    public RCellHTS2Version(String buildCellHTS2Version, String versionCellHTS2Version, String cellHTS2Version) {
        this.buildCellHTS2Version = buildCellHTS2Version;
        this.versionCellHTS2Version = versionCellHTS2Version;
        this.cellHTS2Version = cellHTS2Version;
    }
    public String getBuildCellHTS2Version() {
        return buildCellHTS2Version;
    }

    public void setBuildCellHTS2Version(String buildCellHTS2Version) {
        this.buildCellHTS2Version = buildCellHTS2Version;
    }

    public String getVersionCellHTS2Version() {
        return versionCellHTS2Version;
    }

    public void setVersionCellHTS2Version(String versionCellHTS2Version) {
        this.versionCellHTS2Version = versionCellHTS2Version;
    }

    public String getCellHTS2Version() {
        return cellHTS2Version;
    }

    public void setCellHTS2Version(String cellHTS2Version) {
        this.cellHTS2Version = cellHTS2Version;
    }
}
