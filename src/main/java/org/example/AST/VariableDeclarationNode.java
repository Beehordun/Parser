package org.example.AST;

public record VariableDeclarationNode(
        String type,
        IdentifierExpressionASTNode id,
        ExpressionNode init
) {

}
