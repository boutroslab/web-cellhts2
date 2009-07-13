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
 *
 * this class is a simple semaphore implementation and used to manage the parallel running instances of the R calculation
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 27.03.2009
 * Time: 10:37:01
 * To change this template use File | Settings | File Templates.
 */
public class SemaphoreImpl implements Semaphore {

    //this is a shared variable so we have to synchronize the access to it
    private Integer maxSpace;

    private Integer maxAvailableSpace;

    private HashSet<Long> waitingThreadIDs;

    private HashSet<Long> runningThreadIDs;


    private String threadID;

    public SemaphoreImpl() {
        maxSpace=null;
        waitingThreadIDs=new HashSet<Long>();
        runningThreadIDs=new HashSet<Long>();
    }



    /**
     *    call by reference:true if we are in
     *    false if we are not able to get in
     *
     * @param errorMsgRef this is a call by ref simul to see the waiting message outside this method
     * @param waitMsg the waiting message which shows up
     * @param threadID the id of the thread
     */
    public synchronized void p(String[]errorMsgRef,String waitMsg,long threadID) { //pass
        if(maxSpace==null) {
            System.out.println("FATAL: have to use initMaxParallelRuns() to init max number of parallel runs before using p(errormsg,waitmsg,id), shutting down VM");
            return;
        }


        //if no room is available ..get in queue and give up active process state

        while (maxSpace==0) {
            if(!waitingThreadIDs.contains(threadID)) {
                waitingThreadIDs.add(threadID);
            }
            
            errorMsgRef[0]=waitMsg;

            try {
                wait();
            }
            catch(InterruptedException e) {

            }
        }
        if(waitingThreadIDs.contains(threadID))   {
            waitingThreadIDs.remove(threadID);
        }
        maxSpace--;
        runningThreadIDs.add(threadID);

    }

    /**
     *
     * the semaphore passing method
     *
     * @param threadID the id of the thread
     */
    public synchronized void p(long threadID) { //pass
        if(maxSpace==null) {
            System.out.println("FATAL: have to use initMaxParallelRuns() to init max number of parallel runs before using p(id), shutting down VM");
            return;
        }
        //if no room is available ..get in queue and give up active process state
        while (maxSpace==0) {
            if(!waitingThreadIDs.contains(threadID)) {
                waitingThreadIDs.add(threadID);
            }
            try {
                wait();
            }
            catch(InterruptedException e) {

            }
        }
        if(waitingThreadIDs.contains(threadID))   {
            waitingThreadIDs.remove(threadID);
        }
        maxSpace--;
        runningThreadIDs.add(threadID);

    }

    /**
     *
     * a typical v() semaphore method :-)
     *
     * @param threadID
     */
    public synchronized void v(long threadID) {//verlassen
        if(maxSpace==null) {
            System.out.println("FATAL: have to use initMaxParallelRuns() to init max number of parallel runs before using v(id), shutting down VM");
            return;
        }
        if(runningThreadIDs.contains(threadID))   {
            runningThreadIDs.remove(threadID);
            maxSpace++;
         //wake up
            notify();
        }


    }

    /**
     *
     * remove a thread ID from the queue (if died etc.)
     *
     * @param threadID
     */
    public synchronized void removeThreadID(long threadID) {
        if(waitingThreadIDs.contains(threadID)){
            waitingThreadIDs.remove(threadID);
        }
        else {
            System.out.println(threadID+" is not available on the stack");
            System.out.print("Available IDs:");
            for (Long key : waitingThreadIDs) {
                System.out.print(waitingThreadIDs+",");
            }
            System.out.println();

        }
    }

    /**
     *
     * say how many threads are allowed to run in parallel
     *
     * @param maxSpace
     */
    public void initMaxParallelRuns(int maxSpace) {
         if(this.maxSpace!=null) {
             return;
         }
         if(maxSpace<0) {          
             this.maxSpace=0;
         }
         else {
             this.maxSpace=maxSpace;
         }

         //get the original init value
         maxAvailableSpace=this.maxSpace;
    }

    public Integer getMaxSpace() {
        return maxSpace;
    }

    public Integer getMaxAvailableSpace() {
        return maxAvailableSpace;
    }

    public HashSet<Long> getWaitingThreadIDs() {
        return waitingThreadIDs;
    }

    /**
     *
     * remove running jobs from the queueing system
     *
     * @param jobID  the name to be removed
     */
    public void removeRunningJob(long jobID) {
        if(runningThreadIDs.contains(jobID)) {
           runningThreadIDs.remove(jobID);
           maxSpace++;
        }
    }
    
}
