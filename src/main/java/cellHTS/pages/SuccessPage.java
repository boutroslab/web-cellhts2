package cellHTS.pages;

import org.apache.tapestry5.annotations.InjectPage;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 29.09.2009
 * Time: 16:17:41
 * To change this template use File | Settings | File Templates.
 */
public class SuccessPage {
    @InjectPage
    private SuccessCellHTS2 successPage;

    
    public void setZipFile(String file)  {
        successPage.setZipFile(file);
    }
    public Object onActionFromDownloadResults() {
        if(successPage.getZipFile()!=null) {
            return successPage;
        }
        else {
            return this;
        }
    }
}
