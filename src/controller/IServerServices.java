/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.rmi.*;
import model.Accounts;
/**
 *
 * @author dream
 */
public interface IServerServices extends Remote{
    public boolean logIn(Accounts accounts) throws RemoteException;
}
