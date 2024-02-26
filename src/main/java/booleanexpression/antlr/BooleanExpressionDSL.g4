grammar BooleanExpressionDSL;

command: expression EOF;

expression: '(' expression ')'
            | '!' expression
            | atomic
            | expression OR expression
            | expression AND expression;

atomic: value REL_OP value;

value: DECIMAL | INTEGER | STRING | IDENTIFIER;

REL_OP: '==' | '=' | '!=' | '≠' | '<=' | '≤' | '>=' | '≥' | '>' | '<' | 'is' | 'is not';

OR: '||' | 'OR' | 'Or' | 'oR' | 'or';
AND: '&&' | 'AND' | 'And' | 'AnD' | 'ANd' | 'aND' | 'aNd' | 'anD' | 'and';

DECIMAL: DIGIT+ '.' DIGIT*;
INTEGER: DIGIT+;
IDENTIFIER: (LETTER | '_') (LETTER | DIGIT | '_')* ('.' (LETTER | '_') (LETTER | DIGIT | '_')*)?;
LETTER: [a-zA-Z]+;
STRING: '\'' (~'\'' | '\r' | '\n')* '\'';
DIGIT: [0-9];

WS: [ \t\r\n]+ -> skip;