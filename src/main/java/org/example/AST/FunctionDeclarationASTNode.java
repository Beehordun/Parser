package org.example.AST;

import java.util.List;

public record FunctionDeclarationASTNode(
        String type,
        ExpressionNode name,
        List<ExpressionNode> params,
        StatementASTNode body
) implements StatementASTNode {
}
