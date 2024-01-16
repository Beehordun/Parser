package org.example.AST;

import java.util.List;

public record CallExpressionASTNode(
        String type,
        ExpressionNode callee,
        List<ExpressionNode> arguments
) implements ExpressionNode {
}
