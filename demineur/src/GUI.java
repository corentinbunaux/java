import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.MenuBar;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.event.ActionEvent;

/**
 * Graphical user Inferface
 */

public class GUI extends JPanel {
    private int score = 0;
    private String[] levels = { "Facile", "Moyen", "Difficile" };
    private String selectedLevel = "Facile";
    JPanel panelTop, panelMines, panelBottom;
    JLabel labelScore;
    Case[][] listCases;

    GUI(App app) {
        panelTop = new JPanel();
        panelMines = new JPanel();
        panelBottom = new JPanel();
        labelScore = new Compteur();

        setLayout(new GridLayout(0, 1));
        initMenu(app);
        initPanelTop(app);
        initPanelMines(app, selectedLevel);
        initPanelBottom(app);
    }

    Case getCase(int x, int y) {
        return listCases[x][y];
    }

    void initPanelTop(App app) {
        panelTop.add(labelScore);

        // Niveaux de difficultés
        JComboBox<String> comboBoxLevel = new JComboBox<>(this.levels);
        comboBoxLevel.setSelectedIndex(0);
        comboBoxLevel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (GUI.this.selectedLevel != (String) comboBoxLevel.getSelectedItem()) {
                    GUI.this.selectedLevel = (String) comboBoxLevel.getSelectedItem();
                    restartGame(app, selectedLevel);
                }
            }
        });
        panelTop.add(comboBoxLevel);
        add(panelTop);
    }

    void initPanelMines(App app, String selectedLevel) {
        setLayout(new GridLayout(0, 1));

        // JPanel des mines
        panelMines.setLayout(new GridLayout(app.getChamp().getSizes()[indexOf(selectedLevel)],
                app.getChamp().getSizes()[indexOf(selectedLevel)]));

        listCases = new Case[app.getChamp().getSizes()[indexOf(selectedLevel)]][app.getChamp()
                .getSizes()[indexOf(selectedLevel)]];

        // creation des JLabels pour les mines
        for (int i = 0; i < app.getChamp().getSizes()[indexOf(selectedLevel)]; i++) {
            for (int j = 0; j < app.getChamp().getSizes()[indexOf(selectedLevel)]; j++) {
                Case labelMine;
                labelMine = new Case(app, i, j);
                panelMines.add(labelMine);
                listCases[i][j] = labelMine;
            }
        }
        add(panelMines);
    }

    void initPanelBottom(App app) {
        JButton buttonRestart = new JButton("Recommencer");
        JButton buttonQuit = new JButton("Quitter");
        buttonRestart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame(app, selectedLevel);
            }
        });

        buttonQuit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        panelBottom.add(buttonRestart);
        panelBottom.add(buttonQuit);
        add(panelBottom);
    }

    void initMenu(App app) {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuPartie = new JMenu("Partie");
        menuBar.add(menuPartie);
        JMenuItem mQuitter = new JMenuItem("Quitter", KeyEvent.VK_Q);
        menuPartie.add(mQuitter);
        mQuitter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        app.setJMenuBar(menuBar);
    }

    void restartGame(App app, String selectedLevel) {
        GUI.this.score = 0;
        app.setGameStarted(false);
        panelMines.removeAll();
        initPanelMines(app, selectedLevel);
        add(panelBottom);
        app.pack();
    }

    JPanel getPanelTop() {
        return panelTop;
    }

    JPanel getPanelMines() {
        return panelMines;
    }

    JPanel getPanelBottom() {
        return panelBottom;
    }

    JLabel getLabelScore() {
        return labelScore;
    }

    int indexOf(String selectedLevel) {
        switch (selectedLevel) {
            case "Facile":
                return 0;
            case "Moyen":
                return 1;

            case "Difficile":
                return 2;
            default:
                break;
        }
        return 0;
    }

    String getSelectedLevel() {
        return this.selectedLevel;
    }

    void propagate(App app, int xCoord, int yCoord) {
        if (app.getChamp().getVal(xCoord, yCoord) != 0) {
            app.getGUI().getCase(yCoord, xCoord).setIsDiscovered(true);
            return;
        } else {
            app.getGUI().getCase(yCoord, xCoord).setIsDiscovered(true);

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == j) {
                        continue;
                    }

                    int newX = xCoord + i;
                    int newY = yCoord + j;

                    if (newX >= 0
                            && newX < app.getChamp().getSizes()[app.getGUI().indexOf(app.getGUI().getSelectedLevel())]
                            && newY >= 0
                            && newY < app.getChamp().getSizes()[app.getGUI().indexOf(app.getGUI().getSelectedLevel())]
                            && !app.getGUI().getCase(newY, newX).getIsDiscovered()) {
                        propagate(app, newX, newY);
                    }
                }
            }
        }
    }
}