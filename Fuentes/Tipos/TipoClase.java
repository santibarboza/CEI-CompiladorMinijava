package Tipos;

import TabladeSimbolos.Clase;
import TabladeSimbolos.TablaSimbolos;

public class TipoClase extends Tipo {

	protected String idClase;
	public TipoClase(String idClas){
		idClase=idClas;
	}
	@Override
	public boolean conforme(Tipo t) {
		if(!(t instanceof TipoClase))
			return false;
		else{
			String padre=t.getNombre();
			if(idClase.equals(padre) || t.equals("Object"))
				return true;
			Clase c=TablaSimbolos.TS.getClases().get(idClase);
			while(!c.getIdClase().equals("Object")){
				if(c.getIdClase().equals(padre))
					return true;
				c=TablaSimbolos.TS.getClases().get(c.getHerencia());
			}
			return false;	
		}
	}
	public String getNombre(){
		return idClase;
	}

	@Override
	public boolean esValido() {
		return TablaSimbolos.TS.getClases().get(idClase)!=null;
	}

}