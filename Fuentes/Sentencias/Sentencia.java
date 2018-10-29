package Sentencias;

import Etapa1.Token;
import Excepciones.ErrorSemantico;

public abstract class Sentencia{
	protected Token token;
	
	public Sentencia(Token tok){
		token=tok;
	}
	public Token getToken(){
		return token;
	}
	public abstract boolean chequearSentencias() throws ErrorSemantico;
}
