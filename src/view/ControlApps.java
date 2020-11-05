/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author dream
 */
public class ControlApps extends JFrame {

    JMenuBar jMenuBar;
    JMenu jMenu;
    JMenuItem exit;

    JPanel killPn, appPn;
    JButton killBt;

    DefaultTableModel tm;
    JScrollPane sp;
    JTable tb;

    public ControlApps() {
        initGUI();
        initValue();
        initEvent();

    }

    void initGUI() {
        setSize(1200, 700);
        setLayout(new BorderLayout(5, 5));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jMenu = new JMenu("File");
        exit = new JMenuItem("Exit");
        exit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        jMenu.add(exit);
        exit.getAccessibleContext().setAccessibleDescription("");
        jMenuBar = new JMenuBar();
        jMenuBar.add(jMenu);

        setJMenuBar(jMenuBar);

        appPn = new JPanel();
        appPn.setLayout(new BorderLayout());
        sp = new JScrollPane();
        String[] cols = {"Name", "Number of process", "Memory"};

        tm = new DefaultTableModel(cols, 0);
        tb = new JTable(tm);
        sp.setViewportView(tb);
        appPn.add(sp, BorderLayout.CENTER);
        add(appPn, BorderLayout.CENTER);

        killPn = new JPanel();
        killPn.setLayout(new GridLayout(0, 1, 5, 5));
        killBt = new JButton("Đóng ứng dụng");
        killPn.add(killBt);
        add(killPn, BorderLayout.EAST);
    }

    void initValue() {

    }

    void initEvent() {

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int kq = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn đóng!!!", "Đóng", JOptionPane.YES_NO_OPTION);
                if (kq == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
    }
}
    
