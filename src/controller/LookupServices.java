/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;
import java.rmi.registry.*;

/**
 *
 * @author dream
 */
public class LookupServices {
    private String ip;
    private int port;
    public static IClientServices iclientservices; 
    
    public LookupServices() {
    }

    public LookupServices(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    
    public boolean connect() throws Exception {
        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            iclientservices = (IClientServices) registry.lookup("IClientServices");
        } catch (Exception ex) {
            return false;
        }
        return true;
    }
}
