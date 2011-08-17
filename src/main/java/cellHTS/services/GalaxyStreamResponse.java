package cellHTS.services;

import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.StreamResponse;

import java.io.InputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: Apr 26, 2011
 * Time: 4:53:57 PM
 * To change this template use File | Settings | File Templates.
 */
//for streaming back to galaxy
public class GalaxyStreamResponse  implements StreamResponse {
       private InputStream is = null;

        protected String contentType = "text/tab-separated-values";//"text/plain";// this is the default

            public GalaxyStreamResponse(InputStream is, String filename) {
                    this.is = is;
                   

            }
        public String getContentType() {
                return contentType;
        }

        public InputStream getStream() throws IOException {
                return is;
        }

        public void prepareResponse(Response arg0) {
                arg0.setHeader("Content-Disposition", "attachment; filename=\"result.tsv\"" );
        }

}
