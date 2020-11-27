/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import model.Devices;

/**
 *
 * @author dream
 */
public class RMIServices extends UnicastRemoteObject implements IServerServices {

    public RMIServices() throws RemoteException {
    }

    @Override
    public void setStatus(Devices devices) throws RemoteException {
        
    }
}
