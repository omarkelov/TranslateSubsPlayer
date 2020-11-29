package ru.nsu.fit.markelov.controllers.player.hotkeys;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import ru.nsu.fit.markelov.managers.SceneManager;

import java.io.IOException;
import java.util.List;

/**
 * HotkeysGridPaneLoader class is used for loading a grid pane with player hotkeys description.
 *
 * @author Oleg Markelov
 */
public class HotkeysGridPaneLoader {

    private static final String HOTKEYS_FXML_FILE_NAME = "playerHotkeys.fxml";
    private static final String HOTKEYS_ROW_FXML_FILE_NAME = "playerHotkeysRow.fxml";

    private static final String COLORED_ROW_STYLE_CLASS = "colored-row";

    /**
     * Loads hotkeys layouts and returns a grid pane with hotkeys description.
     *
     * @param sceneManager scene manager.
     * @param hotkeys      list of key event info.
     * @return grid pane with hotkeys description.
     * @throws IOException if cannot load or parse .fxml file.
     */
    public static GridPane loadGridPane(SceneManager sceneManager, List<KeyEventInfo> hotkeys)
        throws IOException
    {
        try {
            GridPane gridPane = (GridPane) sceneManager.loadFXML(HOTKEYS_FXML_FILE_NAME);

            for (int i = 0; i < hotkeys.size(); i++) {
                GridPane tmpGridPane = (GridPane) sceneManager.loadFXML(HOTKEYS_ROW_FXML_FILE_NAME);

                HBox hotkeyHBox = (HBox) tmpGridPane.getChildren().get(0);
                HBox descriptionHBox = (HBox) tmpGridPane.getChildren().get(1);

                if (i % 2 == 0) {
                    hotkeyHBox.getStyleClass().add(COLORED_ROW_STYLE_CLASS);
                    descriptionHBox.getStyleClass().add(COLORED_ROW_STYLE_CLASS);
                }

                ((Text) hotkeyHBox.getChildren().get(0)).setText(hotkeys.get(i).getName());
                ((Text) descriptionHBox.getChildren().get(0)).setText(hotkeys.get(i).getDescription());

                gridPane.add(hotkeyHBox, 0, i + 1);
                gridPane.add(descriptionHBox, 1, i + 1);
            }

            return gridPane;
        } catch (ClassCastException e) {
            throw new IOException(e);
        }
    }
}
