package org.example.AST;

import java.util.List;

public record ProgramASTNode(
        String type,
        List<StatementASTNode> body
) {
}
