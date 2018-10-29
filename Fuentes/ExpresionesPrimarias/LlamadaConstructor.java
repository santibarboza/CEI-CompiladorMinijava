package ExpresionesPrimarias; 

import java.util.LinkedList;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Expresiones.Encadenado;
import Expresiones.Expresion;
import TabladeSimbolos.Clase;
import TabladeSimbolos.Ctor;
import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoClase;

public class LlamadaConstructor extends Llamada {
	protected String idClase;
	public LlamadaConstructor(Token tok,LinkedList<Expresion>param, Encadenado enc) {
		super(tok, enc,param);
		idClase=tok.get_Lexema();
	}
	public String getId(){
		return idClase;
	}
	@Override
	public Tipo chequearSentencias() throws ErrorSemantico {
		Clase clase = TablaSimbolos.TS.getClases().get(idClase);
		if(clase==null)
			throw new ErrorSemantico("La clase "+idClase+" no esta declarada",token);
		Ctor ctor=clase.getCtor();
		TablaSimbolos.escribirCod("		RMEM 1 				; Reserva el espacio para un nuevo"+idClase);
		TablaSimbolos.escribirCod("		PUSH "+(clase.getIndexAtributos()+1)+"				; Tamaño del CIR  de "+idClase);
		TablaSimbolos.escribirCod("		PUSH simple_malloc	; Direccion de lmalloc");
		TablaSimbolos.escribirCod("		CALL				; Reservo el lugar para el CIR en heap");
		TablaSimbolos.escribirCod("		DUP					; Me cargo una copia del objeto nuevo");
		TablaSimbolos.escribirCod("		PUSH VT_"+idClase+"			; Apilo la direccion de la VT");
		TablaSimbolos.escribirCod("		STOREREF 0			; Cargo la VT");
		TablaSimbolos.escribirCod("		DUP					; Me cargo una copia del objeto nuevo");
		TablaSimbolos.escribirCod("		PUSH "+clase.getEtiqueta()+"				; Cargo del Id de la clase");
		TablaSimbolos.escribirCod("		STOREREF 1			; Cargo la VT");
		for(int i=2;i<clase.getIndexAtributos();i++){
			//para inicializar en 0
			TablaSimbolos.escribirCod("		DUP					; Me cargo una copia del objeto nuevo");
			TablaSimbolos.escribirCod("		PUSH 0				; inicializo en 0 el atributo "+(i-1));
			TablaSimbolos.escribirCod("		STOREREF "+i+"			; Cargo la VT");
		}
		TablaSimbolos.escribirCod("		DUP					; Me cargo una copia del objeto nuevo");
		
		
		ctor.matchParametros(paramActuales,token);
		
		TablaSimbolos.escribirCod("		PUSH "+ctor.getEtiqueta()+"			; Cargo direccion del Ctor");
		TablaSimbolos.escribirCod("		CALL				; Llamo al constructor");
		
		
		
		Tipo t=new TipoClase(idClase);
		if(encadenado==null)
			return t;
		else
			return encadenado.chequear(esLI,t);
	}

}
