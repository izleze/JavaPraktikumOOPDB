package main.java.com.company;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class BarOrdersPanel extends JPanel {

    private JPanel upPanel = new JPanel();
    private JPanel midPanel = new JPanel();
    private JPanel botPanel = new JPanel();

    private JLabel drinkNameLabel = new JLabel("Drink name:");
    private JLabel customerLabel = new JLabel("Customer name:");
    private JLabel quantityLabel = new JLabel("Quantity:");

    private Map<String, Integer> drinks = comboBoxDrinksInitializer();
    private JComboBox<String> drinksCombo = new JComboBox<>(new Vector<>(drinks.keySet()));

    private Map<String, Integer> customers = comboBoxCustomerInitializer();
    private JComboBox<String> customersCombo = new JComboBox<>(new Vector<>(customers.keySet()));

    private JTextField quantityTField = new JTextField();

    private JButton addBtn = new JButton("Add");
    private JButton updateBtn = new JButton("Update");
    private JButton delBtn = new JButton("Delete");
    private JButton searchBtn = new JButton("Search:");
    private JButton refresh = new JButton("RefreshData:");

    private JLabel searchLabel = new JLabel("Search by quantity that contains:");
    private JTextField searchTField = new JTextField();

    private JTable table = new JTable();
    private JScrollPane tableScroll = new JScrollPane(table);

    private Map<Integer, Integer> idMapper = new HashMap<>();

    public BarOrdersPanel() {

        JPanel barOrders = new JPanel(new GridLayout(3, 1));
        this.add(barOrders);
        this.add(upPanel);
        this.add(midPanel);
        this.add(botPanel);

        barOrders.add(upPanel, BorderLayout.NORTH);
        barOrders.add(midPanel, BorderLayout.CENTER);
        barOrders.add(botPanel, BorderLayout.SOUTH);

        upPanel.setLayout(new GridLayout(3, 2));
        upPanel.add(drinkNameLabel);
        upPanel.add(drinksCombo);
        upPanel.add(customerLabel);
        upPanel.add(customersCombo);
        upPanel.add(quantityLabel);
        upPanel.add(quantityTField);

        midPanel.setLayout(new GridLayout(3,3));
        midPanel.add(addBtn);
        midPanel.add(updateBtn);
        midPanel.add(delBtn);
        midPanel.add(searchBtn);
        midPanel.add(searchLabel);
        midPanel.add(searchTField);
        midPanel.add(refresh);

        addBtn.setPreferredSize(new Dimension(100, 30));
        updateBtn.setPreferredSize(new Dimension(100, 30));
        delBtn.setPreferredSize(new Dimension(100, 30));
        searchBtn.setPreferredSize(new Dimension(100, 30));
        refresh.setPreferredSize(new Dimension(100, 30));

        botPanel.add(tableScroll);
        tableScroll.setPreferredSize(new Dimension(400, 100));

        table.setModel(new GetCustomerTableData().getOrdersTableData().getDefaultTableModel());

        addBtn.addActionListener(actionEvent -> {
            Integer drinkId = drinks.get(drinksCombo.getItemAt(drinksCombo.getSelectedIndex()));
            Integer customerId = customers.get(customersCombo.getItemAt(customersCombo.getSelectedIndex()));
            int quantity;

            if(drinkId == null || customerId == null) {
                return;
            }

            String sql = "insert into orders values(null,?,?,?);";

            try (Connection conn = MariaDBConnector.getConnection()) {
                quantity = Integer.parseInt(quantityTField.getText());
                if (conn != null) {
                    PreparedStatement state = conn.prepareStatement(sql);
                    state.setInt(1, drinkId);
                    state.setInt(2, customerId);
                    state.setInt(3, quantity);
                    state.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
            }

            table.setModel(new GetCustomerTableData()
                    .getOrdersTableData()
                    .getDefaultTableModel());
        });

        updateBtn.addActionListener(actionEvent -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                return;
            }
            int idOfElement = idMapper.get(row);

            Integer drinkId = drinks.get(drinksCombo.getItemAt(drinksCombo.getSelectedIndex()));
            Integer customerId = customers.get(customersCombo.getItemAt(customersCombo.getSelectedIndex()));
            int quantity;

            if(drinkId == null || customerId == null) {
                return;
            };
            try (Connection conn = MariaDBConnector.getConnection()) {
                if(conn == null) {
                    return;
                }
                quantity = Integer.parseInt(quantityTField.getText());

                String sql = "update orders set drink_id=?, customer_id=?, quantity=? where " +
                        "orderId =" + idOfElement;

                PreparedStatement state = conn.prepareStatement(sql);
                state.setInt(1, drinkId);
                state.setInt(2, customerId);
                state.setInt(3, quantity);
                state.execute();

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            table.setModel(new GetCustomerTableData()
                    .getOrdersTableData()
                    .getDefaultTableModel());
        });

        delBtn.addActionListener(actionEvent -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                return;
            }
            int idOfElement = idMapper.get(row);

            String sql = "DELETE FROM orders where orderId =" + idOfElement;
            MariaDBConnector.executeStatement(sql, "deleted element with id = " + idOfElement);
            table.setModel(new GetCustomerTableData().getOrdersTableData().getDefaultTableModel());
        });

        searchBtn.addActionListener(actionEvent -> {
            int contains = Integer.parseInt(getSearchTField().getText());

            table.setModel(new GetCustomerTableData()
                    .getOrdersBySearchField(contains)
                    .getDefaultTableModel());
        });

        refresh.addActionListener(actionEvent -> {
            table.setModel(new GetCustomerTableData().getOrdersTableData().getDefaultTableModel());
        });
    }

    private class GetCustomerTableData {

        public CustomTableData getOrdersTableData() {
            String sql = "SELECT * FROM orders;";
            return getTableData(sql);
        }

        public CustomTableData getOrdersBySearchField(int contains) {
            String sql = "select * from orders where quantity =" + contains;
            return getTableData(sql);
        }

        private CustomTableData getTableData(String sql) {
            Object[] columns = new Object[3];
            columns[0] = "Drink name";
            columns[1] = "Customer name";
            columns[2] = "Quantity";

            CustomTableData customTableModel = new CustomTableData(columns, idMapper);

            ResultSet resultSet = new MariaDBConnector().executeRSStatement(sql, "GetAll orders sql");
            try {
                int counter = 0;
                while (resultSet.next()) {
                    Object[] data = new Object[3];
                    customTableModel.putDataInMap(counter++, resultSet.getInt(1));
                    ResultSet resultSet1 = new MariaDBConnector().executeRSStatement(
                            "SELECT CONCAT(first_name, ' ', last_name) as Name " +
                            "FROM customer " +
                            "WHERE id =" + resultSet.getInt(2), "get customer name");
                    ResultSet resultSet2 = new MariaDBConnector().executeRSStatement("SELECT name " +
                            "FROM drinks " +
                                    "WHERE drinkId =" + resultSet.getInt(3), "get drink name");
                    if(resultSet1.next()) {
                        data[0] = resultSet1.getString(1);
                    }

                    if(resultSet2.next()) {
                        data[1] = resultSet2.getString(1);
                    }

                    data[2] = resultSet.getInt(4);
                    customTableModel.getDefaultTableModel().addRow(data);
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            return customTableModel;
        }
    }

    public Map<String, Integer> comboBoxDrinksInitializer() {
        Map<String, Integer> comboBoxValue = new HashMap<>();
        String sql = "SELECT name, drinkId " +
                "FROM drinks; ";

        creteComboBoxMap(comboBoxValue, sql);

        return comboBoxValue;
    }

    public Map<String, Integer> comboBoxCustomerInitializer() {
        Map<String, Integer> comboBoxValue = new HashMap<>();
        String sql = "SELECT CONCAT(first_name, ' ', last_name) as Name, id " +
                "FROM customer; ";

        creteComboBoxMap(comboBoxValue, sql);
        return comboBoxValue;
    }

    private void creteComboBoxMap(Map<String, Integer> comboBoxValue, String sql) {
        ResultSet resultSet = new MariaDBConnector().executeRSStatement(sql, "ResultSet got successfully");

        try {
            if (resultSet == null){
                comboBoxValue.put("", Integer.MIN_VALUE);
                return;
            }
            while (resultSet.next()) {
                comboBoxValue.put(resultSet.getString(1), resultSet.getInt(2));
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public JComboBox<String> getDrinksCombo() {
        return drinksCombo;
    }

    public void setDrinksCombo(JComboBox<String> drinksCombo) {
        this.drinksCombo = drinksCombo;
    }

    public JComboBox<String> getCustomersCombo() {
        return customersCombo;
    }

    public void setCustomersCombo(JComboBox<String> customersCombo) {
        this.customersCombo = customersCombo;
    }

    public JTextField getSearchTField() {
        return searchTField;
    }

    public void setSearchTField(JTextField searchTField) {
        this.searchTField = searchTField;
    }
}
