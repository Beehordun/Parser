package org.example.AST;

public record ClassDeclarationASTNode(
        String type,
        ExpressionNode id,
        ExpressionNode superClass,
        StatementASTNode body
) implements StatementASTNode {
}
