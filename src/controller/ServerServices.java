/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import model.Accounts;
import model.Device;
import model.Processes;
import view.TGroupServer;

/**
 *
 * @author dream
 */
public class ServerServices extends UnicastRemoteObject implements IServerServices {

    public ServerServices() throws RemoteException {
        
    }

    public void RunableThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new TGroupServer().setVisible(true);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                
            }
        }).start();
    }
    
    @Override
    public boolean logIn(Accounts accounts) throws RemoteException {
        return true;
    }
    
}
