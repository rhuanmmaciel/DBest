package controller;

import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;

import java.awt.*;

public class ConstantController {

    public static final String APPLICATION_TITLE = "DBest: Database Basics for Engaging Students and Teachers";

    public static final String DAT_FILE_EXTENSION = ".dat";

    public static final String HEADER_FILE_EXTENSION = ".head";

    public static final int TABLE_CELL_HEIGHT = 30;

    public static final int TABLE_CELL_WIDTH = 80;

    public static final int OPERATION_CELL_HEIGHT = 30;

    public static final int OPERATION_CELL_WIDTH = 80;

    public static final String NULL = "null";

    public static final String PK_CSV_TABLE_NAME = "__IDX__";

    public static final CurrentAction NONE_ACTION = new CurrentAction(ActionType.NONE);

    public static final int UI_SCREEN_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.65);

    public static final int UI_SCREEN_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.7);

    private ConstantController() {

    }
}
