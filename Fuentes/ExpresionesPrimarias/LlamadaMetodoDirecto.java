package ExpresionesPrimarias;

import java.util.LinkedList;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Expresiones.Encadenado;
import Expresiones.Expresion;
import TabladeSimbolos.Metodo;
import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoVoid;

public class LlamadaMetodoDirecto extends Llamada {

	protected String idMetvar;
	public LlamadaMetodoDirecto(Token tok,Encadenado enc,LinkedList<Expresion>param) {
		super(tok, enc,param);
		idMetvar=token.get_Lexema();
	}
	protected String getIdMetodo(){
		return idMetvar;
	}
	@Override
	public Tipo chequearSentencias() throws ErrorSemantico {
		Metodo m= TablaSimbolos.claseActual.getMetodos().get(idMetvar);
		if(m==null)
			throw new ErrorSemantico("No existe el metodo "+idMetvar+" en la clase"+TablaSimbolos.claseActual.getIdClase(),token);
		if( TablaSimbolos.metodoActual instanceof Metodo){
			Metodo actual=(Metodo)TablaSimbolos.metodoActual;
			if(actual.esStatic() && !m.esStatic())
				throw new ErrorSemantico("El metodo "+actual.getId()+" es un estatico y no puede llamar al de instancia "+idMetvar,token);
		}
		if(!(m.getTipoRetorno() instanceof TipoVoid))
			TablaSimbolos.escribirCod("		RMEM 1		; reservo un lugar para el retorno ");
		
		//CARGAR THIS
		if(!m.esStatic())
			TablaSimbolos.escribirCod("		LOAD 3	; cargo this");
		
		m.matchParametros(paramActuales,token);
		
		if(m.esStatic()){
			TablaSimbolos.escribirCod("		PUSH "+m.getEtiqueta()+" ; pongo la direccion del metodo");
			TablaSimbolos.escribirCod("		CALL			; llamo al metodo "+m.getEtiqueta());
		}else{
			TablaSimbolos.escribirCod("		DUP	; duplico this");
			TablaSimbolos.escribirCod("		LOADREF 0	; obtengo direccion de VT");	
			TablaSimbolos.escribirCod("		LOADREF "+m.getIndex()+"		; la direccion del metodo"+idMetvar);
			TablaSimbolos.escribirCod("		CALL			; llamo al metodo "+m.getEtiqueta());
		
			
		}
		
		if(encadenado == null)
			return m.getTipoRetorno();
		else
			return encadenado.chequear(esLI,m.getTipoRetorno());
	}
}
