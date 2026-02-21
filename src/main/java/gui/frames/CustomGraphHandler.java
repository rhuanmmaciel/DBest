/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui.frames;


import com.mxgraph.swing.handler.mxGraphHandler;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.swing.util.mxSwingConstants;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import controllers.MainController;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.TooManyListenersException;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

public class CustomGraphHandler extends mxGraphHandler 
{

	/**
	 * 
	 * @param graphComponent
	 */
	public CustomGraphHandler(final mxGraphComponent graphComponent)
	{
		super(graphComponent);
	}

	/**
	 * 
	 */
	protected void installDragGestureHandler()
	{
		DragGestureListener dragGestureListener = new DragGestureListener()
		{
			public void dragGestureRecognized(DragGestureEvent e)
			{
				if (graphComponent.isDragEnabled() && first != null)
				{
					final TransferHandler th = graphComponent
							.getTransferHandler();

					if (th instanceof mxGraphTransferHandler)
					{
						final mxGraphTransferable t = (mxGraphTransferable) ((mxGraphTransferHandler) th)
								.createTransferable(graphComponent);

						if (t != null)
						{
							e.startDrag(null, mxSwingConstants.EMPTY_IMAGE,
									new Point(), t, new DragSourceAdapter()
									{

										/**
										 * 
										 */
										public void dragDropEnd(
												DragSourceDropEvent dsde)
										{
											((mxGraphTransferHandler) th)
													.exportDone(
															graphComponent,
															t,
															TransferHandler.NONE);
											first = null;
										}
									});
						}
					}
				}
			}
		};

		DragSource dragSource = new DragSource();
		dragSource.createDefaultDragGestureRecognizer(graphComponent
				.getGraphControl(),
				(isCloneEnabled()) ? DnDConstants.ACTION_COPY_OR_MOVE
						: DnDConstants.ACTION_MOVE, dragGestureListener);
	}

	/**
	 * 
	 */
	protected void installDropTargetHandler()
	{
		DropTarget dropTarget = graphComponent.getDropTarget();

		try
		{
			if (dropTarget != null)
			{
				dropTarget.addDropTargetListener(this);
				currentDropTarget = dropTarget;
			}
		}
		catch (TooManyListenersException e)
		{
			// should not happen... swing drop target is multicast
		}
	}

	

	/**
	 * 
	 */
	public void mouseMoved(MouseEvent e)
	{
		if (graphComponent.isEnabled() && isEnabled() && !e.isConsumed())
		{
			Cursor cursor = getCursor(e);

			if (cursor != null)
			{
				graphComponent.getGraphControl().setCursor(cursor);
				e.consume();
			}
			else
			{
				graphComponent.getGraphControl().setCursor(DEFAULT_CURSOR);
			}
		}
	}


	/**
	 * 
	 */
	public void dragEnter(DropTargetDragEvent e)
	{
		JComponent component = getDropTarget(e);
		TransferHandler th = component.getTransferHandler();
		boolean isLocal = th instanceof mxGraphTransferHandler
				&& ((mxGraphTransferHandler) th).isLocalDrag();

		if (isLocal)
		{
			canImport = true;
		}
		else
		{
		boolean af = th.canImport(component, e.getCurrentDataFlavors());	
                    canImport = graphComponent.isImportEnabled()
					&& th.canImport(component, e.getCurrentDataFlavors());
		}

                canImport = true;
		if (canImport)
		{
			transferBounds = null;
			setVisible(false);

			try
			{
				Transferable t = e.getTransferable();

				if (t.isDataFlavorSupported(mxGraphTransferable.dataFlavor))
				{
					mxGraphTransferable gt = (mxGraphTransferable) t
							.getTransferData(mxGraphTransferable.dataFlavor);
					dragCells = gt.getCells();

					if (gt.getBounds() != null)
					{
						mxGraph graph = graphComponent.getGraph();
						double scale = graph.getView().getScale();
						transferBounds = gt.getBounds();
						int w = (int) Math.ceil((transferBounds.getWidth() + 1)
								* scale);
						int h = (int) Math
								.ceil((transferBounds.getHeight() + 1) * scale);
						setPreviewBounds(new Rectangle(
								(int) transferBounds.getX(),
								(int) transferBounds.getY(), w, h));

						if (imagePreview)
						{
							// Does not render fixed cells for local preview
							// but ignores movable state for non-local previews
							if (isLocal)
							{
								if (!isLivePreview())
								{
									updateDragImage(graph
											.getMovableCells(dragCells));
								}
							}
							else
							{
								Object[] tmp = graphComponent
										.getImportableCells(dragCells);
								updateDragImage(tmp);

								// Shows no drag icon if import is allowed but none
								// of the cells can be imported
								if (tmp == null || tmp.length == 0)
								{
									canImport = false;
									e.rejectDrag();

									return;
								}
							}
						}

						setVisible(true);
					}
				}

				e.acceptDrag(TransferHandler.COPY_OR_MOVE);
			}
			catch (Exception ex)
			{
				// do nothing
			}

		}
		else
		{
			e.rejectDrag();
		}
	}

