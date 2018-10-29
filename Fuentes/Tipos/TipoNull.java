package Tipos;

public class TipoNull extends Tipo {

	private static TipoNull inst=null;
	@Override
	public boolean conforme(Tipo t) {
		return t instanceof TipoClase;
	}

	@Override
	public boolean esValido() {
		return true;
	}

	@Override
	public String getNombre() {
		return "NULL";
	}

	public static TipoNull getInstancia(){
		if(inst==null)
			inst=new TipoNull();
		return inst;
	}
}
