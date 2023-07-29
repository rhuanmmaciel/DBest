package controller;

import entities.Action;

import java.awt.*;

public class ConstantController {

    public static final String HEADER_FILE_EXTENSION = ".head";
    public static final int TABLE_CELL_HEIGHT = 30;
    public static final int TABLE_CELL_WIDTH = 80;
    public static final int OPERATION_CELL_HEIGHT = 30;
    public static final int OPERATION_CELL_WIDTH = 80;
    public static final String NULL = "null";
    public static final String PK_CSV_TABLE_NAME = "__IDX__";
    public static final Action.CurrentAction NONE_ACTION = new Action.CurrentAction(Action.CurrentAction.ActionType.NONE);
    public static final int UI_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.65);
    public static final int UI_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.7);

}
