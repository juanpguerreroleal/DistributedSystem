/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfazdatos;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author juan_
 */
class Table extends JFrame {

    public static JTable tabla;
    Object[][] data = {};
    String[] columnNames = {"IP", "S.O.", "Velocidad Procesador", "Uso de Procesador", "Ram", "Ram libre"};
    public static DefaultTableModel modelo;

    public Table() {
        modelo = new DefaultTableModel(data, columnNames);
        tabla = new JTable(modelo);
        setLayout(new FlowLayout());
        tabla.setPreferredScrollableViewportSize(new Dimension(900, 300));
        JScrollPane jsp = new JScrollPane(tabla);
        getContentPane().add(jsp, BorderLayout.CENTER);
        tabla.setFillsViewportHeight(true);
    }

    public void updateTable() {
        String ip = null, so = null, vp = null, up = null, ram = null, freeram = null;
        Boolean existe = false;
        modelo = (DefaultTableModel) tabla.getModel();
        for (int i = 0; i < InterfazDatos.clientes.size(); i++) {
            System.out.println(InterfazDatos.clientes.size());
            ip = InterfazDatos.clientes.get(i).ip;
            so = "";
            vp = InterfazDatos.clientes.get(i).vProcesador;
            up = InterfazDatos.clientes.get(i).uProcesador;
            ram = InterfazDatos.clientes.get(i).ram;
            freeram = InterfazDatos.clientes.get(i).freeram;
        }
        try {
            for (client cliente:InterfazDatos.clientes) {
                System.out.println(cliente.ip);
            }
            
            for (int i = 0; i < modelo.getRowCount(); i++) {
                if (ip.equals(modelo.getValueAt(i, 0))) {
                    existe = true;
                    break;
                }

            }
            if (!existe) {
                modelo.addRow(new Object[]{ip, so, vp, up, ram, freeram});
            }
        } catch (Exception e) {
            existe = false;
        }
        System.out.println("Hola");
        if (modelo.getRowCount() == 0) {
            modelo.addRow(new Object[]{ip, so, vp, up, ram, freeram});
        } else if (modelo.getRowCount() != 0) {
            Table.actualizarfilas(ip, up);
        }

        tabla = new JTable(modelo);
        modelo.fireTableDataChanged();
        setVisible(true);
        repaint();
    }

    public static void actualizarfilas(String ip, String uProc) {
        modelo = (DefaultTableModel) tabla.getModel();
        for (int f = 0; f < modelo.getRowCount(); f++) {
            System.out.println(modelo.getValueAt(f, 0).equals(ip));
            if (modelo.getValueAt(f, 0).equals(ip)) {
                modelo.setValueAt(uProc, f, 3);
            }
        }
    }
    public static void eliminarfilas(String ip) {
        modelo = (DefaultTableModel) tabla.getModel();
        for (int f = 0; f < modelo.getRowCount(); f++) {
            System.out.println(modelo.getValueAt(f, 0).equals(ip));
            if (modelo.getValueAt(f, 0).equals(ip)) {
                modelo.removeRow(f);
            }
        }
    }
}
