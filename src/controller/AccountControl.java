package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Accounts;
/*
 @author nxlam
 */
public class AccountControl {
    public boolean checkAccount(Accounts accounts) {
        try {
            DbUtil dbUtil = new DbUtil();
            dbUtil.openConnection();
            String query = "SELECT * FROM accounts WHERE username=? AND password=? LIMIT 1";
            PreparedStatement statement = dbUtil.conn.prepareStatement(query);
            statement.setString(1, accounts.getUsername());
            statement.setString(2, accounts.getPassword());
            ResultSet rs = dbUtil.executeQuery(statement);
            if (rs.next()) {
                dbUtil.closeConnection();
                return true;
            }
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }
    
    public List<Accounts> getAccounts() {
        List<Accounts> list = new ArrayList<>();
        try {
            DbUtil dbUtil = new DbUtil();
            dbUtil.openConnection();
            String query = "SELECT * FROM accounts";
            PreparedStatement statement = dbUtil.conn.prepareStatement(query);
            ResultSet rs = dbUtil.executeQuery(statement);
            while (rs.next()) {
                list.add(new Accounts(rs.getString("username"), rs.getString("password")));
            }
            dbUtil.closeConnection();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return list;
    }
}