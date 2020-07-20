/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Cliente;

import Conexion.Conexion;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

/**
 *
 * @author User
 */
public class Cliente extends Conexion{
    private String pathFile;
    InputStream inFromServer;
    PrintWriter pw;
    OutputStream outToServer;
    int len; // number of files on the server retrieved
    String[] names = new String[10000];
    String[] temp_names;
    
    public Cliente() throws IOException {
        super("cliente");
        
	try {
            inFromServer = cs.getInputStream();
            pw = new PrintWriter(cs.getOutputStream(), true);
            outToServer = cs.getOutputStream();
            ObjectInputStream oin = new ObjectInputStream(inFromServer);
            String s = (String) oin.readObject();
            System.out.println(s);        

            len = Integer.parseInt((String) oin.readObject());
            System.out.println(len);

            temp_names = new String[len];
            
            for(int i = 0; i < len; i++) {
                String filename = (String) oin.readObject();
		System.out.println(filename);
                names[i] = filename;
		temp_names[i] = filename;
            }

            // sort the array of strings that's going to get displayed in the scrollpane
            Arrays.sort(temp_names);
        } 
        catch (Exception exc) {
            System.out.println("Exception: " + exc.getMessage());
        }
    }
    
    public void recibirClient(String path){
	FileInputStream file = null;
	BufferedInputStream bis = null;
        String name = path.substring(path.lastIndexOf("\\")+1, path.length());
        System.out.println(name);
	boolean fileExists = true;

 	try {
            file = new FileInputStream(path);
            bis = new BufferedInputStream(file);
	} catch (FileNotFoundException excep) {
            fileExists = false;
            System.out.println("FileNotFoundException:" + excep.getMessage());
	}       
        try{
            if (fileExists) {
                // send file name to server
       		pw.println(name);
                System.out.println("Upload begins");

                // send file data to server
                sendBytes(bis, outToServer);
                System.out.println("Completed");

                boolean exists = false;

                for(int i = 0; i < len; i++){
                    if(names[i].equals(name)){
                        exists = true;
                        break;
                    }
                }

                if(!exists){
                    names[len] = name;
                    len++;
                }

                String[] temp_names = new String[len];

                for(int i = 0; i < len; i++){
                    temp_names[i] = names[i];
                }

                // sort the array of strings that's going to get displayed in the scrollpane
                Arrays.sort(temp_names);

                // update the contents of the list in scroll pane
                // close all file buffers
                bis.close();
                file.close();
                outToServer.close();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
//        try{
//            PrintStream envio=new PrintStream(cs.getOutputStream()); 
//            FileInputStream origen=new FileInputStream(pathFile);
//            byte[] buffer = new byte[1024];
//            int len;
//            while((len=origen.read(buffer))>0){
//               System.out.println(len);
//               envio.write(buffer,0,len);
//            }
//            cs.close();
//        }catch(Exception e){
//            System.out.println("Exception: " + e.getMessage());
//        }
    }
    
    public void descargarClient(String path, String nameDownload){
	try  {
            String name = path.substring(0, path.lastIndexOf("\\")+1);
            System.out.println(name);
            File directory = new File(name);

            if (!directory.exists()) {
		directory.mkdir();
            }
            boolean complete = true;
            byte[] data = new byte[9022386];
            String file = new String("*" + nameDownload + "*");
            pw.println(file);
            ObjectInputStream oin = new ObjectInputStream(inFromServer);
            String s = (String) oin.readObject();

            if(s.equals("Success")) {
                File f = new File(directory, nameDownload);
                FileOutputStream fileOut = new FileOutputStream(f);
		DataOutputStream dataOut = new DataOutputStream(fileOut);

		//empty file case
		while (complete) {
                    int c = inFromServer.read(data, 0, data.length);
                    if (c == -1) {
			complete = false;
			System.out.println("Completed");

                    } else {
                        dataOut.write(data, 0, c);
                        dataOut.flush();
                    }
		}
		fileOut.close();
            }
            else {
		System.out.println("Requested file not found on the server.");
            }
	} 
	catch (Exception exc) {
            System.out.println("Exception: " + exc.getMessage());
	}
    }    
    
    private static void sendBytes(BufferedInputStream in , OutputStream out) throws Exception {
	int size = 9022386;
	byte[] data = new byte[size];
	int bytes = 0;
	int c = in.read(data, 0, data.length);
	out.write(data, 0, c);
	out.flush();
    }
}
