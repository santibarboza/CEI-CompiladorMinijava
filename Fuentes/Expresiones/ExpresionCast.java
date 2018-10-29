package Expresiones;


import Etapa1.Token;
import Excepciones.ErrorSemantico;
import TabladeSimbolos.Clase;
import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoClase;

public class ExpresionCast extends Expresion {

	protected String idClase;
	protected Operando operando;
	protected String etiq;
	public ExpresionCast(String id,Operando op,Token tok) {
		super(tok);
		idClase=id;
		operando=op;
		etiq=crearEtLugar();

	}

	protected String crearEtLugar(){
		int i=hashCode();
		String EtLugar="N_M_Cast_"+i,lugar="("+token.get_NroLinea()+":"+token.get_NroCol()+")";
		TablaSimbolos.escribirCod(EtLugar+":		DW \""+lugar+"\",0 	; Mensaje de error");
		return EtLugar;
	}
	
	public String getId(){
		return idClase;
	}
	public Operando getOperando(){
		return operando;
	}
	@Override
	public Tipo chequearSentencias() throws ErrorSemantico {
		Tipo t=new TipoClase(idClase);
		if(!(t.conforme(operando.chequearSentencias())))
			throw new ErrorSemantico("El casteo no conforma el tipo de la expresion",token);

		
		Clase c=TablaSimbolos.TS.getClases().get(idClase);
		TablaSimbolos.escribirCod("			;Controlo que la Expresion tenga el tipo del instanceof");
		controlarNull();
		TablaSimbolos.escribirCod("			PUSH "+c.getEtiqueta()+" 	; cargo id de la clase "+idClase);
		TablaSimbolos.escribirCod("			PUSH "+etiq+" ; cargo (n:m");
		TablaSimbolos.escribirCod("			PUSH chequear_Cast ; cargo(n:m)");
		TablaSimbolos.escribirCod("			CALL");
		
		return t;
	}
	
}
