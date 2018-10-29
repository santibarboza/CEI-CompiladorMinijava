package Etapa1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import Excepciones.ErrorLexico;

public class ALex {
	protected FileReader fr;
	protected BufferedReader br;
	protected String linea;
	protected int indice;
	protected int nro_linea;
	protected Hashtable<String,String>palabrasClaves;
	protected Hashtable<Character,String> casosTriviales;
	
	public ALex(String file) throws IOException{
		nro_linea=0;
		File archivo = new File (file);
		fr= new FileReader (archivo);
		br = new BufferedReader(fr);
		recargarLinea();
		cargarClaves();
		cargarTriviales();
	}
	
	/**
	 * Recarga la linea con la siguiente linea del archivo.
	 * @throws IOException 
	 */
	private void recargarLinea() throws IOException{
		indice=0;
		do{
			nro_linea++;
			linea=br.readLine();
		}while(linea!=null && linea.length()==0);//saltea multiples enters	
	}
	
	public Token getToken()throws ErrorLexico,IOException{
		Token ret=null;
		if(linea!=null && indice== linea.length())
			recargarLinea();
		if(linea==null)
			return findeArchivo();

		String T_ID=null;
		char c=linea.charAt(indice);
		if(c==' ' || c==(char)9)
			return saltarBlancos();
		else if(c>='a' && c<='z')
			return idmet();
		else if(c>='A' && c<='Z')
			return idclase();
		else if(c>='0' && c<='9')
			return litInt();
		else if((T_ID=casosTriviales.get(c))!=null){
				indice++;
				return new Token(T_ID,c+"",nro_linea,indice);
		}else{
			
			int nrocol=indice+1;//para adoptar representacion 1..n
		    switch(c){		
			
			case '|':
				indice++;
				if(indice<linea.length() && linea.charAt(indice)==c){
					ret=new Token("T_Or","||",nro_linea,nrocol);
					indice++;
				}else
						throw new ErrorLexico("ErrorLexico en linea "+nro_linea+":"+nrocol+"= El operador | no pertece al lenguaje, el operador puede ser ||");
				break;
			case '&':
				indice++;
				if(indice<linea.length() && linea.charAt(indice)==c){
					ret=new Token("T_And","&&",nro_linea,nrocol);
					indice++;
				}else
						throw new ErrorLexico("ErrorLexico en linea "+nro_linea+":"+nrocol+"= El operador & no pertece al lenguaje, el operador puede ser &&");
				break;

			case '/':
				if((indice+1)<linea.length() && linea.charAt(indice+1)=='/')			//revisar indice
					ret= comentarioSimple();
				else if((indice+1)<linea.length() && linea.charAt(indice+1)=='*')
					ret= comentarioMultilinea();
				else{
						ret=new Token("T_Div","/",nro_linea,nrocol);
						indice++;
					}
				break;
			case '<':
			case '>':
				indice++;
				if((indice)<linea.length() && linea.charAt(indice)=='='){
					ret=new Token("T_OpComp",c+"=",nro_linea,nrocol);
					indice++;
				}
				else
					ret=new Token("T_OpComp",c+"",nro_linea,nrocol);
				break;
			case '!':
				indice++;
				if((indice)<linea.length() && linea.charAt(indice)=='='){
					ret=new Token("T_OpLg","!=",nro_linea,nrocol);
					indice++;
				}
				else
					ret=new Token("T_OpNeg","!",nro_linea,nrocol);
				break;
			case '=':
				indice++;
				if((indice)<linea.length() && linea.charAt(indice)=='='){
					ret=new Token("T_OpLg","==",nro_linea,nrocol);
					indice++;
				}
				else
					ret=new Token("T_OpAsig","=",nro_linea,nrocol);
				break;
			case '\"':
					ret=litString();
					break;
			case '\'':
				ret=litChar();
					break;
			default: throw new ErrorLexico("ErrorLexico en la Linea "+nro_linea+":"+nrocol+"= El token "+c+" no esta valido en el lenguaje");
		}
		    
		}
		return  ret;
	}
	
