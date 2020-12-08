/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.Server;

import controller.DAO.DbUtils;
import controller.remote.*;
import controller.Client.LookupServices;
import java.awt.event.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static StartRemote startRemote;

    private List<Devices> listDevices;
    private List<Processes> listProcesses;
    private Devices devices;
    private Devices devicesReg;
    private Thread checkingThread;
    private Thread checkDeviceThread;
    private boolean flag = false;

    public ServerServices() throws RemoteException {
        runableThread();
    }

    private void runableThread() {
        Thread serverGUIThread = new Thread(new Runnable() { // Constructer Thread
            @Override
            public void run() {
                System.out.println("Constructer Thread Running....");
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
                System.out.println("Constructer Thread Done!");
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
                        if (i == 3) {
                            flag = false;
                        }
                    } catch (Exception ex) {
                    }
                }
            }
        }).start();

        checkingThread = new Thread(new Runnable() { // Connect to client Thread
            @Override
            public void run() {
                while (true) {
                    System.out.println("Checking Thread running...");
                    try {
                        Thread.sleep(5000);
                        if (!flag) {
                            checkDevices();
                            System.out.println("Checking....");
                        }
                        System.out.println("Flag....." + flag);
                        System.out.println("Checking Thread Done!");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        serverGUIThread.start();
        checkingThread.start();
    }

    private void checkDevices() { // Check online devices
        System.out.println("Checking Device running...");
        boolean flagW = true;
        for (int i = 0; !flag && i < listDevices.size(); i++) {
            devices = listDevices.get(i);
            checkDeviceThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Checking Device Thread running...");
                    LookupServices lookupServices = new LookupServices(devices.getIp());
                    System.out.println(devices.getIp());
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
                    System.out.println("Checking Device Thread Done!");
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
            flagW = false;
            update();
        }
        System.out.println("Checking Device Done!");
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
                update();
                flag = false;
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
                        int row = serverGUI.main_table.getSelectedRow();
                        if (row >= 0 && row < listDevices.size()) {
                            LookupServices lookupServices = new LookupServices(listDevices.get(row).getIp());
                            if (lookupServices.connect()) {
                                startRemote = new StartRemote();
                                startRemote.initialize(listDevices.get(row).getIp(), Definitions.REMOTE_PORT);
                                startRemote.createFrame.frame.addWindowListener(new WindowListener() {
                                    @Override
                                    public void windowOpened(WindowEvent e) {
                                    }

                                    @Override
                                    public void windowClosing(WindowEvent e) {
                                        try {
                                            startRemote.sc.close();
                                        } catch (IOException ex) {
                                        }
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
                                flag = false;
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
                remoteThread.start();

                try {
                    remoteThread.join();
                } catch (InterruptedException ex) {
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
