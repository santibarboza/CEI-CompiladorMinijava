package BloqueSystem;

import Etapa1.Token;
import Sentencias.Bloque;
import TabladeSimbolos.TablaSimbolos;

public class BloquePrintS extends Bloque{

	public BloquePrintS(Token tok) {
		super(tok);
	}
	public boolean chequearSentencias() {
		TablaSimbolos.escribirCod("		LOAD 3		; cargo el valor del 1er parametro");
		TablaSimbolos.escribirCod("		SPRINT		; imprimo el tope");
		TablaSimbolos.escribirCod("	;Fin  de Codigo de System.printS");
		TablaSimbolos.escribirCod("		STOREFP		; restauro FP ");
		TablaSimbolos.escribirCod("		RET 1		; retorno liberando 1 variables");
		return true;
	}
}
