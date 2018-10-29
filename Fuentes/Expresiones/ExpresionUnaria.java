package Expresiones;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoBoolean;
import Tipos.TipoInt;

public class ExpresionUnaria extends Expresion {

	protected Expresion expresion;
	public ExpresionUnaria(Token tok,Expresion exp) {
		super(tok);
		expresion=exp;
	}
	public Expresion getExpresion(){
		return expresion;
	}
	@Override
	public Tipo chequearSentencias() throws ErrorSemantico {
		String op=token.get_Lexema();
		if(op.equals("+")){
			if(!(expresion.chequearSentencias() instanceof  TipoInt))
				throw new ErrorSemantico("El operador unario "+op+"solo se puede utilizar con expresiones enteras",token);
			return new TipoInt();
		}else if(op.equals("-")){
			if(!(expresion.chequearSentencias() instanceof  TipoInt))
				throw new ErrorSemantico("El operador unario "+op+"solo se puede utilizar con expresiones enteras",token);
			TablaSimbolos.escribirCod("		NEG ; Complemento el numero en el Tope de la Pila");
			return new TipoInt();
		}else if(op.equals("!")){
			if(!(expresion.chequearSentencias() instanceof  TipoBoolean))
				throw new ErrorSemantico("El operador unario "+op+"solo se puede utilizar con expresiones booleanas",token);
			TablaSimbolos.escribirCod("		NOT ; Niego el Tope de la Pila");
			return new TipoBoolean();
		}
		return null;
	}

}
