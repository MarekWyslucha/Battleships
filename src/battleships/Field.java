package battleships;

import javax.swing.JButton;

/**
 *
 * @author Marek
 */
public abstract class Field extends JButton {
    public static final String imagesPath = System.getProperty("user.dir") + "/images/";

    public static final int fieldSize = 20;

    protected final int xIndex, yIndex;
    protected Content content;

    protected BattleMap battleMap;

    public Field(int x, int y, int xIndex, int yIndex, Content content, BattleMap battleMap) {
        setBounds(x, y, fieldSize, fieldSize);
        this.xIndex = xIndex;
        this.yIndex = yIndex;
        this.content = content;
        this.battleMap = battleMap;
    }

    public void setContent(Content content){
        this.content = content;
        refreshField();
    }
    
    public abstract void refreshField();
}