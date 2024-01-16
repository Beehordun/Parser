package org.example.AST;

public record IterationStatementASTNode(
        String type,
        ExpressionNode test,
        StatementASTNode body
) implements StatementASTNode {
}
