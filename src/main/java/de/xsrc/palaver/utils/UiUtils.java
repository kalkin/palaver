package de.xsrc.palaver.utils;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.FlowHandler;
import org.datafx.controller.flow.container.DefaultFlowContainer;
import org.datafx.controller.flow.context.ViewFlowContext;

import java.util.ResourceBundle;

public class UiUtils {

	public static StackPane getAvatar(String name) {
		int modena_colors[] = {0xe51c23, 0xe91e63, 0x9c27b0, 0x673ab7, 0x5677fc, 0x03e9f4, 0x00bcd4, 0x009688, 0x259b24,
						0x8bc34a, 0xcddc39, 0xffeb3b, 0xffc107, 0xff9800, 0xff5722, 0x795548, 0x9e9e9e, 0x607d8b};
		int color = modena_colors[(int) ((name.hashCode() & 0xffffffffl) % modena_colors.length)];
		String hexcolor = String.format("#%06X", (0xFFFFFF & color));
		Rectangle r = new Rectangle(64, 64);
		r.setFill(Paint.valueOf(hexcolor));
		r.setArcHeight(5);
		r.setArcWidth(5);
		r.setStrokeType(StrokeType.INSIDE);
		r.setStroke(Paint.valueOf("BLACK"));
		StackPane sp = new StackPane();
		Text t = new Text(name.substring(0, 1).toUpperCase());
		t.setFont(Font.font(60));
		sp.getChildren().add(r);
		sp.getChildren().add(t);
		return sp;
	}

	public static synchronized Scene prepareFlow(Flow f,
	                                             ViewFlowContext flowContext) throws FlowException {
		ResourceBundle b = getRessourceBundle();

		DefaultFlowContainer container = new DefaultFlowContainer();
		FlowHandler flowHandler;
		if (flowContext != null) {
			flowHandler = f.createHandler(flowContext);
		} else {
			flowHandler = f.createHandler();
		}
		flowHandler.getViewConfiguration().setResources(b);
		flowHandler.start(container);
		Scene scene = new Scene(container.getView());
		scene.getStylesheets().add("application.css");
		return scene;
	}

	public static ResourceBundle getRessourceBundle() {
		return ResourceBundle.getBundle("i18n.Palaver");
	}

}