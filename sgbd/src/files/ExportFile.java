package files;

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
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.*;

import com.mxgraph.swing.mxGraphComponent;

import controller.ConstantController;
import controller.MainController;
import database.TableCreator;
import dsl.utils.DslUtils;
import entities.Column;
import entities.Tree;
import entities.cells.Cell;
import entities.cells.TableCell;
import database.TuplesExtractor;
import enums.ColumnDataType;
import enums.FileType;
import gui.frames.ErrorFrame;
import gui.frames.forms.importexport.ExportSQLScriptForm;
import net.coobird.thumbnailator.Thumbnails;
import sgbd.query.Operator;

public class ExportFile extends JPanel {

	private final JFileChooser fileChooser = new JFileChooser();

	public ExportFile() {

		fileChooser.setDialogTitle("Salvar arquivo");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(MainController.getLastDirectory());

	}

	public void exportToMySQLScript(Cell cell){

		fileChooser.setSelectedFile(new File("tabela.sql"));

		int userSelection = fileChooser.showSaveDialog(null);

		if (userSelection == JFileChooser.APPROVE_OPTION) {

			MainController.setLastDirectory(new File(fileChooser.getCurrentDirectory().getAbsolutePath()));
			File fileToSave = fileChooser.getSelectedFile();
			String filePath = fileToSave.getAbsolutePath();

			if (!filePath.endsWith(FileType.SQL.EXTENSION)) {

				filePath += FileType.SQL.EXTENSION;
				fileToSave = new File(filePath);

			}

			if (fileToSave.exists()) {
				int result = JOptionPane.showConfirmDialog(null, "O arquivo já existe. Deseja substituir?",
						"Confirmar substituição", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION) {
					exportToMySQLScript(cell);
					return;
				}
			}

			AtomicReference<Boolean> exitReference = new AtomicReference<>(false);

			ExportSQLScriptForm.SQLScriptInf inf = new ExportSQLScriptForm.SQLScriptInf(new StringBuilder(), new StringBuilder(),
					new HashMap<>(), new HashMap<>(), new HashMap<>(), new JCheckBox("Criar Database novo: "), new StringBuilder(),
					new Vector<>(), new Vector<>());

			new ExportSQLScriptForm(cell, inf, exitReference);

			if(exitReference.get()) return;

			try {

				FileWriter sql = new FileWriter(fileToSave);

				String beginning = inf.checkBoxCreateDatabase().isSelected() ? "drop database if exists "+inf.databaseName()+";\n"
				+"create database "+inf.databaseName()+";\n" : "";

				sql.write(beginning);
				sql.write("use " + inf.databaseName()+";\n\n");

				sql.write("CREATE TABLE "+inf.tableName()+" (\n");

				for(String columnName : inf.columnNames().subList(0, inf.columnNames().size()-1)) {

					if(!inf.columnNames().get(0).equals(columnName))
						sql.write(",\n");

					Column c = cell.getColumns().stream().filter(x -> x.getSourceAndName().equals(columnName))
							.findFirst().orElseThrow();
					sql.write("\t");
					sql.write(inf.newColumnNameTxtFields().get(columnName).getText()+" ");
					sql.write(switch (c.getType()){
						case INTEGER, LONG -> "INT ";
						case FLOAT -> "FLOAT ";
						case DOUBLE -> "DOUBLE ";
						case CHARACTER -> "VARCHAR(1) ";
						case STRING, NONE -> "TEXT ";
						case BOOLEAN -> "BOOLEAN ";
					});

					if(!inf.nullCheckBoxes().get(columnName).isSelected())
						sql.write("NOT ");

					sql.write("NULL");

				}

				int nPks = inf.pkCheckBoxes().values().stream().filter(AbstractButton::isSelected).toList().size();
				if(nPks > 0){

					sql.write("\n\tPRIMARY KEY(");
					for(Map.Entry<String, JCheckBox> pk : inf.pkCheckBoxes().entrySet())
						if(pk.getValue().isSelected()) {

							sql.write(inf.newColumnNameTxtFields().get(pk.getKey()).getText());
							nPks--;

							String txt = nPks != 0 ? ", " : ")";
							sql.write(txt);

						}

				}

				sql.write("\n);\n\n");

				for (Vector<Object> row : inf.content()) {

					sql.write("INSERT INTO "+inf.tableName()+" values(");

					for(int i = 0; i < row.size() - 1; i++){

						int finalI = i;
						Vector<String> finalColumnNames = inf.columnNames();
						ColumnDataType type = cell.getColumns().stream().filter(x -> x.getSourceAndName().equals(finalColumnNames.get(finalI)))
								.findFirst().orElseThrow().getType();

						boolean isString = type.equals(ColumnDataType.STRING) || type.equals(ColumnDataType.NONE) || type.equals(ColumnDataType.CHARACTER);

						String data = Objects.toString(row.get(i)).replaceAll("'", "\\\\'");

						if(isString && !data.equals(ConstantController.NULL)) data = "'" + data + "'";

						sql.write(data);

						if(i != row.size() - 2) sql.write(", ");

					}

					sql.write(");\n");

				}

				if(!inf.additionalCommand().isEmpty()){

					sql.write("\n\n");
					sql.write(inf.additionalCommand().toString());

				}

				sql.close();

			}catch (IOException e){

				new ErrorFrame(e.getMessage());

			}

		}

	}

