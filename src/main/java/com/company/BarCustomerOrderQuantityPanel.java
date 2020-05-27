package main.java.com.company;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BarCustomerOrderQuantityPanel extends JPanel {

    private JPanel upPanel = new JPanel();
    private JPanel midPanel = new JPanel();
    private JPanel botPanel = new JPanel();

    private JLabel drinkNameLabel = new JLabel("Search by drink name: ");
    private JLabel quantityLabel = new JLabel("Search by quantity: ");

    private JTextField drinkNameTField = new JTextField();
    private JTextField quantityTField = new JTextField();

    private JButton searchBtn = new JButton("Search:");
    private JButton refreshBtn = new JButton("Refresh:");

    private JTable table = new JTable();
    private JScrollPane tableScroll = new JScrollPane(table);

    private Map<Integer, Integer> idMapper = new HashMap<>();

    public BarCustomerOrderQuantityPanel() {
        JPanel barCustomerOrderQuantityPanel = new JPanel(new GridLayout(3,1));
        this.add(barCustomerOrderQuantityPanel);
        this.add(upPanel);
        this.add(midPanel);
        this.add(botPanel);

        barCustomerOrderQuantityPanel.add(upPanel, BorderLayout.NORTH);
        barCustomerOrderQuantityPanel.add(midPanel, BorderLayout.CENTER);
        barCustomerOrderQuantityPanel.add(botPanel, BorderLayout.SOUTH);

        upPanel.setLayout(new GridLayout(2, 2));
        upPanel.add(drinkNameLabel);
        upPanel.add(drinkNameTField);
        upPanel.add(quantityLabel);
        upPanel.add(quantityTField);

        midPanel.setLayout(new GridLayout(1,2));
        midPanel.add(searchBtn);
        midPanel.add(refreshBtn);

        searchBtn.setPreferredSize(new Dimension(100, 30));
        refreshBtn.setPreferredSize(new Dimension(100, 30));

        botPanel.add(tableScroll);
        tableScroll.setPreferredSize(new Dimension(600, 100));
        table.setModel(new GetCustomerTableData().getCustomerBySearchField(null, null).getDefaultTableModel());

        searchBtn.addActionListener(actionEvent -> {
            String text = getDrinkNameTField().getText();
            String quantity = getQuantityTField().getText();
            Integer quant = null;
            if(!"".equals(quantity)) {
                quant = Integer.parseInt(quantity);
            }
            table.setModel(new GetCustomerTableData().getCustomerBySearchField(text, quant).getDefaultTableModel());
        });

        refreshBtn.addActionListener(actionEvent -> {
            table.setModel(new GetCustomerTableData().getCustomerBySearchField(null, null).getDefaultTableModel());
        });
    }



    private class GetCustomerTableData {

        public CustomTableData getCustomerBySearchField(String contains, Integer quant) {
            String sql = "SELECT o.orderId, o.quantity , d.name , d.price " +
                    "FROM orders o, drinks d " +
                    "where o.drink_id = d.drinkId " +
                    ((contains == null || contains.equals("")) ? "" : "and d.name like '%" + contains + "%' ") +
                    ((quant == null) ? "" : "and o.quantity=" + quant) +
                    ";";
            return getTableData(sql);
        }

        private CustomTableData getTableData(String sql) {
            Object[] columns = new Object[3];
            columns[0] = "Quantity";
            columns[1] = "Drink Name";
            columns[2] = "Drink Price";

            CustomTableData customTableModel = new CustomTableData(columns, idMapper);

            ResultSet resultSet = new MariaDBConnector().executeRSStatement(sql, "GetAll OrderQuantity sql");
            try {
                int counter = 0;
                while (resultSet.next()) {
                    Object[] data = new Object[3];
                    customTableModel.putDataInMap(counter++, resultSet.getInt(1));
                    data[0] = resultSet.getInt(2);
                    data[1] = resultSet.getString(3);
                    data[2] = resultSet.getFloat(4);
                    customTableModel.getDefaultTableModel().addRow(data);
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            return customTableModel;
        }
    }

    public JLabel getDrinkNameLabel() {
        return drinkNameLabel;
    }

    public void setDrinkNameLabel(JLabel drinkNameLabel) {
        this.drinkNameLabel = drinkNameLabel;
    }

    public JLabel getQuantityLabel() {
        return quantityLabel;
    }

    public void setQuantityLabel(JLabel quantityLabel) {
        this.quantityLabel = quantityLabel;
    }

    public JTextField getDrinkNameTField() {
        return drinkNameTField;
    }

    public void setDrinkNameTField(JTextField drinkNameTField) {
        this.drinkNameTField = drinkNameTField;
    }

    public JTextField getQuantityTField() {
        return quantityTField;
    }

    public void setQuantityTField(JTextField quantityTField) {
        this.quantityTField = quantityTField;
    }
}
