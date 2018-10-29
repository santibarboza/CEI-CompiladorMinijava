package TabladeSimbolos;

import java.util.HashSet;
import java.util.LinkedList;

import Etapa1.Token;
import Excepciones.ErrorSemantico;
import Tipos.Tipo;
import Tipos.TipoVoid;

public class Metodo extends Unidad{
	protected boolean isStatic;
	protected Tipo tipoRetorno;
	protected String idMet;
	protected String IdClase;
	
	protected boolean tieneIndex;
	
	public Metodo(Token tok, LinkedList<Variable> params,boolean isStat,Tipo tipoRet) throws ErrorSemantico {
		super(tok, params);
		isStatic=isStat;
		tipoRetorno=tipoRet;
		idMet=tok.get_Lexema();
		IdClase=TablaSimbolos.claseActual.getIdClase();
		tieneIndex=false;
		etiq="lm_"+IdClase+"_"+idMet;
		cargarParametros();
	}

	public boolean esStatic(){
		return isStatic;
	}
	public Tipo getTipoRetorno(){
		return tipoRetorno;
	}
	public String getId(){
		return idMet;
	}
	public void puedeSobreEscribirse(Metodo m) throws ErrorSemantico{
		LinkedList<Variable> params=m.getParamFormales();
		if(!idMet.equals(m.getId()))
			throw new ErrorSemantico("El metodo no se puede redefinir porque su identificador es distinto",m.getToken());
		if(isStatic!=m.esStatic())
			throw new ErrorSemantico("El metodo "+idMet+" no se puede redefinir, porque su forma es distinta",m.getToken());	
		if(!tipoRetorno.getNombre().equals(m.getTipoRetorno().getNombre()))
			throw new ErrorSemantico("El metodo "+idMet+" no se puede redefinir, porque su tipo es distinto",m.getToken());	
		if(parametros.size()!=params.size())
			throw new ErrorSemantico("El metodo "+idMet+" no se puede redefinir, porque la cantidad de parametros es distinta",m.getToken());	
		paramsIgual(params);
		m.setIndex(index);
	}
	private void paramsIgual(LinkedList<Variable> p) throws ErrorSemantico{
		int i;
		Variable padre,hijo;
		for(i=0;i<parametros.size();i++){
			padre=parametros.get(i);
			hijo=p.get(i);
			if(!padre.getTipo().getNombre().equals(hijo.getTipo().getNombre()))
				throw new ErrorSemantico("El metodo "+idMet+" no se puede redefinir, el parametro numero "+(i+1)+" no matchea. Se esperaba un Parametro de tipo "+padre.getTipo().getNombre()+" y encontro un parametro de tipo "+hijo.getTipo().getNombre(),hijo.getToken());
		}
	}
	@Override
	public void chequearDeclaraciones() throws ErrorSemantico {
		if(!tipoRetorno.esValido())
			throw new ErrorSemantico("El tipo de retorno del metodo "+idMet+" es Invalido",token);
	}
	@Override
	public void chequearSentencias() throws ErrorSemantico {
		cantVarLocales=0;
		TablaSimbolos.escribirInicioMetodo(this);
		boolean tieneRet=bloque.chequearSentencias();
		if(!( tieneRet|| tipoRetorno instanceof TipoVoid))
			throw new ErrorSemantico("El metodo "+idMet+" tiene camino/s sin retorno/s",token);
		if(!tieneRet){
			//Tipo void sin return;
			int cantVar=cantVarLocales*(-1);
			int ret=parametros.size();
			if(!isStatic)
				ret++;
			TablaSimbolos.escribirCod("		FMEM "+cantVar+" 		;Libero "+cantVar+" Variables Locales ");
			TablaSimbolos.escribirFinMetodo(IdClase+"_"+idMet,ret);
		}
	}
	public void cargarParametros() throws ErrorSemantico{
		if(isStatic){
			int i,indice=1,n=parametros.size();
			HashSet<String> vars= new HashSet<String>();
			for(i=1;i<=n;i++){
				Variable v= parametros.get(i-1);
				if(vars.contains(v.getNombre()))
					throw new ErrorSemantico("El identificador de variable "+v.getNombre()+" esta en mas de un Parametros",v.getToken());
				v.chequearDeclaraciones();
				indice=(n+3-i);				//ed,pr
				variables.put(v.getNombre(),v);
				vars.add(v.getNombre());
				v.setIndex(indice);
				indice++;
			}
		}else
			super.cargarParametros();
	}
	//para generar codigo
		public String getIdClase(){
		return IdClase;
	}
	public void setIndex(int i){
		index=i;
		tieneIndex=true;
	}	
	public boolean tieneIndex(){
		return tieneIndex;
	}
}
