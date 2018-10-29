		.DATA
idClase_Object:		DW "Object",0		; guardo el id de la clase
idClase_System:		DW "System",0		; guardo el id de la clase
idClase_A:		DW "A",0		; guardo el id de la clase
VT_A:		DW lm_Object_toString	;reservo espacio en la VT para el metodo toString
			DW lm_A_met	;reservo espacio en la VT para el metodo met
			
VT_Object:		DW lm_Object_toString	;reservo espacio en la VT para el metodo toString
			
VT_System:		DW lm_Object_toString	;reservo espacio en la VT para el metodo toString
			

		.CODE
		PUSH lm_A_main ; Cargo el main
		CALL		; Llamo al main
		HALT

		.DATA
		;Mensajes de Error
error_Div0:		DW "Error Division por 0 en (",0 
error_Null:		DW "Error NullPointerException en (",0 
error_Cast1:		DW "Error Casteo Erroneo en ",0 
error_Cast2:		DW "La expresion no es de tipo ",0 

		.CODE
		;Codigo de las rutinas que manejan las Excepciones

		;NullPointerExc_DivPor0(div/null,error,nf,nc)
chequear_Null_Div0:	NOP	
		LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
		LOAD 6		; leo el valor a comparar
		PUSH 0		; apilo el 0/null
		EQ			; comparo el valor con null o 0
		BT	mostrar_Null_Div0 
		STOREFP		; restauro FP
		RET 3		; vuelvo liberando nf,nc y error
mostrar_Null_Div0: NOP 
		PUSH 41		; cargo el )
		LOAD 3 		; leo el numero de columna
		PUSH 58		; cargo el :
		LOAD 4 		; leo el numero de fila
		LOAD 5		; leo el error
		SPRINT		; imprimo el error
		IPRINT		; imprimo el numero de fila
		CPRINT		; imprimo el :
		IPRINT		; imprimo el numero de columna
		CPRINT		; imprimo el )
		PRNLN		; imprimo el \n
		HALT

		;CastException(expresion,idClase,(nf:nc))
chequear_Cast:	NOP	
		LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
		LOAD 4		; cargo idClase
		LOAD 5		; cargo Expresion
		LOADREF 1 	; cargo id del CIR de la expresion 
		EQ			; comparo las ids
		BF	castErroneo 
		STOREFP		; restauro FP
		RET 2		; vuelvo liberando (n:m) e idClase 
castErroneo: NOP 
		LOAD 4 		; cargo id 
		PUSH error_Cast2	;cargo mensaje de error2
		LOAD 3		; cargo (n:m)
		PUSH error_Cast1	;cargo mensaje de error1
		SPRINT		; imprimo el error 1
		SPRINT		; imprimo el (n:m)
		PRNLN		; imprimo el \n
		SPRINT		; imprimo el error 2
		SPRINT		; imprimo el id Clase
		PRNLN		; imprimo el \n
		HALT


		.CODE

Ctor_A:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de Ctor_A
	;Fin  de Codigo de Ctor_A
		STOREFP		; restauro FP
		RET 1		; retorno liberando 1 variables

lm_A_main:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_A_main
		PUSH 0 ; Apilo un literal Entero
		PUSH lm_System_printIln 	; pongo la direccion del metodo
		CALL		; llamo al metodo System.printIln
		PUSH 2 ; Apilo un literal Entero
		PUSH 4 ; Apilo un literal Entero
		ADD ; Sumo los dos elementos en el tope de la pila
		PUSH lm_System_printIln 	; pongo la direccion del metodo
		CALL		; llamo al metodo System.printIln
		FMEM 0 		;Libero 0 Variables Locales 
	;Fin  de Codigo de A_main
		STOREFP		; restauro FP
		RET 0		; retorno liberando 0 variables

lm_A_met:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_A_met
		FMEM 0 		;Libero 0 Variables Locales 
	;Fin  de Codigo de A_met
		STOREFP		; restauro FP
		RET 1		; retorno liberando 1 variables
		.CODE

Ctor_Object:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de Ctor_Object
	;Fin  de Codigo de Ctor_Object
		STOREFP		; restauro FP
		RET 1		; retorno liberando 1 variables

lm_Object_toString:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_Object_toString
		LOAD 3		; cargo this
		LOADREF 1	; cargo idClase
		STORE 4		; escribo el retorno
	;Fin  de Codigo de Object.toString
		STOREFP		; restauro FP 
		RET 1		; retorno liberando this
		.CODE

