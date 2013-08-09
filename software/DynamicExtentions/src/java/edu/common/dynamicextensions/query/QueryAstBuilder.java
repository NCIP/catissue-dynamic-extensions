package edu.common.dynamicextensions.query;

import org.antlr.v4.runtime.misc.NotNull;

import edu.common.dynamicextensions.query.Filter.RelationalOp;
import edu.common.dynamicextensions.query.antlr.AQLBaseVisitor;
import edu.common.dynamicextensions.query.antlr.AQLParser;

public class QueryAstBuilder extends AQLBaseVisitor<Node> {

    public QueryAstBuilder() {
    }

    @Override
    public Node visitNotExpr(@NotNull AQLParser.NotExprContext ctx) {
        return Expression.notExpr(visit(ctx.expr()));
    }

    @Override
    public Node visitOrExpr(@NotNull AQLParser.OrExprContext ctx) {
        return Expression.orExpr(visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    public Expression visitAndExpr(@NotNull AQLParser.AndExprContext ctx) {
        return Expression.andExpr(visit(ctx.expr(0)), visit(ctx.expr(1)));
    }

    public Expression visitParensExpr(@NotNull AQLParser.ParensExprContext ctx) {
        return Expression.parenExpr(visit(ctx.expr()));
    }

    public Node visitCond(@NotNull AQLParser.CondContext ctx) {
        String fieldName = ctx.FIELD().getText();
        String inputSymbol = ctx.OP().getText();
        String inputValue = null;
        
        if (ctx.SLITERAL() != null) {
        	inputValue = ctx.SLITERAL().getText();
        } else if (ctx.FLOAT() != null) {
        	inputValue = ctx.FLOAT().getText();
        } else if (ctx.INT() != null) {
        	inputValue = ctx.INT().getText();
        }
        
        Filter filter = new Filter();
        filter.setFieldName(fieldName);
        filter.setRelOp(RelationalOp.getBySymbol(inputSymbol));
        filter.getValues().add(inputValue);
        return filter;
    }
}