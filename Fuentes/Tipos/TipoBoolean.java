package Tipos;

public class TipoBoolean extends TipoPrimitivo {

	private static TipoBoolean inst=null;
	@Override
	public String getNombre() {
		return "Boolean";
	}

	@Override
	public boolean conforme(Tipo t) {
		return t instanceof TipoBoolean;
	}
	public static TipoBoolean getInstancia(){
		if(inst==null)
			inst=new TipoBoolean();
		return inst;
	}
}
