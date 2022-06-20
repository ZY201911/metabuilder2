package viewers;

import diagram.Diagram;
import geom.Rectangle;
import viewers.diagrams.DiagramViewer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Utility class to create icons that are drawn
 * using graphic primitives.
 */
public final class ImageCreator 
{
	private static final double LINE_WIDTH = 0.6;
	private static final int DIAGRAM_PADDING = 4;
	
	private ImageCreator() {}
	
	/**
	 * Creates an image of an entire diagram, with a white border around.
	 * @param pDiagram The diagram to create an image off.
	 * @return An image of the diagram.
	 * @pre pDiagram != null.
	 */
	public static Image createImage(Diagram pDiagram)
	{
		assert pDiagram != null;
		DiagramViewer viewer = pDiagram.getDiagramViewer();
		Rectangle bounds = viewer.getBounds(pDiagram);
		Canvas canvas = new Canvas(bounds.getWidth() + DIAGRAM_PADDING * 2, 
				bounds.getHeight() + DIAGRAM_PADDING *2);
		GraphicsContext context = canvas.getGraphicsContext2D();
		context.setLineWidth(LINE_WIDTH);
		context.setFill(Color.WHITE);
		context.translate(-bounds.getX()+DIAGRAM_PADDING, -bounds.getY()+DIAGRAM_PADDING);
		viewer.draw(pDiagram, context);
		WritableImage image = new WritableImage(bounds.getWidth() + DIAGRAM_PADDING * 2, 
				bounds.getHeight() + DIAGRAM_PADDING *2);
		canvas.snapshot(null, image);
		return image;
	}
}