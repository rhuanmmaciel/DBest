package ibd.table.boolean_expression;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;
/**
 *
 * @author Alice Zeferino marques
 */
public class ExpressionSolverAlice implements ExpressionSolver{
    @Override
    //resolve uma cadeia de expressões usado recursão
    public boolean solve(Expression exp, DataRow row){
        //pattern matching via if. Pensei em usar switch pattern matching em vez (ficaria mais limpo) mas pelo que vi online parece uma feature um pouco nova? Decidi não arriscar dar algum erro estranho de compatibilidade.
        if (exp instanceof SingleExpression sExp){
            //final da recursão. usa match para resolver a comparação simples, a passa para cima
            return ComparisonTypes.match(row.getValue(sExp.colName), sExp.value, sExp.comparisonType);
        }
        else if (exp instanceof CompositeExpression cExp){
            // executa um AND ou OR (baseado no boolean_connector) das expressões componentes e passa acima.
            //porque o ArrayList expressions não tem tamanho limite, decidi implementar AND e OR para mais de dois elementos.
            //começa com um boolean constante (true para AND, false para OR) e verifica o resultado de cada expressão componente. Se algum deles for falso (para AND) ou true (para OR), o boolean é definido para esse mesmo valor. O estado final to boolean é retornado para cima.
            //em ouras palavras, X AND Y AND Z = (X AND Y) AND Z e X OR Y OR Z = (X OR Y) OR Z.
            if (cExp.boolean_conector == CompositeExpression.AND){
                boolean result = true;
                for (Expression iExp : cExp.expressions){
                   if (!solve(iExp, row)){
                       result = false;
                   }
                }
                return result;
            }
            else {
                boolean result = false;
                for (Expression iExp : cExp.expressions){
                   if (solve(iExp, row)){
                       result = true;
                   }
                }
                return result;
            }
            //código original que assumia exatamente dois elementos na lista
            /*if (cExp.boolean_conector == CompositeExpression.AND){
                return (solve(cExp.expressions.get(0), row) && solve(cExp.expressions.get(1), row));
            }
            else {
                return (solve(cExp.expressions.get(0), row) || solve(cExp.expressions.get(1), row));
            }*/
        }
        else if (exp instanceof NegationExpression nExp){
            //só troca o resultado da expressão a baixo, e passa para cima
            return !(solve(nExp.expression, row));
        }
        
        //failsafe case seja alguma expressão não reconhecida
        return false;
    }
}
