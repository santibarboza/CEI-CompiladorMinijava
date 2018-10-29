package Sentencias;

import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoBoolean;
import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Expresiones.Expresion;

public class SentenciaWhile extends Sentencia {
	
	protected Expresion expresion;
	protected Sentencia sentencia;
	
	public SentenciaWhile(Expresion cond,Sentencia sent,Token tok){
		super(tok);
		expresion=cond;
		sentencia=sent;
	}
	public Expresion getCondicion(){
		return expresion;
	}
	public Sentencia getSentencia(){
		return sentencia;
	}

	@Override
	public boolean chequearSentencias() throws ErrorSemantico {
		int n= hashCode();
		TablaSimbolos.escribirCod("	While_"+n+": 	NOP");
		Tipo t=expresion.chequearSentencias();
		if(!(t instanceof TipoBoolean))
			throw new ErrorSemantico("La condicion del while no es booleana",expresion.getToken());
		TablaSimbolos.escribirCod("		BF endWhile_"+n);
		sentencia.chequearSentencias();
		TablaSimbolos.escribirCod("		JUMP "+"While_"+n);
		TablaSimbolos.escribirCod("	endWhile_"+n+": 	NOP");
		return false;
	}

}
