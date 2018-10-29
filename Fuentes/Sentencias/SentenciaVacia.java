package Sentencias;

import Etapa1.Token;

public class SentenciaVacia extends Sentencia {

	public SentenciaVacia(Token tok){
		super(tok);
	}
	@Override
	public boolean chequearSentencias() {
		//Son correctas semanticamente
		return false;
	}

}
