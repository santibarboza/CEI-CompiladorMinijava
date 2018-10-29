package ExpresionesPrimarias;

import Etapa1.Token;
import Expresiones.Encadenado;
import Expresiones.Operando;

public abstract class ExpresionPrimario extends Operando{

	protected Encadenado encadenado;
	protected boolean esLI;
	
	public ExpresionPrimario(Token tok,Encadenado enc) {
		super(tok);
		encadenado=enc;
		esLI=false;
	}

	public Encadenado getEncadenado(){
		return encadenado;
	}
	public void esLI(){
		esLI=true;
	}
	public abstract boolean terminaEnVariable();
	public abstract boolean terminaEnLlamada();

	
}
