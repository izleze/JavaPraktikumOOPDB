package main.java.com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BarDrinkPanel extends JPanel {

    private JPanel upPanel = new JPanel();
    private JPanel midPanel = new JPanel();
    private JPanel botPanel = new JPanel();

    private JLabel drinkName = new JLabel("Name:");
    private JLabel price = new JLabel("Price:");
    private JLabel searchLabel = new JLabel("Search by drink name that contains:");

    private JTextField drinkNameTField = new JTextField();
    private JTextField priceTField = new JTextField();

    private JButton addBtn = new JButton("Add");
    private JButton updateBtn = new JButton("Update");
    private JButton delBtn = new JButton("Delete");
    private JButton searchBtn = new JButton("Search:");
    private JButton refresh = new JButton("RefreshData:");

    private JTextField searchTField = new JTextField();

    private JTable table = new JTable();
    private JScrollPane tableScroll = new JScrollPane(table);

    private CustomTableData getCustomerTableData = new GetCustomerTableData().getDrinksTableData();
    private Map<Integer, Integer> idMapper = new HashMap<>();

    public BarDrinkPanel() {
        JPanel barDrinks = new JPanel(new GridLayout(3, 1));

        this.add(barDrinks);
        this.add(upPanel);
        this.add(midPanel);
        this.add(botPanel);

        barDrinks.add(upPanel, BorderLayout.NORTH);
        barDrinks.add(midPanel, BorderLayout.CENTER);
        barDrinks.add(botPanel, BorderLayout.SOUTH);

        upPanel.setLayout(new GridLayout(2, 2));
        upPanel.add(drinkName);
        upPanel.add(drinkNameTField);
        upPanel.add(price);
        upPanel.add(priceTField);

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

        table.setModel(getCustomerTableData.getDefaultTableModel());

        addBtn.addActionListener(actionEvent -> {
            String drinkName = getDrinkNameTField().getText();
            String drinkPrice = getPriceTField().getText();

            if("".equals(drinkName) || "".equals(drinkPrice)) {
                return;
            }

            String sql = "insert into drinks values(null,?,?);";

            try (Connection conn = MariaDBConnector.getConnection()) {
                if (conn != null) {
                    PreparedStatement state = conn.prepareStatement(sql);
                    state.setString(1, drinkName);
                    state.setString(2, drinkPrice);
                    state.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            table.setModel(new GetCustomerTableData()
                    .getDrinksTableData()
                    .getDefaultTableModel());
        });

        updateBtn.addActionListener(actionEvent -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                return;
            }
            int idOfElement = idMapper.get(row);

            String name = getDrinkNameTField().getText();
            if("".equals(name)) {
                return;
            }
            float price;
            try (Connection conn = MariaDBConnector.getConnection()) {
                if(conn == null) {
                    return;
                }
                price = Float.parseFloat(getPriceTField().getText());
                String sql = "update drinks set name=?, price=? where " +
                        "drinkId =" + idOfElement;

                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, name);
                preparedStatement.setFloat(2, price);
                preparedStatement.execute();

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            table.setModel(new GetCustomerTableData()
                    .getDrinksTableData()
                    .getDefaultTableModel());
        });

        delBtn.addActionListener(actionEvent -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                return;
            }
            int idOfElement = idMapper.get(row);

            String sql = "DELETE FROM drinks where drinkId =" + idOfElement;
            MariaDBConnector.executeStatement(sql, "deleted element with id = " + idOfElement);
            table.setModel(new GetCustomerTableData().getDrinksTableData().getDefaultTableModel());
        });

        searchBtn.addActionListener(actionEvent -> {
            String contains = getSearchTField().getText();

            if("".equals(contains)) {
                return;
            }

            table.setModel(new GetCustomerTableData()
                    .getDrinksBySearchField(contains)
                    .getDefaultTableModel());
        });

        refresh.addActionListener(actionEvent -> {
            table.setModel(new GetCustomerTableData()
                    .getDrinksTableData()
                    .getDefaultTableModel());
        });
    }

    public JTextField getSearchTField() {
        return searchTField;
    }

    public void setSearchTField(JTextField searchTField) {
        this.searchTField = searchTField;
    }

    private class GetCustomerTableData {

        public CustomTableData getDrinksTableData() {
            String sql = "SELECT * FROM drinks;";
            return getTableData(sql);
        }

        public CustomTableData getDrinksBySearchField(String contains) {
            String sql = "select * from drinks where name like '%" + contains +
                    "%'";
            return getTableData(sql);
        }

        private CustomTableData getTableData(String sql) {
            Object[] columns = new Object[2];
            columns[0] = "Name";
            columns[1] = "Price";

            CustomTableData customTableModel = new CustomTableData(columns, idMapper);

            ResultSet resultSet = new MariaDBConnector().executeRSStatement(sql, "GetAll drinks sql");
            try {
                int counter = 0;
                while (resultSet.next()) {
                    Object[] data = new Object[2];
                    customTableModel.putDataInMap(counter++, resultSet.getInt(1));
                    data[0] = resultSet.getString(2);
                    data[1] = resultSet.getFloat(3);
                    customTableModel.getDefaultTableModel().addRow(data);
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            return customTableModel;
        }
    }

    public JTextField getDrinkNameTField() {
        return drinkNameTField;
    }

    public void setDrinkNameTField(JTextField drinkNameTField) {
        this.drinkNameTField = drinkNameTField;
    }

    public JTextField getPriceTField() {
        return priceTField;
    }

    public void setPriceTField(JTextField priceTField) {
        this.priceTField = priceTField;
    }
}
