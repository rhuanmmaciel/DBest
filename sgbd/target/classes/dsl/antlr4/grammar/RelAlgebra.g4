grammar RelAlgebra;

command: (importStatement | expression | createTable | variableDeclaration) ';' ((importStatement | expression | createTable | variableDeclaration) ';')*;

importStatement: IMPORT pathStatement (nameDeclaration)?;

nameDeclaration: AS RELATION;

pathStatement: (PATH | (THIS RELATION)) '.head';

variableDeclaration: RELATION '=' expression;

createTable: RELATION position?;

expression: (selection | projection | join | leftJoin | rightJoin | cartesianProduct | union | intersection | sort | group) position?;

position: '<' number ',' number '>';

number: DIGIT+;

selection: SELECTION PREDICATE '(' relation ')';
projection: PROJECTION PREDICATE '(' relation ')';
join: JOIN PREDICATE '(' relation ',' relation ')';
leftJoin: LEFTJOIN PREDICATE '(' relation ',' relation ')';
rightJoin: RIGHTJOIN PREDICATE '(' relation ',' relation ')';
cartesianProduct: CARTESIANPRODUCT '(' relation  ',' relation ')';
union: UNION '(' relation ',' relation ')';
intersection: INTERSECTION '(' relation ',' relation ')';
sort: SORT PREDICATE '(' relation ')';
group: GROUP PREDICATE '(' relation ')';
relation: RELATION position? #simple | expression #nested;

SELECTION: 'selection';
PROJECTION: 'projection';
JOIN: 'join';
LEFTJOIN: 'leftJoin';
RIGHTJOIN: 'rightJoin';
UNION: 'union';
CARTESIANPRODUCT: 'cartesianProduct';
INTERSECTION: 'intersection';
SORT: 'sort';
GROUP: 'group';

IMPORT: 'import';
AS: 'as';
RELATION: [a-zA-Z] [a-zA-Z0-9_]*;
PREDICATE: '[' .*? ']';
DIGIT: [0-9];
PATH: '/' [a-zA-Z0-9_/]* [a-zA-Z0-9_];
THIS: 'this.';
WS: [ \t\r\n]+ -> skip;
