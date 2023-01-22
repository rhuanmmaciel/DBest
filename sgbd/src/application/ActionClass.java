package application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxStylesheet;

import entities.Cell;
import entities.OperatorCell;
import entities.TableCell;
import enums.OperationEnums;
import gui.ResultFrame;
import gui.buttons.Diferenca;
import gui.buttons.Juncao;
import gui.buttons.ProdutoCartesiano;
import gui.buttons.Projecao;
import gui.buttons.Renomeacao;
import gui.buttons.Selecao;
import gui.buttons.Uniao;
import gui.forms.FormFrameJuncao;
import gui.forms.FormFrameProjecao;
import gui.forms.FormFrameSelecao;
import sgbd.prototype.Prototype;
import sgbd.table.Table;
import util.ImportCSVFile;

@SuppressWarnings("serial")
public class ActionClass extends JFrame implements ActionListener{
	
	private mxGraph graph;
	private mxGraphComponent graphComponent;
	private Object newCell;
	private Boolean createCell=false;
	private String style;
	private String name;
	private Object jCell;
	private Boolean isOperation;
	private OperationEnums currentType;
	
	private Projecao tipoProjecao;
	private Selecao tipoSelecao;
	private ProdutoCartesiano tipoProdutoCartesiano;
	private Uniao tipoUniao;
	private Diferenca tipoDiferenca;
	private Renomeacao tipoRenomeacao;
	private Juncao tipoJuncao;
	
	private JButton edgeButton;
	private Boolean createEdge=false;
	private Object newParent;
	private JPanel edgePanel;
	
	private JButton importButton;
	private JToolBar toolBar;
	
	private List<Cell> cells;
	
	private Prototype currentPrototype = null;
	private Table currentTable = null;
	
	public ActionClass() {
		super("Jgraph teste");
		initGUI();
	}
	