	public void exportToFyi(Cell cell, List<Column> pkColumns) {

		assert pkColumns != null && !pkColumns.isEmpty();

		fileChooser.setSelectedFile(new File("tabela"+FileType.HEADER.EXTENSION));

		int userSelection = fileChooser.showSaveDialog(null);

		if (userSelection == JFileChooser.APPROVE_OPTION) {

			MainController.setLastDirectory(new File(fileChooser.getCurrentDirectory().getAbsolutePath()));
			File fileToSave = fileChooser.getSelectedFile();
			String filePath = fileToSave.getAbsolutePath();

			if (!filePath.endsWith(FileType.HEADER.EXTENSION)) {

				filePath += FileType.HEADER.EXTENSION;
				fileToSave = new File(filePath);

			}

			String rawFileName = fileChooser.getSelectedFile().getName();
			String headFileName = rawFileName.endsWith(FileType.HEADER.EXTENSION) ?
					rawFileName : rawFileName + FileType.HEADER.EXTENSION;
			String fileName = headFileName.endsWith(FileType.HEADER.EXTENSION) ?
					headFileName.substring(0, headFileName.indexOf(".")) : headFileName;

			if (fileToSave.exists()) {
				int result = JOptionPane.showConfirmDialog(null, "O arquivo já existe. Deseja substituir?",
						"Confirmar substituição", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION) {
					exportToFyi(cell, pkColumns);
					return;
				}
			}

			Map<Integer, Map<String, String>> rows = new HashMap<>();

			Operator operator = cell.getOperator();
			operator.open();
			int i = 0;
			while(operator.hasNext()) {
				Map<String, String> row =  TuplesExtractor.getRow(cell.getOperator(), false);
				if(row != null)
					rows.put(i++, row);
			}

			operator.close();

			AtomicReference<Boolean> exitReference = new AtomicReference<>(false);

			List<Column> columnsWithPk = new ArrayList<>();

			for(Column c : cell.getColumns()){

				Column readyColumn = pkColumns.contains(c) ? new Column(c.getName(), c.getSource(), c.getType(), true)
				: c;
				columnsWithPk.add(readyColumn);

			}

			TableCreator tableCreator = new TableCreator(fileName, columnsWithPk, rows, true);

			if(exitReference.get())
				return;

			TableCell createdCell = tableCreator.getTableCell();

			createdCell.getTable().saveHeader(headFileName);

			createdCell.getTable().close();

			Path source = Paths.get(headFileName);
			String datFileName = fileName + FileType.FYI.EXTENSION;
			Path source1 = Paths.get(datFileName);


			Path destination = Paths.get(filePath);
			Path destination2 = Paths.get(filePath.replace(headFileName, datFileName));

			try {

				Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
				Files.move(source1, destination2, StandardCopyOption.REPLACE_EXISTING);

			} catch (Exception e) {

				new ErrorFrame(e.getMessage());

			}

		}

	}

