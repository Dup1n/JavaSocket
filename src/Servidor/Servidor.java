/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import Conexion.Conexion;
import java.io.IOException;


/**
 *
 * @author User
 */
public class Servidor extends Conexion {
    private String pathFile;
    public Servidor(String path) throws IOException{
        super("servidor");
        pathFile = path;
    }
    
//    public void startServer(){
//        try{
//            
//            System.out.println("Esperando...");
//            cs = ss.accept();
//            InputStream llegada = cs.getInputStream();
//            FileOutputStream destino=new FileOutputStream(pathFile);
//            byte[] buffer = new byte[1024];
//            int len;
//            while((len=llegada.read(buffer))>0){
//                System.out.println(len);
//                destino.write(buffer,0,len);
//            }
//            System.out.println("Fin de la conexion");
//        }catch(Exception e){
//            System.out.println(e.getMessage());
//        }
//    }
    
    public void startServer(){        
        try{
            int id = 1;
            System.out.println("Esperando...");
            while (true) {
                cs = ss.accept();
		System.out.println("Cliente con ID: " + id + " conectado de " + cs.getInetAddress().getHostName() + "...");
		Thread server = new HiloServidor(cs, id, pathFile);
		id++;
		server.start();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
