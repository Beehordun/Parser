import org.example.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {

    Parser parser;

    @BeforeEach
    public void setup() {
        parser = new Parser();
    }

    @Test
    public void testStringLiteral() {
        String stringLiteralInput = "\"University degree\";";
        String generatedAST = parser.parse(stringLiteralInput);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"StringLiteral\",\"value\":\"\\\"University degree\\\"\"}}]}";
        assertEquals(expectedAST, generatedAST);
    }

    @Test
    public void testNumericLiteral() {
        String numericLiteralInput = "123;";
        String generatedAST = parser.parse(numericLiteralInput);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"NumericLiteral\",\"value\":\"123\"}}]}";
        assertEquals(expectedAST, generatedAST);
    }

    @Test
    public void testTrueBooleanLiteral() {
        String trueBooleanInput = "true;";
        String generatedTrueBooleanAST = parser.parse(trueBooleanInput);
        String expectedTrueBooleanAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"BooleanLiteral\",\"value\":\"true\"}}]}";
        assertEquals(generatedTrueBooleanAST, expectedTrueBooleanAST);
    }

    @Test
    public void testFalseBooleanLiteral() {
        String falseBooleanInput = "false;";
        String generatedFalseBooleanAST = parser.parse(falseBooleanInput);
        String expectedFalseBooleanAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"BooleanLiteral\",\"value\":\"false\"}}]}";
        assertEquals(generatedFalseBooleanAST, expectedFalseBooleanAST);
    }

    @Test
    public void testNullLiteral() {
        String falseBooleanInput = "null;";
        String generatedFalseBooleanAST = parser.parse(falseBooleanInput);
        String expectedFalseBooleanAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"NullLiteral\",\"value\":\"null\"}}]}";
        assertEquals(generatedFalseBooleanAST, expectedFalseBooleanAST);
    }

    @Test
    public void testAdditiveExpression() {
        String input = "3 + 2 - (4 - x);";
        String generatedAST = parser.parse(input);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"BinaryExpression\",\"operator\":\"Minus\",\"left\":{\"type\":\"BinaryExpression\",\"operator\":\"Add\",\"left\":{\"type\":\"NumericLiteral\",\"value\":\"3\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"2\"}},\"right\":{\"type\":\"BinaryExpression\",\"operator\":\"Minus\",\"left\":{\"type\":\"NumericLiteral\",\"value\":\"4\"},\"right\":{\"type\":\"Identifier\",\"name\":\"x\"}}}}]}";
        assertEquals(generatedAST, expectedAST);
    }

    @Test
    public void testMultiplicativeExpression() {
        String input = "x * 3 + (4/5);";
        String generatedAST = parser.parse(input);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"BinaryExpression\",\"operator\":\"Add\",\"left\":{\"type\":\"BinaryExpression\",\"operator\":\"Multiply\",\"left\":{\"type\":\"Identifier\",\"name\":\"x\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"3\"}},\"right\":{\"type\":\"BinaryExpression\",\"operator\":\"Divide\",\"left\":{\"type\":\"NumericLiteral\",\"value\":\"4\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"5\"}}}}]}";
        assertEquals(generatedAST, expectedAST);
    }

    @Test
    public void testLogicalANDExpression() {
        String input = "x == 3 && y == 4;";
        String generatedAST = parser.parse(input);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"LogicalExpression\",\"operator\":\"&&\",\"left\":{\"type\":\"BinaryExpression\",\"operator\":\"==\",\"left\":{\"type\":\"Identifier\",\"name\":\"x\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"3\"}},\"right\":{\"type\":\"BinaryExpression\",\"operator\":\"==\",\"left\":{\"type\":\"Identifier\",\"name\":\"y\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"4\"}}}}]}";
        assertEquals(generatedAST, expectedAST);
    }

    @Test
    public void testLogicalORExpression() {
        String input = "x == 3 || y == 4;";
        String generatedAST = parser.parse(input);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"LogicalExpression\",\"operator\":\"||\",\"left\":{\"type\":\"BinaryExpression\",\"operator\":\"==\",\"left\":{\"type\":\"Identifier\",\"name\":\"x\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"3\"}},\"right\":{\"type\":\"BinaryExpression\",\"operator\":\"==\",\"left\":{\"type\":\"Identifier\",\"name\":\"y\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"4\"}}}}]}";
        assertEquals(generatedAST, expectedAST);
    }

    @Test
    public void testLogicalANDORExpression() {
        String input = "x == 3 && y == 4 || z == 5;";
        String generatedAST = parser.parse(input);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"LogicalExpression\",\"operator\":\"||\",\"left\":{\"type\":\"LogicalExpression\",\"operator\":\"&&\",\"left\":{\"type\":\"BinaryExpression\",\"operator\":\"==\",\"left\":{\"type\":\"Identifier\",\"name\":\"x\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"3\"}},\"right\":{\"type\":\"BinaryExpression\",\"operator\":\"==\",\"left\":{\"type\":\"Identifier\",\"name\":\"y\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"4\"}}},\"right\":{\"type\":\"BinaryExpression\",\"operator\":\"==\",\"left\":{\"type\":\"Identifier\",\"name\":\"z\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"5\"}}}}]}";
        assertEquals(generatedAST, expectedAST);
    }

    @Test
    public void testUnaryExpression() {
        String input = "!x;";
        String generatedAST = parser.parse(input);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"UnaryExpression\",\"operator\":\"!\",\"argument\":{\"type\":\"Identifier\",\"name\":\"x\"}}}]}";
        assertEquals(generatedAST, expectedAST);
    }

    @Test
    public void testRelationalExpression() {
        String input = "x > 6 || y < 7 || z >= 4 || a <= 7 || b == 10;";
        String generatedAST = parser.parse(input);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"LogicalExpression\",\"operator\":\"||\",\"left\":{\"type\":\"BinaryExpression\",\"operator\":\">\",\"left\":{\"type\":\"Identifier\",\"name\":\"x\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"6\"}},\"right\":{\"type\":\"LogicalExpression\",\"operator\":\"||\",\"left\":{\"type\":\"BinaryExpression\",\"operator\":\"<\",\"left\":{\"type\":\"Identifier\",\"name\":\"y\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"7\"}},\"right\":{\"type\":\"LogicalExpression\",\"operator\":\"||\",\"left\":{\"type\":\"BinaryExpression\",\"operator\":\">=\",\"left\":{\"type\":\"Identifier\",\"name\":\"z\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"4\"}},\"right\":{\"type\":\"LogicalExpression\",\"operator\":\"||\",\"left\":{\"type\":\"BinaryExpression\",\"operator\":\"<=\",\"left\":{\"type\":\"Identifier\",\"name\":\"a\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"7\"}},\"right\":{\"type\":\"BinaryExpression\",\"operator\":\"==\",\"left\":{\"type\":\"Identifier\",\"name\":\"b\"},\"right\":{\"type\":\"NumericLiteral\",\"value\":\"10\"}}}}}}}]}";
        assertEquals(generatedAST, expectedAST);
    }

    @Test
    public void testAssignmentExpression() {
        String input = "x = 6 + (8 - y);";
        String generatedAST = parser.parse(input);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"ExpressionStatement\",\"expression\":{\"type\":\"AssignmentExpression\",\"operator\":\"=\",\"left\":{\"type\":\"Identifier\",\"name\":\"x\"},\"right\":{\"type\":\"BinaryExpression\",\"operator\":\"Add\",\"left\":{\"type\":\"NumericLiteral\",\"value\":\"6\"},\"right\":{\"type\":\"BinaryExpression\",\"operator\":\"Minus\",\"left\":{\"type\":\"NumericLiteral\",\"value\":\"8\"},\"right\":{\"type\":\"Identifier\",\"name\":\"y\"}}}}}]}";
        assertEquals(generatedAST, expectedAST);
    }

    @Test
    public void testVariableStatement() {
        String input = "let x = 6;";
        String generatedAST = parser.parse(input);
        String expectedAST = "{\"type\":\"Program\",\"body\":[{\"type\":\"VariableStatement\",\"declarations\":[{\"type\":\"VariableDeclaration\",\"id\":{\"type\":\"Identifier\",\"name\":\"x\"},\"init\":{\"type\":\"NumericLiteral\",\"value\":\"6\"}}]}]}";
        assertEquals(generatedAST, expectedAST);
    }
}
