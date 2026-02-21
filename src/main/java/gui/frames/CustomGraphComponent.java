/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui.frames;

import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.mxGraphComponent;
import javax.swing.*;

public class CustomGraphComponent extends mxGraphComponent {

    public CustomGraphComponent(com.mxgraph.view.mxGraph graph) {
        super(graph);
    }
    
    

    @Override
    protected TransferHandler createTransferHandler()
	{
		return new FileTransferHandler();
	}
    
//    @Override
//    protected mxGraphHandler createGraphHandler()
//	{
//		return new CustomGraphHandler(this);
//	}

}