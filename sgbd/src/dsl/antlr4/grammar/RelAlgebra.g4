grammar RelAlgebra;

expressions: expression ( ';' expression )* ';';

expression:	selection | projection | join | cartesian ;

selection: SELECTION PREDICATE '(' relation ')';

projection:	PROJECTION PREDICATE '(' relation ')';

join: JOIN PREDICATE '(' relation ',' relation ')';

cartesian:CARTESIAN '(' relation ( ',' relation	)* ')';

relation: RELATION #simple | expression #nested;

SELECTION: S E L E C T I O N;

PROJECTION:	P R O J E C T I O N;

JOIN: J O I N;

CARTESIAN: C A R T E S I A N;

ATTRIBUTE: '\''.*?'\'';

PREDICATE: '[' .*? ']';

RELATION: [a-zA-Z] [a-zA-Z0-9_]*;

WS:	[ \t\r\n]+ -> skip;

fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');