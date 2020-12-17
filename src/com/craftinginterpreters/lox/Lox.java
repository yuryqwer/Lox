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
        String lineContent = findLineContent(line, source);
        report(line, column, lineContent, message);
    }

    /**
     * 给出行号，从源文件中找出该行的所有内容
     * 没找到时返回空字符串
     * @param line
     * @param source
     * @return
     */
    static String findLineContent(int line, String source) {
        // 要找的某一行n就是第 n-1 个'\n'和第 n 个 '\n' 中间的字符串
        if (line <= 0) {
            return "";
        } else if (line == 1) {
            int i = 0;
            for (; i < source.length(); i++) {
                if (source.charAt(i) == '\n') break;
            }
            return source.substring(0, i);
        } else {
            int start = ordinalIndexOf(source, '\n', line-1);
            if (start != -1) {
                int end = start + 1;
                for (; end < source.length(); end++) {
                    if (source.charAt(end) == '\n') break;
                }
                return source.substring(start+1, end);
            } else {
                return "";
            }
        }
    }

    static int ordinalIndexOf(String source, char c, int index) {
        int count = 0;
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == c) {
                count++;
                if (count == index) return i;
            }
        }
        return -1;
    }

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
                          column - 
                          1;
        for (int i = 0; i < blankspaces; i++) s.append(" ");
        s.append("^--Here.");
        System.err.println(s);

        hadError = true;
    }
}
