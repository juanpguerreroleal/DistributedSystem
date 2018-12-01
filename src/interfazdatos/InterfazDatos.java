/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfazdatos;

import com.sun.management.OperatingSystemMXBean;
import static interfazdatos.Table.modelo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.management.ManagementFactory;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class InterfazDatos {

    public static ArrayList<client> clientes = new ArrayList<>();
    public static ArrayList<String> ips = new ArrayList<>();
    public static ArrayList<String> colaClientes = new ArrayList<>();
    public static boolean soyServidor = true;

    public static void main(String[] args) throws IOException, InterruptedException {
        do {
            //Soy servidor si no esta saturado mi procesador
            soyServidor = !estadoSaturacion();
            //Se obtiene la ip que tiene mejor rank
            String ipOptima = obtenerIpOptima();
            //Se obtiene la direccion local 
            InetAddress direccion = InetAddress.getLocalHost();
            //Se obtiene la ip local
            String miIp = direccion.getHostAddress();
            //Soy servidr si mi procesador no esta saturado y si mi ip es la mas optima
            soyServidor = estadoSaturacion() != true && ipOptima.equals(miIp);
            //Mientras sea servidor realiza el proceso de servidor
            while (soyServidor) {
                Socket s = null;
                ServerSocket ss = new ServerSocket(5432);
                Table gui = new Table();
                gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gui.setSize(600, 300);
                gui.pack();
                gui.setVisible(true);
                while (soyServidor) {
                    try {
                        ExecutorService pool = Executors.newFixedThreadPool(1);
                        //Se acepta una nueva peticion
                        s = ss.accept();
                        //Se crea un nuevo hilo para el cliente
                        Runnable hilo = new ClientThread(s);
                        //Se ejecuta el hilo en un poolThread
                        pool.execute(hilo);
                        pool.shutdown();
                        while (!pool.isTerminated()) {
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    //Se realiza un update de la tabla
                    gui.updateTable();
                    eliminarDesconectados();
                    //Se recorre el ArrayList
                    //TimeUnit.SECONDS.sleep(1);

                    //Se recorre toda la tabla
                    /*for (int f = 0; f < modelo.getRowCount(); f++) {
                        //Si algun valor de cualquier fila de la columna 0 (ip) no esta dentro del ArrayList ips
                        if (!InterfazDatos.ips.contains(modelo.getValueAt(f, 0))) {

                        }
                    }*/
                    //Se verifica si aun si la maquina local es servidor verificando la saturacion
                    soyServidor = !estadoSaturacion();
                    //Se obtiene la ip optima
                    ipOptima = obtenerIpOptima();
                    //Se envia el nuevo servidor a todas las maquinas
                    enviarNuevoServidor(ipOptima);
                }

            }
            while (!soyServidor) {
                System.out.println("Ya no soy servidorrrrrrrrrrrrrrrr");
                Table gui = new Table();
                gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gui.setSize(600, 300);
                gui.pack();
                gui.setVisible(false);
                ObjectOutputStream oos = null;
                ObjectInputStream ois = null;
                Socket s = null;
                String vProcesador, uProcesador, ram, datos, freeram;
                String servidor = recibirNuevoServidor();
                do {
                    try {
                        vProcesador = VProcesador();
                        uProcesador = UProcesador();
                        ram = Ram();
                        freeram = RamFree();
                        //La ip y puerto del servidor actual
                        s = new Socket(servidor, 5432);
                        oos = new ObjectOutputStream(s.getOutputStream());
                        ois = new ObjectInputStream(s.getInputStream());
                        datos = vProcesador + "," + uProcesador + "," + ram + "," + freeram + ",";
                        oos.writeObject(datos);
                        System.out.println("Enviando datos de cliente");
                        servidor = recibirNuevoServidor();
                        soyServidor = estadoSaturacion() != true && ipOptima.equals(miIp);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } while (true);
            }
        } while (true);
    }

    private static String VProcesador() {
        Sigar sigar = new Sigar();
        String output = "";
        String parts[];
        String part2 = null;
        CpuInfo[] cpuInfoList = null;
        try {
            cpuInfoList = sigar.getCpuInfoList();
            for (CpuInfo info : cpuInfoList) {
                output += info.getVendor();
                output += " ";
                output += info.getMhz();
                parts = output.split(" ");
                part2 = parts[1];
                break;
            }
        } catch (SigarException e) {

        }
        return part2;
    }

    private static String UProcesador() {
        String output = "";
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        output += (osBean.getProcessCpuLoad() * 100) + "%";
        return output;
    }

    private static String Ram() throws SigarException {
        Sigar sigar = new Sigar();
        String output = "";
        int mb = 1024 * 1024;
        int gb = 1024 * 1024 * 1024;
        Mem mem = sigar.getMem();
        output += (mem.getTotal() / gb) + "gb";
        return output;
    }

    private static String RamFree() throws SigarException {
        Sigar sigar = new Sigar();
        String output = "";
        int mb = 1024 * 1024;
        int gb = 1024 * 1024 * 1024;
        Mem freemem = sigar.getMem();
        output += (freemem.getFree() / gb) + "gb";
        return output;
    }

    private static boolean estadoSaturacion() {
        boolean estado;
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        long usoProcesador = (long) (osBean.getProcessCpuLoad() * 100);
        estado = colaClientes.size() > 50 && usoProcesador > 90;
        return estado;
    }

    private static void enviarNuevoServidor(String ipOptima) throws IOException {
        MulticastSocket enviador = new MulticastSocket();
        // El dato que queramos enviar en el mensaje, como array de bytes.
        byte[] dato = ipOptima.getBytes();

        // Usamos la direccion Multicast 230.0.0.1, por poner alguna dentro del rango
        // y el puerto 55557, uno cualquiera que esté libre.
        DatagramPacket dgp = new DatagramPacket(dato, dato.length, InetAddress.getByName("127.198.1.0"), 5431);

        // Envío
        enviador.send(dgp);
    }

    private static String recibirNuevoServidor() throws IOException {
        String ipOptima;
        // El mismo puerto que se uso en la parte de enviar.
        MulticastSocket escucha = new MulticastSocket(5431);

        // Nos ponemos a la escucha de la misma IP de Multicast que se uso en la parte de enviar.
        escucha.joinGroup(InetAddress.getByName("127.198.1.0"));

        // Un array de bytes con tamaño suficiente para recoger el mensaje enviado, 
        // bastaría con 4 bytes.
        byte[] dato = new byte[5120];

        // Se espera la recepción. La llamada a receive() se queda
        // bloqueada hasta que llegue un mensaje.
        DatagramPacket dgp = new DatagramPacket(dato, dato.length);
        escucha.receive(dgp);

        // Obtención del dato ya relleno.
        dato = dgp.getData();
        ipOptima = new String(dato, 0, dgp.getLength());
        return ipOptima;
    }

    private static String obtenerIpOptima() throws UnknownHostException {
        String ipOptima;
        //Proceso de la tabla de rankeo para obtener ipOptima
        InetAddress direccion = InetAddress.getLocalHost();
        ipOptima = direccion.getHostAddress();
        return ipOptima;
    }

    public static boolean tablaContieneIp(String ip) {
        boolean contiene = false;
        for (int k = 0; k < modelo.getRowCount(); k++) {
            if (String.valueOf(modelo.getValueAt(k, 0)) == ip) {
                contiene = true;
                break;
            } else {
                contiene = false;
            }
        }
        return contiene;
    }

    public static void eliminarDesconectados() {
        //Se llama al modelo
        modelo = (DefaultTableModel) Table.tabla.getModel();
        for (int f = 0; f < InterfazDatos.clientes.size(); f++) {
            //Se recorre la tabla
            boolean seEncuentra = false;
            for (int k = 0; k < modelo.getRowCount(); k++) {
                //Si el ArrayList no contiene la ip pero la tabla si
                System.out.println(!(InterfazDatos.ips.contains(String.valueOf(modelo.getValueAt(k, 0)))));
                if (!(InterfazDatos.ips.contains(String.valueOf(modelo.getValueAt(k, 0))))) {
                    //Obteniendo la ip a eliminar de la tabla
                    String ipEliminada = String.valueOf(modelo.getValueAt(f, 0));
                    //Eliminando ip a eliminar de la tabla
                    Table.eliminarfilas(ipEliminada);
                }

            }

        }
    }

}
