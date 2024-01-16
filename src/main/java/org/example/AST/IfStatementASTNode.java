package org.example.AST;

public record IfStatementASTNode(
        String type,
        ExpressionNode test,
        StatementASTNode consequent,
        StatementASTNode alternate
) implements StatementASTNode {
}
