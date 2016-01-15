package battleships;

import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Marek
 */
public class BattleMap extends JPanel {

    private static BattleMap instance = null;

    private final int arrayWidth = 22;
    private final int arrayHeight = 14;
    public final int xShoreline = 13;

    private final int ySizeOfDisplayedArray = arrayWidth * 20;
    private final int xStartPosition = 20;
    private final int yStartPosition = 40;

    private Label myVehiclesLeft = new Label();
    private Label enemyVehiclesLeft = new Label();

    private MyField[][] myMap = new MyField[arrayWidth][arrayHeight];
    private EnemyField[][] enemyMap = new EnemyField[arrayWidth][arrayHeight];
    private Content[][] myMapContent = new Content[arrayWidth][arrayHeight];
    private Content[][] enemyMapContent = new Content[arrayWidth][arrayHeight];

    public static BattleMap getInstance() {
        if (instance == null) {
            instance = new BattleMap(20, 20);
        }
        return instance;
    }

    private BattleMap(int x, int y) {
        setLayout(null);
        setBounds(x, y, 2 * ySizeOfDisplayedArray + 2 * 20 + 40, arrayHeight * 20 + 20 + 40);
        setBackground(new Color(204, 153, 0));

        fillMapsWithEarthAndWater();

        addFields();
        setTitle("Moja mapa", 20, 15);
        setTitle("Mapa przeciwnika", 20 + ySizeOfDisplayedArray + 40, 15);

        myVehiclesLeft.setBounds(410, 15, 50, 20);
        enemyVehiclesLeft.setBounds(410 + ySizeOfDisplayedArray + 40, 15, 50, 20);
        add(myVehiclesLeft);
        add(enemyVehiclesLeft);

        setEnemyDefaultVehicles();
        showStats();
    }

    private void setTitle(String title, int x, int y) {
        setFont(Font.decode("Calibri-bold-20"));

        Label label = new Label(title);
        label.setBounds(x, y, 200, 20);
        add(label);

        Label vehiclesLeft = new Label("Pozostało:");
        vehiclesLeft.setBounds(x + 280, y, 100, 20);
        add(vehiclesLeft);
    }

    private void showStats() {
        setFont(Font.decode("Calibri-bold-20"));
        myVehiclesLeft.setText(Flags.myVehiclesLeft + "/41");
        enemyVehiclesLeft.setText(Flags.enemyVehiclesLeft + "/41");
    }

    private void addFields() {
        fillMapsWithEarthAndWater();
        for (int i = 0; i < arrayWidth; i++) {
            for (int j = 0; j < arrayHeight; j++) {
                add(myMap[i][j]);
                add(enemyMap[i][j]);
            }
        }
    }

    private void fillMapsWithEarthAndWater() {
        fillMyMap();
        fillEnemyMap();
    }

    private void fillMyMap() {
        for (int i = 0; i < arrayWidth; i++) {
            for (int j = 0; j < arrayHeight; j++) {
                if (i < xShoreline) {
                    myMapContent[i][j] = Content.MARINE;
                    myMap[i][j] = new MyField(xStartPosition + i * Field.fieldSize,
                            yStartPosition + j * Field.fieldSize,
                            i,
                            j,
                            myMapContent[i][j],
                            this);
                } else {
                    myMapContent[i][j] = Content.LAND;
                    myMap[i][j] = new MyField(xStartPosition + i * Field.fieldSize,
                            yStartPosition + j * Field.fieldSize,
                            i,
                            j,
                            myMapContent[i][j],
                            this);
                }
            }
        }
    }

    private void fillEnemyMap() {
        for (int i = 0; i < arrayWidth; i++) {
            for (int j = 0; j < arrayHeight; j++) {
                if (i < xShoreline) {
                    enemyMapContent[i][j] = Content.MARINE;
                    enemyMap[i][j] = new EnemyField(xStartPosition + i * Field.fieldSize + ySizeOfDisplayedArray + 40,
                            yStartPosition + j * Field.fieldSize,
                            i,
                            j,
                            enemyMapContent[i][j],
                            this);
                } else {
                    enemyMapContent[i][j] = Content.LAND;
                    enemyMap[i][j] = new EnemyField(xStartPosition + i * Field.fieldSize + ySizeOfDisplayedArray + 40,
                            yStartPosition + j * Field.fieldSize,
                            i,
                            j,
                            enemyMapContent[i][j],
                            this);
                }
            }
        }
    }

