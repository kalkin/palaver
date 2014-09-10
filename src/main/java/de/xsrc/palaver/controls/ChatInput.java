package de.xsrc.palaver.controls;

import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.xmpp.PalaverManager;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;

import java.util.logging.Logger;

public class ChatInput extends TextArea {
	private static final Logger logger = Logger.getLogger(ChatInput.class.getName());

	final private KeyCombination SHIFT_ENTER = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);
	final private KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
	private Palaver palaver;

	public ChatInput() {
		super();
		this.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			String msg = this.getText().trim();
			if (ENTER.match(event) && !msg.isEmpty()) { // on enter send
				PalaverManager.sendMsg(this.palaver, msg);
				this.clear();
				event.consume();
			} else if (SHIFT_ENTER.match(event)) { // add a new line.
				this.deleteText(this.getSelection());
				this.insertText(this.getCaretPosition(), "\n");
				event.consume();
			}
		});
	}

	public void setPalaver(Palaver palaver) {
		this.palaver = palaver;
	}

}
