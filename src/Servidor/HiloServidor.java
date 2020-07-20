/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servidor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author User
 */
public class HiloServidor extends Thread {
	int posicionCaracter;
	int completado;
	String nombre, subCadena;
	String nombreFichero;
	Socket cs;
	int contador;
	String nombreDirectorio;

	public HiloServidor(Socket s, int c, String dir) {
		cs = s;
		contador = c;
		nombreDirectorio = dir;
	}

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(cs.getInputStream()));
			InputStream inDelCliente = cs.getInputStream();
			PrintWriter outPw = new PrintWriter(cs.getOutputStream());
			OutputStream output = cs.getOutputStream();

			ObjectOutputStream oout = new ObjectOutputStream(output);
			oout.writeObject("Hola!");

			File ff = new File(nombreDirectorio);
			ArrayList<String> nombres = new ArrayList<String>(Arrays.asList(ff.list()));
			oout.writeObject(String.valueOf(nombres.size()));

			for(String name: nombres) {
				oout.writeObject(name);
			}

			nombre = in.readLine();
			subCadena = nombre.substring(0, 1);

			if (subCadena.equals("*")) {
				posicionCaracter = nombre.lastIndexOf("*");
				nombreFichero = nombre.substring(1, posicionCaracter);
				FileInputStream file = null;
				BufferedInputStream bis = null;
				boolean existeArchivo = true;
				System.out.println("Request to download file " + nombreFichero + " recieved from " + cs.getInetAddress().getHostName() + "...");
				nombreFichero = nombreDirectorio + nombreFichero;
				//System.out.println(nombreFichero);
				try {
					file = new FileInputStream(nombreFichero);
					bis = new BufferedInputStream(file);
				} 
				catch (FileNotFoundException excep) {
					existeArchivo = false;
					System.out.println("FileNotFoundException:" + excep.getMessage());
				}
				if (existeArchivo) {
					oout = new ObjectOutputStream(output);
					oout.writeObject("Success");
					System.out.println("Download begins");
					envioDeBytes(bis, output);
					System.out.println("Completed");
					bis.close();
					file.close();
					oout.close();
					output.close();
				}
				else {
					oout = new ObjectOutputStream(output);
					oout.writeObject("FileNotFound");
					bis.close();
					file.close();
					oout.close();
					output.close();
				}
			} 
			else{
				try {
					boolean complete = true;
					System.out.println("Request to upload file " + nombre + " recieved from " + cs.getInetAddress().getHostName() + "...");
					File directory = new File(nombreDirectorio);
					if (!directory.exists()) {
						System.out.println("Dir made");
						directory.mkdir();
					}

					int size = 9022386;
					byte[] data = new byte[size];
					File fc = new File(directory, nombre);
					FileOutputStream fileOut = new FileOutputStream(fc);
					DataOutputStream dataOut = new DataOutputStream(fileOut);

					while (complete) {
						completado = inDelCliente.read(data, 0, data.length);
						if (completado == -1) {
							complete = false;
							System.out.println("Completed");
						} else {
							dataOut.write(data, 0, completado);
							dataOut.flush();
						}
					}
					fileOut.close();
				} catch (Exception exc) {
					System.out.println(exc.getMessage());
				}
			}
		} 
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	private static void envioDeBytes(BufferedInputStream in , OutputStream out) throws Exception {
		int cantidad = 9022386;
		byte[] dato = new byte[cantidad];
		int c = in .read(dato, 0, dato.length);
		out.write(dato, 0, c);
		out.flush();
	}
}