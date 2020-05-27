package main.java.com.company;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;

import static main.java.com.company.MariaDBConnector.executeStatement;

public class MainFrame extends JFrame {

    JTabbedPane tabbedPane = new JTabbedPane();

    BarCustomerPanel barCustomerPanel = new BarCustomerPanel();
    BarDrinkPanel barDrinkPanel = new BarDrinkPanel();
    BarOrdersPanel barOrdersPanel = new BarOrdersPanel();
    BarCustomerOrderQuantityPanel barCustomerOrderQuantityPanel = new BarCustomerOrderQuantityPanel();

    static {
        //delete old table cosntraint
        dropOrders();

        //create the tables
        createCustomerTable();
        createDrinksTable();
        createOrdersTable();

    }

    public MainFrame() {

        this.setSize(800, 550);
        this.setLayout(new GridLayout(1, 1));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(tabbedPane);

        tabbedPane.addTab("Customers", null, barCustomerPanel, "customers");
        tabbedPane.addTab("Drinks", null, barDrinkPanel, "drinks");
        tabbedPane.addTab("Orders", null, barOrdersPanel, "orders");
        tabbedPane.addTab("OrdersAndQuantity", null, barCustomerOrderQuantityPanel, "ordersNquantity");

        tabbedPane.addChangeListener(event -> {
            if(tabbedPane.getSelectedIndex() == 2) {
                this.barOrdersPanel = new BarOrdersPanel();
                tabbedPane.setComponentAt(2, barOrdersPanel);
            }
            if(tabbedPane.getSelectedIndex() == 3) {
                this.barCustomerOrderQuantityPanel = new BarCustomerOrderQuantityPanel();
                tabbedPane.setComponentAt(3, barCustomerOrderQuantityPanel);
            }
        });
        this.setVisible(true);
    }

    private static void createCustomerTable() {
        String sql =
                "CREATE OR REPLACE TABLE `customer` (" +
                        "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, " +
                        "`first_name` VARCHAR(100) NOT NULL," +
                        "`last_name` VARCHAR(100) NOT NULL," +
                        "`age` TINYINT(3) NOT NULL," +
                        "`discount` BOOL NOT NULL," +
                        "PRIMARY KEY (`id`)" +
                        ")ENGINE=InnoDB;";
        executeStatement(sql, "customer table created");
    }
    
    private static void createDrinksTable() {
        String sql = "CREATE OR REPLACE TABLE `drinks` (" +
                        "`drinkId` INT UNSIGNED auto_increment NOT NULL," +
                        "`name` varchar(100) NOT NULL," +
                        "`price` FLOAT NOT NULL," +
                        "PRIMARY KEY (`drinkId`)" +
                        ");";
        executeStatement(sql, "drinks table created");
    }

    private static void createOrdersTable() {
        String sql = 
                "CREATE OR REPLACE TABLE `orders` (" +
                "`orderId` INT UNSIGNED auto_increment NOT NULL," +
                "`drink_id` INT UNSIGNED NOT NULL," +
                "`customer_id` INT UNSIGNED NOT NULL," +
                "`quantity` INT UNSIGNED NOT NULL," +
                "CONSTRAINT `fk_drinks` " +
                "FOREIGN KEY (drink_id) REFERENCES drinks (drinkId)" +
                "ON DELETE RESTRICT " +
                "ON UPDATE RESTRICT," +
                "CONSTRAINT `fk_customers` " +
                "FOREIGN KEY (customer_id) REFERENCES customer (id)" +
                "ON DELETE RESTRICT " +
                "ON UPDATE RESTRICT," +
                "PRIMARY KEY (`orderId`)" +
                ");";
        executeStatement(sql, "drinks table created");
    }

    private static void dropOrders(){
        String sql = "DROP TABLE IF EXISTS orders;";
        executeStatement(sql, "Dropped table orders");
    }

}
