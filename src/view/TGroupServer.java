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
public class TGroupServer extends JFrame {

    JMenuBar jMenuBar;
    JMenu jMenu;
    JMenuItem exit;

    JPanel buttonPn, mayTramPn, taiKhoanPn, dieuKhienPn;
    JButton mayTramBt, taiKhoanBt, dieuKhienBt;

    DefaultTableModel tm1, tm2;
    JScrollPane sp1, sp2;
    JTable tb1, tb2;

    public TGroupServer() {
        initGUI();
        initValue();
        initEvent();

    }

    void initMayTramTab() {
        mayTramPn = new JPanel();
        mayTramPn.setLayout(new BorderLayout());
        sp1 = new JScrollPane();
        String[] cols = {"Tên", "Tình trạng", "Người sử dụng", "Bắt đầu", "Đã sử dụng", "Ghi chú"};

        tm1 = new DefaultTableModel(cols, 0);
        tb1 = new JTable(tm1);
        sp1.setViewportView(tb1);
        mayTramPn.add(sp1, BorderLayout.CENTER);
    }

    void initTaiKhoanTab() {
        taiKhoanPn = new JPanel();
        taiKhoanPn.setLayout(new BorderLayout());
        sp2 = new JScrollPane();
        String[] cols = {"ID", "Tên tài khoản", "Trạng thái", "Mật khẩu", "Bắt đầu", "Đã sử dụng"};
        tm2 = new DefaultTableModel(cols, 0);
        tb2 = new JTable(tm2);
        sp2.setViewportView(tb2);
        taiKhoanPn.add(sp2, BorderLayout.CENTER);
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

        buttonPn = new JPanel();
        buttonPn.setLayout(new GridLayout(0, 2, 5, 5));
        mayTramBt = new JButton("Máy trạm");
        taiKhoanBt = new JButton("Tài khoản");
        mayTramBt.setFont(new java.awt.Font("Tahoma", 0, 36));
        taiKhoanBt.setFont(new java.awt.Font("Tahoma", 0, 36));
        buttonPn.add(mayTramBt);
        buttonPn.add(taiKhoanBt);
        add(buttonPn, BorderLayout.NORTH);

        initMayTramTab();
        initTaiKhoanTab();
        add(mayTramPn, BorderLayout.CENTER);
        mayTramBt.setEnabled(false);
        
        dieuKhienPn = new JPanel();
        dieuKhienPn.setLayout(new GridLayout(0, 1, 5, 5));
        dieuKhienBt = new JButton("Điều khiển ứng dụng");
        dieuKhienPn.add(dieuKhienBt);
        add(dieuKhienPn, BorderLayout.EAST);
    }

    void initValue() {

    }

    void initEvent() {
        mayTramBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                add(mayTramPn, BorderLayout.CENTER);
                mayTramPn.setVisible(true);
                taiKhoanPn.setVisible(false);
                mayTramBt.setEnabled(false);
                taiKhoanBt.setEnabled(true);
                dieuKhienPn.setVisible(true);

            }
        });

        taiKhoanBt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                taiKhoanPn.setVisible(true);
                mayTramPn.setVisible(false);
                add(taiKhoanPn, BorderLayout.CENTER);
                taiKhoanBt.setEnabled(false);
                mayTramBt.setEnabled(true);
                dieuKhienPn.setVisible(false);

            }

        });
        
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int kq = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn thoát!!!", "Thoát", JOptionPane.YES_NO_OPTION);
                if (kq == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    public static void main(String[] args) {
        new TGroupServer().setVisible(true);
    }
}
