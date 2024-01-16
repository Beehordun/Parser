package org.example.AST;

import java.util.List;

public record BlockStatementASTNode(
        String type,
        List<StatementASTNode> body
) implements StatementASTNode {
}
