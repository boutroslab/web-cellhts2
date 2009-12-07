package cellHTS.classes;

/**
 * Created by IntelliJ IDEA.
 * User: oliverpelz
 * Date: 23.11.2009
 * Time: 17:59:21
 * To change this template use File | Settings | File Templates.
 */
//we use this inner class to keep track of the changed values in the drop down
    //we need this to build it dynamically
    public class SelectedColumn {
        private String columnName;
        private String mappedToColumn;
        private Integer columnNumber;
    //if we map some column to more than one columns
        private String mappedToColumns;

        public SelectedColumn(String columnName) {
            this.columnName = columnName;
        }
        public SelectedColumn() {

        }


        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;

        }

        public String getMappedToColumn() {
            return mappedToColumn;
        }

        public void setMappedToColumn(String mappedToColumn) {
            if(mappedToColumn==null) {
                return;
            }
            this.mappedToColumn = mappedToColumn;
            if(mappedToColumn.contains(":")) {
                Integer intValue = Integer.parseInt(mappedToColumn.split(":")[0]);
                columnNumber=intValue;
            }

        }

        public Integer getColumnNumber() {
            return columnNumber;
        }

        public void setColumnNumber(Integer columnNumber) {
            this.columnNumber = columnNumber;
        }

    public String getMappedToColumns() {
        return mappedToColumns;
    }

    public void setMappedToColumns(String mappedToColumns) {
        this.mappedToColumns = mappedToColumns;
    }
}