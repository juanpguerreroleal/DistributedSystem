/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfazdatos;

/**
 *
 * @author juan_
 */
class client {
    public String ip, vProcesador, uProcesador, ram,freeram;
    
    client(String ip, String vProcesador, String uProcesador, String ram,String freeram ){
        this.ip = ip;
        this.vProcesador = vProcesador;
        this.uProcesador = uProcesador;
        this.ram = ram;
        this.freeram = freeram;
    }
}
