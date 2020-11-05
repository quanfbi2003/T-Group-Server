/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import model.Accounts;
import model.Devices;
import view.TGroupServer;

/**
 *
 * @author dream
 */
public class ServerServices extends UnicastRemoteObject implements IServerServices {

    public static TGroupServer mainView;

    public ServerServices() throws RemoteException {
        RunableThread();
    }

    public void RunableThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mainView = new TGroupServer();
                mainView.setVisible(true);

            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(5000);
                        checkStatus();

                    }
                } catch (Exception ex) {
                }
            }
        }).start();
    }

    @Override
    public boolean logIn(Accounts accounts) throws RemoteException {
        return new AccountControl().checkAccount(accounts);
    }

    @Override
    public void setStatus(Devices devices) throws RemoteException {
        for (int i = 0; i < mainView.listDevices.size(); i++) {
            if (devices.getDeviceUUID().equalsIgnoreCase(mainView.listDevices.get(i).getDeviceUUID())) {
                mainView.listDevices.get(i).setIp(devices.getIp());
                mainView.listDevices.get(i).setStatus(devices.getStatus());
                mainView.initDevices(mainView.listDevices);
                break;
            }
        }
    }

    public void checkStatus() throws Exception {
        System.out.println("Start checking devices....");
        boolean check = false;
        for (int i = 0; i < mainView.listDevices.size(); i++) {
            if (mainView.listDevices.get(i).getStatus().equalsIgnoreCase("ONLINE") || mainView.listDevices.get(i).getStatus().equalsIgnoreCase("AVAILABLE")) {
                LookupServices lookupServices = new LookupServices(mainView.listDevices.get(i).getIp(), 2020);
                try {
                    if (!lookupServices.connect()) {
                        mainView.listDevices.get(i).setIp("");
                        mainView.listDevices.get(i).setStatus("OFFLINE");
                        check = true;
                    }
                } catch (Exception ex) {
                }
            }
        }
        if (check) {
            mainView.initDevices(mainView.listDevices);
        }
        System.out.println("Checking devices successfully!!!");
    }
}
