package battleships;

import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Marek
 */
public class Battleships {

    private static final JFrame window = new JFrame();
    private static BattleMap battleMap;

    private static final JButton start = new JButton("Start");
    private static final JButton save = new JButton("Zapisz stan gry");
    private static final JButton load = new JButton("Wczytaj stan gry");
    private static final JButton newGame = new JButton("Nowa gra");
    private static final JButton setVehicles = new JButton("Ustaw ręcznie pojazdy");
    private static final JButton setDefaultVehicles = new JButton("Ustaw pojazdy losowo");
    private static final JButton rotation = new JButton("Obrót");

    public static void main(String[] args) {
        setFlags();
        ShowMap();
        addButtons();
    }

    private static void setFlags() {
        Flags.gameIsOn = false;
        Flags.myTurn = false;
        Flags.placingPlane = false;
        Flags.rotation = 0;
        Flags.placingVehicles = false;
        Flags.ifHorisontally = true;
        Flags.typeOfVehicle = Content.MARINE;
        Flags.numOfPlacedVehicles = 0;
        Flags.length = 4;

        Flags.enemyVehiclesLeft = Flags.myVehiclesLeft = 41; // without planes so far
    }

    private static void ShowMap() {
        window.setLayout(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        battleMap = BattleMap.getInstance();
        window.add(battleMap);
        window.getContentPane().setBackground(new Color(26, 116, 255));

        window.pack();

        window.setSize(1010, 500);
        window.setTitle("Statki");
        window.setResizable(false);
        window.setVisible(true);
    }

    private static void addButtons() {
        int width = 180;
        int height = 30;

        //first row of buttons
        start.setBounds(20, 380, width, height);
        start.addActionListener((ActionEvent e) -> {
            if (Flags.numOfPlacedVehicles == 16) {
                Flags.gameIsOn = true;
                enableButtons();
                Flags.myTurn = true;

            } else {
                JOptionPane.showMessageDialog(null, "Proszę ustawić wszystkie pojazdy.");
            }

        });
        window.add(start);

        newGame.setBounds(20 + width + 10, 380, width, height);
        newGame.addActionListener((ActionEvent e) -> {
            Flags.gameIsOn = false;
            //Flags.placingVehicles = true;

            Flags.enemyVehiclesLeft = Flags.myVehiclesLeft = 36; // without planes so far

            enableButtons();
            battleMap.clearMyMap();
            battleMap.setEnemyDefaultVehicles();
        });
        window.add(newGame);

        setVehicles.setBounds(20 + width * 2 + 10 * 2, 380, width, height);
        setVehicles.addActionListener((ActionEvent e) -> {
            battleMap.clearMyMap();
            Flags.typeOfVehicle = Content.MARINE;
            Flags.numOfPlacedVehicles = 0;
            Flags.length = 4;
            Flags.placingVehicles = true;
            Flags.placingPlane = true;
        });
        window.add(setVehicles);

        setDefaultVehicles.setBounds(20 + width * 3 + 10 * 3, 380, width, height);
        setDefaultVehicles.addActionListener((ActionEvent e) -> {
            battleMap.setMyDefaultVehicles();
        });
        window.add(setDefaultVehicles);

        //second row of buttons
        save.setBounds(20, 380 + height + 10, width, height);
        save.addActionListener((ActionEvent e) -> {
            if (Flags.numOfPlacedVehicles == 16 || Flags.gameIsOn) {
                if (battleMap.saveMap()) {
                    JOptionPane.showMessageDialog(null, "Zapisano stan gry");
                } else {
                    JOptionPane.showMessageDialog(null, "Stan gry nie został zapisany");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Nie mogę zapisać pustej mapy.");
            }
        });
        window.add(save);

        load.setBounds(20 + width + 10, 380 + height + 10, width, height);
        load.addActionListener((ActionEvent e) -> {
            if (battleMap.loadMap()) {
                JOptionPane.showMessageDialog(null, "Wczytano stan gry");
            } else {
                JOptionPane.showMessageDialog(null, "Stan gry nie został odczytany");
            }
        });
        window.add(load);

        rotation.setBounds(20 + width * 2 + 10 * 2, 380 + height + 10, width, height);
        rotation.addActionListener((ActionEvent e) -> {
            Flags.ifHorisontally = !Flags.ifHorisontally;
            Flags.rotation = (Flags.rotation + 1) % 4;
        });
        window.add(rotation);
    }

    private static void enableButtons() {
        setDefaultVehicles.setEnabled(!Flags.gameIsOn);
        setVehicles.setEnabled(!Flags.gameIsOn);
        rotation.setEnabled(!Flags.gameIsOn);
    }

}
