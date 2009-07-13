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

package cellHTS.dao;

/**
 *
 *
 * This class is an injectable interface which lives as long as your VM lives
 * it can be used to count number of ssessions in your JVM etc.
 *
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 17.03.2009
 * Time: 12:44:54
 * To change this template use File | Settings | File Templates.
 */
public class SessionImpl implements Session {

    Integer sessionNumber;

    public SessionImpl() {
        sessionNumber=0;
    }

    public Integer getCurrentSessionNumber() {
        return sessionNumber;
    }
    public void addSession() {
        sessionNumber++;

    }
    public void removeSession() {         
        if(sessionNumber>0) {
            sessionNumber--;
        }
    }
}