	/**
	 * 
	 */
	public void mousePressed(MouseEvent e)
	{
		if (graphComponent.isEnabled() && isEnabled() && !e.isConsumed()
				&& !graphComponent.isForceMarqueeEvent(e))
		{
			cell = graphComponent.getCellAt(e.getX(), e.getY(), false);
			initialCell = cell;

			if (cell != null)
			{
				if (isSelectEnabled()
						&& !graphComponent.getGraph().isCellSelected(cell))
				{
					graphComponent.selectCellForEvent(cell, e);
					cell = null;
				}

				// Starts move if the cell under the mouse is movable and/or any
				// cells of the selection are movable
				if (isMoveEnabled() && !e.isPopupTrigger())
				{
					start(e);
					e.consume();
				}
			}
			else if (e.isPopupTrigger())
			{
				graphComponent.getGraph().clearSelection();
			}
		}
	}

	

	/**
	 * 
	 * @param e
	 */
	public void dragOver(DropTargetDragEvent e)
	{
		if (canImport)
		{
			mouseDragged(createEvent(e));
			mxGraphTransferHandler handler = getGraphTransferHandler(e);

			if (handler != null)
			{
				mxGraph graph = graphComponent.getGraph();
				double scale = graph.getView().getScale();
				Point pt = SwingUtilities.convertPoint(graphComponent,
						e.getLocation(), graphComponent.getGraphControl());

				pt = graphComponent.snapScaledPoint(new mxPoint(pt)).getPoint();
				handler.setLocation(new Point(pt));

				int dx = 0;
				int dy = 0;

				// Centers the preview image
				if (centerPreview && transferBounds != null)
				{
					dx -= Math.round(transferBounds.getWidth() * scale / 2);
					dy -= Math.round(transferBounds.getHeight() * scale / 2);
				}

				// Sets the drop offset so that the location in the transfer
				// handler reflects the actual mouse position
				handler.setOffset(new Point((int) graph.snap(dx / scale),
						(int) graph.snap(dy / scale)));
				pt.translate(dx, dy);

				// Shifts the preview so that overlapping parts do not
				// affect the centering
				if (transferBounds != null && dragImage != null)
				{
					dx = (int) Math
							.round((dragImage.getIconWidth() - 2 - transferBounds
									.getWidth() * scale) / 2);
					dy = (int) Math
							.round((dragImage.getIconHeight() - 2 - transferBounds
									.getHeight() * scale) / 2);
					pt.translate(-dx, -dy);
				}

				if (!handler.isLocalDrag() && previewBounds != null)
				{
					setPreviewBounds(new Rectangle(pt, previewBounds.getSize()));
				}
			}
		}
		else
		{
			e.rejectDrag();
		}
	}


