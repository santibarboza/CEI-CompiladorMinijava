package Expresiones;


import TabladeSimbolos.Clase;
import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoBoolean;
import Tipos.TipoClase;
import Etapa1.Token;
import Excepciones.ErrorSemantico;

public class ExpresionInstanceOf extends Expresion{
	
	protected String idClase;
	protected Expresion exp;
	
	public ExpresionInstanceOf(Expresion expresion,String ID,Token tok) {
		super(tok);
		exp=expresion;
		idClase=ID;
	}
	public String getIdClase(){
		return idClase;
	}
	public Expresion getExp(){
		return exp;
	}

	@Override
	public Tipo chequearSentencias() throws ErrorSemantico {
		Tipo t=exp.chequearSentencias();
		if(!(t.esValido() && (new TipoClase(idClase)).conforme(t)))
			throw new ErrorSemantico("La expresion no conforma el tipo del Intanceof",token);
		Clase c=TablaSimbolos.TS.getClases().get(idClase);
		controlarNull();
		TablaSimbolos.escribirCod("		LOADREF 1 ; cargo id del CIR de la expresion ");
		TablaSimbolos.escribirCod("		PUSH "+c.getEtiqueta()+" ; cargo id de la clase "+idClase);
		TablaSimbolos.escribirCod("		EQ  ; comparo ids");
		return new TipoBoolean();
	}
	/*
	protected void controlarNull(){
		int i=hashCode();
		String Etiqueta="Correcto_"+i;
		TablaSimbolos.escribirCod("			;Controlo que la expresion no sea nula");
		TablaSimbolos.escribirCod("			DUP		;Duplico la referencia");
		TablaSimbolos.escribirCod("			PUSH 0	;Cargo null");
		TablaSimbolos.escribirCod("			EQ		;Comparo referencia con null");
		TablaSimbolos.escribirCod("			BF "+Etiqueta+"		;Comparo referencia con null");
		TablaSimbolos.escribirCod("			PUSH "+TablaSimbolos.EtiquetaNull+" ;cargo elpuntero al mensaje de error");
		TablaSimbolos.escribirCod("			SPRINT 		; imprimo el Error");
		TablaSimbolos.escribirCod("			PUSH "+crearEtLugar()+" ;cargo elpuntero al mensaje de error");
		TablaSimbolos.escribirCod("			SPRINT 		; imprimo el lugar");
		TablaSimbolos.escribirCod("			PRNLN		; imprimo el \\n");
		TablaSimbolos.escribirCod("			HALT 		; termino con  Error");
		TablaSimbolos.escribirCod("	"+Etiqueta+":		NOP 		; Referencia Correcta");

	}
	*/
}
