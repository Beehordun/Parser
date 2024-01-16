package org.example.AST;

public record ForLoopIterationStatementASTNode(
        String type,
        StatementASTNode init,
        ExpressionNode test,
        ExpressionNode update,
        StatementASTNode body
) implements StatementASTNode{
}
