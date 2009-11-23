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
            this.mappedToColumn = mappedToColumn;
           
        }
    }