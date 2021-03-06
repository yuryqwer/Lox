package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    /**
     * 该标志用来检测代码是否有错误
     * 如果有错误则不运行代码
     */
    static boolean hadError = false;

    /**
     * 运行入口
     * 一共有两种用法
     *   1. 脚本运行：jlox 脚本路径
     *   2. REPL：jlox
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);  // 用法错误，退出码64
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        // 代码错误，退出码65
        if (hadError) {
            System.exit(65);
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            } else {
                run(line);
            }
            hadError = false;  // REPL模式下每轮循环结束将错误标志置为false
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    static void error(int line, int column, String source, String message) {
        String lineContent = Text.findLineContent(line, source);
        report(line, column, lineContent, message);
    }

    /**
     * 错误报告结果的格式如下所示
     * Error: Unexpected character. 
     * 
     *     1 | function(a, b@);
     *                      ^--Here.
     * @param line 在源码中的行数
     * @param column 要报告的位置在该行的列数
     * @param line_content 行的内容
     * @param message 错误信息
     */
    private static void report(int line, 
                               int column, 
                               String line_content, 
                               String message) {
        final String SEPARATOR = " | ";
        final String OFFSET = "    ";
        System.err.println("Error: " + message);
        System.err.println("\n" + OFFSET + line + SEPARATOR + line_content);

        StringBuilder s = new StringBuilder();
        int blankspaces = OFFSET.length() + 
                          String.valueOf(line).length() + 
                          SEPARATOR.length() + 
                          column;
        for (int i = 0; i < blankspaces; i++) s.append(" ");
        s.append("^--Here.");
        System.err.println(s);

        hadError = true;
    }
}
