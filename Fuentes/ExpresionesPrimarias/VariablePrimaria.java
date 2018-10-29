package ExpresionesPrimarias;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Expresiones.Encadenado;
import TabladeSimbolos.Metodo;
import TabladeSimbolos.TablaSimbolos;
import TabladeSimbolos.Variable;
import Tipos.Tipo;

public class VariablePrimaria extends ExpresionPrimario {

	protected String idMetvar;
	public VariablePrimaria(Token tok, Encadenado enc) {
		super(tok, enc);
		idMetvar=tok.get_Lexema();
	}

	public String getIdVar(){
		return idMetvar;
	}

	@Override
	public Tipo chequearSentencias() throws ErrorSemantico {
		boolean esAtrib;
		Variable v= TablaSimbolos.metodoActual.getVariables().get(idMetvar);
		if((esAtrib=(v==null))){	//no es var local o param
			v=TablaSimbolos.claseActual.getAtributos().get(idMetvar);
			if(v==null)
				throw new ErrorSemantico("El identificador "+idMetvar+" no es una variable local, ni un parametro formal, ni un atributo de la clase",token);
			esMetEstatico();
		}
		
		if(esAtrib){
			TablaSimbolos.escribirCod("		LOAD 3 				; cargo this");
			controlarNull();
		}
		
		if(esLI && encadenado==null)
			escribirCodigoLIFinal(esAtrib,v);
		else
			escribirCodigoDefault(esAtrib,v);
		
		if(encadenado==null)
			return v.getTipo();
		else
			return encadenado.chequear(esLI,v.getTipo());
		
	}


	private void escribirCodigoDefault(boolean esAtrib, Variable v) {
		int index=v.getIndex();
		if(!esAtrib)
			TablaSimbolos.escribirCod("		LOAD "+index+" 		; Cargo la variable local o parametro "+v.getNombre() );
		else
			TablaSimbolos.escribirCod("		LOADREF "+index+" 	; Cargo el  el atributo "+v.getNombre());

	}

	private void escribirCodigoLIFinal(boolean esAtrib, Variable v) {
		int index=v.getIndex();
		if(!esAtrib)
			TablaSimbolos.escribirCod("		STORE "+index+" 	; Cargo la variable local o parametro "+v.getNombre() );
		else{

			TablaSimbolos.escribirCod("		SWAP 		; Swapeo, en el tope esta el valor a guardar y en tope+1 this");
			TablaSimbolos.escribirCod("		STOREREF "+index+" 		; Guardo el  el atributo "+v.getNombre());			
		}
			
	}

	private void esMetEstatico() throws ErrorSemantico {
		if(TablaSimbolos.metodoActual instanceof Metodo){
			Metodo m=(Metodo)TablaSimbolos.metodoActual;
			if(m.esStatic())
				throw new ErrorSemantico("Un metodo estatico no puede acceder a atributos de instancia",token);;
		}
	}

	@Override
	public boolean terminaEnVariable() {
		if(encadenado==null)
			return true;
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
