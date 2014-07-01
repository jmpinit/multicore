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

INC - 1 <target>
_op_: dest + 1 -> dest

DEC - 2 <target>
_op_: dest - 1 -> dest

ADD - 3 <dest>, <src>
_op_: dest + src -> dest

ADDI - 4 <dest>, <val>
_op_: dest + val -> dest

SUB - 5 <dest>, <src>
_op_: dest - src -> dest

SUBI - 6 <dest>, <val>
_op_: dest - val -> dest

MULT - 3 <dest>, <src>
_op_: dest * src -> dest

MULTI - 8 <dest>, <val>
_op_: dest * val -> dest

DIV - 9 <dest>, <src>
_op_: dest / src -> dest

DIVI - 10 <dest>, <val>
_op_: dest / val -> dest

AND - 11 <dest>, <src>
_op_: dest & src -> dest

ANDI - 12 <dest>, <val>
_op_: dest & val -> dest

OR - 13 <dest>, <src>
_op_: dest | src -> dest

ORI - 14 <dest>, <val>
_op_: dest | val -> dest

NOR - 15 <dest>, <src>
_op_: dest ^ src -> dest

NORI - 16 <dest>, <val>
_op_: dest ^ val -> dest

NOT - 17 <target>
_op_: ~target -> target

LSL - 18 <target>
_op_: target << 1 -> target

LSR - 19 <target>
_op_: target >> 1 -> target

## moving data

LDI - 20 <dest>, <val>
_op_: val -> dest

MOV - 21 <dest>, <src>
_op_: src -> dest

STO - 22 <destH>, <destL>, <src> 
_op_: src -> dest

GET - 23 <dest>, <srcH>, <srcL>
_op_: src -> dest

PUSH - 24 <src>
_op_: src -> stack; SP--

POP - 25 <dest>
_op_: SP++; stack -> dest

## program flow

GOTO - 26 <valH>, <valL>
_op_: val -> pc

RCALL - 27 <val>
_op_: pc -> stack; val -> pc

RET - 28
_op_: stack -> pc

SGR - 29 <src1>, <src2>
_op_: if src1 > src2 skip

SGRI - 30 <src>, <val>
_op_: if src > val skip

SLS - 31 <src1>, <src2>
_op_: if src1 < src2 skip

SLSI - 32 <src>, <val>
_op_: if src < val skip

SEQ - 33 <src1>, <src2>
_op_: if src1 == src2 skip

SEQI - 34 <src>, <val>
_op_: if src == val skip

SNE - 35 <src1>, <src2>
_op_: if src1 != src2 skip

SNEI - 36 <src>, <val>
_op_: if src != val skip

SCS - 37
_op_: if carry == 1 skip

SCC - 38
_op_: if carry == 0 skip
