package Tipos;

public abstract class Tipo {
	public abstract boolean esValido();
	public abstract String getNombre();
	public abstract boolean conforme(Tipo t);
}
