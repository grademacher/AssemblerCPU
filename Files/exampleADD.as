.wordsize 32
.regcnt 10
.maxmem 0x300


main:

ADDI X1, X1, #100
LDUR X2, [X0, #64]
ADD X3, X1, X2
STUR X3, [X0, #104]





HALT

.pos 0x0100
.single 0x0AB