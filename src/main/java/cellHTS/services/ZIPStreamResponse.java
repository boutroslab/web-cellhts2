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

package cellHTS.services;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.services.Response;

import java.io.InputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 11.12.2008
 * Time: 15:26:50
 * To change this template use File | Settings | File Templates.
 */
    public class ZIPStreamResponse  implements StreamResponse {
            private InputStream is;
            private String filename="default";

            public ZIPStreamResponse(InputStream is, String filename) {
                    this.is = is;
                    this.filename = filename;

            }

            public String getContentType() {
                    return "application/zip";
            }

            public InputStream getStream() throws IOException {
                    return is;
            }

            public void prepareResponse(Response arg0) {
                    arg0.setHeader("Content-Disposition", "attachment; filename="
                                    + filename);
            }
    }


