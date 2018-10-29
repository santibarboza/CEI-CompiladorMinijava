package BloqueSystem;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Sentencias.Bloque;
import TabladeSimbolos.TablaSimbolos;

public class BloqueReadInt extends Bloque {
	public BloqueReadInt(Token tok) {
		super(tok);
	}
	public boolean chequearSentencias() throws ErrorSemantico {
		TablaSimbolos.escribirCod(" 		PUSH 0			; inicializo en 0 el acumulador w");
		TablaSimbolos.escribirCod("leer:	READ			; leo un digito d");
		TablaSimbolos.escribirCod("		DUP				; duplico d");
		TablaSimbolos.escribirCod("		PUSH 10			; apilo un //n");
		TablaSimbolos.escribirCod("		EQ				; lo comparo");
		TablaSimbolos.escribirCod("		BF convertir	; si d no es un //n lo convierto a int");
		TablaSimbolos.escribirCod("		POP				; si es //n lo desapilo");
		TablaSimbolos.escribirCod("		JUMP Salir		; salgo");
		TablaSimbolos.escribirCod("convertir:	NOP");
		TablaSimbolos.escribirCod("		PUSH 48			; cargo 48");
		TablaSimbolos.escribirCod("		SUB				; obtengo el d en int en la pila");
		TablaSimbolos.escribirCod("		SWAP			; pongo a w en la pila");
		TablaSimbolos.escribirCod("		PUSH 10			; cargo 10");
		TablaSimbolos.escribirCod("		MUL				; w = w*10");
		TablaSimbolos.escribirCod("		ADD				; w = w +d");
		TablaSimbolos.escribirCod("		JUMP leer		; leo el siguiente digito");
		TablaSimbolos.escribirCod("salir:	STORE 3");
		TablaSimbolos.escribirCod("	;Fin  de Codigo de System.readInt");
		TablaSimbolos.escribirCod("		STOREFP		; restauro FP ");
		TablaSimbolos.escribirCod("		RET 0		; retorno liberando 0 variables");
		

		return true;
	}
}
