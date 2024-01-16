package org.example.AST;

public record AssignmentExpressionASTNode(
        String type,
        String operator,
        ExpressionNode left,
        ExpressionNode right
) implements ExpressionNode {
}
