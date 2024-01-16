package org.example.AST;

public record ReturnStatementASTNode(
        String type,
        ExpressionNode argument
) implements StatementASTNode {
}
