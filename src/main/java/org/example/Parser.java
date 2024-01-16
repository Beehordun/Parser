package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.AST.*;
import org.example.exceptions.SyntaxException;
import org.example.token.Token;
import org.example.token.TokenType;
import org.example.token.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Convert tokens into AST(Abstract Syntax Tree)
 */
public class Parser {

    private final Set<TokenType> assignmentOperators = Set.of(TokenType.SimpleAssignment, TokenType.ComplexAssignment);
    private final Set<TokenType> additiveOperators = Set.of(TokenType.Add, TokenType.Minus);
    private final  Set<TokenType> multiplicativeOperators = Set.of(TokenType.Multiply, TokenType.Divide);

    private final Set<TokenType> literals = Set.of(
            TokenType.NumericLiteral,
            TokenType.StringLiteral,
            TokenType.BooleanLiteral,
            TokenType.NullLiteral
    );
    private final Tokenizer tokenizer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Parser() {
        tokenizer = new Tokenizer();
    }

    /**
     * parses a program into an AST.
     * Parse recursively starting from the main entry point. -> Program
     */
    public String parse(String input) {
        try {
            tokenizer.init(input);
            return objectMapper.writeValueAsString(program());
        } catch(JsonProcessingException exception) {
            return null;
        }
    }

    /**
     * Program
     * : StatementList
     **/
    private ProgramASTNode program() {
        return new ProgramASTNode(ASTNodeType.Program.name(), statementList());
    }

    /**
     * StatementList
     *     :Statement[]
     * **/
    private List<StatementASTNode> statementList() {
        List<StatementASTNode> statementASTNodes = new ArrayList<>();

        while (!tokenizer.isEOF()) {
            statementASTNodes.add(statement());
        }

        return statementASTNodes;
    }

    /**
     * Statement
     * : ExpressionStatement
     * | BlockStatement
     * | VariableStatement
     * | IterationStatement
     * | FunctionDeclarationStatement
     * | ClassDeclarationStatement
     * | ReturnStatement
     **/

    private StatementASTNode statement() {
        if (tokenizer.isEOF()) return null;
        switch (tokenizer.lookAhead().type()) {
            case IfKeyword -> {
                return ifStatement();
            }
            case VariableKeyword -> {
                return variableStatement();
            }
            case OpenCurlyBracket -> {
                return blockStatement();
            }
            case WhileKeyword, DoKeyword, ForKeyword -> {
                return iterationStatement();
            }
            case DefKeyword -> {
                return functionDeclarationStatement();
            }
            case ReturnKeyword -> {
                return returnStatement();
            }
            case ClassKeyword -> {
                return classDeclarationStatement();
            }
            default -> {
                return new ExpressionStatementASTNode(
                        ASTNodeType.ExpressionStatement.name(),
                        expressionStatement()
                );
            }
        }
    }

    /**
     * IfStatement
     * : If ParenthesizedExpression BlockStatement else BlockStatement
     **/

    private StatementASTNode ifStatement() {
        tokenizer.eatToken(TokenType.IfKeyword);

        ExpressionNode test = parenthesizeExpression();
        StatementASTNode consequent = statement();
        StatementASTNode alternate = null;

        if (tokenizer.lookAhead().type() == TokenType.ElseKeyword) {
            tokenizer.eatToken(TokenType.ElseKeyword);
            alternate = statement();
        }

        return new IfStatementASTNode(
                ASTNodeType.ifStatement.name(),
                test,
                consequent,
                alternate
        );
    }

    /**
     * VariableStatement
     * : let VariableDeclaration[] ;
     **/
    private StatementASTNode variableStatement() {
        tokenizer.eatToken(TokenType.VariableKeyword);
        List<VariableDeclarationNode> variableDeclarations = variableDeclarationList();

        if (!tokenizer.isEOF() && tokenizer.lookAhead().type() == TokenType.Semicolon) tokenizer.eatToken(TokenType.Semicolon);

        return new VariableStatementASTNode(ASTNodeType.VariableStatement.name(), variableDeclarations);
    }

