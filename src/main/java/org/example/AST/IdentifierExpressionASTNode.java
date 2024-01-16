package org.example.AST;

public record IdentifierExpressionASTNode(
        String type,
        String name
) implements ExpressionNode {
}
