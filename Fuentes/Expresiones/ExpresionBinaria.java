package Expresiones;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import TabladeSimbolos.TablaSimbolos;
import Tipos.Tipo;
import Tipos.TipoBoolean;
import Tipos.TipoInt;

public class ExpresionBinaria extends Expresion{

	protected Expresion expLI,expLD;
	
	public ExpresionBinaria(Token tok,Expresion ladoIzquierdo,Expresion ladoDerecho) {
		super(tok);
		expLI=ladoIzquierdo;
		expLD=ladoDerecho;
	}
	public Expresion getLadoIzquierdo(){
		return expLI;
	}
	public Expresion getLadoDerecho(){
		return expLD;
	}
	@Override
	public Tipo chequearSentencias() throws ErrorSemantico {
		Tipo t=null;
		switch(token.get_Lexema()){
			case "+":
				if(!(expLI.chequearSentencias() instanceof TipoInt && expLD.chequearSentencias() instanceof TipoInt))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos int",token);
				t= new TipoInt();
				TablaSimbolos.escribirCod("		ADD ; Sumo los dos elementos en el tope de la pila");
				break;
			case "-":
				if(!(expLI.chequearSentencias() instanceof TipoInt && expLD.chequearSentencias() instanceof TipoInt))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos int",token);
				t= new TipoInt();
				TablaSimbolos.escribirCod("		SUB ; Resto los dos elementos en el tope de la pila");
				break;
			case "*":
				if(!(expLI.chequearSentencias() instanceof TipoInt && expLD.chequearSentencias() instanceof TipoInt))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos int",token);
				t= new TipoInt();
				TablaSimbolos.escribirCod("		MUL; Multiplico  los dos elementos en el tope de la pila");
				break;
			case "/":
				if(!(expLI.chequearSentencias() instanceof TipoInt && expLD.chequearSentencias() instanceof TipoInt))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos int",token);
				t= new TipoInt();
				controlarDIV0();
				TablaSimbolos.escribirCod("		DIV");
				break;
			case "%":
				if(!(expLI.chequearSentencias() instanceof TipoInt && expLD.chequearSentencias() instanceof TipoInt))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos int",token);
				t= new TipoInt();
				TablaSimbolos.escribirCod("		MOD");
				break;
			case "&&":
				if(!(expLI.chequearSentencias() instanceof TipoBoolean && expLD.chequearSentencias() instanceof TipoBoolean))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos booleanos",token);
				t= new TipoBoolean();
				TablaSimbolos.escribirCod("		AND ; Realizo el And entre los dos elementos en el tope de la pila");
				break;
			case "||":
				if(!(expLI.chequearSentencias() instanceof TipoBoolean && expLD.chequearSentencias() instanceof TipoBoolean))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos booleanos",token);
				t= new TipoBoolean();
				TablaSimbolos.escribirCod("		OR ; Realizo el Or entre los dos elementos en el tope de la pila");
				break;
			case "==":
				Tipo TI=expLI.chequearSentencias(), TD=expLD.chequearSentencias();
				if(!(TD.conforme(TI) ||  TI.conforme(TD)))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos de tipos conformantes",token);
				t= new TipoBoolean();
				TablaSimbolos.escribirCod("		EQ ; Apilo 1 si A==B 0 en caso contrario");
				break;
			case "!=":
				TI=expLI.chequearSentencias(); TD=expLD.chequearSentencias();
				if(!(TD.conforme(TI) ||  TI.conforme(TD)))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos de tipos conformantes",token);
				t= new TipoBoolean();
				TablaSimbolos.escribirCod("		NE ; Apilo 1 si A!=B 0 en caso contrario");
				break;
			case "<":
				if(!(expLI.chequearSentencias() instanceof TipoInt && expLD.chequearSentencias() instanceof TipoInt))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos enteros",token);
				t= new TipoBoolean();
				TablaSimbolos.escribirCod("		LT ; Apilo 1 si A<B 0 en caso contrario");
				break;
			case "<=":
				if(!(expLI.chequearSentencias() instanceof TipoInt && expLD.chequearSentencias() instanceof TipoInt))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos enteros",token);
				t= new TipoBoolean();
				TablaSimbolos.escribirCod("		LE ; Apilo 1 si A<=B 0 en caso contrario");
				break;
			case ">":
				if(!(expLI.chequearSentencias() instanceof TipoInt && expLD.chequearSentencias() instanceof TipoInt))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos enteros",token);
				t= new TipoBoolean();
				TablaSimbolos.escribirCod("		GT ; Apilo 1 si A>B 0 en caso contrario");
				break;
			case ">=":
				if(!(expLI.chequearSentencias() instanceof TipoInt && expLD.chequearSentencias() instanceof TipoInt))
					throw new ErrorSemantico("El operador "+token.get_Lexema()+" se aplica solo con 2 operandos enteros",token);
				t= new TipoBoolean();
				TablaSimbolos.escribirCod("		GE ; Apilo 1 si A>=B 0 en caso contrario");
				break;
		}
		return t;
	}
	private void controlarDIV0() {
		TablaSimbolos.escribirCod("			;Controlo que el atributo no acceda a null");
		TablaSimbolos.escribirCod("			PUSH error_Div0		; seteo el error");
		TablaSimbolos.escribirCod("			PUSH "+token.get_NroLinea()+"		; cargo numero de fila");
		TablaSimbolos.escribirCod("			PUSH "+token.get_NroCol()+"		; cargo nuemro columna");
		TablaSimbolos.escribirCod("			PUSH chequear_Null_Div0		; cargo la direccion de chequeo");
		TablaSimbolos.escribirCod("			CALL ");
	}
	

}