	private void initGUI() {
		setSize(800,600);
		setLocationRelativeTo(null);
		
		graph = new mxGraph();
		graphComponent = new mxGraphComponent(graph);
		graphComponent.setPreferredSize(new Dimension(400,400));
		getContentPane().add(graphComponent);
		
	    JPanel containerPanel = new JPanel(new GridLayout(8, 1));
	    mxStylesheet stylesheet = graph.getStylesheet();
	    
	    // criar classe que tenha todos os tipos dai passo soh ela pro de controlar input, criar o action listener na outra classe tbm
	    tipoProjecao = new Projecao(stylesheet);
	    tipoProjecao.getButton().addActionListener(this);
	    containerPanel.add(tipoProjecao.getPanel());
	  
	    tipoSelecao = new Selecao(stylesheet);
	    tipoSelecao.getButton().addActionListener(this);
	    containerPanel.add(tipoSelecao.getPanel());

	    tipoProdutoCartesiano = new ProdutoCartesiano(stylesheet);
	    tipoProdutoCartesiano.getButton().addActionListener(this);
	    containerPanel.add(tipoProdutoCartesiano.getPanel());
	    
	    tipoUniao = new Uniao(stylesheet);
	    tipoUniao.getButton().addActionListener(this);
	    containerPanel.add(tipoUniao.getPanel());

	    tipoDiferenca = new Diferenca(stylesheet);
	    tipoDiferenca.getButton().addActionListener(this);
	    containerPanel.add(tipoDiferenca.getPanel());
	    
	    tipoRenomeacao = new Renomeacao(stylesheet);
	    tipoRenomeacao.getButton().addActionListener(this);
	    containerPanel.add(tipoRenomeacao.getPanel());
	    
	    tipoJuncao = new Juncao(stylesheet);
	    tipoJuncao.getButton().addActionListener(this);
	    containerPanel.add(tipoJuncao.getPanel());

	    edgeButton = new JButton("Edge");
	    edgeButton.setBounds(600, 300, 100, 50);
	    edgeButton.addActionListener(this);
	    edgePanel = new JPanel();
	    edgePanel.add(edgeButton);
	    containerPanel.add(edgePanel);
	    
		toolBar = new JToolBar();
		getContentPane().add(toolBar, BorderLayout.SOUTH);
	    
		importButton = new JButton("Importar tabela");
		toolBar.add(importButton);
		importButton.setHorizontalAlignment(SwingConstants.LEFT);
		importButton.addActionListener(this);
	    
		this.add(containerPanel,BorderLayout.EAST);

		setVisible(true);
		
		Object parent = graph.getDefaultParent();
		graph.getModel().beginUpdate();
		
		mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
		layout.setUseBoundingBox(false);
		layout.execute(parent);
		
		this.cells = new ArrayList<>();
		
		graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				jCell = graphComponent.getCellAt(e.getX(), e.getY());
				Cell cell = cells.stream().filter(x -> x.getCell().equals((mxCell)jCell)).findFirst().orElse(null);
				
				if(createCell == true ) {

					newCell = graph.insertVertex(parent,null, name, e.getX(), e.getY(), 80, 30,style);
					
					cells.add(isOperation ? new OperatorCell(name, style, newCell, currentType) :
												new TableCell(name, style, newCell, currentTable, currentPrototype));
					
					createCell = false;
					
				}
				
				if(jCell != null) {
					
					if(createEdge == true && newParent == null) {
						newParent = jCell;
					}
					
					Cell parentCell = newParent != null ? cells.stream().filter(x -> x.getCell().equals((mxCell)newParent)).findFirst().orElse(null) : null;
					
					if( createEdge == true && jCell != newParent) {
						
						graph.insertEdge(newParent, null,"", newParent, jCell);
						((mxCell) jCell).setParent((mxCell)newParent);
						
						cell.addParent(parentCell);
						
						if(parentCell != null) parentCell.setChild(cell);
						
						if(cell instanceof OperatorCell) {
							
							if(((OperatorCell)cell).getType() == OperationEnums.PROJECAO) new FormFrameProjecao(jCell, cells);
								
							else if(((OperatorCell)cell).getType() == OperationEnums.SELECAO) new FormFrameSelecao(jCell, cells);
								
							else if(((OperatorCell)cell).getType() == OperationEnums.JUNCAO && cell.getParents().size() == 2) new FormFrameJuncao(jCell, cells);
							
						}
						
						newParent = null;
						createEdge = false;
						
					}
				}

				if(e.getButton() == MouseEvent.BUTTON3 && jCell != null) {
					
					graph.getModel().remove(jCell);	

				}
				if(e.getButton() == MouseEvent.BUTTON2 && jCell != null) {
					
					cell.getSourceTableName(name);
					
				}
				
			}
			
		});
		
		graphComponent.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				
				Cell cell = jCell != null ? cells.stream().filter(x -> x.getCell().equals((mxCell)jCell)).findFirst().orElse(null) : null;
				
				if (e.getKeyCode() == KeyEvent.VK_S && jCell != null) {
					new ResultFrame(cell.getContent());
				
				}else if(e.getKeyCode() == KeyEvent.VK_DELETE && jCell != null) {
					
					graph.getModel().remove(jCell);	
				
				}
				
			}
		});
		
		graph.getModel().endUpdate();
			
	}

	private void assignVariables(String styleVar, String nameVar, boolean isOperation, OperationEnums currentType) {
		
		createCell = true;
		newCell = null;
		newParent = null;
		style = styleVar;
		name = nameVar;
		this.isOperation = isOperation;
		this.currentType = currentType;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == tipoProjecao.getButton()) {
			assignVariables("projecao","π  projecao", true, OperationEnums.PROJECAO);
			
		}else if(e.getSource() == tipoSelecao.getButton() ) {
			assignVariables("selecao","σ  selecao", true, OperationEnums.SELECAO);
			
		}else if(e.getSource() == tipoProdutoCartesiano.getButton()) {
			assignVariables("produtoCartesiano","✕  produto cartesiano", true, OperationEnums.PRODUTO_CARTESIANO);
			
		}else if(e.getSource() == tipoUniao.getButton()) {
			assignVariables("uniao","∪  uniao", true, OperationEnums.UNIAO);
			
		}else if(e.getSource() == tipoDiferenca.getButton()) {
			assignVariables("diferenca","-  diferenca", true, OperationEnums.DIFERENCA);
			
		}else if(e.getSource() == tipoRenomeacao.getButton()) {
			assignVariables("renomeacao","ρ  renomeacao", true, OperationEnums.RENOMEACAO);
			
		}else if(e.getSource() == edgeButton) {
			createEdge = true;
			
		}else if(e.getSource() == importButton) {
			ImportCSVFile.importCSVFile();
			String name = ImportCSVFile.getFileName();
			currentTable = ImportCSVFile.getTable();
			currentPrototype = ImportCSVFile.getPrototype();
			assignVariables("tabela", name, false, null);
			
		}else if(e.getSource() == tipoJuncao.getButton()) {
			assignVariables("juncao","|X| juncao", true, OperationEnums.JUNCAO);

		}
			
	}
	
}
