package org.example.token;

import org.example.exceptions.SyntaxException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Group individual characters into tokens.
 * e.g. println("Hello World") ->
 * {
 * id : println,
 * string: [hello, world]
 * }
 * <p>
 * Note that a tokenizer doesn't bother about if the syntax is correct or not.
 **/
public class Tokenizer {
    private String inputString;
    private int cursorPos = 0;

    private final Map<String, TokenType> regexToTokenType = new LinkedHashMap<>();

    public void init(String inputString) {
        this.inputString = inputString;
        regexToTokenType.put("^!", TokenType.LogicalNot);
        regexToTokenType.put("^\\.", TokenType.Dot);
        regexToTokenType.put("^\\[", TokenType.OpenBracket);
        regexToTokenType.put("^]", TokenType.CloseBracket);
        regexToTokenType.put("^let", TokenType.VariableKeyword);
        regexToTokenType.put("^class", TokenType.ClassKeyword);
        regexToTokenType.put("^extends", TokenType.ExtendsKeyword);
        regexToTokenType.put("^super", TokenType.SuperKeyword);
        regexToTokenType.put("^new", TokenType.NewKeyword);
        regexToTokenType.put("^this", TokenType.ThisKeyword);
        regexToTokenType.put("^def", TokenType.DefKeyword);
        regexToTokenType.put("^return", TokenType.ReturnKeyword);
        regexToTokenType.put("^&&", TokenType.LogicalANDOperator);
        regexToTokenType.put("^(\\|){2}", TokenType.LogicalOROperator);
        regexToTokenType.put("^(true|false)", TokenType.BooleanLiteral);
        regexToTokenType.put("^null", TokenType.NullLiteral);
        regexToTokenType.put("^if", TokenType.IfKeyword);
        regexToTokenType.put("^else", TokenType.ElseKeyword);
        regexToTokenType.put("^while", TokenType.WhileKeyword);
        regexToTokenType.put("^do", TokenType.DoKeyword);
        regexToTokenType.put("^for", TokenType.ForKeyword);
        regexToTokenType.put("^\\d+", TokenType.NumericLiteral);
        regexToTokenType.put("^\\w+", TokenType.Identifier);
        regexToTokenType.put("^\"[^\"]*\"", TokenType.StringLiteral);
        regexToTokenType.put("^;", TokenType.Semicolon);
        regexToTokenType.put("^\\{", TokenType.OpenCurlyBracket);
        regexToTokenType.put("^}", TokenType.CloseCurlyBracket);
        regexToTokenType.put("^[/*+-]=", TokenType.ComplexAssignment);
        regexToTokenType.put("^\\+", TokenType.Add);
        regexToTokenType.put("^\\-", TokenType.Minus);
        regexToTokenType.put("^\\*", TokenType.Multiply);
        regexToTokenType.put("^/", TokenType.Divide);
        regexToTokenType.put("^\\(", TokenType.OpenParenthesis);
        regexToTokenType.put("^\\)", TokenType.CloseParenthesis);
        regexToTokenType.put("^[=!]=", TokenType.EqualityOperator);
        regexToTokenType.put("^=", TokenType.SimpleAssignment);
        regexToTokenType.put("^,", TokenType.Comma);
        regexToTokenType.put("^[><]=?", TokenType.RelationalOperator);
    }

    public Token getNextToken() {
        if (isEOF()) return null;
        Token lookAhead = lookAhead();
        updateCursor(lookAhead);
        return lookAhead;
    }

    public Token lookAhead() {
        if (isEOF()) return null;

        while (isNewLine() || isWhiteSpace()) {
            cursorPos += 1;
        }

        String stringToMatch = inputString.substring(cursorPos);
        for (Map.Entry<String, TokenType> entry : regexToTokenType.entrySet()) {
            Matcher matcher = Pattern.compile(entry.getKey()).matcher(stringToMatch);
            if (matcher.find()) {
                String matchedString = matcher.group();
                return new Token(entry.getValue(), matchedString);
            }
        }

        throw new SyntaxException("Invalid token");
    }

    public void skipNextToken() {
        updateCursor(lookAhead());
    }

    public void eatToken(TokenType tokenType) {
        Token lookAheadToken = lookAhead();
        if (tokenType != lookAheadToken.type()) {
            throw new SyntaxException("Token to be eaten is not the same as the look ahead token");
        }
        updateCursor(lookAheadToken);
    }

    private void updateCursor(Token currToken) {
        cursorPos += currToken.value().length();
    }

    private boolean isWhiteSpace() {
        String stringToMatch = inputString.substring(cursorPos);
        Matcher matcher = Pattern.compile("^\s").matcher(stringToMatch);
        return matcher.find();
    }

    private boolean isNewLine() {
        String stringToMatch = inputString.substring(cursorPos);
        Matcher matcher = Pattern.compile("^\\n").matcher(stringToMatch);
        return matcher.find();
    }

    public boolean isEOF() {
        return cursorPos > inputString.length() - 1;
    }

}