	/**
	 * Saltea el comentario Simple
	 * @return el siguiente Token
	 * @throws ErrorLexico
	 */
	private Token comentarioSimple() throws ErrorLexico,IOException{
		recargarLinea();
		return getToken();
	}
	/**
	 * Saltea el comentario MultiLinea
	 * @return el siguiente Token
	 * @throws ErrorLexico
	 */
	private Token comentarioMultilinea() throws ErrorLexico,IOException {
		int linea_inicio=nro_linea,nrocol=indice+1;
		indice+=2; //salteo /*
		while(linea.indexOf("*/",indice)==-1)
		{
			recargarLinea();
			if(linea==null)
				throw new ErrorLexico("ErrorLexico en la Linea"+linea_inicio+":"+nrocol+"= comentario multilinea empieza pero nunca termina");
		}
		indice=linea.indexOf("*/",indice)+2; 
		return getToken();
	}
	/**
	 * Saltea los espacios en blancos y las tabulaciones
	 * @return el token que sigue a los espacios en blanco
	 */
	private Token saltarBlancos() throws ErrorLexico,IOException {
		while(indice<linea.length() && (linea.charAt(indice)==' ' || linea.charAt(indice)==(char)9)){
			indice++;
		}
		return getToken();
	}
	/**
	 * Arma un Identificador de IDMetVar o IDClase
	 * @return
	 */
	private String armarIdentificador() {
		String lex="";
		char c=linea.charAt(indice);
		while(indice<linea.length() && (((c=linea.charAt(indice))>='a'&& c<='z')||(c>='A'&& c<='Z')||(c>='0' && c<='9')|| c=='_')){
			//c=linea.charAt(indice);
			lex+=c;
			indice++;
		}
		return lex;
	}
	/**
	 * Crea un token IdClase
	 * @return el token creado luego de recorrer el archivo
	*/
	private Token idclase() {
		int nrocol=indice+1;
		String lex =armarIdentificador();
		Token ret=null;
		String id=palabrasClaves.get(lex);
		if(id==null)
			ret=new Token("ID_Clase",lex,nro_linea,nrocol);
		else
			ret=new Token(id,lex,nro_linea,nrocol);
		return ret;	}
	
