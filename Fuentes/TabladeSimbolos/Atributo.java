package TabladeSimbolos;

import Etapa1.Token;
import Tipos.Tipo;

public class Atributo extends Variable{

	protected boolean isPublic;
	public Atributo(Tipo t,Token Nombre,boolean isPublico) {
		super(t,Nombre);
		isPublic=isPublico;
	}
	public boolean esPublico(){
		return isPublic;
	}

}
