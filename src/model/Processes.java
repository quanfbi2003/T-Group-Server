/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author dream
 */

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dream
 */
public class Processes implements Serializable{
    public static final long serialVersionUID = 2020;
    private String processName;
    private int processNum;
    private int processMem;

    public Processes() {
    }

    public Processes(String processName, int processNum, int processMem) {
        this.processName = processName;
        this.processNum = processNum;
        this.processMem = processMem;
    }
    
    public Processes(String processName) {
        this.processName = processName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public int getProcessNum() {
        return processNum;
    }

    public void setProcessNum(int processNum) {
        this.processNum = processNum;
    }

    public int getProcessMem() {
        return processMem;
    }

    public void setProcessMem(int processMem) {
        this.processMem = processMem;
    }
}

