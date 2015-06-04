package edu.common.dynamicextensions.query;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.common.dynamicextensions.domain.nui.Control;
import edu.common.dynamicextensions.domain.nui.DataType;
import edu.common.dynamicextensions.domain.nui.FileUploadControl;
import edu.common.dynamicextensions.domain.nui.MultiSelectControl;
import edu.common.dynamicextensions.query.ast.ArithExpressionNode;
import edu.common.dynamicextensions.query.ast.ArithExpressionNode.ArithOp;
import edu.common.dynamicextensions.query.ast.CurrentDateNode;
import edu.common.dynamicextensions.query.ast.DateDiffFuncNode;
import edu.common.dynamicextensions.query.ast.DateIntervalNode;
import edu.common.dynamicextensions.query.ast.ExpressionNode;
import edu.common.dynamicextensions.query.ast.FieldNode;
import edu.common.dynamicextensions.query.ast.FilterExpressionNode;
import edu.common.dynamicextensions.query.ast.FilterNode;
import edu.common.dynamicextensions.query.ast.FilterNode.RelationalOp;
import edu.common.dynamicextensions.query.ast.LiteralValueListNode;
import edu.common.dynamicextensions.query.ast.LiteralValueNode;
import edu.common.dynamicextensions.query.ast.Node;
import edu.common.dynamicextensions.query.ast.QueryExpressionNode;
import edu.common.dynamicextensions.query.ast.SelectListNode;

public class QueryGenerator {
	
	private static String LIMIT_QUERY = "select * from (select rownum rnum, tab.* from (%s) tab where rownum < %d) where rnum >= %d";
	
	private boolean wideRowSupport;
	
	private boolean ic;

    public QueryGenerator() {
    }
    
    public QueryGenerator(boolean wideRowSupport, boolean ic) {
    	this.wideRowSupport = wideRowSupport;
    	this.ic = ic;
    }
   
    public String getCountSql(QueryExpressionNode queryExpr, JoinTree joinTree) {
    	StringBuilder countSql = new StringBuilder();
    	
    	if (wideRowSupport) {
        	String fromClause  = buildFromClause(joinTree);
        	String whereClause = buildWhereClause(queryExpr.getFilterExpr());
    		
        	String alias = joinTree.getAlias();
        	String pk = joinTree.getForm().getPrimaryKey();
    		countSql.append("select count(distinct ")
    			.append(alias).append(".").append(pk).append(")")
    			.append(" from ").append(fromClause)
    			.append(" where ").append(whereClause);    			    		
    	} else {
    		String dataSql = getDataSql(queryExpr, joinTree);
    		countSql.append("select count(*) from (").append(dataSql).append(")");    		
    	}
    	
    	return countSql.toString();
    }

    public String getDataSql(QueryExpressionNode queryExpr, JoinTree joinTree) {
    	String selectClause = buildSelectClause(queryExpr.getSelectList(), joinTree);
        String fromClause  = buildFromClause(joinTree);
        String whereClause = buildWhereClause(queryExpr.getFilterExpr());
        
        String sql = new StringBuilder("select ").append(selectClause)
        	.append(" from ").append(fromClause)
        	.append(" where ").append(whereClause)
        	.toString();        
        return !wideRowSupport ? sql : addOrderBy(sql, joinTree);
    }

    public String getDataSql(QueryExpressionNode queryExpr, JoinTree joinTree, int start, int numRows) {
        String dataSql = getDataSql(queryExpr, joinTree);

        String result = null;
        if(start == 0 && numRows == 0) {
            result = dataSql;          
        } else {
        	String orderedQuery = dataSql;
        	if (!wideRowSupport) {
        		orderedQuery = addOrderBy(dataSql, joinTree);
        	}
            result = String.format(LIMIT_QUERY, orderedQuery, start + numRows, start);
        }
        
        return result;
    }
    
