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
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.*;

import com.mxgraph.swing.mxGraphComponent;

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
import gui.frames.forms.importexport.ExportMySQLScriptForm;
import net.coobird.thumbnailator.Thumbnails;
import sgbd.query.Operator;

public class ExportFile extends JPanel {

	public ExportFile() {

		exportToImage();

	}

	public ExportFile(Cell cell, FileType type) {

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Salvar arquivo");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setCurrentDirectory(MainController.getLastDirectory());

		switch (type){
			case CSV -> exportToCsv(cell, fileChooser);
			case DAT -> exportToDat(cell, fileChooser);
			case MYSQL -> exportToMySQLScript(cell, fileChooser);
		}

	}

	public ExportFile(Tree tree) {

		exportToDsl(tree);

	}

	private void exportToMySQLScript(Cell cell, JFileChooser fileChooser){

		fileChooser.setSelectedFile(new File("tabela.sql"));

		int userSelection = fileChooser.showSaveDialog(null);

		if (userSelection == JFileChooser.APPROVE_OPTION) {

			MainController.setLastDirectory(new File(fileChooser.getCurrentDirectory().getAbsolutePath()));
			File fileToSave = fileChooser.getSelectedFile();
			String filePath = fileToSave.getAbsolutePath();

			if (!filePath.endsWith(".sql")) {

				filePath += ".sql";
				fileToSave = new File(filePath);

			}

			if (fileToSave.exists()) {
				int result = JOptionPane.showConfirmDialog(null, "O arquivo já existe. Deseja substituir?",
						"Confirmar substituição", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION) {
					exportToMySQLScript(cell, fileChooser);
					return;
				}
			}

			AtomicReference<Boolean> exitReference = new AtomicReference<>(false);

			StringBuilder databaseName = new StringBuilder();
			StringBuilder tableName = new StringBuilder();
			Map<String, JTextField> newColumnNames = new HashMap<>();
			Map<String, JCheckBox> pkCheckBoxes = new HashMap<>();
			Map<String, JCheckBox> nullCheckBoxes = new HashMap<>();
			Vector<String> columnNames = new Vector<>();
			Vector<Vector<Object>> content = new Vector<>();
			JCheckBox checkBoxCreateDatabase = new JCheckBox("Criar Database novo: ");

			new ExportMySQLScriptForm(cell, databaseName, tableName, newColumnNames, pkCheckBoxes, nullCheckBoxes,
					checkBoxCreateDatabase, columnNames, content, exitReference);

			columnNames = new Vector<>(columnNames.subList(0, columnNames.size() - 1));

			if(exitReference.get()) return;

			try {

				FileWriter sql = new FileWriter(fileToSave);

				String beginning = checkBoxCreateDatabase.isSelected() ? "drop database if exists "+databaseName+";\n"
				+"create database "+databaseName+";\n" : "";

				sql.write(beginning);
				sql.write("use " + databaseName+";\n\n");

				sql.write("CREATE TABLE "+tableName+" (\n");

				for(String columnName : columnNames) {

					Column c = cell.getColumns().stream().filter(x -> x.getSourceAndName().equals(columnName))
							.findFirst().orElseThrow();
					sql.write(newColumnNames.get(columnName).getText()+" ");
					sql.write(switch (c.getType()){
						case INTEGER, LONG -> "INT ";
						case FLOAT -> "FLOAT ";
						case DOUBLE -> "DOUBLE ";
						case CHARACTER -> "VARCHAR(1) ";
						case STRING, NONE -> "TEXT ";
						case BOOLEAN -> "BOOLEAN ";
					});

					if(!nullCheckBoxes.get(columnName).isSelected())
						sql.write("NOT ");

					sql.write("NULL,\n");

				}

				int nPks = pkCheckBoxes.values().stream().filter(AbstractButton::isSelected).toList().size();
				if(nPks > 0){

					sql.write("PRIMARY KEY(");
					for(Map.Entry<String, JCheckBox> pk : pkCheckBoxes.entrySet())
						if(pk.getValue().isSelected()) {

							sql.write(newColumnNames.get(pk.getKey()).getText());
							nPks--;

							String txt = nPks != 0 ? ", " : ")";
							sql.write(txt);

						}

				}

				sql.write(");\n\n");

				for (Vector<Object> row : content.subList(3, content.size())) {

					sql.write("INSERT INTO "+tableName+" values(");

					for(int i = 0; i < row.size() - 1; i++){

						int finalI = i;
						Vector<String> finalColumnNames = columnNames;
						ColumnDataType type = cell.getColumns().stream().filter(x -> x.getSourceAndName().equals(finalColumnNames.get(finalI)))
								.findFirst().orElseThrow().getType();

						boolean isString = type.equals(ColumnDataType.STRING) || type.equals(ColumnDataType.NONE) || type.equals(ColumnDataType.CHARACTER);

						String inf = Objects.toString(row.get(i)).replaceAll("'", "\\\\'");

						if(isString && !inf.equals("null")) inf = "'" + inf + "'";

						sql.write(inf);

						if(i != row.size() - 2) sql.write(", ");

					}

					sql.write(");\n");

				}

				sql.close();

			}catch (IOException e){

				new ErrorFrame(e.getMessage());

			}

		}

	}