    /**
     * VariableDeclarationList
     *    : VariableDeclaration[]
     * **/
    private List<VariableDeclarationNode> variableDeclarationList() {
        List<VariableDeclarationNode> declarations = new ArrayList<>();
        do {
            declarations.add(variableDeclaration());
        } while (!tokenizer.isEOF() && tokenizer.lookAhead().type() == TokenType.Comma);
        return declarations;
    }

    /**
     * VariableDeclaration
     * : Identifier OptionalVariableInitializer
     **/

    private VariableDeclarationNode variableDeclaration() {
        if (tokenizer.lookAhead().type() == TokenType.Comma) tokenizer.eatToken(TokenType.Comma);
        IdentifierExpressionASTNode identifierNode =
                (IdentifierExpressionASTNode) identifierExpression();

        Token lookAheadToken = tokenizer.lookAhead();

        ExpressionNode init =
                (lookAheadToken.type() == TokenType.Comma || lookAheadToken.type() == TokenType.Semicolon) ? null :
                        variableInitializer();

        return new VariableDeclarationNode(ASTNodeType.VariableDeclaration.name(), identifierNode, init);
    }

    private ExpressionNode variableInitializer() {
        Token lookAheadToken = tokenizer.lookAhead();
        if (lookAheadToken.type() == TokenType.SimpleAssignment) {
            tokenizer.eatToken(TokenType.SimpleAssignment);
        } else {
            throw new SyntaxException("Invalid syntax. Expected an assignment operator but found" + lookAheadToken.type());
        }

        return assignmentExpression();
    }

    /**
     * BlockStatement
     * : {
     *      Statement[]
     * }
     **/

    private StatementASTNode blockStatement() {
        if (tokenizer.lookAhead().type() == TokenType.CloseCurlyBracket) {
            tokenizer.eatToken(TokenType.CloseCurlyBracket);
            return new BlockStatementASTNode(ASTNodeType.BlockStatement.name(), List.of());
        }

        if (tokenizer.lookAhead().type() == TokenType.OpenCurlyBracket) {
            tokenizer.eatToken(TokenType.OpenCurlyBracket);
        }

        List<StatementASTNode> statementList = new ArrayList<>();

        while (!tokenizer.isEOF() && tokenizer.lookAhead().type() != TokenType.CloseCurlyBracket) {
            StatementASTNode statementASTNode = statement();
            if (statementASTNode != null) statementList.add(statementASTNode);
        }

        if (tokenizer.lookAhead().type() == TokenType.CloseCurlyBracket) {
            tokenizer.eatToken(TokenType.CloseCurlyBracket);
        }

        return new BlockStatementASTNode(ASTNodeType.BlockStatement.name(), statementList);
    }

    /**
     * IterationStatement
     * : ForStatement
     * | WhileStatement
     * | DoStatement
     **/

