package com.craftinginterpreters.lox;

class Token {
    final TokenType type;  // Token类型
    final String lexeme;   // 对应源码中的文本
    final Object literal;  // 字面量，没有的话为null
    final int line;        // 在源码中的行数
    final int startColumn; // 开头位置在源码中的列数，从0开始

    Token(TokenType type, String lexeme, Object literal, int line, int startColumn) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.startColumn = startColumn;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