	/**
	 * Crea un token IdMetVar
	 * 
	 * @return el token creado luego de recorrer el archivo
	 * @throws ErrorLexico
	 */
	private Token idmet() throws ErrorLexico{
		int nrocol=indice+1;
		String lex =armarIdentificador();
		Token ret=null;
		String id=palabrasClaves.get(lex);
		if(id==null)
			ret=new Token("ID_MetVar",lex,nro_linea,nrocol);
		else
			ret=new Token(id,lex,nro_linea,nrocol);
		return ret;
	}
	/**
	 * Genera un token de Literal Entero
	 * @return El token del entero
	 * @throws ErrorLexico lanza uan excepcion si no cumple con el formato de un literal
	 */
	private Token litInt() throws ErrorLexico {
		String num="";
		int nrocol=indice+1;
		char c=linea.charAt(indice);
		while(indice<linea.length() && (c=linea.charAt(indice))>='0' && c<='9'){
			num+=c;
			indice++;
		}
		if((c>='a'&& c<='z' )|| (c>='A'&& c<='Z'))
			throw new ErrorLexico("ErrorLexico en la Linea "+nro_linea+":"+nrocol+"= Literal entero erroneo");
		return new Token("LitInt",num,nro_linea,nrocol);
	}
	/**
	 * Genera un token de Literal Char
	 * @return El token del Char
	*/
	private Token litChar() throws ErrorLexico{
		Token ret=null;
		int nrocol=indice+1;
		indice++;//salteo el '
		
		if(indice+1<linea.length()){
			char c1=linea.charAt(indice);
			char c2=linea.charAt(indice+1);
			if(c1=='\\'){
				if(indice+2<linea.length() && (linea.charAt(indice+2))=='\'')
					if(c2=='t')
						ret= new Token("LitChar","\t",nro_linea,nrocol);
					else if(c2=='n')
						ret= new Token("LitChar","\n",nro_linea,nrocol);
					else 
						ret=new Token("LitChar",""+c2,nro_linea,nrocol);
				else
					throw new ErrorLexico("ErrorLexico en la Linea "+nro_linea+":"+nrocol+"= Literal Char que comienza con \\ es invalido, las unicas opciones validas son de la forma \'\\x\'");
				indice++; // salteo el '\\'
			}else if(c2=='\'')
					ret= new Token("LitChar",""+c1,nro_linea,nrocol);
				else
					throw new ErrorLexico("ErrorLexico en la Linea "+nro_linea+":"+nrocol+"= Tiene una comilla simple seguido de un caracter pero luego no se cierra inmediatamente");
			indice+=2;//Salteo el caracter y la comilla de cierre
		}else
			throw new ErrorLexico("ErrorLexico en la Linea "+nro_linea+":"+nrocol+"= El caracter \' solo no es valido");
		
		return ret;
	}
	/**
	 * Genera un token de Literal String
	 * @return El token del String
	*/
	private Token litString() throws ErrorLexico{
		int nrocol=indice+1;
		String lex;
		indice++;	//salteo el " que abre
		int ini=indice;
		if((indice=linea.indexOf('\"',indice))==-1)
		throw new ErrorLexico("ErrorLexico en la Linea "+nro_linea+":"+nrocol+"= Literal String empieza pero nunca termina");
		lex=linea.substring(ini, indice);
		indice++; //salteo el " qe cierra
		return new Token("LitString",lex,nro_linea,nrocol);
	}
	/**
	 * Carga las palabras clave
	 */
	private void cargarClaves(){
		palabrasClaves= new Hashtable<String,String>();
		try{
		palabrasClaves.put("class","T_Class");
		palabrasClaves.put("extends","T_Extends");
		palabrasClaves.put("static","T_Static");
		palabrasClaves.put("dynamic","T_Dynamic");
		palabrasClaves.put("public","T_Public");
		palabrasClaves.put("private","T_Private");
		palabrasClaves.put("void","T_Void");
		palabrasClaves.put("boolean","T_Boolean");
		palabrasClaves.put("char","T_Char");
		palabrasClaves.put("int","T_Int");
		palabrasClaves.put("String","T_String");
		palabrasClaves.put("if","T_If");
		palabrasClaves.put("else","T_Else");
		palabrasClaves.put("while","T_While");
		palabrasClaves.put("return","T_Return");
		palabrasClaves.put("instanceof","T_InsOf");
		palabrasClaves.put("this","T_This");
		palabrasClaves.put("new","T_New");
		palabrasClaves.put("null","T_NULL");
		palabrasClaves.put("true","Lit_Boolean");
		palabrasClaves.put("false","Lit_Boolean");
		}catch(NullPointerException e){
		}
	}
	/**
	 * Carga las casos triviales
	 */
	private void cargarTriviales(){
		casosTriviales= new Hashtable<Character,String>();
		try{
			casosTriviales.put('(',"T_ParenIni");
			casosTriviales.put(')',"T_ParenFin");
			casosTriviales.put('{',"T_LlaveIni");
			casosTriviales.put('}',"T_LlaveFin");
			casosTriviales.put('[',"T_CastIni");
			casosTriviales.put(']',"T_CastFin");
			casosTriviales.put(',',"T_Coma");
			casosTriviales.put('.',"T_Punto");
			casosTriviales.put(';',"T_PyC");
			casosTriviales.put('+',"T_Suma");
			casosTriviales.put('-',"T_Resta");
			casosTriviales.put('*',"T_Prod");
			casosTriviales.put('%',"T_Mod");
		}catch(NullPointerException e){
		}
	}
	/**
	 * Cierra el archivo y devuelve el token EOF
	 * @return un token de tipo EOF
	 */
	private Token findeArchivo(){
		try {
			br.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new Token("EOF","EOF",nro_linea,indice);
	}
	
}
