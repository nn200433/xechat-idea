package cn.xeblog.commons.util;

import cn.hutool.core.lang.Opt;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 服务器工具类
 *
 * @author nn200433
 * @date 2022-07-12 012 08:10:36
 */
public class TreeUtils {

    public static void main(String args[]) {
        // String path = "E:\\workspace\\Regex";
        // String path = "E:\\workspace_web\\app17a";
        // 通过剪贴板读入目录
        // String path =SysClipboardUtil.getSysClipboardText();
        // 使用当前目录作为目录
        String path = "E:\\下载\\监测项";
        // 保存根目录,后续要用到
        // mavenJaveWebFileNameFilter = new MavenJavaWebFileNameFilter(path);
        File dir = new File(path);
        printTreeDir(dir);
        // printTreeFileAndDir(dir, null);
    }

    /**
     * 打印dir表示的目录的目录树.
     *
     * @param dir 表示目录的File对象.
     */
    public static void printTreeDir(File dir) {
        System.out.println(dir.getAbsolutePath());
        printTreeDir(dir, "", 0);
    }

    /**
     * 打印目录树
     *
     * @param dir
     * @param prefix
     * @param deep
     */
    private static void printTreeDir(File dir, String prefix, int deep) {
        if (dir.isDirectory()) {
            // 生成目录下的子目录列表
            File[] dirList = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    // 该文件是目录,并且不能以点号开头,
                    // 点号开头的一般是隐藏文件
                    return new File(dir, name).isDirectory()
                            && !name.startsWith(".");
                }
            });
            String thisPrefix = "";
            String nextPrefix = "";
            for (int i = 0; i < dirList.length; i++) {
                if (deep >= 0) {
                    // 如果不是最后一个元素
                    if ((i + 1 < dirList.length)) {
                        // 不是最后一个目录都打印这个符号
                        thisPrefix = prefix + "├──";
                        // 下一个打印这符号表示展开目录
                        nextPrefix = prefix + "│ ";
                    } else {
                        // 最后一个子目录项
                        thisPrefix = prefix + "└──";
                        //
                        nextPrefix = prefix + "  ";
                    }
                }
                System.out.println(thisPrefix + " " + dirList[i].getName());
                printTreeDir(dirList[i], nextPrefix, deep + 1);
            }
        }
    }


    /**
     * 打印文件跟目录
     *
     * @param dir
     * @param fileNameFilter
     */
    public static void printTreeFileAndDir(File dir, FilenameFilter fileNameFilter) {
        // 打印根目录
        System.out.println(dir.getAbsolutePath());
        printTreeFileAndDir(dir, fileNameFilter, "", 0);
    }

    /**
     * 打印目录树
     *
     * @param dir    目录
     * @param prefix 前缀,需要打印在文件或者目录之前
     * @param deep   深度
     */
    private static void printTreeFileAndDir(File dir, FilenameFilter fileNameFilter, String prefix, int deep) {
        // 列出目录下的子目录
        File[] childs = dir.listFiles(Opt.ofNullable(fileNameFilter).orElse(new DefaultFileNameFilter()));
        // 遍历子目录
        for (int i = 0; i < childs.length; i++) {
            // 本次递归的前缀
            String thisPrefix = "";
            // 下一个递归的前缀
            String nextPrefix = "";
            if (deep >= 0) {
                // 如果不是最后一个元素
                if ((i + 1 < childs.length)) {
                    nextPrefix = prefix + "│ ";
                    thisPrefix = prefix + "├──";
                } else {
                    thisPrefix = prefix + "└──";
                    nextPrefix = prefix + "  ";
                }
            }
            System.out.println(thisPrefix + " " + childs[i].getName());
            if (childs[i].isDirectory()) {
                printTreeFileAndDir(childs[i], fileNameFilter, nextPrefix,
                        deep + 1);
            }
        }
    }

    /**
     * maven java web文件名过滤器
     *
     * @author nn200433
     * @date 2022-07-26 08:34:54
     */
    public static class MavenJavaWebFileNameFilter implements FilenameFilter {
        private String rootPath = null;

        MavenJavaWebFileNameFilter(String rootPath) {
            this.rootPath = rootPath;
        }

        @Override
        public boolean accept(File dir, String name) {
            // 该文件不能以点号开头,
            // 也不能以txt文件结尾
            // 文件名称不能是bin
            // 文件名称不能是target
            if (name.startsWith(".") || name.endsWith(".txt")) {
                return false;
            }
            // 如果是根目录
            if (rootPath.equals(dir.getAbsolutePath())) {
                // 第一级目录下的bin目录,
                // 或者target目录(maven,输出)不是java项目必须目录所以不输出
                if ("bin".equals(name) || "target".equals(name)
                        || "test".equals(name)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * 默认文件名过滤器
     *
     * @author nn200433
     * @date 2022-07-26 08:41:38
     */
    public static class DefaultFileNameFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return true;
        }
    }

}