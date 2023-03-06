package util;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

import entities.Cell;

@SuppressWarnings("serial")
public class ExportTable extends JPanel {
	
	private static JTable table;

	
	public static void exportToCsv(List<List<String>> data,String path) {
		
		 try {
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
		        FileWriter csv = new FileWriter(new File(path));
		        
		        for (int i = 0; i < model.getColumnCount(); i++) {
		            csv.write(model.getColumnName(i) + ",");
		        }

		        csv.write("\n");

		        for (int i = 0; i < model.getRowCount(); i++) {
		            for (int j = 0; j < model.getColumnCount(); j++) {
		                csv.write(model.getValueAt(i, j).toString() + ",");
		            }
		            csv.write("\n");
		        }

		        csv.close();
		    } catch (IOException e) {
		       System.out.println("Error "+e);
		    }
		
	}
	
	
	public static void exportToImage(JFrame frame) {
		
        try {
            Container contentPane = frame.getContentPane();
            BufferedImage image = new BufferedImage(contentPane.getWidth(), contentPane.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();
            contentPane.printAll(g2d);
            g2d.dispose();

            // replace this path to your image
            ImageIO.write(image, "jpeg", new File("Print.png"));
        } catch (IOException e) {
		    System.out.println("Error "+e);
        }

        
	}
	
	protected void paintComponent(Graphics g)
    {
        g.drawRect(50,50,50,50);
    }
	
	public static void saveGraph( List<Cell> cells,String path) {
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
