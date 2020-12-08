/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.Server;

import controller.DAO.DbUtils;
import controller.Client.LookupServices;
import controller.remote.InitConnection;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.*;
import view.*;

/**
 *
 * @author dream
 */
public class ServerServices extends UnicastRemoteObject implements IRMIServerServices {

    private static ServerGUI serverGUI;
    private static BlacklistGUI blacklistGUI;
    private static ControlApps controlApps;
    private List<Devices> listDevices;
    private List<Processes> listProcesses;
    private Devices devices;
    private Devices devicesReg;
    private Thread checkingThread;
    private boolean flag = false;

    public ServerServices() throws RemoteException {
        runableThread();
    }

    private void runableThread() {
        Thread serverGUIThread = new Thread(new Runnable() { // Constructer Thread
            @Override
            public void run() {
                flag = true;
                serverGUI = new ServerGUI();
                serverGUI.setVisible(true);
                listDevices = new ArrayList<>();
                DbUtils.readFile(listDevices);
                registration();
                controlApplication();
                shutdown_and_restart();
                blackList_feature();
                remote();
                update();
            }
        });
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    System.out.println(++i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }).start();

        checkingThread = new Thread(new Runnable() { // Connect to client Thread
            @Override
            public void run() {
                while (true) {
                    try {
                        if (!flag) {
                            checkDevices();
                            System.out.println("Checking....");
                        }
                        System.out.println("Flag....." + flag);
                        Thread.sleep(5000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        serverGUIThread.start();
        try {
            serverGUIThread.join();
        } catch (InterruptedException ex) {
        }
        checkingThread.start();
        flag = false;
    }

    private void checkDevices() { // Check online devices
        boolean flagW = false;
        for (int i = 0; !flag && i < listDevices.size(); i++) {
            devices = listDevices.get(i);
            Thread checkDeviceThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    LookupServices lookupServices = new LookupServices(devices.getIp());
                    try {
                        if (!flag && lookupServices.connect()) {
                            devices = lookupServices.iRMIServices.getDevice(devices);
                        } else {
                            devices.setStatus(Definitions.OFFLINE);
                            devices.setStartTime("");
                        }
                    } catch (Exception ex) {
                        devices.setStatus(Definitions.OFFLINE);
                        devices.setStartTime("");
                    }
                }
            });
            checkDeviceThread.start();
            try {
                checkDeviceThread.join();
            } catch (InterruptedException ex) {
            }
            listDevices.get(i).setStatus(devices.getStatus());
            listDevices.get(i).setStartTime(devices.getStartTime());
            System.out.println(devices.getStatus());
            flagW = true;
        }
        while (true) {
            if (flagW) {
                update();
                break;
            }
        }
    }

    private void registration() { // Registration feature
        devicesReg = new Devices();
        serverGUI.btRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flag = true;
                devicesReg.setIp(serverGUI.ipAddress.getText());
                devicesReg.setDeviceName("MAY " + (listDevices.size() + 1));
                serverGUI.ipAddress.setText("");
                DbUtils.readFile(listDevices);
                listDevices.add(devicesReg);
                DbUtils.writeFile(listDevices);
                flag = false;
                checkDevices();
                JOptionPane.showMessageDialog(null, "Resgisted Successfully!!!");

            }
        });
    }

    private void controlApplication() { // Task Manager feature
        serverGUI.AppsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flag = true;
                int row = serverGUI.main_table.getSelectedRow();
                if (row >= 0 && row < listDevices.size()) {
                    LookupServices lookupServices = new LookupServices(listDevices.get(row).getIp());
                    if (lookupServices.connect()) {
                        controlApps = new ControlApps();
                        controlApps.setVisible(true);

                        Thread servicesThread = new Thread(new Runnable() { // Update process Thread
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        listProcesses = lookupServices.iRMIServices.update();
                                        controlApps.tm.setRowCount(0);
                                        for (Processes i : listProcesses) {
                                            String processMem = String.format("%.1f", i.getProcessMem() / 1024.0) + " MB";
                                            controlApps.tm.addRow(new Object[]{i.getProcessName(), i.getProcessNum(), processMem});
                                        }
                                        Thread.sleep(10000);
                                    } catch (Exception ex) {
                                    }
                                }

                            }
                        });
                        servicesThread.start();

                        controlApps.killBt.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                int killP = controlApps.tb.getSelectedRow();
                                if (killP >= 0 && killP < listProcesses.size()) {
                                    try {
                                        lookupServices.iRMIServices.killProcess(listProcesses.get(killP));
                                        controlApps.tm.removeRow(killP);

                                    } catch (RemoteException ex) {
                                    }
                                }
                            }
                        });

                        controlApps.addWindowListener(new WindowListener() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                flag = false;
                            }

                            @Override
                            public void windowOpened(WindowEvent e) {
                            }

                            @Override
                            public void windowClosed(WindowEvent e) {
                            }

                            @Override
                            public void windowIconified(WindowEvent e) {
                            }

                            @Override
                            public void windowDeiconified(WindowEvent e) {
                            }

                            @Override
                            public void windowActivated(WindowEvent e) {
                            }

                            @Override
                            public void windowDeactivated(WindowEvent e) {
                            }

                        });

                    } else {
                        JOptionPane.showMessageDialog(null, "Client is OFFLINE");
                        flag = false;
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Select Client!!!");
                    flag = false;
                }

            }
        });

    }

    private void update() {
        System.out.println("Updating...");
        serverGUI.tbModel.setRowCount(0);
        for (Devices i : listDevices) {
            serverGUI.tbModel.addRow(new Object[]{i.getDeviceName(), i.getIp(), i.getStatus(), i.getStartTime()});
        }
        serverGUI.tbModel.setRowCount(20);
    }

    private void shutdown_and_restart() {
        serverGUI.ShutdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flag = true;
                int row = serverGUI.main_table.getSelectedRow();
                if (row >= 0 && row < listDevices.size()) {
                    LookupServices lookupServices = new LookupServices(listDevices.get(row).getIp());
                    if (lookupServices.connect()) {
                        int opt = JOptionPane.showConfirmDialog(null, "Do you want to turn off this computer?");
                        if (opt == JOptionPane.YES_OPTION) {
                            try {
                                lookupServices.iRMIServices.shutdown();
                                flag = false;
                            } catch (RemoteException ex) {
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Client is OFFLINE");
                        flag = false;
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Select Client!!!");
                    flag = false;
                }

            }
        });

        serverGUI.RestartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flag = true;
                int row = serverGUI.main_table.getSelectedRow();
                if (row >= 0 && row < listDevices.size()) {
                    LookupServices lookupServices = new LookupServices(listDevices.get(row).getIp());
                    if (lookupServices.connect()) {
                        int opt = JOptionPane.showConfirmDialog(null, "Do you want to restart this computer?");
                        if (opt == JOptionPane.YES_OPTION) {
                            try {
                                lookupServices.iRMIServices.restart();
                                flag = false;
                            } catch (RemoteException ex) {
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Client is OFFLINE");
                        flag = false;
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Select Client!!!");
                    flag = false;
                }

            }
        });
    }

    private void blackList_feature() {
        serverGUI.BlacklistBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flag = true;
                int row = serverGUI.main_table.getSelectedRow();
                if (row >= 0 && row < listDevices.size()) {
                    LookupServices lookupServices = new LookupServices(listDevices.get(row).getIp());
                    if (lookupServices.connect()) {
                        blacklistGUI = new BlacklistGUI();
                        blacklistGUI.setVisible(true);
                        blacklistGUI.id = row;
                        blacklistGUI.BlacklistText.setText(listDevices.get(row).getBlackList());
                        blacklistGUI.cancelBt.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                if (!listDevices.get(blacklistGUI.id).getBlackList().equalsIgnoreCase(blacklistGUI.BlacklistText.getText())) {
                                    int res = JOptionPane.showConfirmDialog(null, "Do you want to exit without saving?");
                                    if (res == JOptionPane.YES_OPTION) {
                                        blacklistGUI.dispose();
                                    }
                                } else {
                                    blacklistGUI.dispose();
                                }
                                flag = false;
                            }
                        });
                        blacklistGUI.saveBt.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                listDevices.get(blacklistGUI.id).setBlackList(blacklistGUI.BlacklistText.getText().trim());
                                LookupServices lookupServices = new LookupServices(listDevices.get(row).getIp());
                                if (lookupServices.connect()) {
                                    try {
                                        lookupServices.iRMIServices.getDevice(listDevices.get(blacklistGUI.id));
                                    } catch (RemoteException ex) {
                                    }
                                }
                                List<Devices> tmp1 = new ArrayList<>();
                                DbUtils.readFile(tmp1);
                                tmp1.get(blacklistGUI.id).setBlackList(listDevices.get(blacklistGUI.id).getBlackList());
                                DbUtils.writeFile(tmp1);
                                JOptionPane.showMessageDialog(null, "Save successfully!!!");
                                blacklistGUI.dispose();
                                tmp1 = null;
                                flag = false;
                            }
                        });
                    } else {
                        JOptionPane.showMessageDialog(null, "Client is OFFLINE");
                        flag = false;
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Select Client!!!");
                    flag = false;
                }
            }
        });
    }

    private void remote() { //Remote feature
        serverGUI.RemoteDesktopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flag = true;
                Thread remoteThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new InitConnection(Definitions.REMOTE_PORT);
                    }
                });
                remoteThread.start();
                int row = serverGUI.main_table.getSelectedRow();
                LookupServices lookupServices = new LookupServices(listDevices.get(row).getIp());
                if (lookupServices.connect()) {
                    try {
                        lookupServices.iRMIServices.remote();
                        flag = false;
                    } catch (RemoteException ex) {
                    }
                } else {
                    flag = false;
                }
            }
        });
    }

    @Override
    public Devices getDevices(Devices devices) throws RemoteException {
        for (Devices i : listDevices) {
            if (i.getIp().equalsIgnoreCase(devices.getIp())) {
                devices.setBlackList(i.getBlackList());
            }
        }
        return devices;
    }
}
