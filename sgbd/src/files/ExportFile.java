package files;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mxgraph.swing.mxGraphComponent;

import controller.MainController;
import database.TableCreator;
import database.TableUtils;
import dsl.utils.DslUtils;
import entities.Tree;
import entities.cells.Cell;
import entities.cells.TableCell;
import entities.utils.TableFormat;
import enums.FileType;
import gui.frames.ErrorFrame;
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

//		if (type == FileType.CSV)
//			exportToCsv(cell, cell.get(), fileChooser);

		if (type == FileType.DAT)
			exportToDat(cell, fileChooser);

	}

	public ExportFile(Tree tree) {

		exportToDsl(tree);

	}

	private void exportToDat(Cell cell, JFileChooser fileChooser) {

		fileChooser.setSelectedFile(new File("tabela"));

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
				TableFormat.Row row =  TableFormat.getRow(cell.getOperator(), false);
				if(row != null)
					rows.put(i++, row.row());
			}

			operator.close();

			TableCell createdCell = TableCreator.createTable(fileName, cell.getColumns(), rows);

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

	private void exportToCsv(Map<Integer, Map<String, String>> data, Cell cell, JFileChooser fileChooser) {
		
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
						exportToCsv(data, cell, fileChooser);
						return;
					}
				}

				FileWriter csv = new FileWriter(fileToSave);

				for (String columnName : data.get(0).keySet()) {

					csv.write(columnName.substring(columnName.indexOf("_") + 1) + ",");

				}

				csv.write("\n");

				for (Map<String, String> row : data.values()) {

					for (String inf : row.values()) {

						csv.write(inf + ",");

					}
					csv.write("\n");

				}

				csv.close();

			}

		} catch (IOException e) {

			System.out.println("Error " + e);

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
