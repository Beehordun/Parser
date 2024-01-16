package org.example.AST;

public enum ASTNodeType {
    Program,
    ExpressionStatement,
    BlockStatement,

    AssignmentExpression,

    Identifier,

    VariableStatement,
    ifStatement,

    WhileStatement,

    ForStatement,

    DoWhileStatement,

    VariableDeclaration,

    BinaryExpression,

    UnaryExpression,

    LogicalExpression,

    FunctionDeclaration,

    ReturnStatement,
    MemberExpression,

    CallExpression,
    NewExpression,
    ClassDeclaration,
    ThisExpression,
    Super
}
