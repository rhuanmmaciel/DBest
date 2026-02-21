/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui.frames;

import java.awt.Component;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CustomDropTarget extends DropTarget {
    private List<DropTargetListener> listeners = new ArrayList<>();

    public CustomDropTarget(Component component) {
        super(component, DnDConstants.ACTION_COPY, null, true);
    }

    @Override
    public void addDropTargetListener(DropTargetListener listener) {
        listeners.add(listener);
    }

    @Override
    public synchronized void drop(DropTargetDropEvent dtde) {
        for (DropTargetListener listener : listeners) {
            listener.drop(dtde);
        }
    }

    @Override
    public synchronized void dragEnter(DropTargetDragEvent dtde) {
        for (DropTargetListener listener : listeners) {
            listener.dragEnter(dtde);
        }
    }

    @Override
    public synchronized void dragOver(DropTargetDragEvent dtde) {
        for (DropTargetListener listener : listeners) {
            listener.dragOver(dtde);
        }
    }

    @Override
    public synchronized void dropActionChanged(DropTargetDragEvent dtde) {
        for (DropTargetListener listener : listeners) {
            listener.dropActionChanged(dtde);
        }
    }

    @Override
    public synchronized void dragExit(DropTargetEvent dte) {
        for (DropTargetListener listener : listeners) {
            listener.dragExit(dte);
        }
    }
}
