package Etapa2;

import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;

import TabladeSimbolos.*;
import Tipos.*;

import BloqueSystem.*;
import Etapa1.ALex;
import Etapa1.Token;
import Excepciones.ErrorLexico;
import Excepciones.ErrorSemantico;
import Excepciones.ErrorSintactico;
import ExpresionesPrimarias.ExpresionPrimario;
import ExpresionesPrimarias.ExpresionThis;
import ExpresionesPrimarias.LlamadaConstructor;
import ExpresionesPrimarias.LlamadaMetodoDirecto;
import ExpresionesPrimarias.LlamadaMetodoEstatico;
import ExpresionesPrimarias.PrimarioConExpresion;
import ExpresionesPrimarias.VariablePrimaria;
import Sentencias.*;
import Expresiones.*;

public class ASint {

	/**
	 * @param args
	 */
	protected TablaSimbolos TS;
	protected ALex alex;
	protected Token tokenActual;
	protected String idTok;
	protected Hashtable<String,String> simbolos;
	
	
	public ASint(ALex lex,String fileO) throws ErrorLexico, IOException{
		TS= new TablaSimbolos(fileO);
		alex=lex;
		tokenActual=lex.getToken();
		idTok=tokenActual.get_IDTOKEN();
		cargarSimbolos();
	}

	
	
	/**
	 * <Inicial> -> <Clase> <Clases> EOF
	 * P(<Inicial>)=P(<Clase>)= { class } 
	 * @throws ErrorSemantico 
 	 */
	public void inicial()throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico{
		cargarPredefinidas();
		Clase();
		Clases();
		//Controlar que alguna clase tiene main,sino error sintactico
		buscarMain();
		match("EOF");
		TS.chequearDeclaraciones();
		TS.chequearSentencias();
		//mostrarIndices();
		TS.cerrarArchivo();
	}
	/**
	 * <Clases> → λ |<Clase><Clases>
	 * P(<Clases>)= { class, λ }
	 * F(<Clases>)= { EOF } 
	 * @throws ErrorSemantico 
	 */
	private void Clases()throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico{
		if(tokenActual.get_IDTOKEN()== "T_Class"){
			Clase();
			Clases();
		}else if(tokenActual.get_IDTOKEN()!= "EOF")
					throw new ErrorSintactico(Error(" EOF o un class"));
	}
	/**
	 * <Clase> → class idClase <Herencia> {<Miembros>}
	 * P(<Clase>)= { class }
	 * @throws ErrorSemantico 
	 */
	private void Clase()throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico{
		String idPadre;
		Clase claseact;
			match("T_Class");
			Token tok=tokenActual;
			noExisteClase(tok);
			match("ID_Clase");
			idPadre=Herencias();
		claseact= new Clase(tok,idPadre);
		TablaSimbolos.claseActual=claseact;
		TS.getClases().put(tok.get_Lexema(), TablaSimbolos.claseActual);
			match("T_LlaveIni");
			Miembros();
			revisarConstructor();
			match("T_LlaveFin");
	}
	/**
	 * <Herencias> → λ | extends idClase
	 * P(<Herencias>)={ λ , extends }
	 * F(<Herencias>)={ { }
	 */
	private String Herencias() throws ErrorSintactico, ErrorLexico, IOException{
		String idPadre="Object";
		if(esIgual("T_Extends")){
			match("T_Extends");
			idPadre=tokenActual.get_Lexema();
			match("ID_Clase","Se Esperaba el Identificador de la clase Padre");
		}else if(!esIgual("T_LlaveIni"))
			throw new ErrorSintactico(Error(" { o un extends"));
		return idPadre;
	}
	/**
	 * <Miembros>→ λ | <Miembro><Miembros>
	 * P(<Miembros>)= { public, private } U { idClase } U { static, dynamic } U { λ }
	 * F(<Miembros>)= { } } 
	 * @throws ErrorSemantico 
	 */
	private void Miembros()throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico{
		if(esIgual("T_Public")||esIgual("T_Private")||esIgual("T_Static")||esIgual("T_Dynamic")||esIgual("ID_Clase")){
			Miembro();
			Miembros();
		}else if(!esIgual("T_LlaveFin"))
				throw new ErrorSintactico(Error("} o inicio de Miembro"));
	}
	
	/**
	 * <Miembro> → <Atributo> | <Ctor> | <Metodo>
	 * P(<Atributo>)= { public, private }
	 * P(<Ctor>)= { idClase }
	 * P(<Metodo>)= { static, dynamic}
	 * @throws ErrorSemantico 
	 */
	private void Miembro()throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico{
		String tokenId=tokenActual.get_IDTOKEN();
		switch(tokenId){
			case "T_Public":
			case "T_Private":
							Atributo();
							break;
			case "T_Static":
			case "T_Dynamic":
							Metodo();
							break;
			case "ID_Clase":
							Ctor();
							break;
			default: 
				throw new ErrorSintactico(Error("Inicio Miembro"));
				
				
		}
	}
	
	/**
	 * <Atributo> → <Visibilidad> <Tipo> <ListaDecVars> ;
	 * P(<Atributo>)= P(<Visibilidad>)= { public, private }
	 * @throws ErrorSemantico 
	 */
	private void Atributo() throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico{
			LinkedList<Token> atribs;
			boolean esPublic;
			Tipo tipo;
			esPublic= Visibilidad();
			tipo=Tipo();
			atribs=ListaDecVars();
			crearAtributos(tipo,esPublic,atribs);
			match("T_PyC");
	}
	private void crearAtributos(Tipo tipo,boolean esPublic,LinkedList<Token> atribs) throws ErrorSemantico{
		Atributo aux;
		for(Token idVar:atribs){
			aux=new Atributo(tipo,idVar,esPublic);
			noExisteAtributo(idVar);
			TablaSimbolos.claseActual.getAtributos().put(idVar.get_Lexema(),aux);
		}
	}
	
	/**
	 * <Metodo> → <FormaMetodo> <TipoMetodo> idMetVar <ArgsFormales> <Bloque>
	 * P(<Metodo>)= P(<FormaMetodo>)= { static, dynamic}
	 * @throws ErrorSemantico 
	 */
	private void Metodo() throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico{
		Metodo metodo;
		boolean isStatic;
		Tipo tipo;
		LinkedList<Variable> parametros;
		isStatic=FormaMetodo();
		tipo=TipoMetodo();
		Token token=tokenActual;
		noExisteMetodo(token);
		match("ID_MetVar");
		parametros=ArgsFormales();
		metodo=new Metodo(token,parametros,isStatic,tipo);
		TablaSimbolos.claseActual.getMetodos().put(token.get_Lexema(), metodo);
		TablaSimbolos.metodoActual=metodo;
		metodo.setBloque(Bloque());
	}
	
