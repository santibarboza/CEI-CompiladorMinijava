package BloqueSystem;

import Etapa1.Token;
import Sentencias.Bloque;
import TabladeSimbolos.TablaSimbolos;

public class BloquePrintBln extends Bloque{

	public BloquePrintBln(Token tok) {
		super(tok);
	}
	public boolean chequearSentencias() {
		TablaSimbolos.escribirCod("		LOAD 3		; cargo el valor del 1er parametro");
		TablaSimbolos.escribirCod("		BPRINT		; imprimo el tope  (0=false,1=true)");
		TablaSimbolos.escribirCod("		PRNLN		; imprimo un \\n");
		TablaSimbolos.escribirCod("	;Fin  de Codigo de System.printBln");
		TablaSimbolos.escribirCod("		STOREFP		; restauro FP ");
		TablaSimbolos.escribirCod("		RET 1		; retorno liberando 1 variables");
		return true;
	}
}
