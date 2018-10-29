package Sentencias;

import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoBoolean;
import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Expresiones.Expresion;

public class SentenciaIf extends Sentencia {

	protected Expresion expresion;
	protected Sentencia sentencia;
	protected Else elseS;

	public SentenciaIf(Expresion cond,Sentencia sent,Token tok,Else sente){
		super(tok);
		expresion=cond;
		sentencia=sent;
		elseS=sente;
	}
	public Expresion getCondicion(){
		return expresion;
	}
	public Sentencia getSentencia(){
		return sentencia;
	}
	@Override
	public boolean chequearSentencias() throws ErrorSemantico {
		boolean termino=false;
		int n=hashCode();
		TablaSimbolos.escribirCod("	if_"+n+":	NOP; inicio del if");
		Tipo t=expresion.chequearSentencias();
		if(!(t instanceof TipoBoolean))
			throw new ErrorSemantico("La condicion del if no es booleana",expresion.getToken());
		TablaSimbolos.escribirCod("		BF else_"+n);
		termino=sentencia.chequearSentencias();
		TablaSimbolos.escribirCod("		JUMP endif_"+n);
		TablaSimbolos.escribirCod("	else_"+n+": 		NOP");
		boolean tiene=false;
		if(elseS!=null )
			tiene=elseS.chequearSentencias();
		termino=termino && tiene;
		TablaSimbolos.escribirCod("	endif_"+n+": 	NOP");
		
		return termino;
	}

}
