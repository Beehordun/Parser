package org.example.AST;

public record ExpressionStatementASTNode(
        String type,
        ExpressionNode expression
) implements StatementASTNode {
}
