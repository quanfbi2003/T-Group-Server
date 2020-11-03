package controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Device;
/*
 @author nxlam
 */
public class DeviceControl {
    public static void main(String[] args) {
        Device device = new Device("demo", "1234");
        DeviceControl ctrl = new DeviceControl();
        System.out.println(ctrl.checkDevice(device));
    }

    public boolean checkDevice(Device device) {
        try {
            DbUtil dbUtil = new DbUtil();
            dbUtil.openConnection();
            String query = "select * from device where deviceName=? and deviceUUID=? limit 1";
            PreparedStatement statement = dbUtil.conn.prepareStatement(query);
            statement.setString(1, device.getDeviceName());
            statement.setString(2, device.getDeviceUUID());
            ResultSet rs = dbUtil.executeQuery(statement);
            if (rs.next()) {
                dbUtil.closeConnection();
                return true;
            }
            return true;
        } catch (SQLException ex) {
            System.out.println(ex);
        }
        return false;
    }
}
