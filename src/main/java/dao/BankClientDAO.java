package dao;

import model.BankClient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BankClientDAO {

    private Connection connection;

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() throws SQLException {
        List<BankClient> list = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client");
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            long id = resultSet.getLong("id");
            String name = resultSet.getString("name");
            String pass = resultSet.getString("password");
            long money = resultSet.getLong("money");
            BankClient client = new BankClient(id, name, pass, money);
            list.add(client);
        }
        resultSet.close();
        preparedStatement.close();
        return list;
    }

    public boolean validateClient(String name, String password) {
        try {
            BankClient client = getClientByName(name);
            if (client.getPassword().equals(password)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateClientsMoney(String name, String password, Long transactValue) throws SQLException {
        if (validateClient(name, password)) {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where name= ?");
            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            Long money = resultSet.getLong(4);
            money += transactValue;
            preparedStatement = connection.prepareStatement("UPDATE bank_client SET money = ? WHERE name LIKE ?");
            preparedStatement.setLong(1, money);
            preparedStatement.setString(2, name);
            preparedStatement.executeUpdate();
            resultSet.close();;
            preparedStatement.close();
        }
    }

    public BankClient getClientById(long id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where id= ?");
        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String name = resultSet.getString("name");
        String password = resultSet.getString("password");
        Long money = resultSet.getLong("money");
        resultSet.close();
        preparedStatement.close();
        return new BankClient(name, password, money);
    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where name = ?");
        preparedStatement.setString(1, name);
        ResultSet result = preparedStatement.executeQuery();
        result.next();
        Long money = result.getLong("money");
        result.close();
        preparedStatement.close();
        return money >= expectedSum;
    }

    public long getClientIdByName(String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where name= ?");
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        Long id = resultSet.getLong("id");
        resultSet.close();
        preparedStatement.close();
        return id;
    }

    public BankClient getClientByName(String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from bank_client where name= ?");
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        String names = resultSet.getNString(2);
        String password = resultSet.getNString(3);
        Long money = resultSet.getLong(4);
        resultSet.close();
        preparedStatement.close();
        return new BankClient(names, password, money);
    }

    public void addClient(BankClient client) throws SQLException {
        String sql = "INSERT INTO bank_client (name, password, money) Values (?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, client.getName());
        preparedStatement.setString(2, client.getPassword());
        preparedStatement.setLong(3, client.getMoney());
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}
