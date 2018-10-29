package ExpresionesPrimarias;

import java.util.LinkedList;

import TabladeSimbolos.Clase;
import TabladeSimbolos.Metodo;
import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoVoid;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Expresiones.Encadenado;
import Expresiones.Expresion;

public class LlamadaMetodoEstatico extends Llamada{

	protected String idMetVar;	//Metodo estatico
	protected String idClase;
	public LlamadaMetodoEstatico(Token tok,String idMet,LinkedList<Expresion> param, Encadenado enc) {
		super(tok, enc, param);
		idMetVar=idMet;
		idClase=tok.get_Lexema();
	}
	public String getIdClase(){
		return idClase;
	}
	public String getIdMet(){
		return idMetVar;
	}
	@Override
	public Tipo chequearSentencias() throws ErrorSemantico {
		Clase c= TablaSimbolos.TS.getClases().get(idClase);
		if(c==null)
			throw new ErrorSemantico("La  clase "+idClase+"No esta definida",token);
		Metodo m= c.getMetodos().get(idMetVar);
		if(m==null)
			throw new ErrorSemantico("No existe el metodo "+idMetVar+" en la clase"+TablaSimbolos.claseActual.getIdClase(),token);
		if(!m.esStatic())
			throw new ErrorSemantico("El metodo "+idMetVar+" no es un metodo estatico",token);

		//GENCODIGO
		if(!(m.getTipoRetorno() instanceof TipoVoid))
			TablaSimbolos.escribirCod("		RMEM 1 ; reservo un lugar para retorno");
		m.matchParametros(paramActuales,token);
		TablaSimbolos.escribirCod("		PUSH "+m.getEtiqueta()+" 	; pongo la direccion del metodo");
		TablaSimbolos.escribirCod("		CALL		; llamo al metodo "+idClase+"."+idMetVar);
		if(encadenado == null)
			return m.getTipoRetorno();
		else
			return encadenado.chequear(esLI,m.getTipoRetorno());
	}

}
