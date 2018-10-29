package Etapa1;

public class Token {
	protected String ID_TOKEN;
	protected String Lexema;
	protected int Nro_Linea;
	protected int Nro_Col;
	
	public Token(String id,String lex, int nro,int col){
		ID_TOKEN=id;
		Lexema=lex;
		Nro_Linea=nro;
		Nro_Col=col;
	}
	public String get_IDTOKEN(){
		return ID_TOKEN;
	}
	public String get_Lexema(){
		return Lexema;
	}
	public int get_NroLinea(){
		return Nro_Linea;
	}
	public int get_NroCol(){
		return Nro_Col;
	}

}
