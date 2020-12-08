/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.Client;
import java.rmi.registry.*;
import model.Definitions;

/**
 *
 * @author dream
 */
public class LookupServices {
    private String ip;
    public static IRMIClientServices iRMIServices; 
    
    public LookupServices(String ip) {
        this.ip = ip;
    }
    
    public boolean connect() {
        try {
            Registry registry = LocateRegistry.getRegistry(ip, Definitions.CLIENT_PORT);
            iRMIServices = (IRMIClientServices) registry.lookup(Definitions.REGISTERED_NAME);
        } catch (Exception ex) {
            return false;
           
        }
        return true;
    }
}
