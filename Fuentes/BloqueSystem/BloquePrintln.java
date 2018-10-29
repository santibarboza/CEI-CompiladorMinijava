package BloqueSystem;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Sentencias.Bloque;
import TabladeSimbolos.TablaSimbolos;

public class BloquePrintln extends Bloque{

	public BloquePrintln(Token tok) {
		super(tok);
	}
	public boolean chequearSentencias() throws ErrorSemantico {

		TablaSimbolos.escribirCod("        PRNLN        ; imprimo un \\n");
		TablaSimbolos.escribirCod("    ;Fin  de Codigo de System.println");
		TablaSimbolos.escribirCod("        STOREFP        ; restauro FP");
		TablaSimbolos.escribirCod("        RET 0        ; retorno liberando 0 variables");

		return true;
	}
}
