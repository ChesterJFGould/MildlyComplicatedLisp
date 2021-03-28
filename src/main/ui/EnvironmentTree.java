package ui;

import model.Environment;
import model.Sexpr;
import model.CharStream;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.HashMap;

// A GUI representation of an Environment.
public class EnvironmentTree extends JTree {
    private Environment env;
    private DefaultMutableTreeNode root;

    // MODIFIES: this
    // EFFECT: Initializes this EnvironmentTree.
    public EnvironmentTree(Environment env) {
        super(new DefaultMutableTreeNode());

        this.env = env;
        this.root = (DefaultMutableTreeNode) (this.getModel().getRoot());

        EnvironmentTree tree = this;

        MouseListener ml = new MouseAdapterPrime(this);

        this.addMouseListener(ml);

        this.update();
    }

    // A MouseAdapter for an EnvironmentTree.
    private static class MouseAdapterPrime extends MouseAdapter {
        private EnvironmentTree tree;

        // MODIFIES: this
        // EFFECT: Initializes this MouseAdapterPrime.
        public MouseAdapterPrime(EnvironmentTree tree) {
            this.tree = tree;
        }

        // MODIFIES: tree
        // EFFECT: Handles a mouse press on the EnvironmentTree.
        // Left double click on a leaf node opens a new value dialogue.
        // Right click on a leaf node opens a new name dialogue.
        public void mousePressed(MouseEvent e) {
            int row = tree.getRowForLocation(e.getX(), e.getY());

            if (row == -1) {
                return;
            }

            TreePath path = tree.getPathForLocation(e.getX(), e.getY());
            Object selected = path.getLastPathComponent();

            if (!(selected instanceof EnvNode)) {
                return;
            }

            EnvNode node = (EnvNode) selected;

            mousePressedCont(node, e);
        }

        // MODIFIES: tree
        // EFFECT: Handles a mouse press on the EnvironmentTree.
        // Left double click on a leaf node opens a new value dialogue.
        // Right click on a leaf node opens a new name dialogue.
        public void mousePressedCont(EnvNode node, MouseEvent e) {
            if (e.getButton() == 1 && e.getClickCount() == 2) {
                String input = JOptionPane.showInputDialog(tree, "New Value: ");
                if (input == null) {
                    return;
                }
                try {
                    Sexpr expr = Sexpr.read(new CharStream(input));
                    expr = expr.eval(tree.env);

                    node.setValue(expr);
                } catch (Exception err) {
                    System.out.println(err.getMessage());
                }
            } else if (e.getButton() == 3) {
                String input = JOptionPane.showInputDialog(tree, "New Name: ");
                if (input == null) {
                    return;
                }

                node.setVar(input);
            }

            tree.update();

        }

    }

    // MODIFIES: this
    // EFFECT: Generates and returns a TreeNode version of the given env.
    private DefaultMutableTreeNode genEnvNodes(Environment env) {
        DefaultMutableTreeNode node;
        if (env.getParent() == null) {
            root.setUserObject("Root Environment");
            node = root;
        } else {
            DefaultMutableTreeNode parent = genEnvNodes(env.getParent());
            node = new DefaultMutableTreeNode("Child Environment");
            parent.add(node);
        }

        for (HashMap.Entry<String, Sexpr> entry : env.getVars().entrySet()) {
            node.add(new EnvNode(entry.getKey(), entry.getValue(), env));
        }

        return node;
    }

    // MODIFIES: this
    // EFFECT: Updates this EnvironmentTree to reflect the env.
    public void update() {
        this.root.removeAllChildren();
        this.genEnvNodes(this.env);
        ((DefaultTreeModel) (this.getModel())).reload();
        this.expand();
    }

    // MODIFIES: this
    // EFFECT: Expands all the nodes in this tree.
    public void expand() {
        for (int i = 0; i < this.getRowCount(); i++) {
            this.expandRow(i);
        }
    }

    // A TreeNode that represents a variable in an Environment.
    private static class EnvNode extends DefaultMutableTreeNode {
        private String var;
        private Sexpr value;
        private Environment env;

        // MODIFIES: this
        // EFFECT: Initializes this EnvNode and its parent.
        public EnvNode(String var, Sexpr value, Environment env) {
            super(var + ": " + value.toString());

            this.var = var;
            this.value = value;
            this.env = env;
        }

        // EFFECT: Returns var.
        public String getVar() {
            return this.var;
        }

        // EFFECT: Returns value.
        public Sexpr getValue() {
            return this.value;
        }

        // MODIFIES: this, env
        // EFFECT: Sets the value of this EnvNode if it exists in the env.
        public void setValue(Sexpr value) {
            if (this.env.set(this.var, value)) {
                this.value = value;
            }
        }

        // MODIFIES: this, env
        // EFFECT: Removes this var from the env and puts this value into the env
        // under the given var.
        public void setVar(String var) {
            this.env.remove(this.var);
            this.env.put(var, this.value);
            this.var = var;
        }
    }
}
