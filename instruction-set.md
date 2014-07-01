# Multicore Instruction Set

## key

* __src/dest/target__ - pointers
* __val__ - immediate
* __H__ - indicates high byte in 16 bit number
* __L__ - indicates low byte in 16 bit number

## format

## instructions

NOP - 0

_op_: nothing

### math

INC - 1 [target]

_op_: dest + 1 into dest

DEC - 2 [target]

_op_: dest - 1 into dest

ADD - 3 [dest], [src]

_op_: dest + src into dest

ADDI - 4 [dest], [val]

_op_: dest + val into dest

SUB - 5 [dest], [src]

_op_: dest - src into dest

SUBI - 6 [dest], [val]

_op_: dest - val into dest

MULT - 3 [dest], [src]

_op_: dest * src into dest

MULTI - 8 [dest], [val]

_op_: dest * val into dest

DIV - 9 [dest], [src]

_op_: dest / src into dest

DIVI - 10 [dest], [val]

_op_: dest / val into dest

AND - 11 [dest], [src]

_op_: dest & src into dest

ANDI - 12 [dest], [val]

_op_: dest & val into dest

OR - 13 [dest], [src]

_op_: dest | src into dest

ORI - 14 [dest], [val]

_op_: dest | val into dest

NOR - 15 [dest], [src]

_op_: dest ^ src into dest

NORI - 16 [dest], [val]

_op_: dest ^ val into dest

NOT - 17 [target]

_op_: ~target into target

LSL - 18 [target]

_op_: target [[ 1 into target

LSR - 19 [target]

_op_: target ]] 1 into target

## moving data

LDI - 20 [dest], [val]

_op_: val into dest

MOV - 21 [dest], [src]

_op_: src into dest

STO - 22 [destH], [destL], [src] 

_op_: src into dest

GET - 23 [dest], [srcH], [srcL]

_op_: src into dest

PUSH - 24 [src]

_op_: src into stack; SP--

POP - 25 [dest]

_op_: SP++; stack into dest

## program flow

GOTO - 26 [valH], [valL]

_op_: val into pc

RCALL - 27 [val]

_op_: pc into stack; val into pc

RET - 28

_op_: stack into pc

SGR - 29 [src1], [src2]

_op_: if src1 ] src2 skip

SGRI - 30 [src], [val]

_op_: if src ] val skip

SLS - 31 [src1], [src2]

_op_: if src1 [ src2 skip

SLSI - 32 [src], [val]

_op_: if src [ val skip

SEQ - 33 [src1], [src2]

_op_: if src1 == src2 skip

SEQI - 34 [src], [val]

_op_: if src == val skip

SNE - 35 [src1], [src2]

_op_: if src1 != src2 skip

SNEI - 36 [src], [val]

_op_: if src != val skip

SCS - 37

_op_: if carry == 1 skip

SCC - 38

_op_: if carry == 0 skip
