package Expresiones;

import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Etapa1.Token;
import Excepciones.ErrorSemantico;

public abstract class Encadenado {
	protected Encadenado encadenado;
	protected Token token;
	protected String idMetVar;
	//protected String etiq;
	
	public Encadenado(Token tok, String id,Encadenado enc){
		encadenado=enc;
		token=tok;
		idMetVar=id;
//		etiq=crearEtLugar();
	}
	public void setEncadenado(Encadenado e){
		encadenado=e;
	}
	public Encadenado getEncadenado(){
		return encadenado;
	}
	public Token getToken(){
		return token;
	}
	public String getId(){
		return idMetVar;
	}
	public abstract Tipo chequear(boolean esLI,Tipo t) throws ErrorSemantico;
	public abstract boolean terminaEnVariable();
	public abstract boolean terminaEnLlamada();
	
	
	protected String crearEtLugar(){
		int i=hashCode();
		String EtLugar="Lugar_"+i,lugar="("+token.get_NroLinea()+":"+token.get_NroCol()+")";;
		TablaSimbolos.escribirCod("			.DATA");
		TablaSimbolos.escribirCod("		"+EtLugar+":		DW \""+lugar+"\",0 ; Mensaje de error");
		TablaSimbolos.escribirCod("			.CODE");
		return EtLugar;
	}
	protected void controlarNull(){
		TablaSimbolos.escribirCod("		;Controlo que el atributo no acceda a null");
		TablaSimbolos.escribirCod("		PUSH error_Null		; seteo el error");
		TablaSimbolos.escribirCod("		PUSH "+token.get_NroLinea()+"		; cargo numero de fila");
		TablaSimbolos.escribirCod("		PUSH "+token.get_NroCol()+"		; cargo nuemro columna");
		TablaSimbolos.escribirCod("		PUSH chequear_Null_Div0		; cargo la direccion de chequeo");
		TablaSimbolos.escribirCod("		CALL ");
	}
}
