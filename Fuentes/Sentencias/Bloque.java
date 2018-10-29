package Sentencias;

import java.util.HashSet;
import java.util.LinkedList;

import TabladeSimbolos.TablaSimbolos;

import Etapa1.Token;
import Excepciones.ErrorSemantico;

public class Bloque extends Sentencia{
	
	protected LinkedList<Sentencia> sentencias;
	protected HashSet<String> variables;
	
	public Bloque(Token tok){
		super(tok);
		sentencias= new LinkedList<Sentencia>();
		variables= new HashSet<String>();
	}
	public void setSentencia(LinkedList<Sentencia> sents){
		sentencias=sents;
	}
	public LinkedList<Sentencia> getSentencias(){
		return sentencias;
	}
	public HashSet<String> getVariablesLocales(){
		return variables;
	}
	

	public boolean chequearSentencias() throws ErrorSemantico {
		int indiceAnterior=TablaSimbolos.metodoActual.getCantVarLocal();
		boolean tieneRet=false,auxiliar=false;
		Bloque ant=TablaSimbolos.bloqueActual;
		TablaSimbolos.bloqueActual=this;
		String Excepciones="";
		for(Sentencia s :sentencias){
			try{
				auxiliar=s.chequearSentencias();
				if(!tieneRet)
					tieneRet=auxiliar;
				else
					throw new ErrorSemantico("La sentencia es Inalcanzable",s.getToken());
			}catch(ErrorSemantico e){Excepciones=Excepciones+e.getMessage()+" \n";}
		}
		if(Excepciones!="")
			throw new ErrorSemantico(Excepciones);
		
		for(String s:variables){
			TablaSimbolos.metodoActual.getVariables().remove(s);
		}
		if(!tieneRet && variables.size()>0)
			TablaSimbolos.escribirCod("		FMEM "+variables.size()+" ;Libero "+variables.size()+" Variables Locales ");
		TablaSimbolos.metodoActual.setCantVarLocal(indiceAnterior);
		TablaSimbolos.bloqueActual=ant;
		
		return tieneRet;
	}
}