    private StatementASTNode iterationStatement() {
        switch (tokenizer.lookAhead().type()) {
            case ForKeyword -> {
                return forStatement();
            }
            case WhileKeyword -> {
                return whileStatement();
            }
            case DoKeyword -> {
                return doWhileStatement();
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * FunctionDeclaration
     *  : def functionName ( OptionalParams ) BlockStatement
     * */

    private StatementASTNode functionDeclarationStatement() {
        tokenizer.eatToken(TokenType.DefKeyword);
        ExpressionNode name = expressionStatement();

        tokenizer.eatToken(TokenType.OpenParenthesis);
        List<ExpressionNode> params = new ArrayList<>();

        while (tokenizer.lookAhead().type() != TokenType.CloseParenthesis) {
            params.add(expressionStatement());
            if (tokenizer.lookAhead().type() == TokenType.Comma) {
                tokenizer.eatToken(TokenType.Comma);
            }
        }

        tokenizer.eatToken(TokenType.CloseParenthesis);

        return new FunctionDeclarationASTNode(
                ASTNodeType.FunctionDeclaration.name(),
                name,
                params,
                statement()
        );
    }


    /**
     * ReturnStatement
     * : return OptExpression ;
     **/
    private StatementASTNode returnStatement() {
        tokenizer.eatToken(TokenType.ReturnKeyword);
        ExpressionNode optExpression =
                (tokenizer.lookAhead().type() == TokenType.Semicolon) ? null : expressionStatement();

        if (tokenizer.lookAhead().type() == TokenType.Semicolon) tokenizer.eatToken(TokenType.Semicolon);
        return new ReturnStatementASTNode(
                ASTNodeType.ReturnStatement.name(),
                optExpression
        );
    }

    /**
     * ClassDeclaration
     *  : 'class' Identifier OptClassExtends BlockStatement
     **/

    private StatementASTNode classDeclarationStatement() {
        tokenizer.eatToken(TokenType.ClassKeyword);
        ExpressionNode id = identifierExpression();
        ExpressionNode optSuperClass =
                (tokenizer.lookAhead().type() == TokenType.ExtendsKeyword) ? classExtends(): null;

        StatementASTNode body = statement();

        return new ClassDeclarationASTNode(
                ASTNodeType.ClassDeclaration.name(),
                id,
                optSuperClass,
                body
        );
    }

    /**
     * Class Extends
     *     : 'extends' Identifier
     * **/
    private ExpressionNode classExtends() {
        tokenizer.eatToken(TokenType.ExtendsKeyword);
        return identifierExpression();
    }

    /**
     * Expression
     * : AssignmentExpression
     **/
    private ExpressionNode expressionStatement() {
        return assignmentExpression();
    }

    /**
     * AssignmentExpression
     * : LogicalORExpression
     * | LeftHandSideExpression ASSIGNMENT_OPERATOR AssignmentExpression
     **/

    private ExpressionNode assignmentExpression() {
        ExpressionNode left = logicalORExpression();
        if (tokenizer.lookAhead() != null && assignmentOperators.contains(tokenizer.lookAhead().type())) {
            return new AssignmentExpressionASTNode(
                    ASTNodeType.AssignmentExpression.name(),
                    tokenizer.getNextToken().value(),
                    returnIfIdentityExpressionASTNode(left),
                    assignmentExpression()
            );
        }

        return left;
    }

    private IdentifierExpressionASTNode returnIfIdentityExpressionASTNode(ExpressionNode node) {
        if (node instanceof IdentifierExpressionASTNode) return (IdentifierExpressionASTNode) node;
        throw new SyntaxException("Expected IdentifierExpressionASTNode but found " + node);
    }

    /**
     * LogicalORExpression
     *  : LogicalANDExpression
     *  | LogicalANDExpression LOGICAL_OR_OPERATOR LogicalORExpression
     * **/
    private ExpressionNode logicalORExpression() {
        ExpressionNode left = logicalANDExpression();
        if (tokenizer.lookAhead() != null && tokenizer.lookAhead().type() == TokenType.LogicalOROperator) {
            return new BinaryExpressionASTNode(
                    ASTNodeType.LogicalExpression.name(),
                    tokenizer.getNextToken().value(),
                    left,
                    logicalORExpression()
            );
        }

        return left;
    }

    /**
     * LogicalANDExpression
     * : EqualityExpression
     * | EqualityExpression LOGICAL_AND_OPERATOR LogicalANDExpression
     * **/

    private ExpressionNode logicalANDExpression() {
        ExpressionNode left = equalityExpression();
        if (tokenizer.lookAhead() != null && tokenizer.lookAhead().type() == TokenType.LogicalANDOperator) {
            return new BinaryExpressionASTNode(
                    ASTNodeType.LogicalExpression.name(),
                    tokenizer.getNextToken().value(),
                    left,
                    logicalANDExpression()
            );
        }
        return left;
    }

    /**
     * EqualityExpression
     * : RelationalExpression
     * | RelationalExpression EQUALITY_OPERATOR EqualityExpression
     */
    private ExpressionNode equalityExpression() {
        ExpressionNode left = relationalExpression();
        if (tokenizer.lookAhead() != null && tokenizer.lookAhead().type() == TokenType.EqualityOperator) {
            return new BinaryExpressionASTNode(
                    ASTNodeType.BinaryExpression.name(),
                    tokenizer.getNextToken().value(),
                    left,
                    equalityExpression()
            );
        }

        return left;
    }

    /**
     * RelationalExpression
     *  : AdditiveExpression
     *  | AdditiveExpression RELATIONAL_OPERATOR RelationalExpression
     * */
    private ExpressionNode relationalExpression() {
        ExpressionNode left = additiveExpression();
        if (tokenizer.lookAhead() != null && tokenizer.lookAhead().type() == TokenType.RelationalOperator) {
            return new BinaryExpressionASTNode(
                    ASTNodeType.BinaryExpression.name(),
                    tokenizer.getNextToken().value(),
                    left,
                    relationalExpression()
            );
        }

        return left;
    }

    /**
     * AdditiveExpression
     * : MultiplicativeExpression
     * | AdditiveExpression ADDITIVE_OPERATOR MultiplicativeExpression
     * ;
     **/
    private ExpressionNode additiveExpression() {
        ExpressionNode left = multiplicativeExpression();

        while (!tokenizer.isEOF() && additiveOperators.contains(tokenizer.lookAhead().type())) {
            String operator = tokenizer.lookAhead().type().name();
            tokenizer.eatToken(tokenizer.lookAhead().type());
            ExpressionNode right = multiplicativeExpression();

            left = new BinaryExpressionASTNode(ASTNodeType.BinaryExpression.name(), operator, left, right);
        }

        if (tokenizer.lookAhead().type() == TokenType.Semicolon) {
            tokenizer.eatToken(TokenType.Semicolon);
            return left;
        }

        return left;
    }

    /**
     * MultiplicativeExpression
     * : UnaryExpression
     * | MultiplicativeExpression MULTIPLICATION_OPERATOR UnaryExpression
     * ;
     **/

    private ExpressionNode multiplicativeExpression() {
        ExpressionNode left = unaryExpression();

        while (!tokenizer.isEOF() && multiplicativeOperators.contains(tokenizer.lookAhead().type())) {
            String operator = tokenizer.lookAhead().type().name();
            tokenizer.skipNextToken();
            ExpressionNode right = unaryExpression();

            left = new BinaryExpressionASTNode(ASTNodeType.BinaryExpression.name(), operator, left, right);
        }

        return left;
    }

    /**
     * UnaryExpression
     * : LeftHandSideExpression
     * | ADDITIVE_OPERATOR UnaryExpression
     * | LOGICAL_NOT UnaryExpression
     **/

    private ExpressionNode unaryExpression() {
        String operator = null;
        Token lookAheadToken = tokenizer.lookAhead();
        if (lookAheadToken.type() == TokenType.Add ||
                lookAheadToken.type() == TokenType.Minus
        ) {
            operator = lookAheadToken.value();
        } else if (lookAheadToken.type() == TokenType.LogicalNot) {
            operator = lookAheadToken.value();
        }

        if (operator != null) {
            // skip operator token
            tokenizer.skipNextToken();
            return new UnaryExpressionASTNode(
                    ASTNodeType.UnaryExpression.name(),
                    operator,
                    unaryExpression()
            );
        }

        return leftHandSideExpression();
    }

    /**
     * LeftHandSideExpression
     * : CallMemberExpression
     * **/
    private ExpressionNode leftHandSideExpression() {
        return callMemberExpression();
    }

    /**
     * CallMemberExpression
     *  : MemberExpression
     *  | CallExpression
     * **/
    private ExpressionNode callMemberExpression() {
        if (tokenizer.lookAhead().type() == TokenType.SuperKeyword) {
            return callExpression(tokenizer, superExpression());
        }
        ExpressionNode member = memberExpression();
        if (tokenizer.lookAhead().type() == TokenType.OpenBracket) {
            return callExpression(tokenizer, member);
        }
        return member;
    }

    private ExpressionNode callExpression(Tokenizer tokenizer, ExpressionNode callee) {
        ExpressionNode callExpression = new CallExpressionASTNode(
                ASTNodeType.CallExpression.name(),
                callee,
                arguments(tokenizer)
        );

        if (!tokenizer.isEOF() && tokenizer.lookAhead().type() == TokenType.OpenBracket) {
            callExpression = callExpression(tokenizer, callExpression);
        }

        return callExpression;
    }

    /**
     * Arguments
     *  : ( OptArgumentList )
     * **/
    private List<ExpressionNode> arguments(Tokenizer tokenizer) {
        // Eat (
        tokenizer.eatToken(TokenType.OpenParenthesis);

        List<ExpressionNode> argumentList =
                (tokenizer.lookAhead().type() != TokenType.CloseParenthesis) ? argumentList(tokenizer) : List.of();

        // Eat )
        tokenizer.eatToken(TokenType.CloseParenthesis);

        return argumentList;
    }

    private List<ExpressionNode> argumentList(Tokenizer tokenizer) {
        List<ExpressionNode> arguments = new ArrayList<>();

        while (tokenizer.lookAhead().type() != TokenType.CloseParenthesis) {
            arguments.add(assignmentExpression());
            // Eat comma
            if (tokenizer.lookAhead().type() == TokenType.Comma) {
                tokenizer.eatToken(TokenType.Comma);
            }
        }

        return arguments;
    }

    /**
     * MemberExpression
     * : PrimaryExpression
     * | MemberExpression .  Identifier
     * | MemberExpression [ 'Expression' ]
     * **/

    private ExpressionNode memberExpression() {
        ExpressionNode object = primaryExpression();

        while(tokenizer.lookAhead().type() == TokenType.Dot || tokenizer.lookAhead().type() == TokenType.OpenBracket) {
            if (tokenizer.lookAhead().type() == TokenType.Dot) {
                tokenizer.eatToken(TokenType.Dot);
                object = new MemberExpressionASTNode(
                        ASTNodeType.MemberExpression.name(),
                        false,
                        object,
                        identifierExpression()
                );
            }

            if (tokenizer.lookAhead().type() == TokenType.OpenBracket) {
                tokenizer.eatToken(TokenType.OpenBracket);
                ExpressionNode property = expressionStatement();
                tokenizer.eatToken(TokenType.CloseBracket);
                object = new MemberExpressionASTNode(
                        ASTNodeType.MemberExpression.name(),
                        true,
                        object,
                        property
                );
            }
        }
        return object;
    }

    /**
     * PrimaryExpression
     * : ParenthesizeExpression
     * | LiteralExpression
     * | Identifier
     * | ThisExpression
     * | NewExpression
     **/

    private ExpressionNode primaryExpression() {
        Token lookAheadToken = tokenizer.lookAhead();
        if (isLiteral(lookAheadToken)) {
            return literalExpression();
        }
        else if  (lookAheadToken.type() == TokenType.OpenParenthesis) {
            return parenthesizeExpression();
        } else if (lookAheadToken.type() == TokenType.Identifier) {
            return identifierExpression();
        }
        else if (lookAheadToken.type() == TokenType.ThisKeyword) {
            return thisExpression();
        } else if (lookAheadToken.type() == TokenType.NewKeyword) {
            return newExpression();
        }
        return leftHandSideExpression();
    }

    private boolean isLiteral(Token lookAheadToken) {
        Set<TokenType> literals = Set.of(
                TokenType.NumericLiteral,
                TokenType.StringLiteral,
                TokenType.BooleanLiteral,
                TokenType.NullLiteral
        );
        return literals.contains(lookAheadToken.type());
    }

    /**
     * Literal
     * : StringLiteral,
     * | NumericLiteral,
     * | BooleanLiteral,
     * | NullLiteral
     ***/

    private ExpressionNode literalExpression() {
        if (tokenizer.isEOF()) return null;
        Token token = tokenizer.getNextToken();

        if (literals.contains(token.type())) {
            String literalValue = token.value();
            return new LiteralASTNode(token.type().name(), literalValue);
        }
        return null;
    }

    /**
     * ParenthesizeExpression
     *   : '(' ExpressionStatement ')'
     * **/

    private ExpressionNode parenthesizeExpression() {
        tokenizer.eatToken(TokenType.OpenParenthesis);
        ExpressionNode expressionNode = expressionStatement();
        tokenizer.eatToken(TokenType.CloseParenthesis);
        return expressionNode;
    }

    private ExpressionNode identifierExpression() {
        return new IdentifierExpressionASTNode(ASTNodeType.Identifier.name(), tokenizer.getNextToken().value());
    }

    /**
     * ThisExpression
     *  : 'this'
     * **/
    private ExpressionNode thisExpression() {
        // eat 'this'
        tokenizer.eatToken(TokenType.ThisKeyword);
        return new BasicExpressionASTNode(ASTNodeType.ThisExpression.name());
    }

    /**
     * NewExpression
     *  : 'new' MemberExpression (Arguments)
     * **/
    private ExpressionNode newExpression() {
        tokenizer.eatToken(TokenType.NewKeyword);
        ExpressionNode callee = memberExpression();
        List<ExpressionNode> arguments =  arguments(tokenizer);
        return  new CallExpressionASTNode(
                ASTNodeType.NewExpression.name(),
                callee,
                arguments
        );
    }

    /**
     * ForStatement
     * : for ( OptForStatementInit ; OptExpression ;  OptExpression ) BlockStatement
     * **/

    private StatementASTNode forStatement() {
        tokenizer.eatToken(TokenType.ForKeyword);
        tokenizer.eatToken(TokenType.OpenParenthesis);
        StatementASTNode init = (tokenizer.lookAhead().type() != TokenType.Semicolon) ? forStatementInit() : null;
        ExpressionNode test = (tokenizer.lookAhead().type() != TokenType.Semicolon) ? expressionStatement() : null;
        ExpressionNode update = (tokenizer.lookAhead().type() != TokenType.Semicolon) ? expressionStatement() : null;
        // skip )
        tokenizer.eatToken(TokenType.CloseParenthesis);
        StatementASTNode body = statement();

        return new ForLoopIterationStatementASTNode(
                ASTNodeType.ForStatement.name(),
                init,
                test,
                update,
                body
        );
    }

    /**
     * ForStatementInit
     *   : VariableStatementInit
     *   | Expression
     * **/

    private StatementASTNode forStatementInit() {
        if (tokenizer.lookAhead().type() == TokenType.VariableKeyword) {
            return variableStatement();
        }
        return new ExpressionStatementASTNode(
                ASTNodeType.ExpressionStatement.name(),
                expressionStatement()
        );
    }

    /**
     * WhileStatement
     * : While ( Expression) BlockStatement
     **/
    public StatementASTNode whileStatement() {
        tokenizer.eatToken(TokenType.WhileKeyword);

        return new IterationStatementASTNode(
                ASTNodeType.WhileStatement.name(),
                parenthesizeExpression(),
                statement()
        );
    }

    /**
     * DoWhileStatement
     *  : Do BlockStatement while (Expression)
     *
     * **/
    private StatementASTNode doWhileStatement() {
        tokenizer.eatToken(TokenType.DoKeyword);
        StatementASTNode body = statement();
        if (tokenizer.lookAhead().type() != TokenType.WhileKeyword) {
            throw new SyntaxException("Invalid syntax. Expected while keyword");
        }
        tokenizer.eatToken(TokenType.WhileKeyword);
        ExpressionNode test = parenthesizeExpression();
        return new IterationStatementASTNode(
                ASTNodeType.DoWhileStatement.name(),
                test,
                body
        );
    }

    /**
     * Super
     * : 'super'
     ***/
    private ExpressionNode superExpression() {
        tokenizer.eatToken(TokenType.SuperKeyword);
        return new BasicExpressionASTNode(ASTNodeType.Super.name());
    }




}