    private String buildSelectClause(SelectListNode selectList, JoinTree joinTree) {
    	StringBuilder select = new StringBuilder();
    	for (ExpressionNode element : selectList.getElements()) { 
    		select.append(getExpressionNodeSql(element, element.getType())).append(", ");
    	}
    	
    	if (select.length() == 0) {
    		select.append("*"); 
    	} else {
    		if (wideRowSupport) {
    			addWideRowMarkerCols(select, selectList, joinTree);    			
    		}
    		
    		select.delete(select.length() - 2, select.length());
    	}    	
    	
    	return select.toString();
    }
    
    private void addWideRowMarkerCols(StringBuilder select, SelectListNode selectList, JoinTree joinTree) {
    	Set<String> wideRowMarkerColumns = new LinkedHashSet<String>();
    	
    	String alias = joinTree.getAlias();
    	String pk = joinTree.getForm().getPrimaryKey();    	
    	wideRowMarkerColumns.add(getWideRowMarkerColumn(alias, pk));
    	
    	for (ExpressionNode element : selectList.getElements()) {
    		if (element instanceof FieldNode) {
    			FieldNode field = (FieldNode)element;
    			Control ctrl = field.getCtrl();
    			if (ctrl instanceof MultiSelectControl) {
    				continue;
    			}
    			
    			alias = field.getTabAlias();
    			pk = ctrl.getContainer().getPrimaryKey();
    			wideRowMarkerColumns.add(getWideRowMarkerColumn(alias, pk));
    		} else {
    			String[] aliasPk = WideRowUtil.getTabAliasPk(joinTree, element);
    			if (aliasPk != null) {
    				wideRowMarkerColumns.add(getWideRowMarkerColumn(aliasPk[0], aliasPk[1]));
    			}
    		}
    	}
    	
    	for (String column : wideRowMarkerColumns) {
    		select.append(column).append(", ");
    	}    	
    }
    
    private String getWideRowMarkerColumn(String alias, String primaryKey) {
    	return "'" + alias + "', " + alias + "." + primaryKey;
    }
    
    private String buildCountClause(JoinTree joinTree) {
    	StringBuilder countClause = new StringBuilder("count(");   	
    	return countClause.append(wideRowSupport ? "distinct " : "")
    		.append(joinTree.getForm().getPrimaryKey())
    		.append(")").toString();    	     
    }
    
    private String buildFromClause(JoinTree joinTree) {
        StringBuilder from = new StringBuilder();
        JoinTree parentTree = joinTree.getParent();
        
        if(parentTree != null) {
        	from.append(" left join ");
        }        
        from.append(joinTree.getTab()).append(" ").append(joinTree.getAlias()).append(" ");
        
        if(parentTree != null) {
            from.append(" on ").append(joinTree.getAlias()).append(".").append(joinTree.getForeignKey())
                .append(" = ").append(parentTree.getAlias()).append(".").append(joinTree.getParentKey());
        }
        
        for (JoinTree child : joinTree.getChildren()) {
        	from.append(buildFromClause(child));
        }

        return from.toString();
    }

    private String buildWhereClause(Node root) {
        String exprStr = null;
        
        if (root instanceof FilterNode) {
            return buildFilter((FilterNode)root);
        } 
        
        FilterExpressionNode expr = (FilterExpressionNode)root;
        String lhs = buildWhereClause(expr.getOperands().get(0));
        String rhs = null;
            
        switch (expr.getOperator()) {
          	case AND:
           	case OR:
           		rhs = buildWhereClause(expr.getOperands().get(1));
           		exprStr = new StringBuilder(lhs).append(" ")
           				.append(expr.getOperator()).append(" ")
           				.append(rhs).toString();
                break;
                    
            case NOT:
            	exprStr = new StringBuilder("NOT (").append(lhs).append(")").toString();
            	break;
            		
            case PARENTHESIS:
            	exprStr = new StringBuilder("(").append(lhs).append(")").toString();
            	break;
            		
            case IDENTITY:
            	exprStr = lhs;
            	break;
            	
            case PAND:
           		rhs = buildWhereClause(expr.getOperands().get(1));
           		exprStr = new StringBuilder(lhs).append(" ")
           				.append(" AND ")
           				.append(rhs).toString();            	
            	break;
        }

        return exprStr;
    }
    
