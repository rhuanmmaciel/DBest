package gui.frames;

import java.util.StringJoiner;

import javax.swing.JDialog;
import javax.swing.JTextPane;

import controllers.ConstantController;
import engine.info.Parameters;
import sgbd.info.Query;

public class GeneralStatsFrame extends JDialog {

    public GeneralStatsFrame() {
        JTextPane textPane = new JTextPane();

        this.add(textPane);

        StringJoiner textPaneText = new StringJoiner("\n");

        String queryStats = String.format("\n%s:", ConstantController.getString("dataframe.query"));
        String primaryKeySearchStat = String.format("%s = %s", ConstantController.getString("PK_SEARCH"), Query.PK_SEARCH);
        String sortedTuplesStat = String.format("%s = %s", ConstantController.getString("SORT_TUPLES"), Query.SORT_TUPLES);
        String filterComparisonStat = String.format("%s = %s", ConstantController.getString("COMPARE_FILTER"), Query.COMPARE_FILTER);
        String joinComparisonStat = String.format("%s = %s", ConstantController.getString("COMPARE_JOIN"), Query.COMPARE_JOIN);
        String distinctTuplesComparisonStat = String.format("%s = %s", ConstantController.getString("COMPARE_DISTINCT_TUPLE"), Query.COMPARE_DISTINCT_TUPLE);

        textPaneText.add(queryStats);
        textPaneText.add(primaryKeySearchStat);
        textPaneText.add(sortedTuplesStat);
        textPaneText.add(filterComparisonStat);
        textPaneText.add(joinComparisonStat);
        textPaneText.add(distinctTuplesComparisonStat);
        textPaneText.add("");

        String diskStats = String.format("\n%s:", ConstantController.getString("dataframe.disk"));
        String ioSeekWriteTimeStat = String.format("%s = %fms", ConstantController.getString("IO_SEEK_WRITE_TIME"), (Parameters.IO_SEEK_WRITE_TIME ) / 1000000f);
        String ioWriteTimeStat = String.format("%s = %fms", ConstantController.getString("IO_WRITE_TIME"), (Parameters.IO_SEEK_WRITE_TIME ) / 1000000f);
        String ioSeekReadTimeStat = String.format("%s = %fms", ConstantController.getString("IO_SEEK_READ_TIME"), (Parameters.IO_SEEK_READ_TIME ) / 1000000f);
        String ioReadTimeStat = String.format("%s = %fms", ConstantController.getString("IO_READ_TIME"), (Parameters.IO_READ_TIME ) / 1000000f);
        String ioSyncTimeStat = String.format("%s = %fms", ConstantController.getString("IO_SYNC_TIME"), (Parameters.IO_SYNC_TIME ) / 1000000f);
        String ioTimeStat = String.format("%s = %fms", ConstantController.getString("IO_TOTAL_TIME"), (Parameters.IO_SYNC_TIME + Parameters.IO_SEEK_WRITE_TIME + Parameters.IO_READ_TIME + Parameters.IO_SEEK_READ_TIME + Parameters.IO_WRITE_TIME ) / 1000000f);
        String blocksLoadedStat = String.format("%s = %d", ConstantController.getString("dataframe.disk.loaded"), Parameters.BLOCK_LOADED);
        String blocksSavedStat = String.format("%s = %d", ConstantController.getString("dataframe.disk.saved"), Parameters.BLOCK_SAVED);

        textPaneText.add(diskStats);
        textPaneText.add(ioSeekWriteTimeStat);
        textPaneText.add(ioWriteTimeStat);
        textPaneText.add(ioSeekReadTimeStat);
        textPaneText.add(ioReadTimeStat);
        textPaneText.add(ioSyncTimeStat);
        textPaneText.add(ioTimeStat);
        textPaneText.add(blocksLoadedStat);
        textPaneText.add(blocksSavedStat);

        textPane.setText(textPaneText.toString());
        textPane.setEditable(false);

        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
    }
}
