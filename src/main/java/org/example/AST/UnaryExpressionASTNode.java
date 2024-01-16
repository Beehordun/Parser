package org.example.AST;

public record UnaryExpressionASTNode(
        String type,
        String operator,
        ExpressionNode argument
) implements ExpressionNode{
}
