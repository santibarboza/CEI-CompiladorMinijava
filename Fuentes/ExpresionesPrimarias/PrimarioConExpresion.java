package ExpresionesPrimarias;

import Excepciones.ErrorSemantico;
import Expresiones.Encadenado;
import Expresiones.Expresion;
import Tipos.Tipo;

public class PrimarioConExpresion extends ExpresionPrimario {

	protected Expresion exp;
	public PrimarioConExpresion(Expresion expresion, Encadenado enc) {
		super(expresion.getToken(), enc);
		exp=expresion;
	}
	public Expresion getExpresion(){
		return exp;
	}
	@Override
	public Tipo chequearSentencias() throws ErrorSemantico {
		Tipo t=exp.chequearSentencias();
		if(encadenado==null)
			return t;
		else
			return encadenado.chequear(esLI,t);
	}
	@Override
	public boolean terminaEnVariable() {
		if(encadenado==null)
			return false;
		else 
			return encadenado.terminaEnVariable();
	}
	@Override
	public boolean terminaEnLlamada() {
		if(encadenado==null)
			return false;
		else 
			return encadenado.terminaEnLlamada();
	}

}
