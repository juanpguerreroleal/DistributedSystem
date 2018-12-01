/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfazdatos;

import static interfazdatos.Table.modelo;
import static interfazdatos.Table.tabla;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author juan_
 */
public class ClientThread implements Runnable {

    String ip = null;
    protected Socket socket;

    public ClientThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    @Override
    public void run() {
        //Se declaran los tuneles de informacion
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        String vProcesador, uProcesador, ram, freeram;
        //
        try {
            //Se recibe la informacion de los Stream
            ois = new ObjectInputStream(this.socket.getInputStream());
            oos = new ObjectOutputStream(this.socket.getOutputStream());
            //Se lee la informacion del cliente
            String data = (String) ois.readObject();
            //Se realiza un split para obtener los distintos datos
            String[] datos = data.split(",");
            //Se obtiene la ip del cliente
            ip = String.valueOf(this.socket.getInetAddress());
            //Se obtiene la velocidad de procesador cliente
            vProcesador = datos[0];
            //Se obtiene el uso de procesador cliente
            uProcesador = datos[1];
            //Se obtiene la ram de cliente
            ram = datos[2];
            //Se obtiene la ram disponible de cliente
            freeram = datos[3];
            //Se crea un objeto cliente con atributos ip, vProcesador, uProcesador, ram y freeram
            client cliente = new client(ip, vProcesador, uProcesador, ram, freeram);
            //Para cada cliente en el ArrayList clientes
            for (client cli : InterfazDatos.clientes) {
                //Si la ip del cliente es igual a la ip de algun cliente en el ArrayList clientes
                if (cli.ip.equals(cliente.ip)) {
                    //Borra al cliente del ArrayList clientes
                    InterfazDatos.clientes.remove(cli);
                    //Borra la ip del cliente del ArrayList ips
                    InterfazDatos.ips.remove(cli.ip);
                } else {
                    //Si la ip del cliente no es igual a la ip de algun cliente en el ArrayList clientes
                    //Table.actualizarfilas(cli.ip, cli.uProcesador);
                }
            }
            //Agrega un nuevo cliente al ArrayList clientes
            InterfazDatos.clientes.add(cliente);
            //Agrega una nueva ip al ArrayList ips
            InterfazDatos.ips.add(cliente.ip);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                //Una vez realizado el try cierra el socket
                System.out.println("Listo, cerrado");
                if( oos != null) oos.close();
                if( ois != null) ois.close();
                if( socket != null) socket.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