    private String addOrderBy(String dataSql, JoinTree joinTree) {
        return new StringBuilder(dataSql)
        	.append(" order by ")
        	.append(joinTree.getAlias()).append(".").append(joinTree.getForm().getPrimaryKey())
        	.toString();	
    }

    private String buildFilter(FilterNode filter) {
    	if (!isValidFilter(filter)) {
    		throw new RuntimeException("Invalid filter"); // add more info here
    	}
    	
        String filterExpr = null, rhs = null;        
        String lhs = getExpressionNodeSql(filter.getLhs(), filter.getLhs().getType());        
        switch (filter.getRelOp()) {
            case STARTS_WITH:
            case ENDS_WITH:
            case CONTAINS:
            	rhs = getStringMatchSql((LiteralValueNode)filter.getRhs(), filter.getRelOp());
            	if (ic) {
            		filterExpr = "upper(" + lhs + ") like " + rhs.toUpperCase();
            	} else {
            		filterExpr = lhs + " like " + rhs;
            	}            	
            	break;
            	
            case EXISTS:
            	filterExpr = lhs + " is not null ";
            	break;
            	
            case NOT_EXISTS:
            	filterExpr = lhs + " is null ";
            	break;
            	            	
            default:
            	rhs = getExpressionNodeSql(filter.getRhs(), filter.getLhs().getType());            	
            	if (filter.getLhs().isString() && ic) {
            		lhs = "upper (" + lhs + ")";
            		if (filter.getRhs() instanceof FieldNode) {
            			rhs = "upper (" + rhs + ")";
            		} else {
            			rhs = rhs.toUpperCase();
            		}
            	}            	
            	filterExpr = lhs + " " + filter.getRelOp().symbol() + " " + rhs;
            	break;            	
        }
        
        return filterExpr;
    }
    
    private boolean isValidFilter(FilterNode filter) {
    	ExpressionNode lhs = filter.getLhs(), rhs = filter.getRhs();
    	boolean isValid = false;
    	if (rhs == null) { // unary operators like exists and not exists
    		isValid = true;
    	} else if (lhs.isString() && rhs.isString()) {
    		isValid = true;
    	} else if (lhs.isDate() && (rhs.isString() || rhs.isDateInterval() || rhs.isDate() || rhs.isNumber())) {
    		isValid = true;
    	} else if (lhs.isString() && (rhs.isDate() || rhs.isDateInterval() || rhs.isNumber())) {
    		isValid = true;
        } else if (lhs.isNumber() && rhs.isNumber()) {
    		isValid = true;
    	} else if (lhs.isDateInterval() && rhs.isNumber()) {
    		isValid = true;
    	} 
    	
    	return isValid;
    }

    private boolean isValidOp(ExpressionNode lhs, RelationalOp op, ExpressionNode rhs) {
    	// check validity of {lhs.getType(), op, rhs.getType()} or {lhs.getType(), op}    	
    	return true;    	
    }

    private String removeQuotes(String value) {
        if (value != null && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
            value = value.substring(1, value.length() - 1);
        }
        
        return value;
    }
    
    private String getExpressionNodeSql(ExpressionNode exprNode, DataType type) {
    	String result = "";
    	
    	if (exprNode instanceof FieldNode) {
    		result = getFieldNodeSql((FieldNode)exprNode);
    	} else if (exprNode instanceof LiteralValueNode) {
    		result = getLiteralValueNodeSql((LiteralValueNode)exprNode, type);
    	} else if (exprNode instanceof LiteralValueListNode) {
    		LiteralValueListNode literalNodes = (LiteralValueListNode)exprNode;
    		StringBuilder literals = new StringBuilder();
    		for (LiteralValueNode literalNode : literalNodes.getLiteralVals()) {
    			if (literals.length() != 0) {
    				literals.append(", ");
    			}
    			literals.append(getLiteralValueNodeSql(literalNode, type));
    		}
    		
    		result = "(" + literals.toString() + ")";    		    		
    	} else if (exprNode instanceof ArithExpressionNode) {
    		result = getArithExpressionNodeSql((ArithExpressionNode)exprNode);    		
    	} else if (exprNode instanceof DateDiffFuncNode) {
    		result = getDateDiffFuncNodeSql((DateDiffFuncNode)exprNode);
    	} else if (exprNode instanceof CurrentDateNode) {
    		result = "sysdate";
    	}
    	
    	return result;
    }
        
