package logic;
import java.util.Stack;

public class Calculator {

    //add closing parentesis and create new bruch to pull and merge with master
    public static int calculate(String s){
        // Format the input string to simplify parsing
        s = s.replaceAll("\\s", ""); // remove whitespace
        s = s.replaceAll("(?<=\\d)\\s*\\(", "*(");
        s = s.replaceAll("\\)\\(", ")*("); // add multiplication operator between parentheses
        s = s.replaceAll("(?<=[0-9])\\(", "-1*("); // replace "-(" with "-1*("
        s = s.replaceAll("(?<=\\()-", "0-"); // replace leading "-" with "0-"
        s = s.replaceAll("(?<=\\()\\+", "0+"); // replace leading "+" with "0+"


        // Check that all parentheses are properly closed
        int openCount = 0;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '(') {
                openCount++;
            } else if (ch == ')') {
                if (openCount == 0) {
                    throw new IllegalArgumentException("Parentheses not balanced");
                }
                openCount--;
            }
        }
        if (openCount != 0) {
            throw new IllegalArgumentException("Parentheses not balanced");
        }

//        // auto closing parentheses at the end of expression
//        int openCount = s.length() - s.replaceAll("\\(", "").length();
//        int closeCount = s.length() - s.replaceAll("\\)", "").length();
//        for (int i = 0; i < openCount - closeCount; i++) {
//            s += ")";
//        }

        // stack for operands
        Stack<Integer> numStack = new Stack<>();

        //stack for operators
        Stack<Character> opStack = new Stack<>();


        // Parse the input string
        for(int i = 0; i < s.length(); i++){
            char ch = s.charAt(i);
            int sign = 1;
            // Parse the number
            if(Character.isDigit(ch)) {
                int val = 0;
                while (i < s.length() && Character.isDigit(s.charAt(i))) {
                    val = val * 10 + (s.charAt(i) - '0');
                    i++;
                }
                i--;
                val *= sign;
                numStack.push(val);
            }
            if (ch == '(') {
                // Push open parentheses onto operator stack
                opStack.push(ch);

            } else if (ch == ')') {
                // Pop operator stack until matching open parenthesis is found
                while (!opStack.isEmpty() && opStack.peek() != '(') {
                    char op = opStack.pop();
                    int num2 = numStack.pop();
                    int num1 = numStack.pop();
                    int res = applyOp(num1, num2, op);
                    numStack.push(res);
                }
                if (!opStack.isEmpty() && opStack.peek() == '(') {
                    opStack.pop();
                }

            } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {

                // Handle negative numbers following an operator or open parenthesis
                if ((ch == '-' && (i == 0 || s.charAt(i - 1) == '(' || s.charAt(i - 1) == '+'
                        || s.charAt(i - 1) == '-' || s.charAt(i - 1) == '*' || s.charAt(i - 1) == '/'))
                        || (ch == '+' && (i == 0 || s.charAt(i - 1) == '('))) {
                    numStack.push(0);
                }
                // Pop operators with higher or equal precedence from stack and calculate result
                while (!opStack.isEmpty() && hasPrecedence(opStack.peek(), ch)) {
                    char op = opStack.pop();
                    int num2 = numStack.pop();
                    int num1 = numStack.pop();
                    int res = applyOp(num1, num2, op);
                    numStack.push(res);
                }

                // Push current operator onto operator stack
                opStack.push(ch);
            }

        }
        // Pop remaining operators from stack and calculate result
        while (!opStack.isEmpty()) {
            char op = opStack.pop();
            int num2 = numStack.pop(); // pop the second number
            int num1 = numStack.pop(); // pop the first number (11 + 18) * 20 - 2
            int res = applyOp(num1, num2, op);
            numStack.push(res);
        }
        // Result is on top of the operand stack
        return numStack.pop();
    }

    public static boolean hasPrecedence(char op1, char op2){
        if(op1 == '*' || op1 == '/') return true;
        if(op1 == '+' || op1 == '-') {
            if(op2 == '+' || op2 == '-') return true;
        }
        return false;
    }

    public static int applyOp(int num1, int num2, char op) {
        switch(op) {
            case '+':
                return num1 + num2;
            case '-':
                return num1 - num2;
            case '*':
                return num1 * num2;
            case '/':
                if(num2 == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                return num1 / num2;
        }
        return 0;
    }
}