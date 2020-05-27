package main.java.com.company;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BarCustomerPanel extends JPanel {

    private JPanel upPanel = new JPanel();
    private JPanel midPanel = new JPanel();
    private JPanel botPanel = new JPanel();

    private JLabel fnameLabel = new JLabel("Name:");
    private JLabel lnameLabel = new JLabel("LastName:");
    private JLabel ageLabel = new JLabel("Age:");
    private JLabel discountLabel = new JLabel("Discount:");

    private JTextField fnameTField = new JTextField();

    private JTextField lnameTField = new JTextField();
    private JTextField ageTField = new JTextField();

    private String[] optionsList = {"False", "True"};
    private JComboBox<String> discountCombo = new JComboBox<>(optionsList);

    private JButton addBtn = new JButton("Add");
    private JButton updateBtn = new JButton("Update");
    private JButton delBtn = new JButton("Delete");
    private JButton searchBtn = new JButton("Search:");
    private JButton refresh = new JButton("RefreshData:");

    private JLabel searchLabel = new JLabel("Search by FirstName that contains:");

    private JTextField searchTField = new JTextField();

    private JTable table = new JTable();
    private JScrollPane tableScroll = new JScrollPane(table);

    private CustomTableData getCustomerTableData = new GetCustomerTableData().getCustomerTableData();
    private Map<Integer, Integer> idMapper = new HashMap<>();

    public BarCustomerPanel() {
        JPanel barCustomers = new JPanel(new GridLayout(3, 1));
        this.add(barCustomers);
        this.add(upPanel);
        this.add(midPanel);
        this.add(botPanel);

        barCustomers.add(upPanel, BorderLayout.NORTH);
        barCustomers.add(midPanel, BorderLayout.CENTER);
        barCustomers.add(botPanel, BorderLayout.SOUTH);

        upPanel.setLayout(new GridLayout(4, 2));
        upPanel.add(fnameLabel);
        upPanel.add(fnameTField);
        upPanel.add(lnameLabel);
        upPanel.add(lnameTField);
        upPanel.add(ageLabel);
        upPanel.add(ageTField);
        upPanel.add(discountLabel);
        upPanel.add(discountCombo);


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
            String fname = getFnameTField().getText();
            String lname = getLnameTField().getText();
            if("".equals(fname) || "".equals(lname)) {
                return;
            }
            int age;
            boolean discount;
            try (Connection conn = MariaDBConnector.getConnection()) {
                age = Integer.parseInt(getAgeTField().getText());
                discount = Boolean.parseBoolean(discountCombo.getItemAt(discountCombo.getSelectedIndex()));

                String sql = "insert into customer values(null,?,?,?,?);";
                if (conn != null) {
                    PreparedStatement state = conn.prepareStatement(sql);
                    state.setString(1, fname);
                    state.setString(2, lname);
                    state.setInt(3, age);
                    state.setBoolean(4, discount);
                    state.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Try again all fields should be good");
            }

            table.setModel(new GetCustomerTableData()
                    .getCustomerTableData()
                    .getDefaultTableModel());
        });

        updateBtn.addActionListener(actionEvent -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                return;
            }
            int idOfElement = idMapper.get(row);

            String fname = getFnameTField().getText();
            String lname = getLnameTField().getText();
            if("".equals(fname) || "".equals(lname)) {
                return;
            }
            int age;
            boolean discount;
            try (Connection conn = MariaDBConnector.getConnection()) {
                if(conn == null) {
                    return;
                }
                age = Integer.parseInt(getAgeTField().getText());
                discount = Boolean.parseBoolean(discountCombo.getItemAt(discountCombo.getSelectedIndex()));

                String sql = "update customer set first_name=?, last_name=?, age=?, discount=? where " +
                        "id =" + idOfElement;

                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, fname);
                preparedStatement.setString(2, lname);
                preparedStatement.setInt(3, age);
                preparedStatement.setBoolean(4, discount);
                preparedStatement.execute();

            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
            table.setModel(new GetCustomerTableData()
                    .getCustomerTableData()
                    .getDefaultTableModel());
        });

        delBtn.addActionListener(actionEvent -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                return;
            }
            int idOfElement = idMapper.get(row);

            String sql = "DELETE FROM customer where id =" + idOfElement;
            MariaDBConnector.executeStatement(sql, "deleted element with id = " + idOfElement);
            table.setModel(new GetCustomerTableData().getCustomerTableData().getDefaultTableModel());
        });

        searchBtn.addActionListener(actionEvent -> {
            String contains = getSearchTField().getText();

            if("".equals(contains)) {
                return;
            }

            table.setModel(new GetCustomerTableData()
                        .getCustomerBySearchField(contains)
                        .getDefaultTableModel());
        });

        refresh.addActionListener(actionEvent -> {
            table.setModel(new GetCustomerTableData().getCustomerTableData().getDefaultTableModel());
        });
    }

    public JTextField getFnameTField() {
        return fnameTField;
    }

    public void setFnameTField(JTextField fnameTField) {
        this.fnameTField = fnameTField;
    }

    public JTextField getLnameTField() {
        return lnameTField;
    }

    public void setLnameTField(JTextField lnameTField) {
        this.lnameTField = lnameTField;
    }

    public JTextField getAgeTField() {
        return ageTField;
    }

    public void setAgeTField(JTextField ageTField) {
        this.ageTField = ageTField;
    }

    public JComboBox<String> getDiscountCombo() {
        return discountCombo;
    }

    public void setDiscountCombo(JComboBox<String> discountCombo) {
        this.discountCombo = discountCombo;
    }

    public JTextField getSearchTField() {
        return searchTField;
    }

    private class GetCustomerTableData {

        public CustomTableData getCustomerTableData() {
            String sql = "SELECT * FROM customer;";
            return getTableData(sql);
        }

        public CustomTableData getCustomerBySearchField(String contains) {
            String sql = "select * from customer where first_name like '%" + contains +
                    "%'";
            return getTableData(sql);
        }

        private CustomTableData getTableData(String sql) {
            Object[] columns = new Object[4];
            columns[0] = "FirstName";
            columns[1] = "LastName";
            columns[2] = "Age";
            columns[3] = "Has discount";

            CustomTableData customTableModel = new CustomTableData(columns, idMapper);

            ResultSet resultSet = new MariaDBConnector().executeRSStatement(sql, "GetAll Customers sql");
            try {
                int counter = 0;
                while (resultSet.next()) {
                    Object[] data = new Object[4];
                    customTableModel.putDataInMap(counter++, resultSet.getInt(1));
                    data[0] = resultSet.getString(2);
                    data[1] = resultSet.getString(3);
                    data[2] = resultSet.getInt(4);
                    data[3] = resultSet.getBoolean(5);
                    customTableModel.getDefaultTableModel().addRow(data);
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }

            return customTableModel;
        }
    }

    public void setSearchTField(JTextField searchTField) {
        this.searchTField = searchTField;
    }
}