    private String getFieldNodeSql(FieldNode field) {
    	String column = field.getCtrl().getDbColumnName();
    	if (field.getCtrl() instanceof FileUploadControl) {
    		column += "_NAME";
    	}
    	
    	return field.getTabAlias() + "." + column;
    }
    
    private String getLiteralValueNodeSql(LiteralValueNode value, DataType coercionType) {
    	String result = "";
    	
        switch (value.getType()) {
    		case STRING:
    			result = "'" + removeQuotes(value.getValues().get(0).toString()) + "'";        			
    			if (coercionType == DataType.DATE) {
    				result = "to_date(" + result + ", 'MM-DD-YYYY')";
    			}        		
    			break;
    			
    		case BOOLEAN:
    			result = ((Boolean)value.getValues().get(0)) ? "1" : "0";
    			break;
    		
    		default:
    			result = value.getValues().get(0).toString();
    			break;
        }
    	
        return result;
    }
    
    private String getStringMatchSql(LiteralValueNode stringNode, RelationalOp op) {
    	String prefix = "", suffix = "";
    	
    	switch (op) {
    	    case CONTAINS:
    	    	prefix = suffix = "%";
    	    	break;
    	    	
    	    case STARTS_WITH:
    	    	suffix = "%";
    	    	break;
    	    	
    	    case ENDS_WITH:
    	    	prefix = "%";
    	    	break;
    	    	
    	    default:
    	    	break;
    	}
    	
    	String value = removeQuotes(stringNode.getValues().get(0).toString());
    	return "'" + prefix + value + suffix + "'";
    }
        
    private String getArithExpressionNodeSql(ArithExpressionNode arithExpr) {    	
    	String expr = "";
    	
    	String loperand = getExpressionNodeSql(arithExpr.getLeftOperand(),  arithExpr.getLeftOperandCoercion());
		if (arithExpr.getLeftOperand().isDate() && arithExpr.getRightOperand().isDateInterval()) {
			DateIntervalNode di = (DateIntervalNode)arithExpr.getRightOperand();
			int months = di.getYears() * 12 + di.getMonths();
			if (months == 0) {
				expr = loperand; 
			} else {
				if (arithExpr.getOp() == ArithOp.MINUS) {
					months = -months;    				    	
				}
				
				expr = "add_months(" + loperand + ", " + months + ")";
			}
			
			if (di.getDays() != 0) {
				expr += " " + arithExpr.getOp().symbol() + " " + di.getDays(); 
			}
		} else {
			String roperand = getExpressionNodeSql(arithExpr.getRightOperand(), arithExpr.getRightOperandCoercion());
			expr = loperand + " " + arithExpr.getOp().symbol() + " " + roperand;
		}
		
		
		return "(" + expr + ")";    	
    }
    
    private String getDateDiffFuncNodeSql(DateDiffFuncNode dateDiff) {
    	String result = "";
    	String loperand = getExpressionNodeSql(dateDiff.getLeftOperand(), DataType.DATE);
		String roperand = getExpressionNodeSql(dateDiff.getRightOperand(), DataType.DATE);
		
		switch (dateDiff.getDiffType()) {
			case YEAR:
				result = "(months_between(" + loperand + ", " + roperand + ") / 12)";
				break;
				
			case MONTH:
				result = "months_between(" + loperand + ", " + roperand + ")";
				break;
				
			case DAY:
				result = "(" + loperand + " - " + roperand + ")";
				break;    			
		}
    	    	
		return result;
    }
}