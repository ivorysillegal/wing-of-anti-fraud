package com.gduf.util;

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
}
