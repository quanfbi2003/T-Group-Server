package controller.DAO;

import java.io.*;
import java.util.List;
import model.Definitions;
import model.Devices;

public class DbUtils {
    
    public static void writeFile(List list) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(Definitions.DATABASE));
            for (Object i : list) {
                out.writeObject(i);
            }
            out.close();
        } catch (IOException e) {
        }
    }
    
    public static void readFile(List<Devices> list) {
        try {
            list.clear();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(Definitions.DATABASE));
            Devices o = null;
            while ((o = (Devices) in.readObject()) != null) list.add(o);
            System.out.println(list.get(0).getDeviceUUID());
            in.close();
        } catch (Exception e) {
        }
    }
}