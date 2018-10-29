package Sentencias;

import Tipos.Tipo;
import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Expresiones.Expresion;
import ExpresionesPrimarias.ExpresionPrimario;

public class SentenciaAsignacion extends Sentencia{

	protected ExpresionPrimario exp;
	protected Expresion ladoDer;
	public SentenciaAsignacion(Token tok,ExpresionPrimario expresion,Expresion ladoDerecho) {
		super(tok);
		exp=expresion;
		ladoDer=ladoDerecho;
	}
	public ExpresionPrimario getExpresionPrimaria(){
		return exp;
	}
	public Expresion getLadoDerecho(){
		return ladoDer;
	}


	@Override
	public boolean chequearSentencias() throws ErrorSemantico {
		Tipo t=ladoDer.chequearSentencias();
		exp.esLI();
		Tipo texp=exp.chequearSentencias();
		if(texp.esValido() && !exp.terminaEnVariable())
			throw new ErrorSemantico("El lado Izquierdo de la asignacion no es una variable",exp.getToken());
		if(!(t.esValido() && t.conforme(texp)))
			throw new ErrorSemantico("El tipo del lado derecho de la asignacion no conforma al lado izquierdo",exp.getToken());
		return false;
	}

}
