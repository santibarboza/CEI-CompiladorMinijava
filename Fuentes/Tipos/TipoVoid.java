package Tipos;


public class TipoVoid extends Tipo {

	private static TipoVoid inst=null;
	@Override
	public boolean conforme(Tipo t) {
		return false;
	}

	@Override
	public boolean esValido() {
		return true;
	}
	@Override
	public String getNombre() {
		return "Void";
	}
	public static TipoVoid getInstancia(){
		if(inst==null)
			inst=new TipoVoid();
		return inst;
	}
}
