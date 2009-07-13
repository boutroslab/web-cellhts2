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

package cellHTS.classes;

/**
 *
 * //this is just a info class which will return all the currently running java threads public
 *
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 12.12.2008
 * Time: 15:52:57
 *
 *
 */
 class ThreadLister {
  private static String printThreadInfo(Thread t, String indent) {
    if (t == null)
      return "no thread was found";
      return indent + "Thread: " + t.getName() + "  Priority: "
        + t.getPriority() + (t.isDaemon() ? " Daemon" : "")
        + (t.isAlive() ? "" : " Not Alive");
  }


    /**
     *
     * Display info about a thread group 
     *
     * @param g   ThreadGroup
     * @param indent  indent
     * @return the Group Info as String
     */
  private static String printGroupInfo(ThreadGroup g, String indent) {
    String returnText="";
    if (g == null)
      return "";
    int numThreads = g.activeCount();
    int numGroups = g.activeGroupCount();
    Thread[] threads = new Thread[numThreads];
    ThreadGroup[] groups = new ThreadGroup[numGroups];

    g.enumerate(threads, false);
    g.enumerate(groups, false);

    System.out.println(indent + "Thread Group: " + g.getName()
        + "  Max Priority: " + g.getMaxPriority()
        + (g.isDaemon() ? " Daemon" : ""));

    for (int i = 0; i < numThreads; i++)
      returnText+=printThreadInfo(threads[i], indent + "    ")+"\n";
    for (int i = 0; i < numGroups; i++)
      returnText+=printGroupInfo(groups[i], indent + "    ")+"\n";
      return returnText;
  }


    /**
     *
     *   Find the root thread group and list it recursively 
     */     
  public static void listAllThreads() {
    ThreadGroup currentThreadGroup;
    ThreadGroup rootThreadGroup;
    ThreadGroup parent;

    // Get the current thread group
    currentThreadGroup = Thread.currentThread().getThreadGroup();

    // Now go find the root thread group
    rootThreadGroup = currentThreadGroup;
    parent = rootThreadGroup.getParent();
    while (parent != null) {
      rootThreadGroup = parent;
      parent = parent.getParent();
    }

    printGroupInfo(rootThreadGroup, "");
  }

  
}