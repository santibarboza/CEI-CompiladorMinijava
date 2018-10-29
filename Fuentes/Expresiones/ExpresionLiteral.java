package Expresiones;

import TabladeSimbolos.TablaSimbolos;
import Tipos.*;
import Etapa1.Token;

public class ExpresionLiteral extends Operando {

	public ExpresionLiteral(Token tok) {
		super(tok);
	}

	@Override
	public Tipo chequearSentencias() {
		String idTok=token.get_IDTOKEN();
		switch(idTok){
		case "T_NULL":
			TablaSimbolos.escribirCod("		PUSH 0 ; Apilo NULL");			
			return new TipoNull();
		case "Lit_Boolean":
			System.out.println(token.get_Lexema());
			if(token.get_Lexema().equals("false"))
				TablaSimbolos.escribirCod("		PUSH 0 ; Apilo falso");
			else
				TablaSimbolos.escribirCod("		PUSH 1 ; Apilo verdadero");
			return new TipoBoolean();
		case "LitInt":
			TablaSimbolos.escribirCod("		PUSH "+token.get_Lexema()+" ; Apilo un literal Entero");
			return new TipoInt();
		case "LitChar":
			TablaSimbolos.escribirCod("		PUSH  "+Gchar(token.get_Lexema())+" ; Apilo un literal Char");
			return new TipoChar();
			
		}
		return null;
	}
	private int Gchar(String txt){
		return txt.charAt(0);
	}
}
