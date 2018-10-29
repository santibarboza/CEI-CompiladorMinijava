package TabladeSimbolos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import Excepciones.ErrorSemantico;
import Sentencias.Bloque;

public class TablaSimbolos {	
	private static FileWriter fichero;
    private static PrintWriter pw;
    
	public static Clase claseActual;
	public static Unidad metodoActual;
	public static Bloque bloqueActual;
	public static TablaSimbolos TS;
	public static Metodo main;
	
	protected Hashtable<String,Clase> Clases;

	public TablaSimbolos(String fileO) throws IOException{
		Clases=new Hashtable<String,Clase>();
		TS=this;
		main=null;
		claseActual=null;
		metodoActual=null;
		bloqueActual=null;
		crearArchivo(fileO);
		escribirCod("		.DATA");
	}
	public Hashtable<String,Clase> getClases(){
		return Clases;
	}
	public void chequearDeclaraciones() throws ErrorSemantico{
		for(Clase c: Clases.values()){
			claseActual=c;
			c.chequearDeclaraciones();
		}
			
	}
	public void chequearSentencias() throws ErrorSemantico{
		escribirCod("\n		.CODE\n		PUSH "+main.etiq+" ; Cargo el main\n		CALL		; Llamo al main\n		HALT\n");
		cargarExcepciones();
		String Excepciones="";
		for(Clase c: Clases.values()){
			try{
				claseActual=c;
				c.chequearSentencias();
			}catch(ErrorSemantico e){Excepciones=Excepciones+"\n\nExcepciones de la Clase "+c.getIdClase()+":"+e.getMessage();}
		}
		if(Excepciones!="")
			throw new ErrorSemantico(Excepciones);
		cargarHeap();
	}
	
	public static void escribirCod(String txt){
		pw.println(txt);
	}
	public static void escribirInicioMetodo(Unidad m){
		String IdMet=m.getEtiqueta();
		escribirCod("\n"+IdMet+":	LOADFP		; almaceno el ED");
		escribirCod("		LOADSP		; almaceno el PR");
		escribirCod("		STOREFP		; seteo el FP actual");
		escribirCod("	;Inicio de Codigo de "+IdMet);
	}
	public static void escribirFinMetodo(String IdMet,int ret){
		escribirCod("	;Fin  de Codigo de "+IdMet);
		escribirCod("		STOREFP		; restauro FP"); 
		escribirCod("		RET "+ret+"		; retorno liberando "+ret+" variables");	
	}
	
	private static void crearArchivo(String fileO) throws IOException{
	    fichero = new FileWriter(fileO);
	    pw = new PrintWriter(fichero);
	}
	public  void cerrarArchivo() throws IOException{
		 if (null != fichero)
             fichero.close();
	}
	private void cargarHeap(){
		escribirCod("\n\nsimple_heap_init:	RET 0	; Retorna inmediatamente");
		escribirCod("simple_malloc:	LOADFP	; Inicialización unidad	");
		escribirCod("		LOADSP	");
		escribirCod("		STOREFP ; Finaliza inicialización del RA	");
		escribirCod("		LOADHL	; hl");
		escribirCod("		DUP	; hl");
		escribirCod("		PUSH 1	; 1");
		escribirCod("		ADD	; hl+1");
		escribirCod("		STORE 4 ; Guarda el resultado (un puntero a la primer celda de la región de memoria)");
		escribirCod("	 	LOAD 3	; Carga la cantidad de celdas a alojar (parámetro que debe ser positivo)");
		escribirCod("		ADD");
		escribirCod("		STOREHL ; Mueve el heap limit (hl). Expande el heap");
		escribirCod("		STOREFP");
		escribirCod("		RET 1	; Retorna eliminando el parámetro");
	}
	
	private void cargarExcepciones(){
		escribirCod("		.DATA");
		escribirCod("		;Mensajes de Error");
		escribirCod("error_Div0:		DW \"Error Division por 0 en (\",0 ");
		escribirCod("error_Null:		DW \"Error NullPointerException en (\",0 ");
		escribirCod("error_Cast1:		DW \"Error Casteo Erroneo en \",0 ");
		escribirCod("error_Cast2:		DW \"La expresion no es de tipo \",0 ");
		escribirCod("\n		.CODE");
		escribirCod("		;Codigo de las rutinas que manejan las Excepciones\n");
		escribirCod("		;NullPointerExc_DivPor0(div/null,error,nf,nc)");
		escribirCod("chequear_Null_Div0:	NOP	");
		escribirCod("		LOADFP		; almaceno el ED");
		escribirCod("		LOADSP		; almaceno el PR");
		escribirCod("		STOREFP		; seteo el FP actual");
		escribirCod("		LOAD 6		; leo el valor a comparar");
		escribirCod("		PUSH 0		; apilo el 0/null");
		escribirCod("		EQ			; comparo el valor con null o 0");
		escribirCod("		BT	mostrar_Null_Div0 ");
		escribirCod("		STOREFP		; restauro FP"); 
		escribirCod("		RET 3		; vuelvo liberando nf,nc y error");
		escribirCod("mostrar_Null_Div0: NOP ");
		escribirCod("		PUSH 41		; cargo el )");
		escribirCod("		LOAD 3 		; leo el numero de columna");
		escribirCod("		PUSH 58		; cargo el :");
		escribirCod("		LOAD 4 		; leo el numero de fila");
		escribirCod("		LOAD 5		; leo el error");
		escribirCod("		SPRINT		; imprimo el error");
		escribirCod("		IPRINT		; imprimo el numero de fila");
		escribirCod("		CPRINT		; imprimo el :");
		escribirCod("		IPRINT		; imprimo el numero de columna");
		escribirCod("		CPRINT		; imprimo el )");
		escribirCod("		PRNLN		; imprimo el \\n");
		escribirCod("		HALT\n");
		escribirCod("		;CastException(expresion,idClase,(nf:nc))");
		escribirCod("chequear_Cast:	NOP	");
		escribirCod("		LOADFP		; almaceno el ED");
		escribirCod("		LOADSP		; almaceno el PR");
		escribirCod("		STOREFP		; seteo el FP actual");
		escribirCod("		LOAD 4		; cargo idClase");
		escribirCod("		LOAD 5		; cargo Expresion");
		escribirCod("		LOADREF 1 	; cargo id del CIR de la expresion ");
		escribirCod("		EQ			; comparo las ids");
		escribirCod("		BF	castErroneo ");
		escribirCod("		STOREFP		; restauro FP"); 
		escribirCod("		RET 2		; vuelvo liberando (n:m) e idClase ");
		escribirCod("castErroneo: NOP ");
		escribirCod("		LOAD 4 		; cargo id ");
		escribirCod("		PUSH error_Cast2	;cargo mensaje de error2");
		escribirCod("		LOAD 3		; cargo (n:m)");
		escribirCod("		PUSH error_Cast1	;cargo mensaje de error1");
		escribirCod("		SPRINT		; imprimo el error 1");
		escribirCod("		SPRINT		; imprimo el (n:m)");
		escribirCod("		PRNLN		; imprimo el \\n");
		escribirCod("		SPRINT		; imprimo el error 2");
		escribirCod("		SPRINT		; imprimo el id Clase");
		escribirCod("		PRNLN		; imprimo el \\n");
		escribirCod("		HALT\n\n");
	}
	
	
}