    public void clearMyMap() {
        clearMap(myMapContent, myMap);
    }

    private void clearEnemyMap() {
        clearMap(enemyMapContent, enemyMap);
    }

    private void clearMap(Content[][] contentMap, Field[][] map) {
        setDefaultMapContent(contentMap, map);
        refreshMap(contentMap, map);
    }

    private void setDefaultMapContent(Content[][] contentMap, Field[][] map) {
        for (int i = 0; i < arrayWidth; i++) {
            for (int j = 0; j < arrayHeight; j++) {
                if (i < xShoreline) {
                    contentMap[i][j] = Content.MARINE;
                } else {
                    contentMap[i][j] = Content.LAND;
                }
            }
        }
    }

    private void refreshMap(Content[][] contentMap, Field[][] map) {
        for (int i = 0; i < arrayWidth; i++) {
            for (int j = 0; j < arrayHeight; j++) {
                map[i][j].setContent(contentMap[i][j]);
            }
        }
    }

    public void shootOnEnemyMap(int xIndex, int yIndex) {
        switch (enemyMapContent[xIndex][yIndex]) {
            case LAND:
                enemyMapContent[xIndex][yIndex] = Content.LAND_EXPLOSION;
                Flags.myTurn = false;
                break;
            case MARINE:
                enemyMapContent[xIndex][yIndex] = Content.MARINE_EXPLOSION;
                Flags.myTurn = false;
                break;
            case VEHICLE:
                Flags.enemyVehiclesLeft--;
                enemyMapContent[xIndex][yIndex] = Content.BROKEN_VEHICLE;
                break;
            case MARINE_EXPLOSION:
            case LAND_EXPLOSION:
            case BROKEN_VEHICLE:
                break;
        }
        enemyMap[xIndex][yIndex].setContent(enemyMapContent[xIndex][yIndex]);
        showStats();
        if (Flags.enemyVehiclesLeft == 0) {
            JOptionPane.showMessageDialog(null, "Wygrałeś!");
        }
    }

    public void shootOnMyMap() {
        while (!Flags.myTurn) {
            if (Flags.myVehiclesLeft != 0 && Flags.enemyVehiclesLeft != 0) {
                Random rnd = new Random();
                int x = rnd.nextInt(arrayWidth);
                int y = rnd.nextInt(arrayHeight);

                switch (myMapContent[x][y]) {
                    case LAND:
                        myMapContent[x][y] = Content.LAND_EXPLOSION;
                        Flags.myTurn = true;
                        break;
                    case MARINE:
                        myMapContent[x][y] = Content.MARINE_EXPLOSION;
                        Flags.myTurn = true;
                        break;
                    case VEHICLE:
                        Flags.myVehiclesLeft--;
                        myMapContent[x][y] = Content.BROKEN_VEHICLE;
                        break;
                    case MARINE_EXPLOSION:
                    case LAND_EXPLOSION:
                    case BROKEN_VEHICLE:
                        break;
                }
                myMap[x][y].setContent(myMapContent[x][y]);
            }
            showStats();
            if (Flags.myVehiclesLeft == 0) {
                JOptionPane.showMessageDialog(null, "Przegrałeś!");
            }
        }
    }

    public boolean ifCanShootOnMyMap(int xIndex, int yIndex) {
        switch (myMapContent[xIndex][yIndex]) {
            case BROKEN_VEHICLE:
            case LAND_EXPLOSION:
            case MARINE_EXPLOSION:
                return false;
        }
        return true;
    }

