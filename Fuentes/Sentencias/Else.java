package Sentencias;

import Etapa1.Token;
import Excepciones.ErrorSemantico;

public class Else {
	protected Token tokenElse;
	protected Sentencia sentencia;
	
	public Else(Token Telse,Sentencia sent){
		tokenElse=Telse;
		sentencia=sent;
	}
	public Token getTokenElse(){
		return tokenElse;
	}
	public Sentencia getSentencia(){
		return sentencia;
	}
	public boolean chequearSentencias() throws ErrorSemantico{
		System.out.println(" Sentencia "+sentencia);
		return sentencia.chequearSentencias();
	}
	
}
