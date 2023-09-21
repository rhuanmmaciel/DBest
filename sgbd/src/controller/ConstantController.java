package controller;

import entities.Action;
import lib.booleanexpression.enums.RelationalOperator;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


public class ConstantController {

    private static final String language = "en";
    private static final String country = "US";
    private static final Locale currentLocale = new Locale(language, country);
    private static final ResourceBundle messages = ResourceBundle.getBundle("language.messages", currentLocale);
    public static final Path ROOT_DIRECTORY = Paths.get("").toAbsolutePath();
    public static final List<String> RELATIONAL_OPERATORS = Arrays.stream(RelationalOperator.values())
            .map(x -> x.symbols)
            .flatMap(Arrays::stream)
            .toList();
    public static final String NOT_EQUAL_SYMBOL = "≠";
    public static final String GREATER_THAN_OR_EQUAL_SYMBOL = "≥";
    public static final String LESS_THAN_OR_EQUAL_SYMBOL = "≤";
    public static final int TABLE_CELL_HEIGHT = 30;
    public static final int TABLE_CELL_WIDTH = 80;
    public static final int OPERATION_CELL_HEIGHT = 30;
    public static final int OPERATION_CELL_WIDTH = 80;
    public static final String NULL = "null";
    public static final String PK_CSV_TABLE_NAME = "__IDX__";
    public static final Action.CurrentAction NONE_ACTION = new Action.CurrentAction(Action.CurrentAction.ActionType.NONE);
    public static final int UI_WIDTH = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * 0.65);
    public static final int UI_HEIGHT = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * 0.7);
    public static final String JCELL_FYI_STYLE = "fyi";
    public static final String JCELL_CSV_STYLE = "csv";
    public static final String JCELL_OPERATION_STYLE = "operation";

    public static String getString(String txt){
        return messages.getString(txt);
    }

}
