package org.example.AST;

import java.util.List;

public record NewExpressionASTNode(
        String type,
        ExpressionNode callee,
        List<ExpressionNode> arguments
) implements ExpressionNode {
}