    public void showVehicle(int xIndex, int yIndex) {
        if (Flags.ifHorisontally) {
            if (ifICanPlaceVehicle(xIndex, yIndex, Flags.length - 1, Flags.ifHorisontally, Flags.typeOfVehicle)) {
                for (int i = 0; i < Flags.length; i++) {
                    try {
                        myMap[xIndex + i][yIndex].setImage(Content.VEHICLE);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                }
            } else {
                for (int i = 0; i < Flags.length; i++) {
                    try {
                        myMap[xIndex + i][yIndex].setImage(Content.BROKEN_VEHICLE);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                }
            }
        } else {
            if (ifICanPlaceVehicle(xIndex, yIndex, Flags.length - 1, Flags.ifHorisontally, Flags.typeOfVehicle)) {
                for (int i = 0; i < Flags.length; i++) {
                    try {
                        myMap[xIndex][yIndex + i].setImage(Content.VEHICLE);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                }
            } else {
                for (int i = 0; i < Flags.length; i++) {
                    try {
                        myMap[xIndex][yIndex + i].setImage(Content.BROKEN_VEHICLE);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                }
            }
        }
    }

    public void showPlane(int xIndex, int yIndex) {
        switch (Flags.rotation) {
            case 0:
                if (ifICanPlacePlane(xIndex, yIndex, Flags.rotation)) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            myMap[xIndex + i][yIndex].setImage(Content.VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            myMap[xIndex + 1][yIndex + i].setImage(Content.VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                } else {
                    for (int i = 0; i < 3; i++) {
                        try {
                            myMap[xIndex + i][yIndex].setImage(Content.BROKEN_VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            myMap[xIndex + 1][yIndex + i].setImage(Content.BROKEN_VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                }
                break;

            case 1:
                if (ifICanPlacePlane(xIndex, yIndex, Flags.rotation)) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            myMap[xIndex + i][yIndex].setImage(Content.VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            myMap[xIndex + 2][yIndex + i - 1].setImage(Content.VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                } else {
                    for (int i = 0; i < 3; i++) {
                        try {
                            myMap[xIndex + i][yIndex].setImage(Content.BROKEN_VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            myMap[xIndex + 2][yIndex + i - 1].setImage(Content.BROKEN_VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                }
                break;

            case 2:
                if (ifICanPlacePlane(xIndex, yIndex, Flags.rotation)) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            myMap[xIndex + i][yIndex].setImage(Content.VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            myMap[xIndex + 1][yIndex - i].setImage(Content.VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                } else {
                    for (int i = 0; i < 3; i++) {
                        try {
                            myMap[xIndex + i][yIndex].setImage(Content.BROKEN_VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            myMap[xIndex + 1][yIndex - i].setImage(Content.BROKEN_VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                }
                break;

            case 3:
                if (ifICanPlacePlane(xIndex, yIndex, Flags.rotation)) {
                    for (int i = 0; i < 3; i++) {
                        try {
                            myMap[xIndex + i][yIndex + 1].setImage(Content.VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            myMap[xIndex][yIndex + i].setImage(Content.VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                } else {
                    for (int i = 0; i < 3; i++) {
                        try {
                            myMap[xIndex + i][yIndex + 1].setImage(Content.BROKEN_VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                        try {
                            myMap[xIndex][yIndex + i].setImage(Content.BROKEN_VEHICLE);
                        } catch (ArrayIndexOutOfBoundsException ex) {
                        }
                    }
                }
                break;
        }
    }

    public void hideVehicle(int xIndex, int yIndex) {
        if (Flags.ifHorisontally) {
            for (int i = 0; i < Flags.length; i++) {
                try {
                    myMap[xIndex + i][yIndex].refreshField();
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
        } else {
            for (int i = 0; i < Flags.length; i++) {
                try {
                    myMap[xIndex][yIndex + i].refreshField();
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
            }
        }
    }

    public void hidePlane(int xIndex, int yIndex) {
        switch (Flags.rotation) {
            case 0:
                for (int i = 0; i < 3; i++) {
                    try {
                        myMap[xIndex + i][yIndex].refreshField();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                    try {
                        myMap[xIndex + 1][yIndex + i].refreshField();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                }
                break;

            case 1:
                for (int i = 0; i < 3; i++) {
                    try {
                        myMap[xIndex + i][yIndex].refreshField();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                    try {
                        myMap[xIndex + 2][yIndex + i - 1].refreshField();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                }
                break;

            case 2:
                for (int i = 0; i < 3; i++) {
                    try {
                        myMap[xIndex + i][yIndex].refreshField();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                    try {
                        myMap[xIndex + 1][yIndex - i].refreshField();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                }
                break;

            case 3:
                for (int i = 0; i < 3; i++) {
                    try {
                        myMap[xIndex + i][yIndex + 1].refreshField();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                    try {
                        myMap[xIndex][yIndex + i].refreshField();
                    } catch (ArrayIndexOutOfBoundsException ex) {
                    }
                }
                break;
        }
    }

    public void setEnemyDefaultVehicles() {
        clearEnemyMap();
        setDefaultVehicles(enemyMapContent, enemyMap);
        Flags.numOfPlacedVehicles = 0;
    }

    public void setMyDefaultVehicles() {
        clearMyMap();
        Flags.numOfPlacedVehicles = 0;
        setDefaultVehicles(myMapContent, myMap);
        Flags.placingVehicles = false;
    }

    private void setDefaultVehicles(Content[][] contentMap, Field[][] map) {
        Random rnd = new Random();
        boolean canBePlaced = false;
        int x, y;

        randomPlacePlane(contentMap, map);

        for (int i = 1; i < 5; i++) { // tells how many vehicles of each type should create (1x4(one long vehicle), 2x3, 3x2, 4x1)
            for (int j = 0; j < i; j++) { //tells which length (0-3 means lenghts 1-4) is being checked now
                randomPlaceVehicle(j, Content.MARINE, contentMap, map);
                Flags.numOfPlacedVehicles++;
            }
            for (int j = 1; j < i; j++) { //tells which length (1-3 means lenghts 2-4) is being checked now
                randomPlaceVehicle(j, Content.LAND, contentMap, map);
                Flags.numOfPlacedVehicles++;
            }
        }
    }

    private void randomPlaceVehicle(int lenght, Content type, Content[][] contentMap, Field[][] map) {
        Random rnd = new Random();
        boolean ifHorisontally = rnd.nextBoolean();
        boolean canBePlaced = false;
        int x, y;

        while (!canBePlaced) {
            if (ifHorisontally) {
                if (type == Content.MARINE) {
                    x = rnd.nextInt(xShoreline - lenght);
                    y = rnd.nextInt(arrayHeight);
                } else {
                    x = rnd.nextInt(arrayWidth - xShoreline - lenght) + xShoreline;
                    y = rnd.nextInt(arrayHeight);
                }

                canBePlaced = ifCanPlaceVehicle(x, y, lenght, ifHorisontally, contentMap, type);
                if (canBePlaced) { // puts vehicle on map (mapContent)
                    placeVehicle(x, y, lenght, ifHorisontally, contentMap, map);
                }
            } else { //vertically
                if (type == Content.MARINE) {
                    x = rnd.nextInt(xShoreline);
                    y = rnd.nextInt(arrayHeight - lenght);
                } else {
                    x = rnd.nextInt(arrayWidth - xShoreline) + xShoreline;
                    y = rnd.nextInt(arrayHeight - lenght);
                }

                canBePlaced = ifCanPlaceVehicle(x, y, lenght, ifHorisontally, contentMap, type);
                if (canBePlaced) { // puts vehicle on map (mapContent)
                    placeVehicle(x, y, lenght, ifHorisontally, contentMap, map);
                }
            }
        }

    }

    public void placeMyVehicle(int xIndex, int yIndex, int lenght, boolean ifHorisontally) {
        placeVehicle(xIndex, yIndex, lenght, ifHorisontally, myMapContent, myMap);
        Flags.numOfPlacedVehicles++;

        if (Flags.placingVehicles) {
            if (Flags.numOfPlacedVehicles == 0) {
                Flags.length = 4;
            } else if (Flags.numOfPlacedVehicles == 1) {
                Flags.length = 3;
            } else if (Flags.numOfPlacedVehicles == 3) {
                Flags.length = 2;
            } else if (Flags.numOfPlacedVehicles == 6) {
                Flags.length = 1;
            } else if (Flags.numOfPlacedVehicles == 10) {
                Flags.length = 4;
                Flags.typeOfVehicle = Content.LAND;
            } else if (Flags.numOfPlacedVehicles == 11) {
                Flags.length = 3;
            } else if (Flags.numOfPlacedVehicles == 13) {
                Flags.length = 2;
            } else if (Flags.numOfPlacedVehicles == 16) {
                Flags.placingVehicles = false;
            }
        }
    }

    private void placeVehicle(int xIndex, int yIndex, int lenght, boolean ifHorisontally, Content[][] contentMap, Field[][] map) {
        if (ifHorisontally) {
            for (int k = 0; k <= lenght; k++) {
                contentMap[xIndex + k][yIndex] = Content.VEHICLE;
                map[xIndex + k][yIndex].setContent(contentMap[xIndex + k][yIndex]);
                map[xIndex + k][yIndex].refreshField();
            }
        } else {
            for (int k = 0; k <= lenght; k++) {
                contentMap[xIndex][yIndex + k] = Content.VEHICLE;
                map[xIndex][yIndex + k].setContent(contentMap[xIndex][yIndex + k]);
                map[xIndex][yIndex + k].refreshField();
            }
        }
    }

    private void randomPlacePlane(Content[][] contentMap, Field[][] map) {
        Random rnd = new Random();
        boolean canBePlaced = false;
        int x, y, rotation;

        while (!canBePlaced) {
            x = rnd.nextInt(arrayWidth);
            y = rnd.nextInt(arrayHeight);
            rotation = rnd.nextInt(4);

            canBePlaced = ifCanPlacePlane(x, y, rotation, contentMap);
            if (canBePlaced) {
                placePlane(x, y, rotation, contentMap, map);
            }
        }
    }

    public void placeMyPlane(int xIndex, int yIndex, int rotation) {
        placePlane(xIndex, yIndex, rotation, myMapContent, myMap);
    }

    private void placePlane(int xIndex, int yIndex, int rotation, Content[][] contentMap, Field[][] map) {
        switch (rotation) {
            case 0:
                placeVehicle(xIndex, yIndex, 2, true, contentMap, map);
                placeVehicle(xIndex + 1, yIndex + 1, 1, false, contentMap, map);
                break;
            case 1:
                placeVehicle(xIndex, yIndex, 1, true, contentMap, map);
                placeVehicle(xIndex + 2, yIndex - 1, 2, false, contentMap, map);
                break;
            case 2:
                placeVehicle(xIndex, yIndex, 2, true, contentMap, map);
                placeVehicle(xIndex + 1, yIndex - 2, 1, false, contentMap, map);
                break;
            case 3:
                placeVehicle(xIndex, yIndex, 2, false, contentMap, map);
                placeVehicle(xIndex + 1, yIndex + 1, 1, true, contentMap, map);
                break;
        }
        Flags.placingPlane = false;
    }

    public boolean ifICanPlaceVehicle(int xIndex, int yIndex, int lenght, boolean ifHorisontally, Content type) {
        return ifCanPlaceVehicle(xIndex, yIndex, lenght, ifHorisontally, myMapContent, type);
    }

    private boolean ifCanPlaceVehicle(int xIndex, int yIndex, int lenght, boolean ifHorisontally, Content[][] contentMap, Content type) {
        if (ifHorisontally) {
            for (int i = 0; i <= lenght; i++) { //this loop checks if vehicle can by placed horisontally 
                if (!ifCanPlacePoint(xIndex + i, yIndex, contentMap, type)) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i <= lenght; i++) { //this loop checks if vehicle can by placed horisontally 
                if (!ifCanPlacePoint(xIndex, yIndex + i, contentMap, type)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean ifICanPlacePoint(int xIndex, int yIndex, Content expectedContent) {
        return ifCanPlacePoint(xIndex, yIndex, myMapContent, expectedContent);
    }

    private boolean ifCanPlacePoint(int xIndex, int yIndex, Content[][] contentMap, Content expectedContent) {
        try {
            if (expectedContent != null && contentMap[xIndex][yIndex] != expectedContent) {
                return false;
            } else {
                int i = (xIndex == 0) ? 0 : -1;
                int maxX = (xIndex == (arrayWidth - 1)) ? 0 : 1;

                int j;
                int jStart = (yIndex == 0) ? 0 : -1;
                int maxY = (yIndex == (arrayHeight - 1)) ? 0 : 1;

                for (; i <= maxX; i++) {
                    for (j = jStart; j <= maxY; j++) {
                        if (contentMap[xIndex + i][yIndex + j] == Content.VEHICLE) {
                            return false;
                        }
                    }
                }

                return true;
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            return false;
        }
    }

    public boolean ifICanPlacePlane(int xIndex, int yIndex, int rotation) {
        return ifCanPlacePlane(xIndex, yIndex, rotation, myMapContent);
    }

    private boolean ifCanPlacePlane(int xIndex, int yIndex, int rotation, Content[][] contentMap) {
        switch (rotation) {
            case 0:
                if (ifCanPlaceVehicle(xIndex, yIndex, 2, true, contentMap, null) && ifCanPlaceVehicle(xIndex + 1, yIndex + 1, 1, false, contentMap, null)) {
                    return true;
                }
                break;
            case 1:
                if (ifCanPlaceVehicle(xIndex, yIndex, 1, true, contentMap, null) && ifCanPlaceVehicle(xIndex + 2, yIndex - 1, 2, false, contentMap, null)) {
                    return true;
                }
                break;
            case 2:
                if (ifCanPlaceVehicle(xIndex, yIndex, 2, true, contentMap, null) && ifCanPlaceVehicle(xIndex + 1, yIndex - 2, 1, false, contentMap, null)) {
                    return true;
                }
                break;
            case 3:
                if (ifCanPlaceVehicle(xIndex, yIndex, 2, false, contentMap, null) && ifCanPlaceVehicle(xIndex + 1, yIndex + 1, 1, true, contentMap, null)) {
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean saveMap() {
        boolean result;
        FileWriter fileWriter = null;
        String lineSeparator = System.getProperty("line.separator");

        try {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            fileChooser.showOpenDialog(this);

            File file = fileChooser.getSelectedFile();
            fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.write((Flags.myTurn ? "true" : "false") + lineSeparator);
            printWriter.write((Flags.gameIsOn ? "true" : "false") + lineSeparator);
            printWriter.write(Flags.myVehiclesLeft + lineSeparator);
            printWriter.write(Flags.enemyVehiclesLeft + lineSeparator);
            printWriter.write((Flags.placingPlane ? "true" : "false") + lineSeparator);
            printWriter.write(Flags.rotation + lineSeparator);
            printWriter.write((Flags.placingVehicles ? "true" : "false") + lineSeparator);
            printWriter.write(Flags.numOfPlacedVehicles + lineSeparator);
            printWriter.write(Flags.length + lineSeparator);
            printWriter.write((Flags.ifHorisontally ? "true" : "false") + lineSeparator);
            printWriter.write(Flags.typeOfVehicle.toString() + lineSeparator);

            for (int i = 0; i < arrayWidth; i++) {
                for (int j = 0; j < arrayHeight; j++) {
                    printWriter.write(myMapContent[i][j].toString() + "#");
                }
                printWriter.write(lineSeparator);
            }

            for (int i = 0; i < arrayWidth; i++) {
                for (int j = 0; j < arrayHeight; j++) {
                    printWriter.write(enemyMapContent[i][j].toString() + "#");
                }
                printWriter.write(lineSeparator);
            }

            result = true;
        } catch (IOException ex) {
            result = false;
        } catch (Exception ex) {
            result = false;
        } finally {
            try {
                fileWriter.close();
            } catch (Exception ex) {
            }
        }
        return result;
    }

    public boolean loadMap() {
        boolean result;
        FileReader fileReader = null;
        String lineSeparator = System.getProperty("line.separator");
        Content[][] myMapContentBuff = new Content[arrayWidth][arrayHeight];
        Content[][] enemyMapContentBuff = new Content[arrayWidth][arrayHeight];

        try {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
            fileChooser.showOpenDialog(this);

            File file = fileChooser.getSelectedFile();
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Flags.myTurn = Boolean.parseBoolean(bufferedReader.readLine());
            Flags.gameIsOn = Boolean.parseBoolean(bufferedReader.readLine());
            Flags.myVehiclesLeft = Integer.parseInt(bufferedReader.readLine());
            Flags.enemyVehiclesLeft = Integer.parseInt(bufferedReader.readLine());
            Flags.placingPlane = Boolean.parseBoolean(bufferedReader.readLine());
            Flags.rotation = Integer.parseInt(bufferedReader.readLine());
            Flags.placingVehicles = Boolean.parseBoolean(bufferedReader.readLine());
            Flags.numOfPlacedVehicles = Integer.parseInt(bufferedReader.readLine());
            Flags.length = Integer.parseInt(bufferedReader.readLine());
            Flags.ifHorisontally = Boolean.parseBoolean(bufferedReader.readLine());
            Flags.typeOfVehicle = Content.valueOf(bufferedReader.readLine().toUpperCase());

            String[] buff;

            for (int i = 0; i < arrayWidth; i++) {
                buff = bufferedReader.readLine().split("#");
                for (int j = 0; j < arrayHeight; j++) {
                    myMapContent[i][j] = Content.valueOf(buff[j]);
                }
            }

            for (int i = 0; i < arrayWidth; i++) {
                buff = bufferedReader.readLine().split("#");
                for (int j = 0; j < arrayHeight; j++) {
                    enemyMapContent[i][j] = Content.valueOf(buff[j]);
                }
            }

            refreshMap(myMapContent, myMap);
            refreshMap(enemyMapContent, enemyMap);

            result = true;
        } catch (IOException ex) {
            result = false;
        } catch (Exception ex) {
            result = false;
        } finally {
            try {
                fileReader.close();
            } catch (Exception ex) {
            }
        }
        return result;
    }

}
