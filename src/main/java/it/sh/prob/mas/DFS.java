package it.sh.prob.mas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DFS {

	public static void main(String[] args) {

		try {
			 
			
			String prologModel = "printf \"hello world\"";

			
			//#!/bin/sh

			String[] command = {"/bin/sh","-c ", "printf hello"};
			String[] cmd = {
					"/bin/sh",
					"-c",
					"printf \"0.9::a. \n 0.3::b. \nquery(a). \nquery(b).\" | problog"
					};

			
			// printf "hello world"
			
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(cmd);
			
			pr.waitFor();
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			System.out.println(pr.exitValue());
			Object[] resuslt = input.lines().toArray();

			for (Object object : resuslt) {
				if (object instanceof String) {
					System.out.println((String) object);
				}
			}
			//working on cli
			// printf "0.9::a.\n query(a).\n" | problog

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}