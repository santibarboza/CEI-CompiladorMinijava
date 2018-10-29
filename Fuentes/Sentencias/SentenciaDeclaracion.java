package Sentencias;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import TabladeSimbolos.TablaSimbolos;
import TabladeSimbolos.Variable;
import Tipos.Tipo;

public class SentenciaDeclaracion extends Sentencia {

	protected Tipo tipo;
	protected LinkedList<Token> variables;
	
	public SentenciaDeclaracion(Token tok,Tipo t,  LinkedList<Token> v){
		super(tok);
		tipo=t;
		variables=v;
	}
	public Tipo getTipo(){
		return tipo;
	}
	public  LinkedList<Token> getVariables(){
		return variables;
	}
	@Override
	public boolean chequearSentencias() throws ErrorSemantico {
		Hashtable<String,Variable> varslocales=TablaSimbolos.metodoActual.getVariables();
		HashSet<String> vars=TablaSimbolos.bloqueActual.getVariablesLocales();
		String idVar;
		int indice=TablaSimbolos.metodoActual.getCantVarLocal();
		if(!tipo.esValido())
			throw new ErrorSemantico("El tipo de la declaracion no es un tipo valido",token);
		for(Token t : variables){
			idVar=t.get_Lexema();
			if(varslocales.get(idVar)!=null)
				throw new ErrorSemantico("Ya existe una variable local o un parametro formal con el nombre "+idVar,t);
			varslocales.put(idVar, new Variable(tipo,t,indice));
			vars.add(idVar);
			indice--;
			//Para generar codigo que no solo reserve sino que inicialice ne 0 cada variable
			TablaSimbolos.escribirCod("		PUSH 0 				; reservo espacio para la variable local "+idVar);
		}
		//TablaSimbolos.escribirCod("		RMEM "+variables.size()+" ; reservo "+variables.size()+" Variables Locales");
		TablaSimbolos.metodoActual.setCantVarLocal(indice);
		return false;
	}

}
