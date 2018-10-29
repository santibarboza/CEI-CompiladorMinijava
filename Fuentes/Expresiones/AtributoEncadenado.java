package Expresiones;

import TabladeSimbolos.Atributo;
import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoClase;
import Etapa1.Token;
import Excepciones.ErrorSemantico;
public class AtributoEncadenado extends Encadenado{

	public AtributoEncadenado(Token tok, String idVar, Encadenado enc) {
		super(tok, idVar, enc);
	}

	@Override
	public Tipo chequear(boolean esLI,Tipo t) throws ErrorSemantico {
		if(!(t instanceof TipoClase))
			throw new ErrorSemantico(idMetVar+" no es un tipo clase, no puede accederse a un atributo",token);
		Atributo a= TablaSimbolos.TS.getClases().get(t.getNombre()).getAtributos().get(idMetVar);
		if(a==null)
			throw new ErrorSemantico("No existe el atributo "+idMetVar+" en la clase"+t.getNombre(),token);
		if(!a.esPublico())
			throw new ErrorSemantico("El atributo "+idMetVar+" no es visible, ya que no es publico",token);
		
		controlarNull();

		int index=a.getIndex();
		if(esLI && encadenado==null){
			TablaSimbolos.escribirCod("		SWAP ; Swapeo, en el tope esta el valor a guardar y en tope+1 this");
			TablaSimbolos.escribirCod("		STOREREF "+index+" ; Guardo el  el atributo "+a.getNombre());
		}
		else
			TablaSimbolos.escribirCod("		LOADREF "+index+" ; Cargo el atributo "+a.getNombre());		
		
		if(encadenado == null){
			return a.getTipo();
		}
		else
			return encadenado.chequear(esLI,a.getTipo());
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
