package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.craftinginterpreters.lox.TokenType.*;

class Scanner {
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int column = 0;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));  // 最后自己加一个EOF
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    // 匹配到注释时一直运行到这句结尾
                    // 当已经碰到换行符时不会运行advance
                    // 因此可以被下一次scanToken中的case '\n'处理
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    // 支持c语言风格的多行注释
                    comment();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // 忽略空白字符
                break;

            case '\n':
                line++;
                break;

            case '"': string(); break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Lox.error(line, column, source, "Unexpected character.");
                }
                break;
        }
    }

    private void comment() {
        // 用栈来处理嵌套注释
        Stack<Character> s = new Stack<>();
        s.push('c');

        while (!s.empty() && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                advance();
            } else if (peek() == '*' && peekNext() == '/') {
                s.pop();
                advance();
                advance();
            } else if (peek() == '/' && peekNext() == '*') {
                s.push('c');
                advance();
                advance();
            } else {
                advance();
            }
        }

        if (isAtEnd() && !s.empty()) {
            Lox.error(line, column, source, "Unterminated comment.");
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void number() {
        while (isDigit(peek())) advance();

        // 匹配小数点部分
        if (peek() == '.' && isDigit(peekNext())) {
            // 消耗 "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        // 没有发现第二个双引号就结束，直接报告错误
        if (isAtEnd()) {
            Lox.error(line, column, source, "Unterminated string.");
            return;
        }

        // 消耗第二个双引号
        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    /**
     * 结合了advance和peek的功能
     * 只有匹配才消耗一个字符
     * @param expected
     * @return
     */
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        advance();
        return true;
    }

    /**
     * 看一下当前位置的字符，但并不消耗它
     * @return
     */
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    /**
     * 判断是否字母或者下划线
     * @param c
     * @return
     */
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
               c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * 判断是否到达文件末尾
     * @return
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * 消耗当前位置的字符
     * @return
     */
    private char advance() {
        char c = source.charAt(current);
        if (c == '\n') {
            column = 0;
        } else {
            column++;
        }
        current++;
        return c;
    }

    /**
     * 方法重载
     * 非字面量的token可以调用此方法
     * 在方法内部调用了两个参数的addToken方法
     * @param type
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}
