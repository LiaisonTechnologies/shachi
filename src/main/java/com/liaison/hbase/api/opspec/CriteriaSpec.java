package com.liaison.hbase.api.opspec;

import java.io.Serializable;

import com.liaison.hbase.util.TreeNode;

public abstract class CriteriaSpec<C extends CriteriaSpec<C, P>, P extends TreeNode<P>> extends StatefulSpec<C, P> implements Serializable {
    
    private static final long serialVersionUID = 7087926388191014497L;
    
    public P and() {
        return getParent();
    }

    public CriteriaSpec(final P parent) throws IllegalArgumentException {
        super(parent);
    }
}
