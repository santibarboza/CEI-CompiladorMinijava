package BloqueSystem;

import Etapa1.Token;
import Sentencias.Bloque;
import TabladeSimbolos.TablaSimbolos;

public class BloquePrintC extends Bloque {

	public BloquePrintC(Token tok) {
		super(tok);
	}
	public boolean chequearSentencias() {
		TablaSimbolos.escribirCod("		LOAD 3		; cargo el valor del 1er parametro");
		TablaSimbolos.escribirCod("		CPRINT		; imprimo el c(tope)");
		TablaSimbolos.escribirCod("	;Fin  de Codigo de System.printC");
		TablaSimbolos.escribirCod("		STOREFP		; restauro FP");
		TablaSimbolos.escribirCod("		RET 1		; retorno liberando 1 variables");
		return true;
	}
}