	/**
	 * 
	 */
	public void mouseDragged(MouseEvent e)
	{
		// LATER: Check scrollborder, use scroll-increments, do not
		// scroll when over ruler dragging from library
		if (graphComponent.isAutoScroll())
		{
			graphComponent.getGraphControl().scrollRectToVisible(
					new Rectangle(e.getPoint()));
		}

		if (!e.isConsumed())
		{
			gridEnabledEvent = graphComponent.isGridEnabledEvent(e);
			constrainedEvent = graphComponent.isConstrainedEvent(e);

			if (constrainedEvent && first != null)
			{
				int x = e.getX();
				int y = e.getY();

				if (Math.abs(e.getX() - first.x) > Math.abs(e.getY() - first.y))
				{
					y = first.y;
				}
				else
				{
					x = first.x;
				}

				e = new MouseEvent(e.getComponent(), e.getID(), e.getWhen(),
						e.getModifiers(), x, y, e.getClickCount(),
						e.isPopupTrigger(), e.getButton());
			}

			if (isVisible() && isMarkerEnabled())
			{
				marker.process(e);
			}

			if (first != null)
			{
				if (movePreview.isActive())
				{
					double dx = e.getX() - first.x;
					double dy = e.getY() - first.y;

					if (graphComponent.isGridEnabledEvent(e))
					{
						mxGraph graph = graphComponent.getGraph();

						dx = graph.snap(dx);
						dy = graph.snap(dy);
					}

					boolean clone = isCloneEnabled()
							&& graphComponent.isCloneEvent(e);
					movePreview.update(e, dx, dy, clone);
					e.consume();
				}
				else if (cellBounds != null)
				{
					double dx = e.getX() - first.x;
					double dy = e.getY() - first.y;

					if (previewBounds != null)
					{
						setPreviewBounds(new Rectangle(getPreviewLocation(e,
								gridEnabledEvent), previewBounds.getSize()));
					}

					if (!isVisible() && graphComponent.isSignificant(dx, dy))
					{
						if (imagePreview && dragImage == null
								&& !graphComponent.isDragEnabled())
						{
							updateDragImage(cells);
						}

						setVisible(true);
					}

					e.consume();
				}
			}
		}
	}


	/**
	 * 
	 * @param e
	 */
	public void dragExit(DropTargetEvent e)
	{
		mxGraphTransferHandler handler = getGraphTransferHandler(e);

		if (handler != null)
		{
			handler.setLocation(null);
		}

		dragCells = null;
		setVisible(false);
		marker.reset();
		reset();
	}

	/**
	 * 
	 * @param e
	 */
	public void drop(DropTargetDropEvent e)
	{
		if (canImport)
		{
			mxGraphTransferHandler handler = getGraphTransferHandler(e);
			MouseEvent event = createEvent(e);

			// Ignores the event in mouseReleased if it is
			// handled by the transfer handler as a drop
			if (handler != null && !handler.isLocalDrag())
			{
				event.consume();
			}

			mouseReleased(event);
		}
	}

