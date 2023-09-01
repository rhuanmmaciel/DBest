package gui.frames.forms.create;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import entities.Column;
import entities.cells.TableCell;

public class FormFrameCreateTable extends JDialog implements ActionListener, DocumentListener {

    private JPanel contentPanel;

    private JTable table;

    private DefaultTableModel model;

    private JButton createTableButton;

    private JButton cancelButton;

    private JButton addRowButton;

    private JButton addColumnButton;

    private JButton createDataButton;

    private JTextField tableNameTextField;

    private final TableCell tableCell;

    private final AtomicReference<Boolean> exitReference;

    private final List<Column> columns;

    {
        this.columns = new ArrayList<>();
        this.tableCell = null;
    }

    public FormFrameCreateTable(AtomicReference<Boolean> exitReference) {
        super((Window) null);

        this.setModal(true);

        this.exitReference = exitReference;

        this.initializeGUI();
    }

    private void initializeGUI() {
        this.model = new DefaultTableModel();
        this.table = new JTable(this.model);

        this.createTableButton = new JButton("Criar tabela");
        this.createTableButton.addActionListener(this);

        this.cancelButton = new JButton("Cancelar");
        this.cancelButton.addActionListener(this);

        this.setBounds(100, 100, 1250, 600);
        this.setLocationRelativeTo(null);

        this.contentPanel = new JPanel();
        this.contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        this.setContentPane(this.contentPanel);

        JScrollPane scrollPane = new JScrollPane(this.table);

        JLabel lblNewLabel = new JLabel("Crie sua tabela: ");

        this.addColumnButton = new JButton("Adicionar coluna");
        this.addColumnButton.addActionListener(this);

        this.addRowButton = new JButton("Adicionar linha");
        this.addRowButton.addActionListener(this);

        this.tableNameTextField = new JTextField();
        this.tableNameTextField.getDocument().addDocumentListener(this);
        this.tableNameTextField.setColumns(10);

        JLabel tableNameLabel = new JLabel("Nome da tabela:");

        this.createDataButton = new JButton("Gerador de dados");
        this.createDataButton.addActionListener(this);

        GroupLayout groupLayoutContentPane = new GroupLayout(this.contentPanel);
        groupLayoutContentPane.setHorizontalGroup(
            groupLayoutContentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayoutContentPane.createSequentialGroup()
                    .addGap(37)
                    .addGroup(groupLayoutContentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayoutContentPane.createSequentialGroup()
                            .addComponent(lblNewLabel)
                            .addGap(303)
                            .addComponent(tableNameLabel)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(this.tableNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap()
                        )
                        .addGroup(groupLayoutContentPane.createParallelGroup(Alignment.LEADING)
                            .addGroup(groupLayoutContentPane.createSequentialGroup()
                                .addComponent(this.addColumnButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(this.addRowButton)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(this.createDataButton)
                                .addGap(981)
                            )
                            .addGroup(groupLayoutContentPane.createSequentialGroup()
                                .addGroup(groupLayoutContentPane.createParallelGroup(Alignment.TRAILING)
                                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 1171, GroupLayout.PREFERRED_SIZE)
                                    .addGroup(groupLayoutContentPane.createSequentialGroup()
                                        .addComponent(this.cancelButton)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(this.createTableButton)
                                    )
                                )
                                .addContainerGap()
                            )
                        )
                    )
                )
        );
        groupLayoutContentPane.setVerticalGroup(
            groupLayoutContentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayoutContentPane.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayoutContentPane.createParallelGroup(Alignment.TRAILING)
                        .addGroup(groupLayoutContentPane.createSequentialGroup()
                            .addGroup(groupLayoutContentPane.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblNewLabel)
                                .addComponent(tableNameLabel)
                            )
                            .addGap(18)
                        )
                        .addGroup(groupLayoutContentPane.createSequentialGroup()
                            .addComponent(this.tableNameTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                        )
                    )
                    .addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE)
                    .addGroup(groupLayoutContentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayoutContentPane.createSequentialGroup()
                            .addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                            .addGroup(groupLayoutContentPane.createParallelGroup(Alignment.BASELINE)
                                .addComponent(this.addColumnButton)
                                .addComponent(this.addRowButton)
                                .addComponent(this.createDataButton)
                            )
                            .addGap(55)
                        )
                        .addGroup(groupLayoutContentPane.createSequentialGroup()
                            .addGap(71)
                            .addGroup(groupLayoutContentPane.createParallelGroup(Alignment.BASELINE)
                                .addComponent(this.createTableButton)
                                .addComponent(this.cancelButton))
                            .addContainerGap()
                        )
                    )
                )
        );

        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent event) {
                FormFrameCreateTable.this.exitReference.set(true);
            }
        });

        this.updateButtons();
        this.contentPanel.setLayout(groupLayoutContentPane);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == this.cancelButton) {
            this.exitReference.set(true);
            this.dispose();
        } else if (event.getSource() == this.createTableButton) {
            this.createTable();
        } else if (event.getSource() == this.addColumnButton) {
            new FormFrameAddColumn(this.model, this.columns);
        } else if (event.getSource() == this.addRowButton) {
            this.model.insertRow(this.table.getRowCount(), new Object[]{});
        } else if (event.getSource() == this.createDataButton) {
            new FormFrameCreateData(this.columns, this.model, this.table);
        }

        this.updateButtons();
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        this.updateButtons();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        this.updateButtons();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        this.updateButtons();
    }

    private void updateButtons() {
        this.createTableButton.setEnabled(this.table.getRowCount() > 0 && !this.tableNameTextField.getText().isEmpty());
        this.addRowButton.setEnabled(this.table.getColumnCount() > 0);
        this.createDataButton.setEnabled(this.table.getRowCount() > 0);

        this.updateToolTipText();
    }

    private void updateToolTipText() {
        String btnCreateDataToolTipText = "";
        String btnAddRowToolTipText = "";
        String btnCreateTableToolTipText = "";

        if (this.table.getColumnCount() <= 0) {
            btnCreateDataToolTipText = btnAddRowToolTipText = btnCreateTableToolTipText = "- A tabela não contém colunas";
        } else if (this.table.getRowCount() <= 0) {
            btnCreateDataToolTipText = btnCreateTableToolTipText = "- A tabela não contém linhas";
        } else if (this.tableNameTextField.getText().isEmpty()) {
            btnCreateTableToolTipText = "- A tabela não contém nome";
        }

        UIManager.put("ToolTip.foreground", Color.RED);

        this.addRowButton.setToolTipText(btnAddRowToolTipText.isEmpty() ? null : btnAddRowToolTipText);
        this.createDataButton.setToolTipText(btnCreateDataToolTipText.isEmpty() ? null : btnCreateDataToolTipText);
        this.createTableButton.setToolTipText(btnCreateTableToolTipText.isEmpty() ? null : btnCreateTableToolTipText);
    }

    private void createTable() {
        Map<Integer, Map<String, String>> content = new HashMap<>();

        for (int i = 0; i < this.table.getRowCount(); i++) {
            Map<String, String> line = new HashMap<>();

            for (int j = 0; j < this.table.getColumnCount(); j++) {
				if (this.table.getValueAt(i, j) == null || this.table.getValueAt(i, j).toString() == null) {
					line.put(this.table.getColumnName(j), "");
				} else {
					line.put(this.table.getColumnName(j), this.table.getValueAt(i, j).toString());
				}
            }

            content.put(i, line);
        }

        boolean exit = false;

        AtomicReference<Boolean> exitReference = new AtomicReference<>(exit);
        StringBuilder primaryKeyName = new StringBuilder();

        //new PrimaryKey(content, primaryKeyName, exitReference);

        if (!exitReference.get()) {
            //tableCell = TableCreator.createTable(textFieldTableName.getText(), primaryKeyName.toString(), columns, content, false);
        } else {
            exitReference.set(true);
        }

        this.dispose();
    }

    public TableCell getResult() {
        return this.tableCell;
    }
}
