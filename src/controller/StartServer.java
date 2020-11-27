/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.DAO.DbUtils;
import controller.RMI.RMIServer;
import controller.RMI.RMIServices;
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
    private static RMIServices rmiServices;
    private static ServerGUI serverGUI;
    private List<Devices> listDevices;
    private static DbUtils dbUtils;
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
                dbUtils.readFile(listDevices);
                checkDevices(listDevices);
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
//        serverGUIThread.start();

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

    private void checkDevices(List<Devices> listDevices) {
        for (Devices i : listDevices) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LookupServices lookupServices = new LookupServices();
                    try {
                        if (lookupServices.connect()) {
                            i.setStatus(Definitions.ONLINE);
                        }
                    } catch (Exception ex) {
                        i.setStatus(Definitions.OFFLINE);
                    }
                }
            }).start();
        }
    }

    private void registration() {
        devices = new Devices();
        serverGUI.btRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                devices.setDeviceUUID(serverGUI.deviceUUID.getText());
                devices.setIp(serverGUI.ipAddress.getText());
                devices.setDeviceName("MAY " + (listDevices.size()+1));
                serverGUI.deviceUUID.setText("");
                serverGUI.ipAddress.setText("");
                listDevices.add(devices);
                update();
                List<Devices> tmp = new ArrayList<>();
                dbUtils.readFile(tmp);
                tmp.add(devices);
                dbUtils.writeFile(tmp);
                JOptionPane.showMessageDialog(null, "Resgisted Successfully!!!");
            }
        });
    }

    private void update() {
        for (Devices i : listDevices) {
            serverGUI.tbModel.addRow(new Object[]{i.getDeviceName(), i.getIp(), i.getStatus(), i.getStartTime(), ""});
        }
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
