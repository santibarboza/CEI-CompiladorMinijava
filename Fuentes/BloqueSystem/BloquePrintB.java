package BloqueSystem;

import Etapa1.Token;
import Sentencias.Bloque;
import TabladeSimbolos.TablaSimbolos;

public class BloquePrintB extends Bloque {

	public BloquePrintB(Token tok) {
		super(tok);
	}
	public boolean chequearSentencias(){
		TablaSimbolos.escribirCod("		LOAD 3		; cargo el valor del 1er parametro");
		TablaSimbolos.escribirCod("		BPRINT		; imprimo el tope (0=false,1=true)");
		TablaSimbolos.escribirCod("	;Fin  de Codigo de System.printB");
		TablaSimbolos.escribirCod("		STOREFP		; restauro FP ");
		TablaSimbolos.escribirCod("		RET 1		; retorno liberando 1 variables");
		return true;
	}

}
