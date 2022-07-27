package cn.xeblog.commons.util;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.io.FilenameFilter;

/**
 * 目录树生成工具类
 *
 * @author nn200433
 * @date 2022-07-12 012 08:10:36
 */
public class TreeUtils {

    /**
     * 获取目录树
     *
     * @param dir 目录
     * @return {@link String }
     * @author nn200433
     */
    public static String getTreeDir(File dir) {
        StringBuffer treeBuffer = new StringBuffer();
        // 打印根目录
        treeBuffer.append(dir.getName()).append(StrUtil.CRLF);
        getTreeDir(dir, "", 0, treeBuffer);
        return treeBuffer.toString();
    }

    /**
     * 获取目录树
     *
     * @param dir        目录
     * @param prefix     前缀
     * @param deep       深度
     * @param treeBuffer 目录树保存文本
     * @author nn200433
     */
    private static void getTreeDir(File dir, String prefix, int deep, StringBuffer treeBuffer) {
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
                treeBuffer.append(thisPrefix + " " + dirList[i].getName()).append(StrUtil.CRLF);
                getTreeDir(dirList[i], nextPrefix, deep + 1, treeBuffer);
            }
        }
    }


    /**
     * 获取目录树跟文件
     *
     * @param dir            目录
     * @param fileNameFilter 文件名过滤器
     * @return {@link String }
     * @author nn200433
     */
    public static String getTreeFileAndDir(File dir, FilenameFilter fileNameFilter) {
        StringBuffer treeBuffer = new StringBuffer();
        // 打印根目录
        treeBuffer.append(dir.getName()).append(StrUtil.CRLF);
        getTreeFileAndDir(dir, fileNameFilter, "", 0, treeBuffer);
        return treeBuffer.toString();
    }

    /**
     * 获取目录树跟文件
     *
     * @param dir            目录
     * @param fileNameFilter 文件名过滤器
     * @param prefix         前缀
     * @param deep           深度
     * @param treeBuffer     目录树保存文本
     * @author nn200433
     */
    private static void getTreeFileAndDir(File dir, FilenameFilter fileNameFilter, String prefix, int deep,
                                          StringBuffer treeBuffer) {
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
            treeBuffer.append(thisPrefix + " " + childs[i].getName()).append(StrUtil.CRLF);
            if (childs[i].isDirectory()) {
                getTreeFileAndDir(childs[i], fileNameFilter, nextPrefix, deep + 1, treeBuffer);
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