
package BloqueSystem;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Sentencias.Bloque;
import TabladeSimbolos.TablaSimbolos;

public class BloqueRead extends Bloque {

	public BloqueRead(Token tok) {
		super(tok);
	}
	public boolean chequearSentencias() throws ErrorSemantico {
		TablaSimbolos.escribirCod("		READ		; leo el valor");
		TablaSimbolos.escribirCod("		STORE 3		; lo guardo en el retorno");
		TablaSimbolos.escribirCod("	;Fin  de Codigo de System.read");
		TablaSimbolos.escribirCod("		STOREFP		; restauro FP ");
		TablaSimbolos.escribirCod("		RET 0		; retorno liberando 0 variables");
		return true;
	}
	
}
