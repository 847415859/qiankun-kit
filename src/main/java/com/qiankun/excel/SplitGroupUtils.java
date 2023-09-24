//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.qiankun.excel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SplitGroupUtils {
    public SplitGroupUtils() {
    }

    public static void main(String[] args) {
        String text1 = "list[list[0][var]].name";
        List<SplitGroupUtils.Group> list1 = group(text1, "[", "]", true);
        System.out.println(text1);
        System.out.println(list1);
        String text2 = "1+(2+3*(1+3))*5";
        List<SplitGroupUtils.Group> list2 = group(text2, "(", ")", true);
        System.out.println(text2);
        System.out.println(list2);
    }

    public static List<SplitGroupUtils.Group> group(String text, String left, String right, boolean fill) {
        List<Integer> leftList = findIndex(text, left);
        List<Integer> rightList = findIndex(text, right);
        if (leftList.size() != rightList.size()) {
        }

        List<SplitGroupUtils.Group> list = new ArrayList(leftList.size());

        for(int i = leftList.size() - 1; i >= 0; --i) {
            int leftIdx = (Integer)leftList.get(i);

            for(int j = 0; j < rightList.size(); ++j) {
                int rightIdx = (Integer)rightList.get(j);
                if (leftIdx < rightIdx) {
                    list.add(new SplitGroupUtils.Group(leftIdx, rightIdx, 1, text, left, right));
                    rightList.remove(j);
                    break;
                }
            }
        }

        list.sort(Comparator.comparingInt(SplitGroupUtils.Group::getLeft));
        List<SplitGroupUtils.Group> list1 = buildGroupTree(list, -1, 2147483647);
        if (fill) {
            if (list1 == null) {
                list1 = new ArrayList();
            }

            fillValues((List)list1, text, 0, text.length() - 1);
        }

        return (List)list1;
    }

    private static void fillValues(List<SplitGroupUtils.Group> list, String text, int start, int end) {
        if (list.isEmpty()) {
            if (start <= end) {
                list.add(new SplitGroupUtils.Group(start, end, 0, text, (String)null, (String)null));
            }

        } else {
            SplitGroupUtils.Group group = null;

            for(int i = 0; i < list.size(); ++i) {
                group = (SplitGroupUtils.Group)list.get(i);
                if (group.left > start) {
                    list.add(i++, new SplitGroupUtils.Group(start, group.left - 1, 0, text, (String)null, (String)null));
                    start = group.right + 1;
                }

                if (group.children != null) {
                    fillValues(group.children, text, group.left + 1, group.right - 1);
                }
            }

            if (group.right < end) {
                list.add(new SplitGroupUtils.Group(group.right + 1, end, 0, text, (String)null, (String)null));
            }

        }
    }

    private static List<SplitGroupUtils.Group> buildGroupTree(List<SplitGroupUtils.Group> list, int left, int right) {
        List<SplitGroupUtils.Group> trees = new ArrayList();

        for(int i = 0; i < list.size(); ++i) {
            SplitGroupUtils.Group iGroup = (SplitGroupUtils.Group)list.get(i);
            if (iGroup.left > left && iGroup.right < right) {
                if (i + 1 <= list.size() - 1) {
                    SplitGroupUtils.Group group_1 = (SplitGroupUtils.Group)list.get(i + 1);
                    if (iGroup.left < group_1.left && iGroup.right > group_1.right) {
                        iGroup.children = buildGroupTree(list, iGroup.left, iGroup.right);
                        left = iGroup.right;
                    }
                }

                trees.add(iGroup);
            }
        }

        return trees.isEmpty() ? null : trees;
    }

    private static List<Integer> findIndex(String text, String s) {
        int index = -1;
        ArrayList list = new ArrayList();

        while((index = text.indexOf(s, index + 1)) != -1) {
            list.add(index);
        }

        return list;
    }

    public static class Group {
        private int left;
        private int right;
        private int type;
        private String value;
        private String context;
        private List<SplitGroupUtils.Group> children;

        public Group() {
        }

        Group(int left, int right, int type, String sub, String l, String r) {
            this.left = left;
            this.right = right;
            this.type = type;
            this.value = sub.substring(left, right + 1);
            this.context = sub.substring(left + (l != null ? l.length() : 0), right - (r != null ? r.length() : 0) + 1);
        }

        public int getLeft() {
            return this.left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getRight() {
            return this.right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        public int getType() {
            return this.type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getContext() {
            return this.context;
        }

        public void setContext(String context) {
            this.context = context;
        }

        public List<SplitGroupUtils.Group> getChildren() {
            return this.children;
        }

        public void setChildren(List<SplitGroupUtils.Group> children) {
            this.children = children;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("{ ");
            sb.append("left: ").append(this.left);
            sb.append(", right: ").append(this.right);
            sb.append(", type: ").append(this.type);
            sb.append(", value: ").append(this.value);
            sb.append(", context: ").append(this.context);
            sb.append(", children: ").append(this.children);
            sb.append(" }");
            return sb.toString();
        }
    }
}
