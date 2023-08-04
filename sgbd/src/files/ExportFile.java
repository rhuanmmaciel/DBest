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
			case FYI -> exportToDat(cell, fileChooser);
			case SQL -> exportToMySQLScript(cell, fileChooser);
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

						if(isString && !data.equals("null")) data = "'" + data + "'";

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

	private void exportToDat(Cell cell, JFileChooser fileChooser) {

		fileChooser.setSelectedFile(new File("tabela"+ConstantController.HEADER_FILE_EXTENSION));

		int userSelection = fileChooser.showSaveDialog(null);

		if (userSelection == JFileChooser.APPROVE_OPTION) {

			MainController.setLastDirectory(new File(fileChooser.getCurrentDirectory().getAbsolutePath()));
			File fileToSave = fileChooser.getSelectedFile();
			String filePath = fileToSave.getAbsolutePath();

			if (!filePath.endsWith(ConstantController.HEADER_FILE_EXTENSION)) {

				filePath += ConstantController.HEADER_FILE_EXTENSION;
				fileToSave = new File(filePath);

			}

			String headFileName = fileChooser.getSelectedFile().getName() + ConstantController.HEADER_FILE_EXTENSION;
			String fileName = headFileName.endsWith(ConstantController.HEADER_FILE_EXTENSION) ?
					headFileName.substring(0, headFileName.indexOf(".")) : headFileName;

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

			TableCreator tableCreator = new TableCreator(fileName, cell.getColumns(), rows, true);

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

	private void exportToCsv(Cell cell, JFileChooser fileChooser) {
		
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

			tree.getLeaves().forEach(table -> FileUtils.copyDatFilesWithHead(
					table.getName()+ConstantController.HEADER_FILE_EXTENSION, table.getName(), Path.of(finalPath)));
		
		}
		
	}

}
