package TabladeSimbolos;

import java.util.HashSet;
import java.util.Hashtable;

import Etapa1.Token;
import Excepciones.ErrorSemantico;

public class Clase{
	protected Token token;
	protected String idClase;
	protected String idPadre;
	protected boolean actualizada;
	protected Ctor ctor;
	protected Hashtable<String,Atributo> atributos;
	protected Hashtable<String,Metodo> metodos;
	protected int indexAtributos;
	protected int indexMetodos;
	
	protected String etiqueta;
	
	public Clase(Token tok,String idClasePadre){
		actualizada=false;
		token=tok;
		idClase=token.get_Lexema();
		idPadre=idClasePadre;
		ctor=null;
		atributos= new Hashtable<String,Atributo> ();
		metodos= new Hashtable<String,Metodo>();
		indexAtributos=2;	//el 0 es VT y 1 es ID
		indexMetodos=0;		//Desplazamiento en VT
		crearIds();
	}
	
	public void setCtor(Ctor constructor) throws ErrorSemantico{
		if(ctor!=null)
			throw new ErrorSemantico("Se declararon 2 Constructores",token); //Aregleglar forma
		ctor=constructor;
	}
	public Token getToken(){
		return token;
	}
	public String getHerencia(){
		return idPadre;
	}
	public String getIdClase(){
			return idClase;
	}
	public Ctor getCtor(){
		return ctor;
	}
	public boolean tienePadre(){
		return idPadre!=null;
	}
	public Hashtable<String,Atributo> getAtributos(){
		return atributos;
	}
	public Hashtable<String,Metodo> getMetodos(){
		return metodos;
	}
	public boolean estaActualizada(){
		return actualizada;
	}
	private void actualizarTablasHerencias() throws ErrorSemantico{
		if(!idClase.equals("Object") && !actualizada){
			Clase clasePadre=TablaSimbolos.TS.getClases().get(idPadre);
			clasePadre.actualizarTablasHerencias();
			for(Metodo heredado: clasePadre.getMetodos().values()){
				Metodo redefinido= metodos.get(heredado.getId()); 
				if(redefinido!=null)
					heredado.puedeSobreEscribirse(redefinido);
				else
					metodos.put(heredado.getId(), heredado);
			}
			indexAtributos=clasePadre.getIndexAtributos();
			for(Atributo a: atributos.values()){
				a.setIndex(indexAtributos);
				indexAtributos++;
			}
				
			for(Atributo heredado :clasePadre.getAtributos().values()){
				String idHeredado=heredado.getNombre();
				if(!heredado.esPublico() || atributos.get(idHeredado)!=null)
					idHeredado="@"+idHeredado;
				atributos.put(idHeredado, heredado);
			}
			indexMetodos=clasePadre.getIndexMetodos();
			for(Metodo met : metodos.values()){
				if(!met.tieneIndex() && !met.esStatic()){
					met.setIndex(indexMetodos);
					indexMetodos++;
				}
			}

		}
		else
			for(Metodo met : metodos.values()){
				if(!met.tieneIndex() && !met.esStatic()){
					met.setIndex(indexMetodos);
					indexMetodos++;
				}
			}
		actualizada=true;
	}
	private void controlarHerenciaCircular() throws ErrorSemantico{
		String padre=idPadre;
		Clase clasePadre;
		HashSet<String> clasesVisitadas=new HashSet<String>();
		while(!idClase.equals("Object") && !padre.equals("Object")){
			clasePadre=TablaSimbolos.TS.getClases().get(padre);
			if(clasePadre==null)
				throw new ErrorSemantico("La clase padre: "+idPadre+" no esta definida",token);
			if(clasesVisitadas.contains(padre))
				throw new ErrorSemantico("La clase "+idClase+" tiene Herencia Circular ",token);
			clasesVisitadas.add(padre);
			padre=clasePadre.getHerencia();
		}
	}
	public void chequearDeclaraciones() throws ErrorSemantico {
		TablaSimbolos.claseActual=this;
		controlarHerenciaCircular();
		actualizarTablasHerencias();
		Atributo atribs[]=new Atributo[indexAtributos]; 
		Metodo mets[]=new Metodo [metodos.size()]; 
		ctor.chequearDeclaraciones();
		for(Metodo met: metodos.values()){	
			met.chequearDeclaraciones();
			if(! met.esStatic())
				mets[met.getIndex()]= met;
		}
		for(Atributo at: atributos.values()){
			at.chequearDeclaraciones();
			atribs[at.getIndex()]= at;

		}
		int i;
		String txt="";
		for(i=0;i<indexMetodos;i++)
			txt+="DW "+mets[i].getEtiqueta()+"	;reservo espacio en la VT para el metodo "+mets[i].getId()+"\n			";
		
		if(txt!="")
			txt="VT_"+idClase+":		"+txt;
		else
			txt="VT_"+idClase+":		NOP;	VT sin Metodos";
		TablaSimbolos.escribirCod(txt);

	}
	private void crearIds() {
		etiqueta="idClase_"+idClase;
		TablaSimbolos.escribirCod(etiqueta+":		DW \""+idClase+"\",0		; guardo el id de la clase");
		
	}

	public void chequearSentencias() throws ErrorSemantico {
		String Excepciones="";
		TablaSimbolos.escribirCod("		.CODE");
		try{
			ctor.chequearSentencias();
		}catch(ErrorSemantico ex){Excepciones="\nExcepciones del Constructor de la Clase"+idClase+":\n"+ex.getMessage();}
		for(Metodo met: metodos.values()){
			try{
				String ID=met.getIdClase();
				if(ID!=null &&idClase.equals(ID)){
					TablaSimbolos.metodoActual=met;
					met.chequearSentencias();
				}
			}catch(ErrorSemantico e){Excepciones=Excepciones+" \nExcepciones del metodo "+met.getId()+":\n"+e.getMessage();}
		}
		if(Excepciones!="")
			throw new ErrorSemantico(Excepciones);
	}
	
	//para generar codigo
	public int getIndexAtributos(){
		return indexAtributos;
	}
	public int getIndexMetodos(){
		return indexMetodos;
	}
	public String getEtiqueta(){
		return etiqueta;
	}
}
