package ExpresionesPrimarias;
import TabladeSimbolos.*;
import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Expresiones.Encadenado;
import Tipos.Tipo;
import Tipos.TipoClase;

public class ExpresionThis extends ExpresionPrimario {

	protected String idClase;
	public ExpresionThis(Token tok,Encadenado enc) {
		super(tok,enc);
		idClase=TablaSimbolos.claseActual.getIdClase();
	}

	@Override
	public Tipo chequearSentencias() throws ErrorSemantico {
		esMetEstatico();
		
		Clase clase=TablaSimbolos.TS.getClases().get(idClase);
		if(clase!=null){
			Tipo t=new TipoClase(idClase);
			TablaSimbolos.escribirCod("		LOAD 3 ; Cargo this");
			if(encadenado==null)
				return t;
			else
				return encadenado.chequear(esLI,t);
		}else
			throw new ErrorSemantico("Clase no definida",token);
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
			return false;
		else 
			return encadenado.terminaEnLlamada();
	}
	private void esMetEstatico() throws ErrorSemantico {
		if(TablaSimbolos.metodoActual instanceof Metodo){
			Metodo m=(Metodo)TablaSimbolos.metodoActual;
			if(m.esStatic())
				throw new ErrorSemantico("Un metodo estatico no puede utilizar this",token);;
		}
	}

}