	/**
	 * <Ctor> → idClase <ArgsFormales> <Bloque>
	 * P(<Ctor>)= { idClase }
	 * @throws ErrorSemantico 
	 */
	private void Ctor() throws ErrorSintactico, ErrorLexico, IOException, ErrorSemantico{
		Token token=tokenActual;
		LinkedList<Variable> parametros;
		Ctor constructor;
		match("ID_Clase");
		parametros=ArgsFormales();
		constructor= new Ctor(token,parametros);
		TablaSimbolos.claseActual.setCtor(constructor);
		TablaSimbolos.metodoActual=constructor;
		constructor.setBloque(Bloque());
	}
	
	
	/**
	 * <Visibilidad> → public | private
	 */
	private boolean Visibilidad()throws ErrorSintactico, ErrorLexico, IOException{
		boolean ret=esIgual("T_Public");
		if(esIgual("T_Public"))
			match("T_Public");
		else
			match("T_Private");		
		return ret;
	}
	
	
	/**
	 * <ArgsFormales> → ( <ListaArgs_Formales> )
	 * P(<ArgsFormales>)= { ( }
	 */
	private LinkedList<Variable> ArgsFormales()throws ErrorSintactico, ErrorLexico, IOException{
		LinkedList<Variable> vars;
		match("T_ParenIni");
		vars=ListaArgs_Formales();
		match("T_ParenFin");
		return vars;
	}
	
	
	/**
	 * <ListaArgs_Formales> →λ | <ListaArgsFormales>
	 * P(<ListaArgsFormales>)= {λ,idClase, boolean, char, int, String}
	 * F(<ListaArgs_Formales>)= { ) }
	 */
	private LinkedList<Variable> ListaArgs_Formales()throws ErrorSintactico, ErrorLexico, IOException{
		if(esTipo()){
			return ListaArgsFormales();
		}else if(!esIgual("T_ParenFin"))
			throw new ErrorSintactico(Error("Tipo o )"));
		return new LinkedList<Variable>(); //lista de argumentos vacia
	}
	
	
	/**
	 * <ListaArgsFormales> → <ArgFormal> <LAF>
	 * P(<ArgFormal>)= {idClase, boolean, char, int, String}
	 */
	private LinkedList<Variable> ListaArgsFormales()throws ErrorSintactico, ErrorLexico, IOException{
		LinkedList<Variable>vars;
		Variable v;
		v=ArgFormal();
		vars=LAF();
		vars.addFirst(v);
		return vars;
	}
	