	/**
	 * 
	 */
	public void mouseReleased(MouseEvent e)
	{
		if (graphComponent.isEnabled() && isEnabled() && !e.isConsumed())
		{
			mxGraph graph = graphComponent.getGraph();
			double dx = 0;
			double dy = 0;

			if (first != null && (cellBounds != null || movePreview.isActive()))
			{
				double scale = graph.getView().getScale();
				mxPoint trans = graph.getView().getTranslate();

				// TODO: Simplify math below, this was copy pasted from
				// getPreviewLocation with the rounding removed
				dx = e.getX() - first.x;
				dy = e.getY() - first.y;

				if (cellBounds != null)
				{
					double dxg = ((cellBounds.getX() + dx) / scale)
							- trans.getX();
					double dyg = ((cellBounds.getY() + dy) / scale)
							- trans.getY();

					if (gridEnabledEvent)
					{
						dxg = graph.snap(dxg);
						dyg = graph.snap(dyg);
					}

					double x = ((dxg + trans.getX()) * scale) + (bbox.getX())
							- (cellBounds.getX());
					double y = ((dyg + trans.getY()) * scale) + (bbox.getY())
							- (cellBounds.getY());

					dx = Math.round((x - bbox.getX()) / scale);
					dy = Math.round((y - bbox.getY()) / scale);
				}
			}

			if (first == null
					|| !graphComponent.isSignificant(e.getX() - first.x,
							e.getY() - first.y))
			{
				// Delayed handling of selection
				if (cell != null && !e.isPopupTrigger() && isSelectEnabled()
						&& (first != null || !isMoveEnabled()))
				{
					graphComponent.selectCellForEvent(cell, e);
				}

				// Delayed folding for cell that was initially under the mouse
				if (graphComponent.isFoldingEnabled()
						&& graphComponent.hitFoldingIcon(initialCell, e.getX(),
								e.getY()))
				{
					fold(initialCell);
				}
				else
				{
					// Handles selection if no cell was initially under the mouse
					Object tmp = graphComponent.getCellAt(e.getX(), e.getY(),
							graphComponent.isSwimlaneSelectionEnabled());

					if (cell == null && first == null)
					{
						if (tmp == null)
						{
							if (!graphComponent.isToggleEvent(e))
							{
								graph.clearSelection();
							}
						}
						else if (graph.isSwimlane(tmp)
								&& graphComponent.getCanvas()
										.hitSwimlaneContent(graphComponent,
												graph.getView().getState(tmp),
												e.getX(), e.getY()))
						{
							graphComponent.selectCellForEvent(tmp, e);
						}
					}

					if (graphComponent.isFoldingEnabled()
							&& graphComponent.hitFoldingIcon(tmp, e.getX(),
									e.getY()))
					{
						fold(tmp);
						e.consume();
					}
				}
			}
			else if (movePreview.isActive())
			{
				if (graphComponent.isConstrainedEvent(e))
				{
					if (Math.abs(dx) > Math.abs(dy))
					{
						dy = 0;
					}
					else
					{
						dx = 0;
					}
				}

				mxCellState markedState = marker.getMarkedState();
				Object target = (markedState != null) ? markedState.getCell()
						: null;

				// FIXME: Cell is null if selection was carried out, need other variable
				//trace("cell", cell);

				if (target == null
						&& isRemoveCellsFromParent()
						&& shouldRemoveCellFromParent(graph.getModel()
								.getParent(initialCell), cells, e))
				{
					target = graph.getDefaultParent();
				}

				boolean clone = isCloneEnabled()
						&& graphComponent.isCloneEvent(e);
				Object[] result = movePreview.stop(true, e, dx, dy, clone,
						target);

				if (cells != result)
				{
					graph.setSelectionCells(result);
				}

				e.consume();
			}
			else if (isVisible())
			{
				if (constrainedEvent)
				{
					if (Math.abs(dx) > Math.abs(dy))
					{
						dy = 0;
					}
					else
					{
						dx = 0;
					}
				}

				mxCellState targetState = marker.getValidState();
				Object target = (targetState != null) ? targetState.getCell()
						: null;

				if (graph.isSplitEnabled()
						&& graph.isSplitTarget(target, cells))
				{
					graph.splitEdge(target, cells, dx, dy);
				}
				else
				{
					moveCells(cells, dx, dy, target, e);
				}

				e.consume();
			}
		}

		reset();
	}

	/**
	 * 
	 */
	protected void fold(Object cell)
	{
		boolean collapse = !graphComponent.getGraph().isCellCollapsed(cell);
		graphComponent.getGraph().foldCells(collapse, false,
				new Object[] { cell });
	}

	/**
	 * 
	 */
	public void reset()
	{
		if (movePreview.isActive())
		{
			movePreview.stop(false, null, 0, 0, false, null);
		}

		setVisible(false);
		marker.reset();
		initialCell = null;
		dragCells = null;
		dragImage = null;
		cells = null;
		first = null;
		cell = null;
	}

