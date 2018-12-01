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
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        String vProcesador, uProcesador, ram, freeram;
        do {
            try {
                ois = new ObjectInputStream(this.socket.getInputStream());
                oos = new ObjectOutputStream(this.socket.getOutputStream());
                String data = (String) ois.readObject();
                String[] datos = data.split(",");
                ip = String.valueOf(this.socket.getInetAddress());
                vProcesador = datos[0];
                uProcesador = datos[1];
                ram = datos[2];
                freeram = datos[3];
                client cliente = new client(ip, vProcesador, uProcesador, ram, freeram);
                for (client cli : InterfazDatos.clientes) {
                    if (cli.ip.equals(cliente.ip)) {
                        System.out.println("Entra a remove");
                        InterfazDatos.clientes.remove(cli);
                        InterfazDatos.ips.remove(cli.ip);
                    }
                    else {
                    Table.actualizarfilas(cli.ip, cli.uProcesador);
                    }
                }
                InterfazDatos.clientes.add(cliente);
                InterfazDatos.ips.add(cliente.ip);
            } catch (IOException | ClassNotFoundException ex) {
            } finally {
                System.out.println("Listo, cerrado");
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                break;
            }
        } while (true);
    }

}