Ctor_System:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de Ctor_System
	;Fin  de Codigo de Ctor_System
		STOREFP		; restauro FP
		RET 1		; retorno liberando 1 variables

lm_System_read:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_read
		READ		; leo el valor
		STORE 3		; lo guardo en el retorno
	;Fin  de Codigo de System.read
		STOREFP		; restauro FP 
		RET 0		; retorno liberando 0 variables

lm_System_printBln:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_printBln
		LOAD 3		; cargo el valor del 1er parametro
		BPRINT		; imprimo el tope  (0=false,1=true)
		PRNLN		; imprimo un \n
	;Fin  de Codigo de System.printBln
		STOREFP		; restauro FP 
		RET 1		; retorno liberando 1 variables

lm_System_printCln:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_printCln
		LOAD 3		; cargo el valor del 1er parametro
		CPRINT		; imprimo el c(tope)
		PRNLN		; imprimo un \n
	;Fin  de Codigo de System.printCln
		STOREFP		; restauro FP
		RET 1		; retorno liberando 1 variables

lm_System_printI:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_printI
		LOAD 3		; cargo el valor del 1er parametro
		IPRINT		; imprimo el tope
	;Fin  de Codigo de System.printI
		STOREFP		; restauro FP 
		RET 1		; retorno liberando 1 variables

lm_System_printIln:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_printIln
		LOAD 3		; cargo el valor del 1er parametro
		IPRINT		; imprimo el tope
		PRNLN		; imprimo un \n
	;Fin  de Codigo de System.printIln
		STOREFP		; restauro FP 
		RET 1		; retorno liberando 1 variables

lm_System_printC:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_printC
		LOAD 3		; cargo el valor del 1er parametro
		CPRINT		; imprimo el c(tope)
	;Fin  de Codigo de System.printC
		STOREFP		; restauro FP
		RET 1		; retorno liberando 1 variables

lm_System_println:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_println
        PRNLN        ; imprimo un \n
    ;Fin  de Codigo de System.println
        STOREFP        ; restauro FP
        RET 0        ; retorno liberando 0 variables

lm_System_printB:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_printB
		LOAD 3		; cargo el valor del 1er parametro
		BPRINT		; imprimo el tope (0=false,1=true)
	;Fin  de Codigo de System.printB
		STOREFP		; restauro FP 
		RET 1		; retorno liberando 1 variables

lm_System_printSln:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_printSln
		LOAD 3		; cargo el valor del 1er parametro
		SPRINT		; imprimo el tope
		PRNLN		; imprimo un \n
	;Fin  de Codigo de System.printSln
		STOREFP		; restauro FP 
		RET 1		; retorno liberando 1 variables

lm_System_readInt:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_readInt
 		PUSH 0			; inicializo en 0 el acumulador w
leer:	READ			; leo un digito d
		DUP				; duplico d
		PUSH 10			; apilo un //n
		EQ				; lo comparo
		BF convertir	; si d no es un //n lo convierto a int
		POP				; si es //n lo desapilo
		JUMP Salir		; salgo
convertir:	NOP
		PUSH 48			; cargo 48
		SUB				; obtengo el d en int en la pila
		SWAP			; pongo a w en la pila
		PUSH 10			; cargo 10
		MUL				; w = w*10
		ADD				; w = w +d
		JUMP leer		; leo el siguiente digito
salir:	STORE 3
	;Fin  de Codigo de System.readInt
		STOREFP		; restauro FP 
		RET 0		; retorno liberando 0 variables

lm_System_printS:	LOADFP		; almaceno el ED
		LOADSP		; almaceno el PR
		STOREFP		; seteo el FP actual
	;Inicio de Codigo de lm_System_printS
		LOAD 3		; cargo el valor del 1er parametro
		SPRINT		; imprimo el tope
	;Fin  de Codigo de System.printS
		STOREFP		; restauro FP 
		RET 1		; retorno liberando 1 variables


simple_heap_init:	RET 0	; Retorna inmediatamente
simple_malloc:	LOADFP	; Inicialización unidad	
		LOADSP	
		STOREFP ; Finaliza inicialización del RA	
		LOADHL	; hl
		DUP	; hl
		PUSH 1	; 1
		ADD	; hl+1
		STORE 4 ; Guarda el resultado (un puntero a la primer celda de la región de memoria)
	 	LOAD 3	; Carga la cantidad de celdas a alojar (parámetro que debe ser positivo)
		ADD
		STOREHL ; Mueve el heap limit (hl). Expande el heap
		STOREFP
		RET 1	; Retorna eliminando el parámetro
