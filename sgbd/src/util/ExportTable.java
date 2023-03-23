package util;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import entities.Cell;
import enums.FileType;
import sgbd.table.Table;

@SuppressWarnings("serial")
public class ExportTable extends JPanel {
	
	private JTable table;
	
	public ExportTable(JFrame frame) {
		
		exportToImage(frame);
	
	}
	
	public ExportTable(AtomicReference<Cell> cell, FileType type) {
		
		if(type == FileType.CSV)
			exportToCsv(cell.get().getContent());
		
		else if(type == FileType.DAT)
			exportToDat(cell.get().getData().getSources().get(0));
		
	}
	
	public ExportTable( List<Cell> cells, String path, int a){
		
		saveGraph(cells, path);
		
	}
	
	public void exportToDat(Table table) {
		
	    JFileChooser fileChooser = new JFileChooser();
	    fileChooser.setDialogTitle("Salvar arquivo");
	    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    
	    String defaultFileName = "tabela.head";
	    fileChooser.setSelectedFile(new File(defaultFileName));
	    
	    int userSelection = fileChooser.showSaveDialog(null);
	    
	    if (userSelection == JFileChooser.APPROVE_OPTION) {
	    	
	        File fileToSave = fileChooser.getSelectedFile();
	        String filePath = fileToSave.getAbsolutePath();
	        
	        if (!filePath.endsWith(".head")) {
	        	
	            filePath += ".head";
	            fileToSave = new File(filePath);
	            
	        }
	        
	        table.saveHeader(fileChooser.getSelectedFile().getName());
	      
			Path source = Paths.get(fileChooser.getSelectedFile().getName());
	        
	        Path destination = Paths.get(filePath);
	        
	        try {
	        	
	        	Files.move(source, destination);
	        
	        } catch (Exception e) {
	        
	        	System.err.println("Ocorreu um erro ao mover o arquivo: " + e.getMessage());
	        
	        }
			
	    }
	    
	}
	
	public void exportToCsv(List<List<String>> data) {
		
		try {
		    
		    JFileChooser fileChooser = new JFileChooser();
		    fileChooser.setDialogTitle("Salvar arquivo");
		    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    
		    String defaultFileName = "tabela.csv";
		    fileChooser.setSelectedFile(new File(defaultFileName));
		    
		    int userSelection = fileChooser.showSaveDialog(null);
		    
		    if (userSelection == JFileChooser.APPROVE_OPTION) {
		    	
		        File fileToSave = fileChooser.getSelectedFile();
		        String filePath = fileToSave.getAbsolutePath();
		        if (!filePath.endsWith(".csv")) {
		            filePath += ".csv";
		            fileToSave = new File(filePath);
		        }
		        
		        FileWriter csv = new FileWriter(fileToSave);
		        
		        for (String columnName : data.get(0)) {
		        	
		            csv.write(columnName.substring(columnName.indexOf(".")+1) + ",");
		        
		        }
		        
		        csv.write("\n");
		        
		        data.remove(0);
		        
		        for (List<String> row : data) {
		        	
		            for (String inf : row) {
		                
		            	csv.write(inf + ",");
		            
		            }
		            csv.write("\n");
		            
		        }
		        
		        csv.close();
		        
		    }
		    
		} catch (IOException e) {
		    
			System.out.println("Error "+e);
		
		}


		
	}
	
	
	public void exportToImage(JFrame frame) {
		
		try {

			JFileChooser fileChooser = new JFileChooser();
		    fileChooser.setDialogTitle("Salvar imagem");
		    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		    
		    String defaultFileName = "arvore.jpg";
		    fileChooser.setSelectedFile(new File(defaultFileName));
		    
		    
		    int userSelection = fileChooser.showSaveDialog(frame);

		    if (userSelection == JFileChooser.APPROVE_OPTION) {

		    	File fileToSave = fileChooser.getSelectedFile();
		        
		        String path = fileToSave.getPath();
		        if (!path.toLowerCase().endsWith(".jpeg") && !path.toLowerCase().endsWith(".jpg")) {
		            path += ".jpeg";
		        }

		        Container contentPane = frame.getContentPane();
		        BufferedImage image = new BufferedImage(contentPane.getWidth(), contentPane.getHeight(),
		                								BufferedImage.TYPE_INT_RGB);
		        Graphics2D g2d = image.createGraphics();
		        contentPane.printAll(g2d);
		        g2d.dispose();
		        ImageIO.write(image, "jpeg", new File(path));
		    }
		} catch (IOException e) {
		    System.out.println("Error " + e);
		}


        
	}
	
	protected void paintComponent(Graphics g)
    {
        g.drawRect(50,50,50,50);
    }
	
	public void saveGraph( List<Cell> cells,String path) {
		try {
	        FileWriter csv = new FileWriter(new File(path));
	        
            

	        for(int i=0; i< cells.size();i++) {
	        	csv.write("Cell,Name,Style,Length,Width,X,Y");

		        csv.write("\n");
	        	
	            csv.write(cells.get(0).getCell().toString() + ",");
	            csv.write(cells.get(0).getName() + ",");
	            csv.write(cells.get(0).getStyle() + ",");
	            csv.write(cells.get(0).getLength() + ",");
	            csv.write(cells.get(0).getWidth() + ",");
	            csv.write(cells.get(0).getX() + ",");
	            csv.write(cells.get(0).getY() + ",");
	            List<List<String>> data = cells.get(0).getContent();
	            
	            List<String> columnsName = new ArrayList<>();
				
				if(data != null && !data.isEmpty()) {
					
					columnsName = data.get(0);
					List<String> firstLine = new ArrayList<>(data.remove(0));
				
					String[][] dataArray = data.stream()
			                .map(l -> l.stream().toArray(String[]::new))
			                .toArray(String[][]::new);;
			                
			        data.add(0, firstLine);        
			                
			        String[] columnsNameArray = columnsName.stream().toArray(String[]::new); 
			        
					
					
					table = new JTable(dataArray, columnsNameArray);
				
				}
			 
			 
		        TableModel model = table.getModel();		        
		        for (int j = 0; j < model.getColumnCount(); j++) {
		            csv.write(model.getColumnName(j) + ",");
		        }

		        csv.write("\n");

		        for (int j = 0; j < model.getRowCount(); j++) {
		            for (int k = 0; k < model.getColumnCount(); k++) {
		                csv.write(model.getValueAt(j, k).toString() + ",");
		            }
		            csv.write("\n");
		        }

		        csv.write("\n");
			}
	        
	        csv.close();
	        
	        
		}catch (IOException e) {
		    System.out.println("Error "+e);
        }

	}

	

    
	
	
	
}
