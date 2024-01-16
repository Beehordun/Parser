package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        String input = "{\"University of Ibadan\"; \n 123;456789;\n \"Java 123 %\";\n  {4567;   {  \" Ade oba\";  }} 123; }";
        String input1 = "if ((x <= 6 && y > 9) || (y > 90)) { if (z) { b = 8; } else { b = 10; } x = 7; } else { let y = 7 * (4 + 5);  z = \"Aderinsola\"}";
        String input2 = "do { x += 1; } while (x < 10)";
        String input3 = "for (; ;) { x += 1; }";
        String input4 = "def square(x, y, z) { x = 10;  return ;}";
        String input5 = "let s = \"Hello world\"; let i = 0; while (i < s.length) { s[i]; i += 1; }";
        String input6 = "a.b.c[\"d\"];";
        String input7 = "console.log(x);";
        String input8 = "class Square extends Point { def add() {return x * 2;}}";
        String input9 = "def run(x, y) { d = new Point(x, y); return d; }";
        String input10 = "d = new Point(x, y);";

        //evaluate();

        Parser parser = new Parser();
       System.out.println(parser.parse(input10));
      /* Matcher matcher = Pattern.compile("^]").matcher("]");

        if (matcher.find()) {
            System.out.println("matched " + matcher.group());
        } else {
            System.out.println("Not matched");
        } */

        /*

        3a + 4b - 6a +3c

        -3a + 4b + 3c

        currOperator = +

        {
           a: -3

           sum:

        }



         */
    }

    private static void evaluate() {
        Map<Character, Integer> store = new HashMap<>();
        String input = "-3a + 4b + 3c -7c +56d +7a + 90d";
        int curPos = 0;

        Set<Character> operators = Set.of('-', '+');
        char currOperator = '+';
        int currDigit = 0;

        while (curPos < input.length()) {
            char currChar = input.charAt(curPos);
            if (operators.contains(currChar)) {
                currOperator = currChar;
            }
            if (Character.isDigit(currChar)) {
                StringBuilder num = new StringBuilder();
                while (Character.isDigit(input.charAt(curPos))) {
                    num.append(input.charAt(curPos));
                    curPos += 1;
                }
                currDigit = Integer.parseInt(num.toString());
            }

            if (Character.isLetter(input.charAt(curPos))) {
                char key = input.charAt(curPos);
                int currVal = store.getOrDefault(key, 0);
                int newVal = 0;
                if (currOperator == '+') {
                    newVal = currVal + currDigit;
                } else if (currOperator == '-') {
                    newVal = currVal -currDigit;
                }
                store.put(key, newVal);
            }
            curPos += 1;
        }

        StringBuilder output = new StringBuilder();

        store.forEach((key, value) -> {
            String coefficient = (value > 0) ? "+" + value : value.toString();
            String each = coefficient + key + " ";
            output.append(each);
        });


        System.out.println(output);
    }
}