/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.RMI;

import java.rmi.RemoteException;
import java.rmi.registry.*;
/**
 *
 * @author dream
 */
public class RMIServer {
    public void startServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(2020);
            registry.rebind("ServerServices", new RMIServices());
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Server is online!");
    }
}
