package org.example.AST;

public record LiteralASTNode(
        String type,
        Object value
) implements ExpressionNode {

}
