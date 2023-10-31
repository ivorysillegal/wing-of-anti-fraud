package com.gduf.util;

import com.gduf.pojo.community.VoteSecondComment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TreeUtils {

    public static  <T> TreeNode<T> createTree(List<T> list, Function<T, Integer> idGetter) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        Map<Integer, List<TreeNode<T>>> nodeMap = new HashMap<>();
        Map<Integer, List<T>> parentChildMap = new HashMap<>();

        // 创建节点映射
        for (T item : list) {
            int parentId = idGetter.apply(item);
            TreeNode<T> node = new TreeNode<>(item);
            List<TreeNode<T>> parentNodeList = nodeMap.get(parentId);
            if (parentNodeList == null) {
                parentNodeList = new ArrayList<>();
                nodeMap.put(parentId, parentNodeList);
            }
            parentNodeList.add(node);
            parentChildMap.putIfAbsent(parentId, new ArrayList<>());
            parentChildMap.get(parentId).add(item);
        }

        // 构建树形结构
        TreeNode<T> root = null;
        for (T item : list) {
            int parentId = idGetter.apply(item);
            List<TreeNode<T>> parentNodeList = nodeMap.get(parentId);
            if (parentNodeList != null) {
                if (parentId == 0) {
                    root = parentNodeList.get(0);
                } else {
                    List<T> children = parentChildMap.get(parentId);
                    if (children != null) {
                        for (T childItem : children) {
                            if (childItem != item) {  // 排除当前节点本身
                                TreeNode<T> childNode = nodeMap.get(idGetter.apply(childItem)).get(0);
                                parentNodeList.get(0).children.add(childNode);
                            }
                        }
                    }
                }
            }
        }

        return root;
    }

    //    找爹
    public static List<TreeNode<VoteSecondComment>> findParent(List<TreeNode<VoteSecondComment>> comments) {

//        对于每一个节点均进行操作
        for (TreeNode<VoteSecondComment> comment : comments) {

            // 防止checkForComodification(),而建立一个新集合
            ArrayList<TreeNode<VoteSecondComment>> fatherChildren = new ArrayList<>();

            // 递归处理子级的回复，即回复内有回复
            findChildren(comment, fatherChildren);

            // 将递归处理后的集合放回父级的孩子中
            comment.setChildren(fatherChildren);
        }
        return comments;
    }

    public static void findChildren(TreeNode<VoteSecondComment> parent, List<TreeNode<VoteSecondComment>> fatherChildren) {

        // 找出直接子级
//        查找已经这个爹已经有的孩子
        List<TreeNode<VoteSecondComment>> comments = parent.getChildren();

        // 遍历直接子级的子级
//        对于每个孩子 孩子也有可能是爹
        for (TreeNode<VoteSecondComment> comment : comments) {

            // 若非空，则还有子级，递归
            if (!comment.getChildren().isEmpty()) {
                findChildren(comment, fatherChildren);
            }

//            递归到了最后 这个逼没有孩子
            // 已经到了最底层的嵌套关系，将该回复放入新建立的集合
            fatherChildren.add(comment);

//            加入了集合之后 这个人就没有孩子了（一开始的目的就是 解除所有的父子关系）
            // 容易忽略的地方：将相对底层的子级放入新建立的集合之后
            // 则表示解除了嵌套关系，对应的其父级的子级应该设为空
            comment.setChildren(new ArrayList<>());
        }
    }
}
