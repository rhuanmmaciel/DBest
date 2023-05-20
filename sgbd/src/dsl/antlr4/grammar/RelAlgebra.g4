grammar RelAlgebra;

expressions: expression ( ';' expression )* ';';

expression: (selection | projection | join | leftJoin | rightJoin | cartesianProduct | union) position? ;

position: '<' number ',' number '>';

number: DIGIT+;

selection: SELECTION PREDICATE '(' relation ')';

projection: PROJECTION PREDICATE '(' relation ')';

join: JOIN PREDICATE '(' relation ',' relation ')';

leftJoin: LEFTJOIN PREDICATE '(' relation ',' relation ')';

rightJoin: RIGHTJOIN PREDICATE '(' relation ',' relation ')';

cartesianProduct: CARTESIANPRODUCT '(' relation  ',' relation ')';

union: UNION PREDICATE '(' relation ',' relation ')';

relation: RELATION #simple | expression #nested;

SELECTION: S E L E C T I O N;

PROJECTION: P R O J E C T I O N;

JOIN: J O I N;

LEFTJOIN: L E F T J O I N;

RIGHTJOIN: R I G H T J O I N;

UNION: U N I O N;

CARTESIANPRODUCT: C A R T E S I A N P R O D U C T;

ATTRIBUTE: '\''.*?'\'';

PREDICATE: '[' .*? ']';

RELATION: [a-zA-Z] [a-zA-Z0-9_]*;

DIGIT: [0-9];

WS: [ \t\r\n]+ -> skip;

fragment A: ('a' | 'A');
fragment B: ('b' | 'B');
fragment C: ('c' | 'C');
fragment D: ('d' | 'D');
fragment E: ('e' | 'E');
fragment F: ('f' | 'F');
fragment G: ('g' | 'G');
fragment H: ('h' | 'H');
fragment I: ('i' | 'I');
fragment J: ('j' | 'J');
fragment K: ('k' | 'K');
fragment L: ('l' | 'L');
fragment M: ('m' | 'M');
fragment N: ('n' | 'N');
fragment O: ('o' | 'O');
fragment P: ('p' | 'P');
fragment Q: ('q' | 'Q');
fragment R: ('r' | 'R');
fragment S: ('s' | 'S');
fragment T: ('t' | 'T');
fragment U: ('u' | 'U');
fragment V: ('v' | 'V');
fragment W: ('w' | 'W');
fragment X: ('x' | 'X');
fragment Y: ('y' | 'Y');
fragment Z: ('z' | 'Z');
