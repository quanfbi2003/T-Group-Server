/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.RMIServer;

import controller.RMIServer.RMIServerServices;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import model.Definitions;
/**
 *
 * @author dream
 */
public class RMIServer {
    
    public static RMIServerServices rmiServices;
    
    public void startServer() {
        try {
            Registry registry = LocateRegistry.createRegistry(Definitions.SERVER_PORT);
            rmiServices = new RMIServerServices();
            registry.rebind(Definitions.REGISTERED_NAME, rmiServices);
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("RMIServer is online!");
    }
}
