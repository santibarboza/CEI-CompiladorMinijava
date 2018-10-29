package TabladeSimbolos;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Expresiones.Expresion;
import Sentencias.Bloque;
import Tipos.Tipo;

public abstract class Unidad extends InfoIndexada{
		protected Token token;
		protected Hashtable<String,Variable> variables;
		protected Bloque bloque;
		protected LinkedList<Variable> parametros; 
		protected int cantVarLocales;
		protected String etiq;
		
		public Unidad(Token tok, LinkedList<Variable> params) throws ErrorSemantico{
			super(-1);		//para que los metodos estaticos y Ctores queden con index -1
			variables=  new Hashtable<String,Variable>();
			parametros=params;
			bloque=null;
			token= tok;
			cantVarLocales=0;
			etiq="";
		}
		public void setBloque(Bloque bloques) throws ErrorSemantico{
			if(bloque!=null)
				throw new ErrorSemantico("Se declararon 2 Bloques",token); //Aregleglar forma
			bloque=bloques;
		}
		public Token getToken(){
			return token;
		}
		public Hashtable<String,Variable> getVariables(){
			return variables;
		}
		public Bloque getBloque(){
			return bloque;
		}
		public LinkedList<Variable> getParamFormales(){
			return parametros;
		}
		
		public void cargarParametros() throws ErrorSemantico{
			int i,indice=1,n=parametros.size();
			HashSet<String> vars= new HashSet<String>();
			for(i=1;i<=n;i++){
				Variable v= parametros.get(i-1);
				if(vars.contains(v.getNombre()))
					throw new ErrorSemantico("El identificador de variable "+v.getNombre()+" esta en mas de un Parametros",v.getToken());
				v.chequearDeclaraciones();
				indice=(n+4-i);				//ed,pr,this,
				variables.put(v.getNombre(),v);
				vars.add(v.getNombre());
				v.setIndex(indice);
				indice++;
			}
		}

		/**
		 * Devuelve verdadero si la cantidad y tipos de parametros conforman
		 * @param paramsActuales
		 * @return
		 * @throws ErrorSemantico
		 */
		public void matchParametros(LinkedList<Expresion> paramsActuales,Token t) throws ErrorSemantico{
			int i;
			Expresion actual=null;
			Variable formal=null;
			Tipo tipoActual=null;
			if(parametros.size()!=paramsActuales.size())
				throw new ErrorSemantico("La cantidad de Argumentos Actuales no es la correcta",t);
			for(i=0;i<parametros.size();i++){
				actual=paramsActuales.get(i);
				formal=parametros.get(i);
				tipoActual=actual.chequearSentencias();
				//Tengo el parametro en la pila
				if(!(this instanceof Metodo && ((Metodo)this).esStatic()))
					TablaSimbolos.escribirCod("		SWAP	; Bajo el this");
				if(!tipoActual.conforme(formal.getTipo()))
					throw new ErrorSemantico("El parametro Actual numero "+(i+1)+" no coincide con su Parametro Formal. Se esperaba una expresion de tipo "+formal.getTipo().getNombre()+" y se encontro una expresion de tipo "+tipoActual.getNombre(),actual.getToken());
			}
		}
		public abstract void chequearDeclaraciones() throws ErrorSemantico;
		public abstract void chequearSentencias() throws ErrorSemantico;

		
		//Para generacion de codigo
		
		public void setCantVarLocal(int i){
			cantVarLocales=i;
		}
		public int getCantVarLocal(){
			return cantVarLocales;
		}	
		public String getEtiqueta(){
			return etiq;
		}
		public void setEtiqueta(String txt){
			etiq=txt;
		}

}
