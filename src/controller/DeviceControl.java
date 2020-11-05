package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Devices;
/*
 @author nxlam
 */
public class DeviceControl {
    public boolean checkDevice(Devices device) {
        try {
            DbUtil dbUtil = new DbUtil();
            dbUtil.openConnection();
            String query = "SELECT * FROM devices WHERE deviceName=? AND deviceUUID=? LIMIT 1";
            PreparedStatement statement = dbUtil.conn.prepareStatement(query);
            statement.setString(1, device.getDeviceName());
            statement.setString(2, device.getDeviceUUID());
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
    
    public List<Devices> getDevices() {
        List<Devices> list = new ArrayList<>();
        try {
            DbUtil dbUtil = new DbUtil();
            dbUtil.openConnection();
            String query = "SELECT * FROM devices";
            PreparedStatement statement = dbUtil.conn.prepareStatement(query);
            ResultSet rs = dbUtil.executeQuery(statement);
            while (rs.next()) {
                list.add(new Devices(rs.getString("deviceName"), rs.getString("deviceUUID")));
            }
            dbUtil.closeConnection();
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return list;
    }
}