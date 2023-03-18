package util;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImportGraph {

	
	
	public ImportGraph() {
	
		JFileChooser fileUpload = new JFileChooser();
		//fileUpload.showOpenDialog(null);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
		fileUpload.setFileFilter(filter);
		
		int res = fileUpload.showOpenDialog(null);
		
		if(res == JFileChooser.APPROVE_OPTION) {
			
			//createData(fileUpload);
			//createTable(currentFileName, columnsNameList, rows);	
		}
		
	}
	
		
		
}
