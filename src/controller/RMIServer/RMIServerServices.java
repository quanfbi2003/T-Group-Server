/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.RMIServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import model.Devices;

/**
 *
 * @author dream
 */
public class RMIServerServices extends UnicastRemoteObject implements IRMIServerServices {

    public RMIServerServices() throws RemoteException {
    }

    @Override
    public void setStatus(Devices devices) throws RemoteException {
        
    }
}
