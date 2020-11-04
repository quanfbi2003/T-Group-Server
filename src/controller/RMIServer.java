/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.rmi.RemoteException;
import java.rmi.registry.*;
/**
 *
 * @author dream
 */
public class RMIServer {
    private void startServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(2020);
            registry.rebind("IServerServices", new ServerServices());
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Server is online!");
    }
    
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RMIServer server = new RMIServer();
                server.startServer();
            }
        }).start();
    }
}
