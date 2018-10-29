package ExpresionesPrimarias;

import java.util.LinkedList;

import Etapa1.Token;
import Expresiones.Encadenado;
import Expresiones.Expresion;

public abstract class Llamada extends ExpresionPrimario{

	protected LinkedList<Expresion>paramActuales;
	public Llamada(Token tok, Encadenado enc,LinkedList<Expresion>param) {
		super(tok, enc);
		paramActuales=param;
	}
	public LinkedList<Expresion> getParamametros(){
		return paramActuales;
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
			return true;
		else 
			return encadenado.terminaEnLlamada();
	}
	
}
