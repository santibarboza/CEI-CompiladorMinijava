package Sentencias;

import TabladeSimbolos.Metodo;
import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoVoid;
import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Expresiones.Expresion;

public class SentenciaReturn extends Sentencia {

	protected Expresion expresion;
	
	public SentenciaReturn(Token tok,Expresion exp){
		super(tok);
		expresion=exp;
	}
	public Expresion getExpresion(){
		return expresion;
	}

	@Override
	public boolean chequearSentencias() throws ErrorSemantico {
		if(!(TablaSimbolos.metodoActual instanceof Metodo))
			throw new ErrorSemantico("Los constructores no pueden tener un return",token);
		Metodo met=((Metodo)(TablaSimbolos.metodoActual));
		Tipo tipo=met.getTipoRetorno();
		int cantVar=met.getCantVarLocal() *(-1);
		int ret=met.getParamFormales().size();
		if(!met.esStatic())
			ret++;
		if(expresion==null){
			if(!(tipo instanceof TipoVoid))
				throw new ErrorSemantico("La sentencia return; solo puede ser ejecutada en metodos con tipo de retorno void",token);
			TablaSimbolos.escribirCod("		FMEM "+cantVar+" 		;Libero "+cantVar+" Variables Locales ");
			TablaSimbolos.escribirFinMetodo(met.getIdClase()+"_"+met.getId(),ret);
		}else {
			Tipo t=expresion.chequearSentencias();
			if(!(t.esValido() && t.conforme(tipo)))
				throw new ErrorSemantico("El tipo del return no es valido para el metodo",token);
			if(tipo instanceof TipoVoid)
				TablaSimbolos.escribirCod("		STORE "+(ret+2)+"	; lo guardo en el retorno");//ret+ ED+PR
			else
				TablaSimbolos.escribirCod("		STORE "+(ret+3)+"	; lo guardo en el retorno");//ret+ ED+PR
			TablaSimbolos.escribirCod("		FMEM "+cantVar+"		;Libero "+cantVar+" Variables Locales ");
			TablaSimbolos.escribirFinMetodo(met.getIdClase()+"_"+met.getId(),ret);
		}
		return true;
	}

}
