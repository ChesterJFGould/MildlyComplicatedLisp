package ui;

import model.CharStream;
import model.Environment;
import model.Pair;
import model.Sexpr;
import persistence.JsonIO;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

// The enclosing ui class.
public class MainUI extends JFrame {
    EnvironmentTree envTree;
    Environment env;

    // MODIFIES: this
    // EFFECT: Initializes this MainUI.
    public MainUI(Environment env) {
        super("Simple Lisp GUI");
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(layout);
        setMinimumSize(new Dimension(100, 100));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.env = env;
        this.envTree = new EnvironmentTree(env);

        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weighty = 1.0;
        c.weightx = 1.0;
        JScrollPane tree = new JScrollPane(this.envTree);
        layout.setConstraints(tree, c);
        add(tree);
        c.weighty = 0.05;
        JTextField input = new JTextField("");
        DefaultListModel outputList = new DefaultListModel<String>();

        this.constructorCont(input, outputList, layout, c);
    }

    // MODIFIES: this
    // EFFECT: Initializes this MainUI.
    private void constructorCont(JTextField input,
                                 DefaultListModel outputList,
                                 GridBagLayout layout,
                                 GridBagConstraints c) {
        input.addActionListener(createInputActionListener(input, outputList));
        layout.setConstraints(input, c);
        add(input);
        c.weighty = 2.0;
        JScrollPane output = new JScrollPane(new JList<>(outputList));
        layout.addLayoutComponent(output, c);
        add(output);

        JMenuBar menuBar = new JMenuBar();
        JMenu stateMenu = new JMenu("state");
        menuBar.add(stateMenu);

        MainUI ui = this;

        JMenuItem saveItem = stateMenu.add(createSaveAbstractAction());

        saveItem.setText("save");

        JMenuItem loadItem = stateMenu.add(createLoadAbstractAction());

        loadItem.setText("load");

        this.setJMenuBar(menuBar);

        setVisible(true);
    }

    // MODIFIES: this env
    // EFFECT: Creates a new ActionListener that handle user text input.
    private ActionListener createInputActionListener(JTextField input, DefaultListModel outputList) {
        Environment env = this.env;
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                input.setText("");

                outputList.add(0, "> " + e.getActionCommand());

                try {
                    Sexpr expr = Sexpr.read(new CharStream(e.getActionCommand()));

                    expr = expr.eval(env);

                    outputList.add(0, expr.toString());
                } catch (Exception err) {
                    outputList.add(0, "Error: " + err.getMessage());
                }

                update();
            }
        };
    }

    // MODIFIES: this
    // EFFECT: Creates an AbstractAction that opens a save dialogue.
    private AbstractAction createSaveAbstractAction() {
        MainUI ui = this;
        return new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(new File("data/"));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Simple Lisp Object Files", "slo");

                chooser.setFileFilter(filter);
                int status = chooser.showOpenDialog(ui);

                if (status == JFileChooser.APPROVE_OPTION) {
                    String path = chooser.getSelectedFile().getPath();

                    try {
                        JsonIO.write(env.toJson(), path);
                    } catch (Exception err) {
                        System.out.println(err.getMessage());
                    }

                    Environment.resetSerializedTags();
                    Pair.resetSerializedTags();
                }

                ui.update();
            }
        };
    }

    // MODIFIES: this
    // EFFECT: Creates an AbstractAction that opens a load dialogue.
    private AbstractAction createLoadAbstractAction() {
        MainUI ui = this;
        return new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(new File("data/"));
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Simple Lisp Object Files", "slo");

                chooser.setFileFilter(filter);
                int status = chooser.showOpenDialog(ui);

                if (status == JFileChooser.APPROVE_OPTION) {
                    String path = chooser.getSelectedFile().getPath();

                    Environment.resetHeap();
                    Pair.resetHeap();

                    try {
                        Environment newEnv = Environment.fromJson(JsonIO.read(path));
                        env.merge(newEnv);
                    } catch (Exception err) {
                        System.out.println(err.getMessage());
                    }

                    Environment.restoreHeapPointer();
                    Pair.restoreHeapPointer();

                }

                ui.update();
            }
        };
    }

    // MODIFIES: this
    // EFFECT: Updates this to reflect the env.
    public void update() {
        this.envTree.update();
        this.setVisible(true);
    }
}
