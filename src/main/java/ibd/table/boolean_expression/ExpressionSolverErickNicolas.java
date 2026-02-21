package ibd.table.boolean_expression;

import java.util.ArrayList;
import java.util.List;

import ibd.table.ComparisonTypes;
import ibd.table.prototype.DataRow;
import ibd.table.prototype.column.IntegerColumn;
import ibd.table.prototype.column.StringColumn;

//@Override

public class ExpressionSolverErickNicolas implements ExpressionSolver{
	public boolean solve(Expression exp, DataRow row) {
		
		
		if (exp instanceof SingleExpression) {
			SingleExpression se = (SingleExpression)exp;
			switch(se.comparisonType) {
				case 1:
					if(se.colName == "id") {
						int id = row.getInt("id");
						int valor = (int) se.value;
						if(id == valor)
							return true;
						else
							return false;
					}
					if(se.colName == "year") {
						int ano = row.getInt("year");
						int valor = (int) se.value;
						if(ano == valor)
							return true;
						else
							return false;
					}
					if(se.colName == "title") {
						String titulo = row.getString("title");
						String valor = (String) se.value;
						if(titulo.equals(valor))
							return true;
						else
							return false;
					}
					if(se.colName == "genre") {
						String genero = row.getString("genre");
						String valor = (String) se.value;
						if(genero.equals(valor))
							return true;
						else
							return false;
					}
					if(se.colName == "cost") {
						int custo = row.getInt("cost");
						int valor = (int) se.value;
						if(custo == valor)
							return true;
						else
							return false;
					}
					break;
				case 2:
					if(se.colName == "id") {
						int id = row.getInt("id");
						int valor = (int) se.value;
						if(id != valor)
							return true;
						else
							return false;
					}
					if(se.colName == "year") {
						int ano = row.getInt("year");
						int valor = (int)se.value;
						if(ano != valor)
							return true;
						else
							return false;
					}
					if(se.colName == "title") {
						String titulo = row.getString("title");
						String valor = (String) se.value;
						if(titulo != valor)
							return true;
						else
							return false;
					}
					if(se.colName == "genre") {
						String genero = row.getString("genre");
						String valor = (String) se.value;
						if(genero != valor)
							return true;
						else
							return false;
					}
					if(se.colName == "cost") {
						int custo = row.getInt("cost");
						int valor = (int) se.value;
						if(custo != valor)
							return true;
						else
							return false;
					}
					break;
				case 3:
					if(se.colName == "id") {
						int id = row.getInt("id");
						int valor = (int) se.value;
						if(id > valor)
							return true;
						else
							return false;
					}
					if(se.colName == "year") {
						int ano = row.getInt("year");
						int valor = (int)se.value;
						if(ano > valor)
							return true;
						else
							return false;
					}
					
					if(se.colName == "cost") {
						int custo = row.getInt("cost");
						int valor = (int) se.value;
						if(custo > valor)
							return true;
						else
							return false;
					}
					break;
				case 4:
					if(se.colName == "id") {
						int id = row.getInt("id");
						int valor = (int) se.value;
						if(id >= valor)
							return true;
						else
							return false;
					}
					if(se.colName == "year") {
						int ano = row.getInt("year");
						int valor = (int)se.value;
						if(ano >= valor)
							return true;
						else
							return false;
					}
					
					if(se.colName == "cost") {
						int custo = row.getInt("cost");
						int valor = (int) se.value;
						if(custo >= valor)
							return true;
						else
							return false;
					}
					break;
				case 5:
					if(se.colName == "id") {
						int id = row.getInt("id");
						int valor = (int) se.value;
						if(id < valor)
							return true;
						else
							return false;
					}
					if(se.colName == "year") {
						int ano = row.getInt("year");
						int valor = (int)se.value;
						if(ano < valor)
							return true;
						else
							return false;
					}
				
					if(se.colName == "cost") {
						int custo = row.getInt("cost");
						int valor = (int) se.value;
						if(custo < valor)
							return true;
						else
							return false;
					}
					break;
				case 6:
					if(se.colName == "id") {
						int id = row.getInt("id");
						int valor = (int) se.value;
						if(id <= valor)
							return true;
						else
							return false;
					}
					if(se.colName == "year") {
						int ano = row.getInt("year");
						int valor = (int)se.value;
						if(ano <= valor)
							return true;
						else
							return false;
					}
					
					if(se.colName == "cost") {
						int custo = row.getInt("cost");
						int valor = (int) se.value;
						if(custo <= valor)
							return true;
						else
							return false;
					}
					break;
				
			}
			
		}
		
		

		
		
		else if(exp instanceof CompositeExpression) {
			 int x = 5;
	 		 int y = 3;
			 CompositeExpression ce = (CompositeExpression) exp;
			 switch(ce.boolean_conector) {
			 	 case 0:
			 		 List<Expression> ex = ce.expressions;
			 		 for(Expression se1 : ex) {
			 			SingleExpression se = (SingleExpression) se1;
			 			switch(se.comparisonType) {
			 				case 1:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id == valor)
										x = 0;
									else 
										x = 1;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int) se.value;
									if(ano == valor) 
										x = 0;
									else 
										x = 1;
									
								}
								else if(se.colName == "title") {
									String titulo = row.getString("title");
									String valor = (String) se.value;
									if(titulo.equals(valor))
										x = 0;
									else 
										x = 1;
									
									
								}
								else if(se.colName == "genre") {
									String genero = row.getString("genre");
									String valor = (String) se.value;
									if(genero.equals(valor))
										x = 0;
									else 
										x = 1;
									
									
								}
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo == valor)
										x = 0;
									else 
										x = 1;
									
								}
								break;
							case 2:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id != valor)
										x = 0;
									else 
										x = 1;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int)se.value;
									if(ano != valor)
										x = 0;
									else 
										x = 1;
									
								}
								else if(se.colName == "title") {
									String titulo = row.getString("title");
									String valor = (String) se.value;
									if(titulo != valor)
										x = 0;
									else 
										x = 1;
									
								}
								else if(se.colName == "genre") {
									String genero = row.getString("genre");
									String valor = (String) se.value;
									if(genero != valor)
										x = 0;
									else 
										x = 1;
									
								}
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo != valor)
										x = 0;
									else 
										x = 1;
									
								}
								break;
							case 3:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id > valor)
										x = 0;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int)se.value;
									if(ano > valor)
										x = 0;
									
								}
								/*if(se.colName == "title") {
									String titulo = row.getString("title");
									String valor = (String) se.value;
									if(titulo != valor)
										return true;
									else
										return false;
								}
								if(se.colName == "genre") {
									String genero = row.getString("genre");
									String valor = (String) se.value;
									if(genero != valor)
										return true;
									else
										return false;
								}*/
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo > valor)
										x = 0;
									else 
										x = 1;
									
								}
								break;
							case 4:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id >= valor)
										x = 0;
									else 
										x = 1;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int)se.value;
									if(ano >= valor)
										x = 0;
									else 
										x = 1;
									
								}
								
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo >= valor)
										x = 0;
									else 
										x = 1;
									
								}
								break;
							case 5:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id < valor)
										x = 0;
									else 
										x = 1;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int)se.value;
									if(ano < valor)
										x = 0;
									else 
										x = 1;
									
								}
								/*if(se.colName == "title") {
									String titulo = row.getString("title");
									String valor = (String) se.value;
									if(titulo != valor)
										return true;
									else
										return false;
								}
								if(se.colName == "genre") {
									String genero = row.getString("genre");
									String valor = (String) se.value;
									if(genero != valor)
										return true;
									else
										return false;
								}*/
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo < valor)
										x = 0;
									else 
										x = 1;
									
								}
								break;
							case 6:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id <= valor)
										x = 0;
									else 
										x = 1;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int)se.value;
									if(ano <= valor)
										x = 0;
									else 
										x = 1;
									
								}
								
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo <= valor)
										x = 0;
									else 
										x = 1;
									
								}
								break;
							
						}
			 			
				 		
				 			
			 		 }
			 		 if(x == 0)
			 			 return true;
			 		 break;
			 	 case 1:

			 		 List<Expression> ex2 = ce.expressions;
			 		
			 		 for(Expression se1 : ex2) {
			 			SingleExpression se = (SingleExpression) se1;
			 			switch(se.comparisonType) {
			 				case 1:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id == valor)
										x = 0;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int) se.value;
									if(ano == valor)
										x = 0;
								}
								else if(se.colName == "title") {
									String titulo = row.getString("title");
									String valor = (String) se.value;
									if(titulo.equals(valor))
										x = 0;
									
								}
								else if(se.colName == "genre") {
									String genero = row.getString("genre");
									String valor = (String) se.value;
									if(genero.equals(valor))
										x = 0;
									
								}
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo == valor)
										x = 0;
									
								}
								break;
							case 2:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id != valor)
										x = 0;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int)se.value;
									if(ano != valor)
										x = 0;
									
								}
								else if(se.colName == "title") {
									String titulo = row.getString("title");
									String valor = (String) se.value;
									if(titulo != valor)
										x = 0;
									
								}
								else if(se.colName == "genre") {
									String genero = row.getString("genre");
									String valor = (String) se.value;
									if(genero != valor)
										x = 0;
									
								}
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo != valor)
										x = 0;
									
								}
								break;
							case 3:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id > valor)
										x = 0;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int)se.value;
									if(ano > valor)
										x = 0;
									
								}
						
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo > valor)
										x = 0;
									
								}
								break;
							case 4:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id >= valor)
										x = 0;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int)se.value;
									if(ano >= valor)
										x = 0;
									
								}
							
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo >= valor)
										x = 0;
									
								}
								break;
							case 5:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id < valor)
										x = 0;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int)se.value;
									if(ano < valor)
										x = 0;
									
								}
								
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo < valor)
										x = 0;
									
								}
								break;
							case 6:
								if(se.colName == "id") {
									int id = row.getInt("id");
									int valor = (int) se.value;
									if(id <= valor)
										x = 0;
									
								}
								else if(se.colName == "year") {
									int ano = row.getInt("year");
									int valor = (int)se.value;
									if(ano <= valor)
										x = 0;
									
								}
							
								else if(se.colName == "cost") {
									int custo = row.getInt("cost");
									int valor = (int) se.value;
									if(custo <= valor)
										x = 0;
									
								}
								break;
							
						}
			 			
				 		for(Expression se2 : ex2) {
					 		SingleExpression se3 = (SingleExpression) se2; 
					 		
				 			switch(se3.comparisonType) {
				 				case 1:
									if(se.colName == "id") {
										int id = row.getInt("id");
										int valor = (int) se.value;
										if(id == valor)
											y = 0;
										
									}
									else if(se.colName == "year") {
										int ano = row.getInt("year");
										int valor = (int) se.value;
										if(ano == valor)
											y = 8;
										
									}
									else if(se.colName == "title") {
										String titulo = row.getString("title");
										String valor = (String) se.value;
										if(titulo.equals(valor))
											y = 0;
										
									}
									else if(se.colName == "genre") {
										String genero = row.getString("genre");
										String valor = (String) se.value;
										if(genero.equals(valor))
											y = 0;
										
									}
									else if(se.colName == "cost") {
										int custo = row.getInt("cost");
										int valor = (int) se.value;
										if(custo == valor)
											y = 0;
										
									}
									break;
								case 2:
									if(se.colName == "id") {
										int id = row.getInt("id");
										int valor = (int) se.value;
										if(id != valor)
											y = 0;
										
									}
									else if(se.colName == "year") {
										int ano = row.getInt("year");
										int valor = (int)se.value;
										if(ano != valor)
											y = 0;
										
									}
									else if(se.colName == "title") {
										String titulo = row.getString("title");
										String valor = (String) se.value;
										if(titulo != valor)
											y = 0;
										
									}
									else if(se.colName == "genre") {
										String genero = row.getString("genre");
										String valor = (String) se.value;
										if(genero != valor)
											y = 0;
										
									}
									else if(se.colName == "cost") {
										int custo = row.getInt("cost");
										int valor = (int) se.value;
										if(custo != valor)
											y = 0;
										
									}
									break;
								case 3:
									if(se.colName == "id") {
										int id = row.getInt("id");
										int valor = (int) se.value;
										if(id > valor)
											y = 0;
										
									}
									else if(se.colName == "year") {
										int ano = row.getInt("year");
										int valor = (int)se.value;
										if(ano > valor)
											y = 0;
										
									}
								
									else if(se.colName == "cost") {
										int custo = row.getInt("cost");
										int valor = (int) se.value;
										if(custo > valor)
											y = 0;
										
									}
									break;
								case 4:
									if(se.colName == "id") {
										int id = row.getInt("id");
										int valor = (int) se.value;
										if(id >= valor)
											y = 0;
										
									}
									else if(se.colName == "year") {
										int ano = row.getInt("year");
										int valor = (int)se.value;
										if(ano >= valor)
											y = 0;
										
									}
									
									else if(se.colName == "cost") {
										int custo = row.getInt("cost");
										int valor = (int) se.value;
										if(custo >= valor)
											y = 0;
										
									}
									break;
								case 5:
									if(se.colName == "id") {
										int id = row.getInt("id");
										int valor = (int) se.value;
										if(id < valor)
											y = 0;
										
									}
									else if(se.colName == "year") {
										int ano = row.getInt("year");
										int valor = (int)se.value;
										if(ano < valor)
											y = 0;
										
									}
								
									else if(se.colName == "cost") {
										int custo = row.getInt("cost");
										int valor = (int) se.value;
										if(custo < valor)
											y = 0;
										
									}
									break;
								case 6:
									if(se.colName == "id") {
										int id = row.getInt("id");
										int valor = (int) se.value;
										if(id <= valor)
											y = 0;
										
									}
									else if(se.colName == "year") {
										int ano = row.getInt("year");
										int valor = (int)se.value;
										if(ano <= valor)
											y = 0;
										
									}
								
									else if(se.colName == "cost") {
										int custo = row.getInt("cost");
										int valor = (int) se.value;
										if(custo <= valor)
											y = 0;
										
									}
									break;
								
							}
				 			if(x == 0 || y == 0)
				 				return true;
					 			 
					 }
				 }
			 		 break;
			 }
			
		}
		
	    return false;    
	}
}
