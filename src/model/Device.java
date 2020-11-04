/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author dream
 */
public class Device implements Serializable{
    public static final long serialVersionUID = 2020;
    private String deviceName;
    private String deviceUUID;
    private String status;
    private String username;
    private Date startTime = new Date();
    private long duringTime;
    private String note = "NULL";

    public Device() {
    }

    public Device(String deviceName, String deviceUUID) {
        this.deviceName = deviceName;
        this.deviceUUID = deviceUUID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceUUID() {
        return deviceUUID;
    }

    public void setDeviceUUID(String deviceUUID) {
        this.deviceUUID = deviceUUID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public long getDuringTime() {
        return duringTime;
    }

    public void setDuringTime(long duringTime) {
        this.duringTime = duringTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    public Object toObject () {
        SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        return new Object[]{deviceName, status, username, 
            date_format.format(startTime), (duringTime/(24 * 60 * 60 * 1000))
                +":"+((duringTime%(24 * 60 * 60 * 1000))/(60 * 60 * 1000))
                +":"+(((duringTime%(24 * 60 * 60 * 1000))%(60 * 60 * 1000))/(60 * 1000))
                +":"+((((duringTime%(24 * 60 * 60 * 1000))%(60 * 60 * 1000))%(60 * 1000))/(1000)), note};
    }
    
}
