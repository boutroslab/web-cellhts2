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

package data;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 17.11.2008
 * Time: 14:33:08
 * To change this template use File | Settings | File Templates.
 */
public class DataFile implements Serializable {
    Integer channel = null;
    String fileName=null;
    Integer plateNumber = null;
    Integer replicate = null;



    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DataFile(String fileName,Integer plateNumber,Integer replicate,Integer channel) {
        this.fileName=fileName;
        this.plateNumber= plateNumber;
        this.replicate=replicate;
        this.channel=channel;
    }
    public DataFile(String fileName) {
       this.fileName=fileName;
       this.plateNumber= null;
       this.replicate=null;
       this.channel=null; 
    }
    public DataFile() {
        
    }

    public Integer getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(Integer plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Integer getReplicate() {
        return replicate;
    }

    public void setReplicate(Integer replicate) {
        this.replicate = replicate;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "DataFile{" +
                "channel=" + channel +
                ", fileName='" + fileName + '\'' +
                ", plateNumber=" + plateNumber +
                ", replicate=" + replicate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataFile dataFile = (DataFile) o;

        if (channel != null ? !channel.equals(dataFile.channel) : dataFile.channel != null) return false;
        if (fileName != null ? !fileName.equals(dataFile.fileName) : dataFile.fileName != null) return false;
        if (plateNumber != null ? !plateNumber.equals(dataFile.plateNumber) : dataFile.plateNumber != null)
            return false;
        if (replicate != null ? !replicate.equals(dataFile.replicate) : dataFile.replicate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = channel != null ? channel.hashCode() : 0;
        result = 31 * result + (fileName != null ? fileName.hashCode() : 0);
        result = 31 * result + (plateNumber != null ? plateNumber.hashCode() : 0);
        result = 31 * result + (replicate != null ? replicate.hashCode() : 0);
        return result;
    }
}
