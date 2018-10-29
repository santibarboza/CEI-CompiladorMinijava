package Expresiones;

import java.util.LinkedList;

import TabladeSimbolos.Metodo;
import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoClase;
import Tipos.TipoVoid;

import Etapa1.Token;
import Excepciones.ErrorSemantico;

public class LlamadaEncadenado extends Encadenado {

	protected LinkedList<Expresion> parametros;
	public LlamadaEncadenado(Token tok, String idMet,LinkedList<Expresion> param, Encadenado enc) {
		super(tok, idMet, enc);
		parametros=param;
	}
	public LlamadaEncadenado(Token tok, String idMet,LinkedList<Expresion> param) {
		super(tok, idMet, null);
		parametros=param;
	}
	public LinkedList<Expresion> getParametros(){
		return parametros;
	}
	@Override
	public Tipo chequear(boolean esLI,Tipo t) throws ErrorSemantico{
		if(!(t instanceof TipoClase))
			throw new ErrorSemantico("El receptor de la llamada del metodo "+idMetVar+" no es un tipo clase",token);
		Metodo m= TablaSimbolos.TS.getClases().get(t.getNombre()).getMetodos().get(idMetVar);
		if(m==null)
			throw new ErrorSemantico("No existe el metodo "+idMetVar+" en la clase"+t.getNombre(),token);
		//if(m.esStatic())
		//	throw new ErrorSemantico("El metodo "+idMetVar+" es un Metodo estatico, tiene que llamarse con una clase,no con objeto receptor",token);
		
		int index=m.getIndex();
		if(!(m.getTipoRetorno() instanceof TipoVoid)){
			TablaSimbolos.escribirCod("		RMEM 1		; reservo un lugar para el retorno ");
			TablaSimbolos.escribirCod("		SWAP		; bajo this ");
		}
		m.matchParametros(parametros, token);
		
		
//		Si met estatico s epuede
		if(m.esStatic()){
			TablaSimbolos.escribirCod("		POP		;Me quito el CIR porque el metodo es estatico");	
			TablaSimbolos.escribirCod("		PUSH "+m.getEtiqueta()+" 	;cargo la direccion del metodoc");	
			TablaSimbolos.escribirCod("		CALL		;llamo al metodo");
		}else{
			controlarNull();
			TablaSimbolos.escribirCod("		DUP			; duplico this");
			TablaSimbolos.escribirCod("		LOADREF 0	; obtengo direccion de VT");
			TablaSimbolos.escribirCod("		LOADREF "+index+"	; obtengo direccion del metodo"+m.getId());
			TablaSimbolos.escribirCod("		CALL	; llamo al metodo "+m.getId());
		}
		
		if(encadenado == null)
			return m.getTipoRetorno();
		else
			return encadenado.chequear(esLI,m.getTipoRetorno());
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
