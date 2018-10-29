package Expresiones;

import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Etapa1.Token;
import Excepciones.ErrorSemantico;

public abstract class Expresion{
	protected Token token;
	
	public Expresion(Token tok){
		token=tok;
	}
	public Token getToken(){
		return token;
	}
	public abstract Tipo chequearSentencias() throws ErrorSemantico;
	
	protected void controlarNull(){
		TablaSimbolos.escribirCod("		;Controlo que el atributo no acceda a null");
		TablaSimbolos.escribirCod("		PUSH error_Null		; seteo el error");
		TablaSimbolos.escribirCod("		PUSH "+token.get_NroLinea()+"		; cargo numero de fila");
		TablaSimbolos.escribirCod("		PUSH "+token.get_NroCol()+"		; cargo nuemro columna");
		TablaSimbolos.escribirCod("		PUSH chequear_Null_Div0		; cargo la direccion de chequeo");
		TablaSimbolos.escribirCod("		CALL ");
	}
}
