package Etapa1;
import java.io.IOException;

import Etapa2.ASint;
public class Principal {

	/**
	 * @param args
	 * @throws  
	 */
	public static void main(String[] args) {	
		
		if(args.length==0)
			System.out.println("Error: Faltan  Parametros de entrada");
		else{
		try{
				String fileI=args[0];
				String fileO=fileI+".asm";
				if(args.length>1)
					fileO=args[1];
				ALex alex=new ALex(fileI);
				ASint asi= new ASint(alex,fileO);
				System.out.println("Inicia Compilacion...");
				asi.inicial();
				System.out.println("Programa Correcto Semanticamente");  
			}catch(Excepciones.ErrorLexico e){
				System.out.println(e.getMessage());
			}catch(IOException e){
				System.out.println(e.getMessage());
			}catch(Excepciones.ErrorSintactico e){
				System.out.println(e.getMessage());
			}catch(Excepciones.ErrorSemantico e){
				System.out.println(e.getMessage());
			}  
		}
	}}	
