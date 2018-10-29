

package Tipos;

public class TipoString extends TipoPrimitivo{

	private static TipoString inst=null;
	@Override
	public boolean conforme(Tipo t) {
		return t instanceof TipoString;
	}
	@Override
	public String getNombre() {
		return "String";
	}
	public static TipoString getInstancia(){
		if(inst==null)
			inst=new TipoString();
		return inst;
	}
}
