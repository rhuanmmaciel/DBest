grammar RelAlgebra;

command: (importStatement | expression | createTable | variableDeclaration) ';' ((importStatement | expression | createTable | variableDeclaration) ';')*;

importStatement: IMPORT pathStatement (nameDeclaration)?;

nameDeclaration: AS RELATION;

pathStatement: (PATH | (THIS RELATION)) '.head';

variableDeclaration: RELATION '=' expression;

createTable: RELATION position?;

expression: (selection | projection | join | leftJoin | rightJoin | cartesianProduct | union | intersection | sort |
 group | aggregation | rename) position?;

position: '<' number ',' number '>';

number: DIGIT+;

selection: SELECTION PREDICATE unary;
projection: PROJECTION PREDICATE unary;
sort: SORT PREDICATE unary;
group: GROUP PREDICATE unary;
rename: RENAME PREDICATE unary;
aggregation: AGGREGATION PREDICATE unary;

join: JOIN PREDICATE binary;
leftJoin: LEFT_JOIN PREDICATE binary;
rightJoin: RIGHT_JOIN PREDICATE binary;
cartesianProduct: CARTESIAN_PRODUCT binary;
union: UNION binary;
intersection: INTERSECTION binary;

unary: '(' relation ')';
binary: '(' relation ',' relation ')';

relation: RELATION position? #simple | expression #nested;

SELECTION: 'selection';
PROJECTION: 'projection';
JOIN: 'join';
LEFT_JOIN: 'leftJoin';
RIGHT_JOIN: 'rightJoin';
UNION: 'union';
CARTESIAN_PRODUCT: 'cartesianProduct';
INTERSECTION: 'intersection';
SORT: 'sort';
GROUP: 'group';
AGGREGATION: 'aggregation';
RENAME: 'rename';

IMPORT: 'import';
AS: 'as';
RELATION: [a-zA-Z] [a-zA-Z0-9_]*;
PREDICATE: '[' .*? ']';
DIGIT: [0-9];
PATH: '/' [a-zA-Z0-9_/]* [a-zA-Z0-9_];
THIS: 'this.';
WS: [ \t\r\n]+ -> skip;
