package it.sh.prob.mas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Stream;

public class DFS {

	public static void main(String[] args) {
			//create file file temp
			//run problog on the file
			//take the result 
			//measure file
			
		
			
			try {
				double startT = System.currentTimeMillis();					
				Runtime rt = Runtime.getRuntime();
				Process pr = rt.exec("problog /home/fd/Documents/phd/workspace/ProbMASSH/rules/test.pl");
				BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line=null;
				Stream<String> result = input.lines();
			 	double finisihT = System.currentTimeMillis();
				
				double totalT = (finisihT-startT);
				System.out.println("STARTED: "+ startT);
				System.out.println("FINISH TIME:"+ finisihT);
				System.out.println("total time(SEC) :" +totalT );
				System.out.println("Done bro....");
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			

		}

}