	/**
	 * Returns true if the given cells should be removed from the parent for the specified
	 * mousereleased event.
	 */
	protected boolean shouldRemoveCellFromParent(Object parent, Object[] cells,
			MouseEvent e)
	{
		if (graphComponent.getGraph().getModel().isVertex(parent))
		{
			mxCellState pState = graphComponent.getGraph().getView()
					.getState(parent);

			return pState != null && !pState.contains(e.getX(), e.getY());
		}

		return false;
	}

	/**
	 * 
	 * @param dx
	 * @param dy
	 * @param e
	 */
	protected void moveCells(Object[] cells, double dx, double dy,
			Object target, MouseEvent e)
	{
		mxGraph graph = graphComponent.getGraph();
		boolean clone = e.isControlDown() && isCloneEnabled();

		if (clone)
		{
			cells = graph.getCloneableCells(cells);
		}
		
		if (cells.length > 0)
		{
			// Removes cells from parent
			if (target == null
					&& isRemoveCellsFromParent()
					&& shouldRemoveCellFromParent(
							graph.getModel().getParent(initialCell), cells, e))
			{
				target = graph.getDefaultParent();
			}
	
			Object[] tmp = graph.moveCells(cells, dx, dy, clone, target,
					e.getPoint());
	
			if (isSelectEnabled() && clone && tmp != null
					&& tmp.length == cells.length)
			{
				graph.setSelectionCells(tmp);
			}
		}
	}

	/**
	 *
	 */
	public void paint(Graphics g)
	{
            if (MainController.selectionRectangle != null) {
            Graphics2D tmp = (Graphics2D) g.create();
            tmp.setColor(new Color(0, 0, 255, 100)); // Semi-transparent blue
            tmp.fillRect(MainController.selectionRectangle.x, MainController.selectionRectangle.y, MainController.selectionRectangle.width, MainController.selectionRectangle.height);
        }
            
		if (isVisible() && previewBounds != null)
		{
			if (dragImage != null)
			{
				// LATER: Clipping with mxUtils doesnt fix the problem
				// of the drawImage being painted over the scrollbars
				Graphics2D tmp = (Graphics2D) g.create();

				if (graphComponent.getPreviewAlpha() < 1)
				{
					tmp.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER,
							graphComponent.getPreviewAlpha()));
				}

				tmp.drawImage(dragImage.getImage(), previewBounds.x,
						previewBounds.y, dragImage.getIconWidth(),
						dragImage.getIconHeight(), null);
				tmp.dispose();
			}
			else if (!imagePreview)
			{
				mxSwingConstants.PREVIEW_BORDER.paintBorder(graphComponent, g,
						previewBounds.x, previewBounds.y, previewBounds.width,
						previewBounds.height);
			}
		}
	}

	/**
	 * 
	 */
	protected MouseEvent createEvent(DropTargetEvent e)
	{
		JComponent component = getDropTarget(e);
		Point location = null;
		int action = 0;

		if (e instanceof DropTargetDropEvent)
		{
			location = ((DropTargetDropEvent) e).getLocation();
			action = ((DropTargetDropEvent) e).getDropAction();
		}
		else if (e instanceof DropTargetDragEvent)
		{
			location = ((DropTargetDragEvent) e).getLocation();
			action = ((DropTargetDragEvent) e).getDropAction();
		}

		if (location != null)
		{
			location = convertPoint(location);
			Rectangle r = graphComponent.getViewport().getViewRect();
			location.translate(r.x, r.y);
		}

		// LATER: Fetch state of modifier keys from event or via global
		// key listener using Toolkit.getDefaultToolkit().addAWTEventListener(
		// new AWTEventListener() {...}, AWTEvent.KEY_EVENT_MASK). Problem
		// is the event does not contain the modifier keys and the global
		// handler is not called during drag and drop.
		int mod = (action == TransferHandler.COPY) ? InputEvent.CTRL_MASK : 0;

		return new MouseEvent(component, 0, System.currentTimeMillis(), mod,
				location.x, location.y, 1, false, MouseEvent.BUTTON1);
	}

	

}