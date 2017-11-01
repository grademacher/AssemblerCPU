.wordsize 32
.regcnt 10
.maxmem 0x200


main:

ADDI X0, X0, #10

sub:

SUBI X0, X0, #1

CBNZ X0, sub


HALT

data:
.double 0x0AB     ; place 0xAB in a 8-byte location
.single 0x0AB     ; place 0xAB in a 4-byte location
.half   0x0AB     ; place 0xAB in a 2-byte location
.align 8
.byte   0x0AB
.pos 0x0100
.byte 0x0AB

stack:
