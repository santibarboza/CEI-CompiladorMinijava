package Sentencias;

import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoVoid;
import Etapa1.Token;
import Excepciones.ErrorSemantico;
import ExpresionesPrimarias.ExpresionPrimario;

public class SentenciaLlamada extends Sentencia {

	protected ExpresionPrimario exp;
	public SentenciaLlamada(Token tok,ExpresionPrimario expresion) {
		super(tok);
		exp=expresion;
	}
	public ExpresionPrimario getExp(){
		return exp;
	}

	@Override
	public boolean chequearSentencias() throws ErrorSemantico {
		Tipo texp=exp.chequearSentencias();
		if(texp.esValido() && !exp.terminaEnLlamada())
			throw new ErrorSemantico("La expresion no es una llamada a un metodo",exp.getToken());
		if(!(texp instanceof TipoVoid))
			TablaSimbolos.escribirCod("		POP		; desapilo el retorno, por voidling");
		return false;
	}

}
