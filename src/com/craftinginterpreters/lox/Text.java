package com.craftinginterpreters.lox;

class Text {
    /**
     * 给出行号，从源文件中找出该行的所有内容 没找到时返回空字符串
     * 
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
                if (source.charAt(i) == '\n')
                    break;
            }
            return source.substring(0, i);
        } else {
            int start = ordinalIndexOf(source, '\n', line - 1);
            if (start != -1) {
                int end = start + 1;
                for (; end < source.length(); end++) {
                    if (source.charAt(end) == '\n')
                        break;
                }
                return source.substring(start + 1, end);
            } else {
                return "";
            }
        }
    }

    /**
     * 在source中寻找c第index次出现的位置 index必须大于0
     * 
     * @param source
     * @param c
     * @param index
     * @return
     */
    static int ordinalIndexOf(String source, char c, int index) {
        int count = 0;
        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == c) {
                count++;
                if (count == index)
                    return i;
            }
        }
        return -1;
    }
}
