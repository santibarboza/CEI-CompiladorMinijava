package BloqueSystem;

import Etapa1.Token;
import Sentencias.Bloque;
import TabladeSimbolos.TablaSimbolos;

public class BloqueToString extends Bloque {

	public BloqueToString(Token tok) {
		super(tok);
	}
	public boolean chequearSentencias(){
		TablaSimbolos.escribirCod("		LOAD 3		; cargo this");
		TablaSimbolos.escribirCod("		LOADREF 1	; cargo idClase");
		TablaSimbolos.escribirCod("		STORE 4		; escribo el retorno");
		TablaSimbolos.escribirCod("	;Fin  de Codigo de Object.toString");
		TablaSimbolos.escribirCod("		STOREFP		; restauro FP ");
		TablaSimbolos.escribirCod("		RET 1		; retorno liberando this");
		return true;
	}

}
