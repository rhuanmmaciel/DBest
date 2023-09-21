package gui.frames;

import controller.ConstantController;
import engine.info.Parameters;
import sgbd.info.Query;

import javax.swing.*;

public class GeneralStatsFrame extends JDialog {

    public GeneralStatsFrame(){

        JTextPane textPane = new JTextPane();
        add(textPane);

        textPane.setText(ConstantController.getString("dataframe.query") + ":\n" +
                ConstantController.getString("dataframe.query.pkSearch") + " = " +
                (Query.PK_SEARCH) + "\n" +
                ConstantController.getString("dataframe.query.sortedTuples") + " = " +
                (Query.SORT_TUPLES ) + "\n" +
                ConstantController.getString("dataframe.query.filterComparison") + " = " +
                (Query.COMPARE_FILTER ) + "\n" +
                ConstantController.getString("dataframe.query.joinComparison") + " = " +
                (Query.COMPARE_JOIN ) + "\n" +
                ConstantController.getString("dataframe.query.distinctTuplesComparison") + " = " +
                (Query.COMPARE_DISTINCT_TUPLE ) + "\n\n" +

                ConstantController.getString("dataframe.disk") + ":" + "\n" +
                ConstantController.getString("dataframe.disk.IOSeekWriteTime") + " = " +
                (Parameters.IO_SEEK_WRITE_TIME ) / 1000000f + "ms" + "\n" +
                ConstantController.getString("dataframe.disk.IOWriteTime") + " = " +
                (Parameters.IO_WRITE_TIME ) / 1000000f + "ms" + "\n" +
                ConstantController.getString("dataframe.disk.IOSeekReadTime") + " = " +
                (Parameters.IO_SEEK_READ_TIME ) / 1000000f + "ms" + "\n" +
                ConstantController.getString("dataframe.disk.IOReadTime") + " = " +
                (Parameters.IO_READ_TIME ) / 1000000f + "ms" + "\n" +
                ConstantController.getString("dataframe.disk.IOSyncTime") + " = " +
                (Parameters.IO_SYNC_TIME ) / 1000000f + "ms" + "\n" +
                ConstantController.getString("dataframe.disk.IOTime") + " = " + (Parameters.IO_SYNC_TIME
                + Parameters.IO_SEEK_WRITE_TIME
                + Parameters.IO_READ_TIME
                + Parameters.IO_SEEK_READ_TIME
                + Parameters.IO_WRITE_TIME ) / 1000000f + "ms" + "\n" +
                ConstantController.getString("dataframe.block.loaded") + " = " +
                (Parameters.BLOCK_LOADED ) + "\n" +
                ConstantController.getString("dataframe.block.saved") + " = " +
                (Parameters.BLOCK_SAVED ));

        textPane.setEditable(false);

        pack();
        setVisible(true);
        setLocationRelativeTo(null);

    }

}
