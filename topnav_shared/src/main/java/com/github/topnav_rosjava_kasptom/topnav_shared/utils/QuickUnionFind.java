package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

/**
 * https://algs4.cs.princeton.edu/15uf/UF.java.html
 */

public class QuickUnionFind {
    private int[] id;
    private int[] sz;

    public QuickUnionFind(int maxId) {
        id = new int[maxId];
        sz = new int[maxId];

        for (int i = 0; i < id.length; i++) {
            id[i] = i + 1;
            sz[i] = 1;
        }
    }

    public boolean find(int p, int q) {
        return root(p) == root(q);
    }

    public void union(int p, int q) {
        int i = root(p);
        int j = root(q);

        if (sz[i - 1] < sz[j - 1]) {
            id[i - 1] = j;
            sz[j - 1] += sz[i - 1];
        } else {
            id[j - 1] = i;
            sz[i - 1] += sz[j - 1];
        }
    }

    private int root(int p) {
        while (p != id[p - 1]) {
            id[p - 1] = id[id[p - 1] - 1];
            p = id[p - 1];
        }
        return p;
    }

    public int[] getParentIds() {
        return id;
    }
}
