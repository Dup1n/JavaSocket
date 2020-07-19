/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import Conexion.Conexion;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    
    public void startServer(){
        try{
            
            System.out.println("Esperando...");
            cs = ss.accept();
            InputStream llegada = cs.getInputStream();
            FileOutputStream destino=new FileOutputStream(pathFile);
            byte[] buffer = new byte[1024];
            int len;
            while((len=llegada.read(buffer))>0){
                System.out.println(len);
                destino.write(buffer,0,len);
            }
            System.out.println("Fin de la conexion");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
