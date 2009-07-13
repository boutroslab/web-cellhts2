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

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 27.03.2009
 * Time: 10:53:56
 * To change this template use File | Settings | File Templates.
 */
public interface Semaphore {
    public void p(long threadID);

    public void p(String[] errorMsgRef, String errorMsg, long threadID);

    public void v(long threadID);

    public void initMaxParallelRuns(int maxSpace);

    public Integer getMaxSpace();

    public Integer getMaxAvailableSpace();

    public HashSet<Long> getWaitingThreadIDs();

    public void removeRunningJob(long threadID);

}


