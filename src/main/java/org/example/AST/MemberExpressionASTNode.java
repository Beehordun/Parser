package org.example.AST;

public record MemberExpressionASTNode(
        String type,
        boolean computed,
        ExpressionNode object,
        ExpressionNode property
) implements ExpressionNode {
}
