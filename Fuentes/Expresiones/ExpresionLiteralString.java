package Expresiones;

import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoString;
import Etapa1.Token;

public class ExpresionLiteralString extends ExpresionLiteral {

	protected String etiqueta; 
	public ExpresionLiteralString(Token tok) {
		super(tok);
		crearEtiqueta();
	}
	public Tipo chequearSentencias() {
		TablaSimbolos.escribirCod("		PUSH "+etiqueta+" ; Apilo un literal String");
		return new TipoString();
	}
	private void crearEtiqueta(){
		int i=hashCode();
		etiqueta="Literal_String_"+i;
		TablaSimbolos.escribirCod(etiqueta+":		DW \""+token.get_Lexema()+"\",0		; Almaceno el String");
	}
}
