package org.example;

public class Main {
    public static void main(String[] args) {
        String input = """
                       class Square extends Point {
                       
                         def add() { 
                            return x * 2; 
                         } 
                       
                         def loopAround(x) {
                             do { 
                                x += 1; 
                             } while (x < 10)
                             
                             let s = \"Hello world\"; 
                             let i = 0; 
                             while (i < s.length) {
                                s[i]; 
                                i += 1; 
                             }
                         } 
                         
                            
                         def instance(x, y) {
                            console.log("Getting instance");
                            let d = null;
                            d = new Point(x, y);
                            if (d == null) {
                               return x;
                            } else {
                               return d;
                            }
                         }
                       }
                """;
        Parser parser = new Parser();
        System.out.println(parser.parse(input.trim()));
    }
}