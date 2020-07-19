/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import Conexion.Conexion;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author User
 */
public class Cliente extends Conexion{
    private String pathFile;
    
    public Cliente(String path) throws IOException {
        super("cliente");
        pathFile = path;
    }
    
    public void startClient(){
        try{
            PrintStream envio=new PrintStream(cs.getOutputStream()); 
            FileInputStream origen=new FileInputStream(pathFile);
            byte[] buffer = new byte[1024];
            int len;
            while((len=origen.read(buffer))>0){
               System.out.println(len);
               envio.write(buffer,0,len);
            }
            cs.close();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
