package org.example.AST;

import java.util.List;

public record VariableStatementASTNode(
        String type,
        List<VariableDeclarationNode> declarations
) implements StatementASTNode {
}
