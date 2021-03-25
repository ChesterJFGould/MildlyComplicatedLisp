package ui;

import model.Environment;
import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {
    EnvironmentTree envTree;
    Environment env;

    public MainUI(Environment env) {
        super("Simple Lisp GUI");
        setLayout(new GridLayout(1, 1));
        setMinimumSize(new Dimension(100, 100));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.env = env;
        this.envTree = new EnvironmentTree(env);
        add(this.envTree);

        setVisible(true);
    }

    public void update() {
        this.remove(envTree);
        this.envTree = new EnvironmentTree(env);
        this.add(this.envTree);
        setVisible(true);
    }
}
