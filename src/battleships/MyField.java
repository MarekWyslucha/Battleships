package battleships;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import javax.swing.ImageIcon;

/**
 *
 * @author Marek
 */
public class MyField extends Field {

    MouseAdapter mouseAdapter = new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            if (Flags.placingVehicles) {
                if (Flags.placingPlane) {
                    battleMap.showPlane(xIndex, yIndex);
                } else {
                    battleMap.showVehicle(xIndex, yIndex);
                }
            }
        }

        public void mouseExited(java.awt.event.MouseEvent evt) {
            if (Flags.placingVehicles) {
                if (Flags.placingPlane) {
                    battleMap.hidePlane(xIndex, yIndex);
                } else {
                    battleMap.hideVehicle(xIndex, yIndex);
                }
            }
        }
    };

    public MyField(int x, int y, int xIndex, int yIndex, Content content, BattleMap battleMap) {
        super(x, y, xIndex, yIndex, content, battleMap);
        refreshField();
        addMouseListener();

        addActionListener((ActionEvent e) -> {
            if (Flags.placingVehicles) {
                if (Flags.placingPlane) {
                    if (battleMap.ifICanPlacePlane(xIndex, yIndex, Flags.rotation)) {
                        battleMap.placeMyPlane(xIndex, yIndex, Flags.rotation);
                    }
                }
                if (battleMap.ifICanPlaceVehicle(xIndex, yIndex, Flags.length - 1, Flags.ifHorisontally, Flags.typeOfVehicle)) {
                    battleMap.placeMyVehicle(xIndex, yIndex, Flags.length - 1, Flags.ifHorisontally);
                    refreshField();
                }
            }
        });
    }

    public void addMouseListener() {
        addMouseListener(mouseAdapter);
    }
//
//    public void removeMouseListener() {
//        removeMouseListener(mouseAdapter);
//    }

    public void setImage(Content content) {
        switch (content) {
            case VEHICLE:
                setIcon(new ImageIcon(Field.imagesPath + "vehicle.png"));
                break;
            case BROKEN_VEHICLE:
                setIcon(new ImageIcon(Field.imagesPath + "broken_vehicle.png"));
                break;
        }
    }

    @Override
    public void refreshField() {
        switch (content) {
            case LAND:
                setIcon(new ImageIcon(Field.imagesPath + "land.png"));
                break;
            case MARINE:
                setIcon(new ImageIcon(Field.imagesPath + "water.png"));
                break;
            case LAND_EXPLOSION:
                setIcon(new ImageIcon(Field.imagesPath + "land_explosion.png"));
                break;
            case MARINE_EXPLOSION:
                setIcon(new ImageIcon(Field.imagesPath + "water_explosion.png"));
                break;
            case BROKEN_VEHICLE:
                setIcon(new ImageIcon(Field.imagesPath + "broken_vehicle.png"));
                break;
            case VEHICLE:
                setIcon(new ImageIcon(Field.imagesPath + "vehicle.png"));
                break;
        }
    }
}
