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

package cellHTS.pages;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.annotations.Persist;
import cellHTS.dao.Semaphore;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * this class will be used as a infomration page how many jobs are currently running and further thread information such as
 * how many slots are still free
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 02.04.2009
 * Time: 13:09:51
 * To change this template use File | Settings | File Templates.
 */
public class QueueInfo {
    //this page outputs info about the semaphore a.k.a. all the current processes
    //and information about threads
    @Inject
    private Semaphore semaphore;

    @Persist
    private int freeSlots;
    @Persist
    private int availableSlots;
    @Persist
    private String currentTime;

    @Persist
    private Long currentWaitingThreads;

    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

    public void onActivate() {
        currentTime = now();
        availableSlots = semaphore.getMaxAvailableSpace();
        freeSlots = semaphore.getMaxSpace();
        long waitingThreads=semaphore.getWaitingThreadIDs().size();
        currentWaitingThreads = waitingThreads;
    }


    public static String now() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }


    //getter 

   

    public int getFreeSlots() {
        return freeSlots;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public Long getCurrentWaitingThreads() {
        return currentWaitingThreads;
    }
}
