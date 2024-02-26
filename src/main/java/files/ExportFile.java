package files;

import com.mxgraph.swing.mxGraphComponent;
import controllers.ConstantController;
import controllers.MainController;
import database.TableCreator;
import database.TuplesExtractor;
import dsl.utils.DslUtils;
import entities.Column;
import entities.Tree;
import entities.cells.Cell;
import entities.cells.TableCell;
import enums.ColumnDataType;
import enums.FileType;
import gui.frames.ErrorFrame;
import gui.frames.forms.importexport.ExportSQLScriptForm;
import gui.frames.main.MainFrame;
import net.coobird.thumbnailator.Thumbnails;
import sgbd.query.Operator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class ExportFile extends JPanel {

    private final JFileChooser fileChooser = new JFileChooser();

    public ExportFile() {
        this.fileChooser.setDialogTitle(ConstantController.getString("exportFile.saveFile"));
        this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.fileChooser.setCurrentDirectory(MainController.getLastDirectory());
    }

    public void exportToMySQLScript(Cell cell) {
        String pathname = String.format("%s.sql", ConstantController.getString("file.tableFileName"));
        this.fileChooser.setSelectedFile(new File(pathname));

        int userSelection = this.fileChooser.showSaveDialog(null);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        MainController.setLastDirectory(new File(this.fileChooser.getCurrentDirectory().getAbsolutePath()));

        File fileToSave = this.fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();

        if (!filePath.endsWith(FileType.SQL.extension)) {
            filePath = String.format("%s.sql", filePath);
            fileToSave = new File(filePath);
        }

        if (fileToSave.exists()) {
            int selectedOption = JOptionPane.showConfirmDialog(
                null, ConstantController.getString("file.substitution"),
                ConstantController.getString("file.substitutionConfirmation"), JOptionPane.YES_NO_OPTION
            );

            if (selectedOption == JOptionPane.NO_OPTION) {
                this.exportToMySQLScript(cell);
                return;
            }
        }

        AtomicReference<Boolean> exitReference = new AtomicReference<>(false);

        String text = String.format("%s: ", ConstantController.getString("exportFile.createNewDatabase"));

        ExportSQLScriptForm.SQLScriptInf inf = new ExportSQLScriptForm.SQLScriptInf(
            new StringBuilder(), new StringBuilder(), new HashMap<>(), new HashMap<>(),
            new HashMap<>(), new JCheckBox(text), new StringBuilder(), new Vector<>(), new Vector<>()
        );

        new ExportSQLScriptForm(cell, inf, exitReference);

        if (exitReference.get()) return;

        try {
            FileWriter sql = new FileWriter(fileToSave);

            StringBuilder sqlContent = new StringBuilder();

            if (inf.checkBoxCreateDatabase().isSelected()) {
                sqlContent.append(String.format("DROP DATABASE IF EXISTS %s;%n%n", inf.databaseName()));
                sqlContent.append(String.format("CREATE DATABASE IF NOT EXISTS %s;%n%n", inf.databaseName()));
            }

            sqlContent.append(String.format("USE %s;%n%n", inf.databaseName()));
            sqlContent.append(String.format("DROP TABLE IF EXISTS %s;%n%n", inf.tableName()));
            sqlContent.append(String.format("CREATE TABLE IF NOT EXISTS %s (%n", inf.tableName()));

            for (String columnName : inf.columnNames().subList(0, inf.columnNames().size() - 1)) {
                if (!inf.columnNames().getFirst().equals(columnName)) {
                    sqlContent.append(",\n");
                }

                Column column = cell
                    .getColumns()
                    .stream()
                    .filter(c -> c.getSourceAndName().equals(columnName))
                    .findFirst()
                    .orElseThrow();

                String name = inf.newColumnNameTxtFields().get(columnName).getText();

                String type = switch (column.DATA_TYPE) {
                    case INTEGER, LONG -> "INT";
                    case FLOAT -> "FLOAT";
                    case DOUBLE -> "DOUBLE";
                    case CHARACTER -> "VARCHAR(1)";
                    case STRING, NONE -> "TEXT";
                    case BOOLEAN -> "BOOLEAN";
                };

                sqlContent.append(String.format("\t%s %s ", name, type));

                if (!inf.nullCheckBoxes().get(columnName).isSelected()) {
                    sqlContent.append("NOT ");
                }

                sqlContent.append("NULL");
            }

            int numberOfPrimaryKeys = inf
                .pkCheckBoxes()
                .values()
                .stream()
                .filter(AbstractButton::isSelected)
                .toList()
                .size();

            if (numberOfPrimaryKeys > 0) {
                sqlContent.append(",\n\n\tPRIMARY KEY (");

                for (Map.Entry<String, JCheckBox> primaryKey : inf.pkCheckBoxes().entrySet()) {
                    if (primaryKey.getValue().isSelected()) {
                        sqlContent.append(inf.newColumnNameTxtFields().get(primaryKey.getKey()).getText());

                        numberOfPrimaryKeys--;

                        sqlContent.append(numberOfPrimaryKeys != 0 ? ", " : ")");
                    }
                }
            }

            sqlContent.append("\n);\n\n");

            for (Vector<Object> row : inf.content()) {
                sqlContent.append(String.format("INSERT INTO %s VALUES (", inf.tableName()));

                for (int i = 0; i < row.size() - 1; i++) {
                    int finalI = i;

                    Vector<String> finalColumnNames = inf.columnNames();

                    ColumnDataType type = cell
                        .getColumns()
                        .stream()
                        .filter(c -> c.getSourceAndName().equals(finalColumnNames.get(finalI)))
                        .findFirst()
                        .orElseThrow()
                        .DATA_TYPE;

                    boolean isString = type == ColumnDataType.STRING || type == ColumnDataType.NONE || type == ColumnDataType.CHARACTER;

                    String data = Objects.toString(row.get(i)).replaceAll("'", "\\\\'");

                    if (isString && !data.equalsIgnoreCase("null")) {
                        data = String.format("'%s'", data);
                    }

                    sqlContent.append(data);

                    if (i != row.size() - 2) {
                        sqlContent.append(", ");
                    }
                }

                sqlContent.append(");\n");
            }

            if (!inf.additionalCommand().isEmpty()) {
                sqlContent.append(String.format("%n%n%s", inf.additionalCommand()));
            }

            sql.write(sqlContent.toString());

            sql.close();
        } catch (IOException exception) {
            new ErrorFrame(exception.getMessage());
        }
    }

    public void exportToFYI(Cell cell, List<Column> primaryKeyColumns) {
        if (primaryKeyColumns == null || primaryKeyColumns.isEmpty()) return;

        String pathname = String.format("%s%s", ConstantController.getString("file.tableFileName"), FileType.HEADER.extension);

        this.fileChooser.setSelectedFile(new File(pathname));

        int userSelection = this.fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            MainController.setLastDirectory(new File(this.fileChooser.getCurrentDirectory().getAbsolutePath()));
            File fileToSave = this.fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.endsWith(FileType.HEADER.extension)) {
                filePath += FileType.HEADER.extension;
                fileToSave = new File(filePath);
            }

            String headFileName = this.fileChooser.getSelectedFile().getName();
            String fileName = headFileName.endsWith(FileType.HEADER.extension) ? headFileName.substring(0, headFileName.indexOf(".")) : headFileName;

            if (fileToSave.exists()) {
                int result = JOptionPane.showConfirmDialog(null, ConstantController.getString("file.substitution"), ConstantController.getString("file.substitutionConfirmation"), JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.NO_OPTION) {
                    this.exportToFYI(cell, primaryKeyColumns);
                    return;
                }
            }

            Map<Integer, Map<String, String>> rows = new HashMap<>();

            Operator operator = cell.getOperator();
            operator.open();

            int i = 0;

            while (operator.hasNext()) {
                Map<String, String> row = TuplesExtractor.getRow(cell.getOperator(), false);

                if (row != null) {
                    rows.put(i++, row);
                }
            }

            operator.close();

            AtomicReference<Boolean> exitReference = new AtomicReference<>(false);

            List<Column> columnsWithPrimaryKey = new ArrayList<>();

            for (Column column : cell.getColumns()) {
                Column readyColumn = primaryKeyColumns.contains(column) ? new Column(column.NAME, column.SOURCE, column.DATA_TYPE, true) : column;

                columnsWithPrimaryKey.add(readyColumn);
            }

            if (exitReference.get()) return;

            TableCell createdCell = TableCreator.createFYITable(fileName, columnsWithPrimaryKey, rows, fileToSave, true);

            createdCell.getTable().saveHeader(headFileName);
            createdCell.getTable().close();

            Path headSourcePath = Paths.get(headFileName);
            String datFileName = String.format("%s%s", fileName, FileType.FYI.extension);
            Path datSourcePath = Paths.get(datFileName);

            Path headDestinationPath = Paths.get(filePath);
            Path datDestinationPath = Paths.get(filePath.replace(headFileName, datFileName));

            try {
                Files.move(headSourcePath, headDestinationPath, StandardCopyOption.REPLACE_EXISTING);
                Files.move(datSourcePath, datDestinationPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception exception) {
                new ErrorFrame(exception.getMessage());
            }
        }
    }

    public void exportToCSV(Cell cell) {
        try {
            String defaultFileName = String.format("%s%s", cell.getSources().stream().findFirst().orElse(null).getName(), FileType.CSV.extension);

            this.fileChooser.setSelectedFile(new File(defaultFileName));

            int userSelection = this.fileChooser.showSaveDialog(null);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                MainController.setLastDirectory(new File(this.fileChooser.getCurrentDirectory().getAbsolutePath()));

                File fileToSave = this.fileChooser.getSelectedFile();
                String filePath = fileToSave.getAbsolutePath();

                if (!filePath.endsWith(FileType.CSV.extension)) {
                    filePath = String.format("%s%s", filePath, FileType.CSV.extension);
                    fileToSave = new File(filePath);
                }

                if (fileToSave.exists()) {
                    int result = JOptionPane.showConfirmDialog(null, ConstantController.getString("file.substitution"), ConstantController.getString("file.substitutionConfirmation"), JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.NO_OPTION) {
                        this.exportToCSV(cell);
                        return;
                    }
                }

                FileWriter csv = new FileWriter(fileToSave);

                boolean columnsPut = false;

                for (Map<String, String> row : TuplesExtractor.getAllRows(cell.getOperator(), true)) {
                    if (!columnsPut) {
                        boolean repeatedColumnName = false;

                        Set<String> columnNames = new HashSet<>();

                        for (String column : row.keySet()) {
                            if (!columnNames.add(Column.removeSource(column))) {
                                repeatedColumnName = true;
                            }
                        }

                        int i = 0;

                        for (String inf : row.keySet()) {
                            if (i++ != 0) {
                                csv.write(",");
                            }

                            String columnName = repeatedColumnName ? inf : Column.removeSource(inf);
                            csv.write(columnName);
                        }

                        csv.write("\n");
                        columnsPut = true;
                    }

                    int i = 0;

                    for (String inf : row.values()) {
                        if (i++ != 0) {
                            csv.write(",");
                        }

                        csv.write(inf);
                    }

                    csv.write("\n");
                }

                csv.close();
            }
        } catch (IOException exception) {
            new ErrorFrame(exception.getMessage());
        }
    }

    private void exportToImage() {
        try {
            mxGraphComponent component = MainFrame.getGraphComponent();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(ConstantController.getString("exportFile.saveImage"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            String defaultFileName = String.format("%s.jpeg", ConstantController.getString("file.treeFileName"));
            fileChooser.setSelectedFile(new File(defaultFileName));

            int userSelection = fileChooser.showSaveDialog(component);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String path = fileToSave.getPath();

                if (!path.toLowerCase().endsWith(".jpeg") && !path.toLowerCase().endsWith(".jpg")) {
                    path = String.format("%s.jpeg", path);
                }

                Dimension size = component.getGraphControl().getSize();
                BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = image.createGraphics();

                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, 0, size.width, size.height);

                component.getGraphControl().paint(g2d);

                g2d.dispose();

                Thumbnails.of(image).size(size.width, size.height).outputQuality(1.0f).toFile(new File(path));
            }
        } catch (IOException exception) {
            System.out.printf(String.format("%s: %s", ConstantController.getString("file.error.toSave"), exception.getMessage()));
        }
    }

    public void exportToDsl(Tree tree) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(ConstantController.getString("exportFile.saveTree"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        String defaultFileName = String.format("%s.txt", ConstantController.getString("file.treeFileName"));
        fileChooser.setSelectedFile(new File(defaultFileName));

        if (fileChooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) return;

        String filePath = fileChooser.getSelectedFile().getPath();

        if (!filePath.toLowerCase().endsWith(".txt")) {
            filePath = String.format("%s.txt", filePath);
        }

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(DslUtils.generateDslTree(tree));
        } catch (IOException exception) {
            System.out.printf(String.format("%s: %s", ConstantController.getString("file.error.toSave"), exception.getMessage()));
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException exception) {
                System.out.printf(String.format("%s: %s", ConstantController.getString("file.error.toSave"), exception.getMessage()));
            }
        }

        final String finalPath = filePath.substring(0, filePath.lastIndexOf("/") + 1);

        tree.getLeaves().forEach(table -> {
            String tableName = table.getName();

            FileUtils.copyDatFilesWithHead(String.format("%s%s", tableName, FileType.HEADER.extension), tableName, Path.of(finalPath));
        });
    }
}
