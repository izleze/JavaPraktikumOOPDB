package main.java.com.company;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.Map;

public class CustomTableData {

    private Map<Integer, Integer> mapOfIds;
    private DefaultTableModel defaultTableModel;

    public CustomTableData(Object[] columns, Map<Integer,Integer> map) {
        defaultTableModel = new DefaultTableModel(columns, 0);
        mapOfIds = map;
    }

    public Map<Integer, Integer> getMapOfIds() {
        return mapOfIds;
    }

    public void putDataInMap(int rowId, int id) {
        mapOfIds.put(rowId, id);
    }

    public void setMapOfIds(Map<Integer, Integer> mapOfIds) {
        this.mapOfIds = mapOfIds;
    }

    public DefaultTableModel getDefaultTableModel() {
        return defaultTableModel;
    }

    public void setDefaultTableModel(DefaultTableModel defaultTableModel) {
        this.defaultTableModel = defaultTableModel;
    }
}
