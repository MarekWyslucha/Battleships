package battleships;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;

/**
 *
 * @author Marek
 */
public class EnemyField extends Field {

    public EnemyField(int x, int y, int xIndex, int yIndex, Content content, BattleMap battleMap) {
        super(x, y, xIndex, yIndex, content, battleMap);
        refreshField();

        addActionListener((ActionEvent e) -> {
            if (Flags.myVehiclesLeft != 0 && Flags.enemyVehiclesLeft != 0) {
                if (Flags.myTurn) {
                    battleMap.shootOnEnemyMap(xIndex, yIndex);
                    refreshField();

                    battleMap.shootOnMyMap();
                }
            }
        });
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
            default:
                if(xIndex < battleMap.xShoreline)
                    setIcon(new ImageIcon(Field.imagesPath + "water.png"));
                else
                    setIcon(new ImageIcon(Field.imagesPath + "land.png"));
                break;
        }
    }
}
