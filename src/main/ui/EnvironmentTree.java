package ui;

import model.Environment;
import model.Sexpr;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;

public class EnvironmentTree extends JTree {
    private Environment env;

    public EnvironmentTree(Environment env) {
        super(genEnvNode(env));
    }

    private static DefaultMutableTreeNode genEnvNode(Environment env) {
        DefaultMutableTreeNode node;
        if (env.getParent() == null) {
            node = new DefaultMutableTreeNode("Root Environment");
        } else {
            DefaultMutableTreeNode parent = genEnvNode(env.getParent());
            node = new DefaultMutableTreeNode("Child Environment");
            parent.add(node);
        }

        for (HashMap.Entry<String, Sexpr> entry : env.getVars().entrySet()) {
            node.add(new DefaultMutableTreeNode(entry.getKey() + ": " + entry.getValue().toString()));
        }

        return node;
    }

    public void update() {

    }
}
