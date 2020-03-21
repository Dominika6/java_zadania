import java.util.*;
import java.lang.*;
import java.io.*;

//klasa odpowiedzialna za przeprowadzanie operacji na stosie
abstract class Stack<Item> implements Iterable<Item> {
    private int n;
    private Node first;

    private class Node {
        private Item item;
        private Node next;
    }
    public Stack() {
        first = null;
        n = 0;
    }
    public boolean isEmpty() {
        return first == null;
    }
    public int size() {
        return n;
    }
    //umieszcza element na wierzchołku stosu
    public void push(Item item) {
        Node oldfirst = first;
        first = new Node();
        first.item = item;
        first.next = oldfirst;
        n++;
    }
    //usuwa element bedący na wierzchołku stosu
    public Item pop() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        Item item = first.item;
        first = first.next;
        n--;
        return item;
    }
    //zwraca pierwszy element stosu (ten na wierzchu)
    public Item peek() {
        if (isEmpty()) throw new NoSuchElementException("Stack underflow");
        return first.item;
    }
}
//główny program
class ExpressionParser {
    private static String operators = "+-*/^";
    private static String delimiters = "() " + operators;
    public static boolean flag = true;
    //usuwamy spacje
    private static boolean isDelimiter(String token) {
        if (token.length() != 1) return false;
        for (int i = 0; i < delimiters.length(); i++) {
            //.charAt zwraca określony znak z łańcuch znaków
            if (token.charAt(0) == delimiters.charAt(i)) return true;
        }
        return false;
    }
    //czy jest operatorem
    private static boolean isOperator(String token) {
        //minus unarny
        if (token.equals("u-")) return true;
        for (int i = 0; i < operators.length(); i++) {
            if (token.charAt(0) == operators.charAt(i)) return true;
        }
        return false;
    }
    //czy jest funkcją (pierwiastek, sześcian, 10 potęga )
    private static boolean isFunction(String token) {
        if (token.equals("sqrt") || token.equals("cube") || token.equals("pow10")) return true;
        return false;
    }
    //ustawiamy priorytety operacji
    private static int priority(String token) {
        if (token.equals("(")) return 1;
        if (token.equals("+") || token.equals("-")) return 2;
        if (token.equals("*") || token.equals("/")) return 3;
        if (token.equals("^")) return 4;
        return 4;
    }

    public static List<String> parse(String infix) {
        List<String> postfix = new ArrayList<String>();
        Deque<String> stack = new ArrayDeque<String>();
        StringTokenizer tokenizer = new StringTokenizer(infix, delimiters, true);
        String prev = "";
        String curr = "";
        //.hasMoreTokens jeżeli jeszcze są to zwraca leksemy (wyrazy)
        while (tokenizer.hasMoreTokens()) {
            curr = tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens() && isOperator(curr)) {
                System.out.println("Wyrazenie nie jest poprawne");
                flag = false;
                return postfix;
            }
            //kolejno sprawdzamy wyrazy (leksemy) wpisanego na konsoli wyrazenia
            // i odpowiedznio wstawiamy ja na stos
            if (curr.equals(" ")) continue;
            if (isFunction(curr)) stack.push(curr);
            else if (isDelimiter(curr)) {
                if (curr.equals("(")) stack.push(curr);

                //co jeśli napotkamy nawias zamykający
                else if (curr.equals(")")) {
                    while (!stack.peek().equals("(")) {
                        postfix.add(stack.pop());
                        if (stack.isEmpty()) {
                            System.out.println("Problem z nawiasami");
                            flag = false;
                            return postfix;
                        }
                    }
                    stack.pop();
                    if (!stack.isEmpty() && isFunction(stack.peek())) {
                        postfix.add(stack.pop());
                    }
                }
                //wyciągamy górne elementy stosu do listy wynikowej, dopóki priorytet bieżącego operatora
                //jest mniejszy/równy od tego znajdującego się na górze stosu
                //następnie umieszczamy obecny operator na stos
                else {
                    if (curr.equals("-") && (prev.equals("") || (isDelimiter(prev)  && !prev.equals(")")))) {
                        curr = "u-";
                    }
                    else {
                        while (!stack.isEmpty() && (priority(curr) <= priority(stack.peek()))) {
                            postfix.add(stack.pop());
                        }
                    }
                    stack.push(curr);
                }
            }
            else {
                postfix.add(curr);
            }
            prev = curr;
        }
        //po przeczytaniu wszystkich elementów z konsoli, wpisujemy elementy ze stosu do listy wynikowej
        while (!stack.isEmpty()) {
            if (isOperator(stack.peek())) postfix.add(stack.pop());
            else {
                System.out.println("Problem z nawiasami");
                flag = false;
                return postfix;
            }
        }
        //zwracamy listtę wynikową
        return postfix;
    }
}

class onp {
    //obliczamy wartość danego wyrażenia
    public static Double calc(List<String> postfix) {
        Deque<Double> stack = new ArrayDeque<Double>();
        //jeśli napotkamy liczbę, umieszczamy ją na stosie
        for (String x : postfix) {
            if (x.equals("sqrt")) stack.push(Math.sqrt(stack.pop()));
            else if (x.equals("cube")) {
                Double tmp = stack.pop();
                stack.push(tmp * tmp * tmp);
            }
            //dla kolejnych operacji wykonujemy odpowiednie działania
            else if (x.equals("pow10")) stack.push(Math.pow(10, stack.pop()));
            else if (x.equals("+")) stack.push(stack.pop() + stack.pop());
            else if (x.equals("-")) {
                Double b = stack.pop();
                Double a = stack.pop();
                stack.push(a - b);
            }
            else if (x.equals("*")) stack.push(stack.pop() * stack.pop());
            else if (x.equals("/")) {
                Double b = stack.pop();
                Double a = stack.pop();
                stack.push(a / b);
            }
            else if (x.equals("^")){
                Double exponent = stack.pop();
                Double base = stack.pop();
                stack.push(Math.pow(base, exponent));
            }
            else if (x.equals("u-")) stack.push(-stack.pop());
            else stack.push(Double.valueOf(x));
        }
        //zwracamy ostatni element stosu
        return stack.pop();
    }
    public static void main (String[] args) {
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        ExpressionParser n = new ExpressionParser();
        List<String> expression = n.parse(s);
        boolean flag = n.flag;
        if (flag) {
            for (String x : expression) System.out.print(x + " ");
            System.out.println();
            System.out.println(calc(expression));
        }
    }
}
