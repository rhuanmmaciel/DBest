grammar RelAlgebra;

command: (importStatement | expression | createTable | variableDeclaration) ';' ((importStatement | expression | createTable | variableDeclaration) ';')*;

importStatement: IMPORT pathStatement (nameDeclaration)?;

nameDeclaration: AS RELATION;

pathStatement: (PATH | (THIS RELATION)) '.head';

variableDeclaration: RELATION '=' expression;

createTable: RELATION asOperator? position?;

expression: (selection | projection | selectColumns | join | leftJoin | rightJoin | cartesianProduct | union | intersection | sort |
 group | aggregation | rename | indexer) position?;

position: '<' number ',' number '>';

asOperator: ':' RELATION;

number: DIGIT+;

selection: 'selection' PREDICATE unary;
projection: 'projection' PREDICATE unary;
selectColumns: 'selectColumns' PREDICATE unary;
sort: 'sort' PREDICATE unary;
group: 'group' PREDICATE unary;
rename: 'rename' PREDICATE unary;
aggregation: 'aggregation' PREDICATE unary;
indexer: 'indexer' PREDICATE unary;

join: 'join' PREDICATE binary;
leftJoin: 'leftJoin' PREDICATE binary;
rightJoin: 'rightJoin' PREDICATE binary;
cartesianProduct: 'cartesianProduct' binary;
union: 'union' binary;
intersection: 'intersection' binary;

unary: '(' relation ')';
binary: '(' relation ',' relation ')';

relation: RELATION asOperator? position? #simple | expression #nested;

IMPORT: 'import';
AS: 'as' | 'As' | 'aS' | 'AS';
RELATION: [a-zA-Z] [a-zA-Z0-9_]*;
PREDICATE: '[' .*? ']';
DIGIT: [0-9];
PATH: '/' [a-zA-Z0-9_/]* [a-zA-Z0-9_];
THIS: 'this.';
WS: [ \t\r\n]+ -> skip;