	/**
	 *<LAF>→ λ | , <ListaArgsFormales>
	 *P(<LAF>)= { λ , ,}
	 *F(<LAF>)= { ) }  
	 */
	private LinkedList<Variable> LAF()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_Coma")){
			match("T_Coma");
			return ListaArgsFormales();
		}else if(!esIgual("T_ParenFin"))
				throw new ErrorSintactico(Error(" ) o , "));
		return new LinkedList<Variable>();
	}
	
	/**
	 *<ArgFormal> → <Tipo> idMetVar
	 *P(<Tipo>)= {idClase, boolean, char, int, String} 
	 */
	private Variable ArgFormal()throws ErrorSintactico, ErrorLexico, IOException{
		Tipo tipo=Tipo();
		Token tok=tokenActual;
		match("ID_MetVar");
		return new Variable(tipo,tok);
	}
	
	/**
	 * <FormaMetodo> → static | dynamic
	 */
	private boolean FormaMetodo()throws ErrorSintactico, ErrorLexico, IOException{
		boolean isStatic=esIgual("T_Static");
		if(isStatic)
				match("T_Static");
		else 
			match("T_Dynamic");
		return isStatic;
	}
	
	/**
	 * <TipoMetodo> → <Tipo> | void
	 *P(<Tipo>)= {idClase, boolean, char, int, String} 
	 */
	private Tipo TipoMetodo()throws ErrorSintactico, ErrorLexico, IOException{
		Tipo tipoRetorno;
		if(esIgual("T_Void")){
			match("T_Void");
			tipoRetorno=TipoVoid.getInstancia();
		}else if(esTipo())
			tipoRetorno=Tipo();
		else
			throw new ErrorSintactico(Error("Tipo de Metodo"));
		return tipoRetorno;
	}

	
	
	
	/**
	 * <ListaDecVars> → idMetVar <LDV>
	 */
	private LinkedList<Token> ListaDecVars()throws ErrorSintactico, ErrorLexico, IOException{
		LinkedList<Token>lista;
		Token id= tokenActual;
		match("ID_MetVar");
		lista=LDV();
		lista.addFirst(id);
		return lista;
	}
	
	/**
	 * <LDV>→ λ | , <ListaDecVars>
	 *P(<LDV>)= { λ , ,}
	 *F(<LDV>)= { ; }  
	 */
	private LinkedList<Token>LDV()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_Coma")){
			match("T_Coma");
			return ListaDecVars();
		}else if(!esIgual("T_PyC"))
				throw new ErrorSintactico(Error(" ; o , "));
		return new LinkedList<Token>();
	}
	
	/**
	 * <Bloque> → { <Sentencias>}
	 */
	private Bloque Bloque()throws ErrorSintactico, ErrorLexico, IOException{
			Bloque ant=TablaSimbolos.bloqueActual;
			Token token=tokenActual;
			LinkedList<Sentencia> sents;
		match("T_LlaveIni");
			Bloque bloque= new Bloque(token);
			TablaSimbolos.bloqueActual=bloque;
			sents=Sentencias();
			bloque.setSentencia(sents);
		match("T_LlaveFin");
			TablaSimbolos.bloqueActual=ant;
		return bloque;
	}
	
	/**
	 *<Sentencias> → λ | <Sentencia><Sentencias>
	 *P(<Sentencia>)={ ;, idClase, boolean, char, int, String, if, while, {, return, (, this, idMetVar, new }
	 *F(<Sentencias>)={ } }  
	 */
	private LinkedList<Sentencia> Sentencias()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_PyC")||esIgual("T_ParenIni")||esIgual("T_This")||esIgual("ID_MetVar")||esIgual("T_New")||esIgual("ID_Clase")||esIgual("T_If")||esIgual("T_While")||esIgual("T_LlaveIni")||esIgual("T_Return")||esTipo()){
			Sentencia sent=Sentencia();
			LinkedList<Sentencia> sents=Sentencias();
			sents.addFirst(sent);
			return sents;
		}else if(!esIgual("T_LlaveFin"))
			throw new ErrorSintactico(Error("} o el Icicio de una Sentencia"));
		return new LinkedList<Sentencia>();
	}
	
	/**
	 *<Sentencia>→ ;
	 *<Sentencia>→ <Primario> <AsigOLlamada>;
	 *<Sentencia>→ <Tipo> <ListaDecVars> ;
	 *<Sentencia>→ <Bloque>
	 *<Sentencia>→ if ( <Expresion> ) <Sentencia> <Else>
	 *<Sentencia>→ while ( <Expresion> ) <Sentencia>
	 *<Sentencia>→ return <Expresiones> ;
	 *P(<Primario>)= { (, this, idMetVar, new}
	 *P(<Tipo>)= {idClase, boolean, char, int, String}
	 *P(<Bloque>)= { { }
	 */
	private Sentencia Sentencia()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_PyC")){
			Token t=tokenActual;
			match("T_PyC");
			return new SentenciaVacia(t);
		}else if(esIgual("T_ParenIni")||esIgual("T_This")||esIgual("ID_MetVar")||esIgual("T_New")){
					ExpresionPrimario exp;
					Sentencia sent;
				exp= Primario();
				sent= AsigOLlamada(exp);
				match("T_PyC");
					return sent;
		}else if(esTipo()){
			Token tok=tokenActual;
			Tipo t=Tipo();
			LinkedList<Token> vars=ListaDecVars();
			match("T_PyC");
			return new SentenciaDeclaracion(tok,t,vars);
		}else if(esIgual("T_LlaveIni")){
			return Bloque();
		}else if(esIgual("T_If")){
			Token token=tokenActual;
			match("T_If");
			match("T_ParenIni");
			Expresion cond=Expresion();
			match("T_ParenFin");
			Sentencia sent= Sentencia();
			Else elses=Else();
			return new SentenciaIf(cond,sent,token,elses);
		}else if(esIgual("T_While")){
			Token token=tokenActual;
			match("T_While");
			match("T_ParenIni");
			Expresion cond=Expresion();
			match("T_ParenFin");
			Sentencia sent=Sentencia();
			return new SentenciaWhile(cond,sent,token);
		}else if(esIgual("T_Return")){
			Token tok=tokenActual;
			match("T_Return");
			Expresion exp=Expresiones();
			match("T_PyC");
			return new SentenciaReturn(tok,exp);
		}else
			throw new ErrorSintactico(Error("una Sentencia "));
	}
	
	
	
	
	/**
	 *<AsigOLlamada>→ λ | = <Expresion>
	 *F(<AsigOLlamada>)= {;}
	 */
	private Sentencia AsigOLlamada(ExpresionPrimario exp)throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_OpAsig")){
			Expresion expresion;
			Token tok=tokenActual;
			match("T_OpAsig");
			expresion=Expresion();
			return new SentenciaAsignacion(tok,exp,expresion);
		}else if(!esIgual("T_PyC"))
			throw new ErrorSintactico(Error(" ;, ="));
		return new SentenciaLlamada(exp.getToken(),exp);
	}
	
	/**
	 *<Else>→ λ | else <Sentencia>
	 *F(<Else>)= { ;, (, this, idMetVar, new, idClase,boolean, char, int, String, if, while, {,return,} } U {else}
	 */
	private Else Else()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_Else")){
			Token telse=tokenActual;
			match("T_Else");
			Sentencia sentElse=Sentencia();
			return new Else(telse,sentElse);
		}else if(!(esIgual("T_LlaveFin")||esIgual("T_PyC")||esIgual("T_ParenIni")||esIgual("T_This")||esIgual("ID_MetVar")||esIgual("T_New")||esIgual("ID_Clase")||esIgual("T_If")||esIgual("T_While")||esIgual("T_LlaveIni")||esIgual("T_Return")||esTipo()))
			throw new ErrorSintactico(Error("} o un Inicio de Sentencia"));
		return null;
	}
	
	/**
	 *<Expresiones>→λ | <Expresion>
	 *P(<ExpOr>)={+,- ,!, [, null, true, false,intLiteral, charLiteral, stringLiteral, (,this, idMetVar, new}
	 *F(<Expresiones>)= { ; }
	 */
	private Expresion Expresiones()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_Suma")||esIgual("T_Resta")||esIgual("T_OpNeg")||esIgual("T_CastIni")||esOperando())
			return Expresion();
		else if(!esIgual("T_PyC"))
			throw new ErrorSintactico(Error("; o un Inicio de Expresion"));
		return null;
	}
	
	/**
	 *<Expresion> → <ExpOr>
	 */
	private Expresion Expresion()throws ErrorSintactico, ErrorLexico, IOException{
		return ExpOr();
	}
	
	/**
	 * <ExpOr> → <ExpAnd> <Exp_Ors>
	 * P(<ExpAnd>)= {+, -, !, [, null,true, false, intLiteral, charLiteral,stringLiteral, (, this, idMetVar, new}
	 * F(<ExpOr>)={; ,), ,} 
	 */
	private Expresion ExpOr()throws ErrorSintactico, ErrorLexico, IOException{
		Expresion exp=ExpAnd();
		return Exp_Ors(exp);
	}
	
	/**
	 * <ExpAnd> → <ExpIg><Exp_Ands>
	 * P(<ExpIg>)= {+, -, !, [,null, true, false, intLiteral,charLiteral, stringLiteral, (, this,idMetVar, new}
	 */
	private Expresion ExpAnd()throws ErrorSintactico, ErrorLexico, IOException{
		Expresion exp=ExpIg();
		return Exp_Ands(exp);
	}

	/**F
	 * <ExpIg> → <ExpComp> <Exp_Igs>
	 * P(<ExpComp>)= {+, -, !, [,null, true, false, intLiteral,charLiteral, stringLiteral, (, this,idMetVar, new}
	 */
	private Expresion ExpIg()throws ErrorSintactico, ErrorLexico, IOException{
		Expresion exp=ExpComp();
		return Exp_Igs(exp);
	}
	
	/**
	 * <ExpComp> → <ExpAd> <Exp_Comps>
	 * P(<ExpAd>)= {+, -, !, [,null, true, false, intLiteral,charLiteral, stringLiteral, (, this,idMetVar, new}
	 */
	private Expresion ExpComp()throws ErrorSintactico, ErrorLexico, IOException{
		Expresion exp=ExpAd();
		return Exp_Comps(exp);
	}
	
	/**
	 * <ExpAd> → <ExpMul> <Exp_Ads>
	 * P(<ExpMul>)= {+, -, !, [,null, true, false, intLiteral,charLiteral, stringLiteral, (, this,idMetVar, new}
	 */
	private Expresion ExpAd()throws ErrorSintactico, ErrorLexico, IOException{
		Expresion exp=ExpMul();
		return Exp_Ads(exp);
	}
	
	/**
	 * <ExpMul> → <ExpUn> <Exp_Muls>
	 * P(<ExpUn>)= {+, -, !, [,null, true, false, intLiteral,charLiteral, stringLiteral, (, this,idMetVar, new}
	 */
	private Expresion ExpMul()throws ErrorSintactico, ErrorLexico, IOException{
		Expresion exp=ExpUn();
		return Exp_Muls(exp);
	}
	
	/**
	 * <ExpUn> → <OpUn> <ExpUn> | <ExpCast>
	 * P(<OpUn)= {+,- , !}
	 * P(<ExpCast>)={ [, null, true, false,intLiteral, charLiteral, stringLiteral, (,this, idMetVar, new }
	 */
	private Expresion ExpUn()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_Suma")||esIgual("T_Resta")||esIgual("T_OpNeg")){
			Token token= tokenActual;
			OpUn();
			Expresion exp=ExpUn();
			return new ExpresionUnaria(token,exp);
		}else if(esIgual("T_CastIni")||esOperando())
			return ExpCast();
		else
			throw new ErrorSintactico(Error("inicio de Expresion"));
	}
	
	
	
	/**
	 * <ExpCast> → [ idClase ] <Operando> | <Operando>
	 * P(Operando)={null, true, false, intLiteral,charLiteral, stringLiteral, (, this,idMetVar, new}
	 */
	private Expresion ExpCast()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_CastIni")){
			Token token=tokenActual;
			match("T_CastIni");
			String idClase=tokenActual.get_Lexema();
			match("ID_Clase");
			match("T_CastFin");
			Operando op=Operando();
			return new ExpresionCast(idClase,op,token);
		}else
			return Operando();
	}
	
	/**
	 * <Exp_Ors> → λ | || <ExpAnd> <Exp_Ors>
	 * F(<Exp_Ors>)= { ; , ),  , }
	 */
	private Expresion Exp_Ors(Expresion expIzq)throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_Or")){
			Token token=tokenActual;
			match("T_Or");
			Expresion expDer= ExpAnd();
			Expresion exp= new ExpresionBinaria(token,expIzq,expDer);
			return Exp_Ors(exp);
		}else if(!(esIgual("T_PyC")||esIgual("T_Coma")||esIgual("T_ParenFin")))
			throw new ErrorSintactico(Error(";, ( , , o ||"));
		return expIzq;
	}
	
	/**
	 * <Exp_Ands>→ λ | && <ExpIg> <Exp_Ands>
	 *  F(<Exp_Ands>)= {||,; , ), , }
	 */
	private Expresion Exp_Ands(Expresion expIzq)throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_And")){
			Token token=tokenActual;
			match("T_And");
			Expresion expDer=ExpIg();
			Expresion exp= new ExpresionBinaria(token,expIzq,expDer);
			return Exp_Ands(exp);
		}else if(!(esIgual("T_PyC")||esIgual("T_Coma")||esIgual("T_ParenFin")||esIgual("T_Or")))
			throw new ErrorSintactico(Error("; , ,),|| o &&"));
		return expIzq;
	}

	/**
	 * <Exp_Igs>→ λ | <OpIg> <ExpComp> <Exp_Igs>
	 * P(OpIg)= { ==, != }
	 * F(<Exp_Igs>)= {||,; , ), &&}
	 */
	private Expresion Exp_Igs(Expresion expIzq)throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_OpLg")){
			Token token=tokenActual;
			match("T_OpLg");
			Expresion expDer=ExpComp();
			Expresion exp= new ExpresionBinaria(token,expIzq,expDer);
			return	Exp_Igs(exp);
		}else if(!(esIgual("T_PyC")||esIgual("T_Coma")||esIgual("T_ParenFin")||esIgual("T_Or")||esIgual("T_And")))
			throw new ErrorSintactico(Error("; , ,),||, &&, == o !="));
		return expIzq;
	}
	
	/**
	 * <Exp_Comps>→ λ | instanceof idClase |<OpComp> <ExpAd>
	 * P(<OpComp>)= { <, <=, >, >=}
	 * F(<Exp_Comps>)=  {;, ), ||,&&,==,!=} 
	 */
	private Expresion Exp_Comps(Expresion expIzq)throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_InsOf")){
			Token token=tokenActual;
			match("T_InsOf");
			String idClase=tokenActual.get_Lexema();
			match("ID_Clase");
			return new ExpresionInstanceOf(expIzq,idClase,token);
		}else if(esIgual("T_OpComp")){
			Token token=tokenActual;
			match("T_OpComp");
			Expresion expDer=ExpAd();
			return new ExpresionBinaria(token,expIzq,expDer);
		}else if(!(esIgual("T_PyC")||esIgual("T_Coma")||esIgual("T_ParenFin")||esIgual("T_InsOf")||esIgual("T_Or")||esIgual("T_And")||esIgual("T_OpLg")))
			throw new ErrorSintactico(Error("; , ,),||, &&, ==, !=, instanceof, o Operador de Comparacion"));
		return expIzq;
	}
	
	/**
	 * <Exp_Ads>→ λ | <OpAd> <ExpMul> <Exp_Ads>
	 * P(<OpAd>)= { -, + }
	 * F(<Exp_Ads>)= { ;, ), ||,&&,==,!=,<, <=, >, >=,instanceof}
	 */
	private Expresion Exp_Ads(Expresion expIzq)throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_Suma")||esIgual("T_Resta")){
			Token token=tokenActual;
			OpAd();
			Expresion expDer=ExpMul();
			Expresion exp= new ExpresionBinaria(token,expIzq,expDer);
			return Exp_Ads(exp);
		}else if(!(esIgual("T_PyC")||esIgual("T_Coma")||esIgual("T_InsOf")||esIgual("T_ParenFin")||esIgual("T_Or")||esIgual("T_And")||esIgual("T_OpLg")||esIgual("T_OpComp")||esIgual("T_InsOf")))
				throw new ErrorSintactico(Error("; , ,),||, &&, ==, !=, instanceof, Operador de Comparacion,instanceof, + o -"));
		return expIzq;
	}
	
	/**
	 *<Exp_Muls>→ λ |<OpMul> <ExpUn><Exp_Muls>
	 *P(<OpMul>)={*,/,%}
	 *F(<Exp_Muls>)= { ;, ,  ), ||, &&, ==, !=,instanceof,<, >,>=,<=, -, +}	
	 */
	private Expresion Exp_Muls(Expresion expIzq)throws ErrorSintactico, ErrorLexico, IOException{
		if(esOpMul()){
			Token token=tokenActual;
			OpMul();
			Expresion expDer=ExpUn();
			Expresion exp= new ExpresionBinaria(token,expIzq,expDer);
			return Exp_Muls(exp);
		}else if(!(esIgual("T_PyC")||esIgual("T_InsOf")||esIgual("T_Coma")||esIgual("T_ParenFin")||esIgual("T_Or")||esIgual("T_And")||esIgual("T_OpLg")||esIgual("T_OpComp")||esIgual("T_InsOf")||esIgual("T_Suma")||esIgual("T_Resta")))
			throw new ErrorSintactico(Error(", o ;, ), ||, &&, ==, !=, instanceof, Operador de Comparacion, instanceof, +, -, *, / o % "));
		return expIzq;
	}	 
	private void OpMul() throws ErrorSintactico, ErrorLexico, IOException{
		switch(idTok){
			case "T_Prod":
			case "T_Div":
			case "T_Mod":
				match(idTok);
				break;
		}
	}
	private void OpAd() throws ErrorSintactico, ErrorLexico, IOException{
		switch(idTok){
		case "T_Suma":
		case "T_Resta":
			match(idTok);
			break;
		}
	}
	private void OpUn() throws ErrorSintactico, ErrorLexico, IOException{
		switch(idTok){
			case "T_Suma":
			case "T_Resta":
			case "T_OpNeg":
				match(idTok);
				break;
		}
	}
	/**
	 *<Tipo> → <TipoPrimitivo> |idClase 
	 */
	private Tipo Tipo()throws ErrorSintactico, ErrorLexico, IOException{
		Tipo tipo;
		if(esIgual("ID_Clase")){
			tipo=new TipoClase(tokenActual.get_Lexema());
			match("ID_Clase");
		}else if(esTipoPrimitivo())
				tipo= TipoPrimitivo();
		else
			throw new ErrorSintactico(Error("Tipo"));
		return tipo;
	}
	
	/**
	 *<TipoPrimitivo> → boolean | char | int | String |idClase 
	 */
	private Tipo TipoPrimitivo()throws ErrorSintactico, ErrorLexico, IOException{
		Tipo t;		
		switch(idTok){
			case "T_Boolean":
					t=TipoBoolean.getInstancia();
					match("T_Boolean");
					break;					
			case "T_Char":
				t=TipoChar.getInstancia();
				match("T_Char");
				break;	
			case "T_Int":
				t=TipoInt.getInstancia();
				match("T_Int");
				break;	
			case "T_String":
				t= TipoString.getInstancia();
				match("T_String");
				break;	
			default:
				throw new ErrorSintactico(Error("Tipo"));
		}
		return t;
	}
	/**
	 *<Operando> → <>
	 *<Operando> → <Primario> 
	 *P(<Literal>)= { null, true, false, intLiteral,charLiteral, stringLiteral}
	 *P(<Primario>)= { (, this, idMetVar, new}
	 */
	private Operando Operando()throws ErrorSintactico, ErrorLexico, IOException{
		if(esLiteral())
			return Literal();
		else 
			return Primario();
	}	
	private ExpresionLiteral Literal() throws ErrorSintactico, ErrorLexico, IOException{
		Token token=tokenActual;
		switch(idTok){
			case "T_NULL":
			case "Lit_Boolean":
			case "LitInt":
			case "LitChar":
				match(idTok);
				break;
			case "LitString":
				match(idTok);
				return new ExpresionLiteralString(token);
			default:
				throw new ErrorSintactico("Literal");
		}
		return new ExpresionLiteral(token);
	}
	/**
	 * <Primario> → (<PrimAux>
	 * <Primario>→this <LlamadaoIdEncadenados>
	 * <Primario>→idMetVar <ArgsActOLam><LlamadaoIdEncadenados>
	 * <Primario>→new idClase <ArgsActuales> <LlamadaoIdEncadenados>
	 */
	private ExpresionPrimario Primario()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_ParenIni")){
			match("T_ParenIni");
			return PrimAux();
		}else if(esIgual("T_This")){
				Token tok=tokenActual;
			match("T_This");
			Encadenado enc= LlamadaoIdEncadenados();
			return new ExpresionThis(tok,enc);
		}else if(esIgual("ID_MetVar")){
			Token tok=tokenActual;
			match("ID_MetVar");
			LinkedList<Expresion> params=ArgsActOLam();
			Encadenado enc= LlamadaoIdEncadenados();
			if(params==null)
				return new VariablePrimaria(tok,enc);
			else
				return new LlamadaMetodoDirecto(tok,enc,params);
		}else if(esIgual("T_New")){
			match("T_New");
			Token token=tokenActual;
			match("ID_Clase");
			LinkedList<Expresion> params=ArgsActuales();
			Encadenado enc= LlamadaoIdEncadenados();
			return new LlamadaConstructor(token,params,enc);
		}else throw new ErrorSintactico(Error("Inicio de Primario"));
	}	
	
	/**
	 * <PrimAux>→<Expresion> ) <LlamadaoIdEncadenados>
	 * <PrimAux>→idClase . idMetVar <ArgsActuales>) <LlamadaoIdEncadenados>
	 * P(<Expresion>)= 
	 */
	private ExpresionPrimario PrimAux()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("ID_Clase")){
				Encadenado enc;
				LinkedList<Expresion> params;
				Token tok=tokenActual;
			match("ID_Clase");
			match("T_Punto");
			String idMetVar=tokenActual.get_Lexema();
			match("ID_MetVar");
			params=ArgsActuales();
			match("T_ParenFin");
			enc= LlamadaoIdEncadenados();
			return new LlamadaMetodoEstatico(tok,idMetVar,params,enc);
		}else{
			Expresion exp= Expresion();
			match("T_ParenFin");
			Encadenado enc= LlamadaoIdEncadenados();
			return new PrimarioConExpresion(exp,enc);
		}
	}	
	
	
	/**
	 * <LlamadaoIdEncadenados>→ λ |<LlamadaoIdEncadenado> <LlamadaoIdEncadenados>
	 * P(<LlamadaoIdEncadenado>)=  { . }
	 * F(<LlamadaoIdEncadenados>)= { =,this, idMetVar, new, ||, ; , ),&&,==,!=,instanceof, <, >, <=,>=, +,-,*, /, %} }
	 */
	private Encadenado LlamadaoIdEncadenados()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_Punto")){
			Encadenado ret=LlamadaoIdEncadenado();
			Encadenado enc=LlamadaoIdEncadenados();
				ret.setEncadenado(enc);
				return ret;
		}else if(!(esIgual("T_OpAsig")||esIgual("T_InsOf")||esIgual("T_This")||esIgual("T_Coma")||esIgual("ID_MetVar")||esIgual("T_New")||esIgual("T_Or")||esIgual("T_PyC")||esIgual("T_ParenFin")||esIgual("T_And")||esIgual("T_OpComp")||esIgual("T_OpLg")||esIgual("T_Suma")||esIgual("T_Resta")||esOpMul()))
			throw new ErrorSintactico(Error("(, this, idMetVar, new, =, *, /, %, ;, .,), ||, &&, ==, !=, instanceof, <, >,>=,<=,  - o +"));
		return null;
	}	
	
	/**
	 * <LlamadaoIdEncadenado> → . idMetVar <ArgsActOLam>
	 */
	private Encadenado LlamadaoIdEncadenado()throws ErrorSintactico, ErrorLexico, IOException{
			Token token=tokenActual;
		match("T_Punto");
			String idmetvar=tokenActual.get_Lexema();
		match("ID_MetVar");
		LinkedList<Expresion> params= ArgsActOLam();
			if(params==null)
				return new AtributoEncadenado(token,idmetvar,null);
			else
				return new LlamadaEncadenado(token,idmetvar,params);
	}
	/**
	 * <ArgsActOLam> →λ |<ArgsActuales> 
	 * P(<ArgsActuales>)= { ( }
	 * F(<ArgsActOLam>)= {=,., this, idMetVar, new, ||, ; , ,),&&,==,!=,instanceof, <, >, <=,>=, +,-,*, /, %}
	 */
	private LinkedList<Expresion> ArgsActOLam()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_ParenIni"))
			return ArgsActuales();
		else if(!(esIgual("T_OpAsig")||esIgual("T_InsOf")||esIgual("T_Punto")||esIgual("T_Coma")||esIgual("T_This")||esIgual("ID_MetVar")||esIgual("T_New")||esIgual("T_Or")||esIgual("T_PyC")||esIgual("T_ParenFin")||esIgual("T_And")||esIgual("T_OpComp")||esIgual("T_OpLg")||esIgual("T_Suma")||esIgual("T_Resta")||esOpMul()))
			throw new ErrorSintactico("Error Sintactico ("+tokenActual.get_NroLinea()+":"+tokenActual.get_NroCol()+")= el lexema "+tokenActual.get_Lexema()+" es Sintacticamente invalido");
		return null;
	}
	/**
	 * <ArgsActuales> → ( <Lista_Exps>)
	 */
	private LinkedList<Expresion> ArgsActuales()throws ErrorSintactico, ErrorLexico, IOException{
			match("T_ParenIni");
			LinkedList<Expresion> exps = Lista_Exps();
			match("T_ParenFin");
			return exps;
	}	
	/**
	 * <Lista_Exps>→ λ | <ListaExps>
	 */
	private LinkedList<Expresion> Lista_Exps()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_Suma")||esIgual("T_Resta")||esIgual("T_OpNeg")||esIgual("T_CastIni")||esOperando())
			return ListaExps();
		else if(!esIgual("T_ParenFin"))
			throw new ErrorSintactico(Error(" { ,+,-,!,[ o Operando"));
		return new LinkedList<Expresion>();
	}
	/**
	 * <ListaExps> → <Expresion><LE>
	 */
	private LinkedList<Expresion> ListaExps()throws ErrorSintactico, ErrorLexico, IOException{
		Expresion exp= Expresion();
		LinkedList<Expresion> exps=LE();
		exps.addFirst(exp);
		return exps;
	}
	/**
	 * <LE> → λ | , <ListaExps> 
	 */
	private LinkedList<Expresion> LE()throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual("T_Coma")){
			match("T_Coma");
			return ListaExps();
		}else if(!esIgual("T_ParenFin"))
			throw new ErrorSintactico(Error(" ( o ,"));
		return new LinkedList<Expresion> ();
	}
	
	private boolean esIgual(String txt){
		return idTok.equals(txt);
	}
	private boolean esTipo(){
		return esTipoPrimitivo()||esIgual("ID_Clase");
	}
	private boolean esTipoPrimitivo(){
		return (esIgual("T_Boolean")||esIgual("T_Char")||esIgual("T_Int")||esIgual("T_String"));
	}
	private boolean esLiteral(){
		return (esIgual("T_NULL")||esIgual("Lit_Boolean")||esIgual("LitInt")||esIgual("LitChar")||esIgual("LitString"));
	}
	private boolean esOperando(){
		return esLiteral() ||esIgual("T_ParenIni")||esIgual("T_This")||esIgual("T_New")||esIgual("ID_MetVar"); 
	}
	private boolean esOpMul(){
		return esIgual("T_Prod")||esIgual("T_Div")||esIgual("T_Mod");
	}
	private String Error(String esperado){
		return"Error Sintactico ("+tokenActual.get_NroLinea()+":"+tokenActual.get_NroCol()+")= Se esperaba un "+esperado+" y se encontro un "+tokenActual.get_Lexema();
	}
	
	/**
	 * 
	 * @param ID_T
	 * @throws ErrorSintactico
	 * @throws IOException 
	 * @throws ErrorLexico 
	 */
	private void match(String ID_T)throws ErrorSintactico, ErrorLexico, IOException{
		String posibles=simbolos.get(ID_T);
		if(esIgual(ID_T)){
			tokenActual=alex.getToken();
			idTok=tokenActual.get_IDTOKEN();
		}else
			throw new ErrorSintactico(Error(posibles));
	}
	
	
	/**
	 * @param ID_T
	 * @throws ErrorSintactico
	 * @throws IOException 
	 * @throws ErrorLexico 
	 */
	private void match(String ID_T,String Err)throws ErrorSintactico, ErrorLexico, IOException{
		if(esIgual(ID_T)){
			tokenActual=alex.getToken();
			idTok=tokenActual.get_IDTOKEN();
		}else
			throw new ErrorSintactico("Error Sintactico ("+tokenActual.get_NroLinea()+":"+tokenActual.get_NroCol()+")="+Err);
	}
