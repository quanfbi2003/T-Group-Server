/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.RMIClient.LookupServices;
import controller.DAO.DbUtils;
import controller.RMIServer.RMIServer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.Definitions;
import model.Devices;
import view.ServerGUI;

/**
 *
 * @author dream
 */
public class StartServer {

    private static RMIServer serverRMI;
    private static LookupServices lookupServies;
    private static ServerGUI serverGUI;
    private List<Devices> listDevices;
    private Devices devices;

    public StartServer() {
        runableThread();
    }

    private void runableThread() {
        Thread serverGUIThread = new Thread(new Runnable() {
            @Override
            public void run() {
                serverGUI = new ServerGUI();
                serverGUI.setVisible(true);
                listDevices = new ArrayList<>();
                DbUtils.readFile(listDevices);
                update();
                checkDevices();
                registration();
            }
        });
        serverGUIThread.start();

        Thread serverRMIThread = new Thread(new Runnable() {
            @Override
            public void run() {
                serverRMI = new RMIServer();
                serverRMI.startServer();
            }
        });
        serverRMIThread.start();

        Thread servicesThread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        servicesThread.start();

        Thread timeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long activeTime = 0;
                int hour = (int) activeTime / (1000 * 60 * 60);
                int min = (int) (activeTime % (1000 * 60 * 60)) / (1000 * 60);
                String activeT = Integer.toString(hour) + ":" + Integer.toString(min);

            }
        });
        timeThread.start();
    }

    private void checkDevices() {
        for (int i = 0; i < listDevices.size(); i++) {
            devices = listDevices.get(i);
            Thread checkDeviceThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    LookupServices lookupServices = new LookupServices(devices.getIp());
                    try {
                        if (lookupServices.connect()) {
                            devices = lookupServices.iRMIServices.getDevice();
                        } else {
                            devices.setStatus(Definitions.OFFLINE);
                        }
                    } catch (Exception ex) {
                        devices.setStatus(Definitions.OFFLINE);
                    }
                    System.out.println(devices.getStartTime());
                }
            });
            checkDeviceThread.start();
            try {
                checkDeviceThread.join();
            } catch (InterruptedException ex) {
            }
            listDevices.get(i).setStatus(devices.getStatus());
            listDevices.get(i).setStartTime(devices.getStartTime());
        }
        update();
    }

    private void registration() {
        devices = new Devices();
        serverGUI.btRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                devices.setDeviceUUID(serverGUI.deviceUUID.getText());
                devices.setIp(serverGUI.ipAddress.getText());
                devices.setDeviceName("MAY " + (listDevices.size() + 1));
                serverGUI.deviceUUID.setText("");
                serverGUI.ipAddress.setText("");
                listDevices.add(devices);
                update();
                List<Devices> tmp = new ArrayList<>();
                DbUtils.readFile(tmp);
                tmp.add(devices);
                DbUtils.writeFile(tmp);
                JOptionPane.showMessageDialog(null, "Resgisted Successfully!!!");
            }
        });
    }

    private void update() {
        serverGUI.tbModel.setRowCount(0);
        for (Devices i : listDevices) {
            long hour = i.getStartTime()/(1000*60*60);
            long min = (i.getStartTime()%(1000*60*60))/(1000*60);
            String startTime = Integer.toString((int) hour) + " : " + Integer.toString((int) min);
            serverGUI.tbModel.addRow(new Object[]{i.getDeviceName(), i.getIp(), i.getStatus(), startTime, ""});
        }
        serverGUI.tbModel.setRowCount(20);
    }

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new StartServer();
            }
        }).start();
    }
}
