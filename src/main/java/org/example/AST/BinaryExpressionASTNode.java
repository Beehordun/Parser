package org.example.AST;

public record BinaryExpressionASTNode(
        String type,
        String operator,
        ExpressionNode left,
        ExpressionNode right
) implements ExpressionNode {
}
