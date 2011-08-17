package cellHTS.pages;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.services.ExceptionReporter;
import cellHTS.components.Layout;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: Aug 15, 2011
 * Time: 1:00:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExceptionReport implements ExceptionReporter {        
    @Property
    private Throwable exception;

    public void reportException(Throwable exception)
    {
        this.exception = exception;
    }
}
