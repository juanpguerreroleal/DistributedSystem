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
        //Inicializamos las variables de control que nos ayudaran a imprimir los datos en la tabla
        String ip = null, so = null, vp = null, up = null, ram = null, freeram = null;
        //Variable de control booleana 
        Boolean existe = false;
        //Se llama al modelo de la tabla
        modelo = (DefaultTableModel) tabla.getModel();
        //For que recorre el ArrayList clientes para imprimir los datos en la tabla
        for (client cliente : InterfazDatos.clientes) {
            //Se asignan sus datos para utilizarlos despues
            ip = cliente.ip;
            so = "";
            vp = cliente.vProcesador;
            up = cliente.uProcesador;
            ram = cliente.ram;
            freeram = cliente.freeram;
            //Se recorre la tabla en busca de la existencia de el cliente
            for (int i = 0; i < modelo.getRowCount(); i++) {
                //Si el cliente se encuentra en la tabla entonces si existe, sino se encuentra no existe
                if (ip.equals(modelo.getValueAt(i, 0))) {
                    existe = true;
                    break;
                }else{
                    existe = false;
                }
            }
            //Si no existe el cliente en la tabla agregarlo a la tabla
            if (!existe) {
                modelo.addRow(new Object[]{ip, so, vp, up, ram, freeram});
            }
            //Si existe el cliente en la tabla, actualzar su dato del uso de procesador
            else if(existe){
                Table.actualizarfilas(ip, up);
            }
        }
        //Modelo
        modelo = (DefaultTableModel) tabla.getModel();
        //Se llama al modelo de la tabla
        tabla = new JTable(modelo);
        //Se le notifica al modelo que los datos cambiaron
        modelo.fireTableDataChanged();
        //Se vuelve visible la tabla
        setVisible(true);
        //Se redibuja la tabla
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
