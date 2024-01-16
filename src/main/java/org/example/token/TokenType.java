package org.example.token;

public enum TokenType {
    Semicolon,
    StringLiteral,
    NumericLiteral,
    OpenCurlyBracket,
    CloseCurlyBracket,

    Add,
    Minus,
    WhileKeyword,
    DoKeyword,
    ForKeyword,

    Multiply,
    Divide,

    OpenParenthesis,
    CloseParenthesis,

    SimpleAssignment,
    ComplexAssignment,

    Identifier,
    VariableKeyword,
    IfKeyword,
    ElseKeyword,
    Comma,

    BooleanLiteral,
    NullLiteral,

    RelationalOperator,

    LogicalANDOperator,
    LogicalOROperator,
    EqualityOperator,

    LogicalNot,

    DefKeyword,
    ReturnKeyword,

    OpenBracket,
    CloseBracket,
    Dot,
    ClassKeyword,
    ExtendsKeyword,
    SuperKeyword,
    NewKeyword,
    ThisKeyword
}
