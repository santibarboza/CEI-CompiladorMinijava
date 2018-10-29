package Tipos;

public class TipoChar extends TipoPrimitivo {

	private static TipoChar inst=null;
	@Override
	public boolean conforme(Tipo t) {
		return t instanceof TipoChar;
	}

	@Override
	public String getNombre() {
		return "Char";
	}
	public static TipoChar getInstancia(){
		if(inst==null)
			inst=new TipoChar();
		return inst;
	}

}