	private void exportToDat(Cell cell, JFileChooser fileChooser) {

		fileChooser.setSelectedFile(new File("tabela.head"));

		int userSelection = fileChooser.showSaveDialog(null);

		if (userSelection == JFileChooser.APPROVE_OPTION) {

			MainController.setLastDirectory(new File(fileChooser.getCurrentDirectory().getAbsolutePath()));
			File fileToSave = fileChooser.getSelectedFile();
			String filePath = fileToSave.getAbsolutePath();

			if (!filePath.endsWith(".head")) {

				filePath += ".head";
				fileToSave = new File(filePath);

			}

			String headFileName = fileChooser.getSelectedFile().getName() + ".head";
			String fileName = headFileName.endsWith(".head") ? headFileName.substring(0, headFileName.indexOf(".")) : headFileName;

			if (fileToSave.exists()) {
				int result = JOptionPane.showConfirmDialog(null, "O arquivo já existe. Deseja substituir?",
						"Confirmar substituição", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION) {
					exportToDat(cell, fileChooser);
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

			TableCreator tableCreator = new TableCreator(fileName, cell.getColumns(), rows, exitReference, true);

			if(exitReference.get())
				return;

			TableCell createdCell = tableCreator.getTableCell();

			createdCell.getTable().saveHeader(headFileName);

			createdCell.getTable().close();

			Path source = Paths.get(headFileName);
			String datFileName = fileName + ".dat";
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

	private void exportToCsv(Cell cell, JFileChooser fileChooser) {
		
		try {

			String defaultFileName = cell.getAllSourceTables().stream().findFirst().orElse(null).getName() + ".csv";
			fileChooser.setSelectedFile(new File(defaultFileName));

			int userSelection = fileChooser.showSaveDialog(null);

			if (userSelection == JFileChooser.APPROVE_OPTION) {

				MainController.setLastDirectory(new File(fileChooser.getCurrentDirectory().getAbsolutePath()));
				
				File fileToSave = fileChooser.getSelectedFile();
				String filePath = fileToSave.getAbsolutePath();
				if (!filePath.endsWith(".csv")) {
					filePath += ".csv";
					fileToSave = new File(filePath);
				}

				if (fileToSave.exists()) {
					int result = JOptionPane.showConfirmDialog(null, "O arquivo já existe. Deseja substituir?",
							"Confirmar substituição", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.NO_OPTION) {
						exportToCsv(cell, fileChooser);
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

						for (String inf : row.keySet()) {

							String columnName = repeatedColumnName ? inf : Column.removeSource(inf);
							csv.write(columnName + ",");

						}
						csv.write("\n");
						columnsPut = true;

					}

					for (String inf : row.values())
						csv.write(inf + ",");

					csv.write("\n");

				}

				csv.close();

			}

		} catch (IOException e) {

			new ErrorFrame(e.getMessage());

		}

	}

	private void exportToImage() {
		
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

	private void exportToDsl(Tree tree) {
		
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

			tree.getLeaves().forEach(table -> FileUtils.copyDatFilesWithHead(table.getName()+".head", table.getName(), Path.of(finalPath)));
		
		}
		
	}

}
