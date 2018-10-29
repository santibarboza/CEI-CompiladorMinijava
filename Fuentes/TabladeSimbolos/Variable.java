package TabladeSimbolos;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
//import Excepciones.ErrorSemantico;
import Tipos.Tipo;

public class Variable extends InfoIndexada{
	protected Token token;
	protected Tipo tipo;
	protected String id;
	
	public Variable(Tipo t,Token tok){
		tipo=t;
		token=tok;
		id=tok.get_Lexema();
	}
	public Variable(Tipo t,Token tok,int i){
		super(i);
		tipo=t;
		token=tok;
		id=tok.get_Lexema();
	}
	public Tipo getTipo(){
		return tipo;
	}
	public Token getToken(){
		return token;
	}
	public  String getNombre(){
		return id;
	}
	
	public void chequearDeclaraciones() throws ErrorSemantico {
		if(!tipo.esValido())
			throw new ErrorSemantico("La variable "+id+" tiene un tipo invalido",token);
	}
	
}
