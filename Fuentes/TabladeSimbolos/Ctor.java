package TabladeSimbolos;

import java.util.LinkedList;

import Etapa1.Token;
import Excepciones.ErrorSemantico;

public class Ctor extends Unidad{
	protected String idClase;
	public Ctor(Token tok, LinkedList<Variable> params) throws ErrorSemantico{
		super(tok, params);
		idClase=tok.get_Lexema();
		if(!idClase.equals(TablaSimbolos.claseActual.getIdClase()))
			throw new ErrorSemantico("El nombre del Constructor es distinto del de la clase",tok);
		etiq="Ctor_"+idClase;
		cargarParametros();
	}
	public String getIdClase(){
		
		return idClase;
	}

	@Override
	public void chequearDeclaraciones() {
		
	}

	@Override
	public void chequearSentencias() throws ErrorSemantico {
		TablaSimbolos.escribirInicioMetodo(this);
		TablaSimbolos.metodoActual=this;
		bloque.chequearSentencias();
		TablaSimbolos.escribirFinMetodo("Ctor_"+idClase, parametros.size()+1);

	}
	
}