/**
 * Carga los Simbolos de los Tokens
 */
private void cargarSimbolos(){
	simbolos= new Hashtable<String,String>();
	try{
		simbolos.put("T_ParenIni","(");
		simbolos.put("T_ParenFin",")");
		simbolos.put("T_LlaveIni","{");
		simbolos.put("T_LlaveFin","}");
		simbolos.put("T_CastIni","[");
		simbolos.put("T_CastFin","]");
		simbolos.put("T_Coma",",");
		simbolos.put("T_Punto",".");
		simbolos.put("T_PyC",";");
		simbolos.put("T_Suma","+");
		simbolos.put("T_Resta","-");
		simbolos.put("T_Prod","*");
		simbolos.put("T_Mod","%");
		simbolos.put("T_Class","class");
		simbolos.put("T_Extends","extends");
		simbolos.put("T_Static","static");
		simbolos.put("T_Dynamic","dynamic");
		simbolos.put("T_Public","public");
		simbolos.put("T_Private","private");
		simbolos.put("T_Void","void");
		simbolos.put("T_Boolean","boolean");
		simbolos.put("T_Char","char");
		simbolos.put("T_Int","int");
		simbolos.put("T_String","String");
		simbolos.put("T_If","if");
		simbolos.put("T_Else","else");
		simbolos.put("T_While","while");
		simbolos.put("T_Return","return");
		simbolos.put("T_InsOf","instanceof");
		simbolos.put("T_This","this");
		simbolos.put("T_New","new");
		simbolos.put("T_NULL","null");
		simbolos.put("Lit_Boolean","true o flase");
		simbolos.put("LitChar","Literal Char");
		simbolos.put("LitInt","Literal Int");
		simbolos.put("LitString","Literal String");
		simbolos.put("ID_MetVar","Identificador de metodo o variable");
		simbolos.put("ID_Clase","Identificador de Clase");
		simbolos.put("T_OpAsig","=");
		simbolos.put("T_OpLg","== o !=");
		simbolos.put("T_OpNeg","!");
		simbolos.put("T_OpComp","<,>,<=,o >=");
		simbolos.put("T_Div","/");
		simbolos.put("T_And","&&");
		simbolos.put("T_Or","||");
		
	}catch(NullPointerException e){
		}
	}

	
	//Completar y validar Tabla de Simbolos
	private void cargarPredefinidas() throws ErrorSemantico{
		Token tokenId=new Token("ID_Clase","Object",0,0);
		Clase Object=new Clase(tokenId,null);
		TS.getClases().put("Object", Object);
		TablaSimbolos.claseActual=Object;
		Ctor ctor=new Ctor(tokenId,new LinkedList<Variable>());
		ctor.setBloque(new Bloque(tokenId));
		Object.setCtor(ctor);
		
			LinkedList<Variable> param=new LinkedList<Variable>();
			Token token= new Token("Id_MetVar","toString",0,0);
			Metodo toString= new Metodo(token,param,false,new TipoString());
			toString.setBloque(new BloqueToString(token));
			Object.getMetodos().put("toString", toString);
			
	
		
		tokenId=new Token("ID_Clase","System",0,0);
		Clase System= new Clase(tokenId,"Object");
		TS.getClases().put("System", System);
		TablaSimbolos.claseActual=System;
		ctor=new Ctor(tokenId,new LinkedList<Variable>());
		ctor.setBloque(new Bloque(tokenId));
		System.setCtor(ctor);
		
		Hashtable<String,Metodo> metodos=System.getMetodos();
		TipoVoid tipoVoid=TipoVoid.getInstancia();
		TipoInt tipoInt=TipoInt.getInstancia();
		TipoBoolean tipoBoolean=TipoBoolean.getInstancia();
		TipoChar tipoChar=TipoChar.getInstancia();
		TipoString tipoString=TipoString.getInstancia();
		
		String idMet="read";
		LinkedList<Variable> parametros=new LinkedList<Variable>();
		Token tokMet= new Token("Id_MetVar",idMet,0,0);
		Metodo met= new Metodo(tokMet,parametros,true,tipoInt);
		met.setBloque(new BloqueRead(tokMet));
		metodos.put(idMet, met);

		idMet="readInt";
		parametros=new LinkedList<Variable>();
		tokMet= new Token("Id_MetVar",idMet,0,0);
		met= new Metodo(tokMet,parametros,true,tipoInt);
		met.setBloque(new BloqueReadInt(tokMet));
		metodos.put(idMet, met);


		String idVar="b";
		idMet="printB";
		parametros=new LinkedList<Variable>();
		tokMet= new Token("Id_MetVar",idMet,0,0);
		Token tokVar= new Token("Id_MetVar",idVar,0,0);
		met= new Metodo(tokMet,parametros,true,tipoVoid);
		met.setBloque(new BloquePrintB(tokMet));
		metodos.put(idMet, met);
		parametros.add(new Variable(tipoBoolean,tokVar));
		
		idVar="c";
		idMet="printC";
		parametros=new LinkedList<Variable>();
		tokMet= new Token("Id_MetVar",idMet,0,0);
		tokVar= new Token("Id_MetVar",idVar,0,0);
		met= new Metodo(tokMet,parametros,true,tipoVoid);
		met.setBloque(new BloquePrintC(tokMet));
		metodos.put(idMet, met);
		parametros.add(new Variable(tipoChar,tokVar));
		
		idVar="i";
		idMet="printI";
		parametros=new LinkedList<Variable>();
		tokMet= new Token("Id_MetVar",idMet,0,0);
		tokVar= new Token("Id_MetVar",idVar,0,0);
		met= new Metodo(tokMet,parametros,true,tipoVoid);
		met.setBloque(new BloquePrintI(tokMet));
		metodos.put(idMet, met);
		parametros.add(new Variable(tipoInt,tokVar));
		
		idVar="s";
		idMet="printS";
		parametros=new LinkedList<Variable>();
		tokMet= new Token("Id_MetVar",idMet,0,0);
		tokVar= new Token("Id_MetVar",idVar,0,0);
		met= new Metodo(tokMet,parametros,true,tipoVoid);
		met.setBloque(new BloquePrintS(tokMet));
		metodos.put(idMet, met);
		parametros.add(new Variable(tipoString,tokVar));
		
		idMet="println";
		parametros=new LinkedList<Variable>();
		tokMet= new Token("Id_MetVar",idMet,0,0);
		met= new Metodo(tokMet,parametros,true,new TipoVoid());
		met.setBloque(new BloquePrintln(tokMet));
		metodos.put(idMet, met);
		
		idVar="b";
		idMet="printBln";
		parametros=new LinkedList<Variable>();
		tokMet= new Token("Id_MetVar",idMet,0,0);
		tokVar= new Token("Id_MetVar",idVar,0,0);
		met= new Metodo(tokMet,parametros,true,tipoVoid);
		met.setBloque(new BloquePrintBln(tokMet));
		metodos.put(idMet, met);
		parametros.add(new Variable(tipoBoolean,tokVar));
		
		idVar="c";
		idMet="printCln";
		parametros=new LinkedList<Variable>();
		tokMet= new Token("Id_MetVar",idMet,0,0);
		tokVar= new Token("Id_MetVar",idVar,0,0);
		met= new Metodo(tokMet,parametros,true,tipoVoid);
		met.setBloque(new BloquePrintCln(tokMet));
		metodos.put(idMet, met);
		parametros.add(new Variable(tipoChar,tokVar));
		
		idVar="i";
		idMet="printIln";
		parametros=new LinkedList<Variable>();
		tokMet= new Token("Id_MetVar",idMet,0,0);
		tokVar= new Token("Id_MetVar",idVar,0,0);
		met= new Metodo(tokMet,parametros,true,tipoVoid);
		met.setBloque(new BloquePrintIln(tokMet));
		metodos.put(idMet, met);
		parametros.add(new Variable(tipoInt,tokVar));
		
		idVar="s";
		idMet="printSln";
		parametros=new LinkedList<Variable>();
		tokMet= new Token("Id_MetVar",idMet,0,0);
		tokVar= new Token("Id_MetVar",idVar,0,0);
		met= new Metodo(tokMet,parametros,true,tipoVoid);
		met.setBloque(new BloquePrintSln(tokMet));
		metodos.put(idMet, met);
		parametros.add(new Variable(tipoString,tokVar));
		
		
	}
	/**
	 * Setea el primer main que encuetra en la tabla de simbolos como el main del proyecto
	 * @throws ErrorSemantico si ninguna clase definida tiene un metodo main
	 */
	private void buscarMain() throws ErrorSemantico{
		boolean encontre=false;
		Metodo Mmain=null;
		 for(Clase c:TS.getClases().values()){
			 Mmain=c.getMetodos().get("main");
			 encontre=(Mmain!=null && Mmain.getParamFormales().isEmpty() && Mmain.esStatic() && (Mmain.getTipoRetorno() instanceof TipoVoid));
			 if(encontre)
				 break;
		 }
		 if(!encontre)
			 throw new ErrorSemantico("Ninguna Clase declarada tiene un metodo Main",tokenActual);
		 TablaSimbolos.main=Mmain;
	}
	/**
	 * Revisa que la clase actual tenga un constructor, en caso de no tenerlo le crea uno por defecto
	 * @throws ErrorSemantico
	 */
	private void revisarConstructor() throws ErrorSemantico{
		Clase c=TablaSimbolos.claseActual;
		Token t=c.getToken();
		Ctor ctor=new Ctor(t,new LinkedList<Variable>());
		ctor.setBloque(new Bloque(t));
		if(c.getCtor()==null)
			c.setCtor(ctor);
	}
	/**
	 * chequea que no exista una clase con el mismo id que el token pasado por parametro en la tabla de simbolos
	 * @param clase
	 * @throws ErrorSemantico si el chequeo falla
	 */
	private void noExisteClase(Token clase) throws ErrorSemantico{
		if(TS.getClases().get(clase.get_Lexema())!=null)
			throw new ErrorSemantico("La clase "+clase.get_Lexema()+" esta definida mas de una vez",clase);
	}
	/**
	 * chequea que no exista un metodo con el mismo id que el token pasado por parametro en la tabla de simbolos de la clase
	 * Ademas verifica que el atributo no se llame como la clase o algun metodo de ella
	 * @param clase
	 * @throws ErrorSemantico si algun chequeo falla
	 */
	private void noExisteAtributo(Token atrib) throws ErrorSemantico{
		if((TablaSimbolos.claseActual.getAtributos().get(atrib.get_Lexema()))!=null)
			throw new ErrorSemantico("El Atributo "+atrib.get_Lexema()+" esta definido mas de una vez en la clase",atrib);
		if(TablaSimbolos.claseActual.getIdClase().equals(atrib.get_Lexema()))
			throw new ErrorSemantico("El atributo tiene el mismo nombre que la clase",atrib);
		if((TablaSimbolos.claseActual.getMetodos().get(atrib.get_Lexema()))!=null)
			throw new ErrorSemantico("El atributo tiene el mismo nombre que un Metodo",atrib);
	}
	/**
	 * chequea que no exista una clase con el mismo id que el token pasado por parametro en la tabla de simbolos
	 * Ademas verifica que no exista un atributo con el mismo nombre
	 * @param clase
	 * @throws ErrorSemantico s algun chequeo falla
	 */
	private void noExisteMetodo(Token met) throws ErrorSemantico{
		if((TablaSimbolos.claseActual.getMetodos().get(met.get_Lexema()))!=null)
			throw new ErrorSemantico("El Metodo "+met.get_Lexema()+" esta definido mas de una vez en la clase",met);
		if((TablaSimbolos.claseActual.getAtributos().get(met.get_Lexema()))!=null)
			throw new ErrorSemantico("El Metodo tiene el mismo nombre que un Atributo",met);
	}
}










