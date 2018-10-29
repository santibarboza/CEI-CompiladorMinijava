package Tipos;

public class TipoInt extends TipoPrimitivo {

	private static TipoInt inst=null;
	@Override
	public boolean conforme(Tipo t) {
		return t instanceof TipoInt;
	}
	@Override
	public String getNombre() {
		return "Int";
	}
	public static TipoInt getInstancia(){
		if(inst==null)
			inst=new TipoInt();
		return inst;
	}
}