	public void exportToCsv(Cell cell) {
		
		try {

			String defaultFileName = cell.getAllSourceTables().stream().findFirst().orElse(null).getName() + FileType.CSV.EXTENSION;
			fileChooser.setSelectedFile(new File(defaultFileName));

			int userSelection = fileChooser.showSaveDialog(null);

			if (userSelection == JFileChooser.APPROVE_OPTION) {

				MainController.setLastDirectory(new File(fileChooser.getCurrentDirectory().getAbsolutePath()));
				
				File fileToSave = fileChooser.getSelectedFile();
				String filePath = fileToSave.getAbsolutePath();
				if (!filePath.endsWith(FileType.CSV.EXTENSION)) {
					filePath += FileType.CSV.EXTENSION;
					fileToSave = new File(filePath);
				}

				if (fileToSave.exists()) {
					int result = JOptionPane.showConfirmDialog(null, "O arquivo já existe. Deseja substituir?",
							"Confirmar substituição", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.NO_OPTION) {
						exportToCsv(cell);
						return;
					}
				}

				FileWriter csv = new FileWriter(fileToSave);

				boolean columnsPut = false;

				for (Map<String, String> row : TuplesExtractor.getAllRows(cell.getOperator(), true)) {

					if(!columnsPut){

						boolean repeatedColumnName = false;
						Set<String> columnNames = new HashSet<>();
						for(String column : row.keySet())
							if(!columnNames.add(Column.removeSource(column))) repeatedColumnName = true;

						int i = 0;
						for (String inf : row.keySet()) {

							if(i++ != 0)
								csv.write(",");

							String columnName = repeatedColumnName ? inf : Column.removeSource(inf);
							csv.write(columnName);

						}
						csv.write("\n");
						columnsPut = true;

					}

					int i = 0;
					for (String inf : row.values()) {
						if(i++ != 0)
							csv.write(",");

						csv.write(inf);

					}

					csv.write("\n");

				}

				csv.close();

			}

		} catch (IOException e) {

			new ErrorFrame(e.getMessage());

		}

	}

	public void exportToImage() {
		
		try {
			
			mxGraphComponent component = MainController.getGraphComponent();
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setDialogTitle("Salvar imagem");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

			String defaultFileName = "arvore.jpeg";
			fileChooser.setSelectedFile(new File(defaultFileName));

			int userSelection = fileChooser.showSaveDialog(component);

			if (userSelection == JFileChooser.APPROVE_OPTION) {
				File fileToSave = fileChooser.getSelectedFile();
				String path = fileToSave.getPath();
				if (!path.toLowerCase().endsWith(".jpeg") && !path.toLowerCase().endsWith(".jpg")) {
					path += ".jpeg";
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
			
		} catch (IOException e) {
			
			System.out.println("Error " + e);
			
		}
		
	}

	public void exportToDsl(Tree tree) {
		
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Salvar árvore");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		String defaultFileName = "arvore.txt";
		fileChooser.setSelectedFile(new File(defaultFileName));
		
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

			File fileToSave = fileChooser.getSelectedFile();
			String path = fileToSave.getPath();
			if (!path.toLowerCase().endsWith(".txt") && !path.toLowerCase().endsWith(".txt")) 
				path += ".txt";
			
			try {
				
			    BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			    writer.write(DslUtils.generateDslTree(tree)); 
			    writer.close();
			    
			} catch (IOException e) {
				
			    System.out.println("Ocorreu um erro ao salvar o arquivo: " + e.getMessage());
			    
			}
			
			final String finalPath = path.substring(0, path.lastIndexOf("/") + 1);

			tree.getLeaves().forEach(table -> FileUtils.copyDatFilesWithHead(
					table.getName()+FileType.HEADER.EXTENSION, table.getName(), Path.of(finalPath)));
		
		}
		
	}

}
