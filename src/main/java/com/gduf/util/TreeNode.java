package com.gduf.util;

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
    T value;
    List<TreeNode<T>> children;

    public TreeNode(T value) {
        this.value = value;
        this.children = new ArrayList<>();
    }
}