/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.Server;

import java.rmi.RemoteException;
import java.rmi.registry.*;
import model.Definitions;
/**
 *
 * @author dream
 */
public class StartServer {
    
    public StartServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(Definitions.SERVER_PORT);
            registry.rebind(Definitions.REGISTERED_NAME, new ServerServices());
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("RMIServer is online!");
    }
    
    public static void main(String[] args) {
        new Thread(new Runnable() { // RMI Thread
            @Override
            public void run() {
                new StartServer();
            }
        }).start();
    }
}
