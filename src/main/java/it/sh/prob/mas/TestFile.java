package it.sh.prob.mas;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TestFile {
	String path ="./Test.txt";
	//String path = "/root/Desktop/Test.txt";
	//String path = "/root/Desktop/progettofine/ProbMASSH-test/Res.txt";	//cambiare con directory progetto
	File file = new File(path);
		
	//public void creaFile() {
	//	if (file.exists()){
	//		//bisogna eliminare il file oppure trovare modo di sovrascrivere nel txt
			//System.out.println("Il file " + path + " esiste");
	//	} else
	//		try {
	//			if (file.createNewFile()){
	//				System.out.println("Il file " + path + " è stato creato");
	//				}
	//				else{
	//					System.out.println("Il file " + path + " non può essere creato");
	//				}
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//	}
	
	public void writeFile(String tempo) {
		//String path = "./Test.txt";
		try {
			File file = new File(path);
			FileWriter fw = new FileWriter(file,true);
			fw.write(tempo);
			fw.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}


}
	

