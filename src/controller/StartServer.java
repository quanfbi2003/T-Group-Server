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
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import model.Definitions;
import model.Devices;
import model.Processes;
import view.ControlApps;
import view.ServerGUI;

/**
 *
 * @author dream
 */
public class StartServer {

    private static RMIServer serverRMI;
    private static LookupServices lookupServices;
    private static ServerGUI serverGUI;
    private static ControlApps controlApps;
    private List<Devices> listDevices;
    private List<Processes> listProcesses;
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
                registration();
                controlApplication();
                Thread checkingThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            checkDevices();
                            try {
                                Thread.sleep(5000);
                            } catch (Exception ex) {
                            }
                        }
                    }
                });
                checkingThread.start();
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
        }
        update();
    }

    private void registration() {
        devices = new Devices();
        serverGUI.btRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                devices.setIp(serverGUI.ipAddress.getText());
                devices.setDeviceName("MAY " + (listDevices.size() + 1));
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

    private void controlApplication() {
        serverGUI.AppsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = serverGUI.main_table.getSelectedRow();
                if (row >= 0 && row < listDevices.size()) {
                    lookupServices = new LookupServices(listDevices.get(row).getIp());
                    if (lookupServices.connect()) {
                        controlApps = new ControlApps();
                        controlApps.setVisible(true);
                        Thread servicesThread = new Thread(new Runnable() {
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

                    } else {
                        JOptionPane.showMessageDialog(null, "Client is OFFLINE");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Select Client!!!");
                }

            }
        });

    }

    private void update() {
        serverGUI.tbModel.setRowCount(0);
        SimpleDateFormat date_format = new SimpleDateFormat("hh:mm:ss");
        for (Devices i : listDevices) {
            serverGUI.tbModel.addRow(new Object[]{i.getDeviceName(), i.getIp(), i.getStatus(), i.getStartTime()});
        }
        serverGUI.tbModel.setRowCount(20);
    }

    private void shutdown_and_restart() {
        serverGUI.ShutdownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = serverGUI.main_table.getSelectedRow();
                if (row >= 0 && row < listDevices.size()) {
                    lookupServices = new LookupServices(listDevices.get(row).getIp());
                    if (lookupServices.connect()) {
                        int opt = JOptionPane.showConfirmDialog(null, "Do you want to turn off this computer?");
                        if (opt == 1) {
                            try {
                                lookupServices.iRMIServices.shutdown();
                            } catch (RemoteException ex) {
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Client is OFFLINE");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Select Client!!!");
                }
            }
        });
        
        serverGUI.RestartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = serverGUI.main_table.getSelectedRow();
                if (row >= 0 && row < listDevices.size()) {
                    lookupServices = new LookupServices(listDevices.get(row).getIp());
                    if (lookupServices.connect()) {
                        int opt = JOptionPane.showConfirmDialog(null, "Do you want to restart this computer?");
                        if (opt == 1) {
                            try {
                                lookupServices.iRMIServices.restart();
                            } catch (RemoteException ex) {
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Client is OFFLINE");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Select Client!!!");
                }
            }
        });
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
