package ru.nsu.fit.markelov.controllers.player;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import ru.nsu.fit.markelov.managers.SceneManager;
import ru.nsu.fit.markelov.subtitles.BOMSrtParser;
import ru.nsu.fit.markelov.subtitles.JavaFxSubtitles;
import uk.co.caprica.vlcj.subs.Spus;
import uk.co.caprica.vlcj.subs.handler.SpuHandler;

import java.io.FileReader;
import java.io.IOException;

public class SubtitlesControl {

    private SpuHandler subtitlesHandler;
    private RadioMenuItem currentSubtitlesMenuItem;

    private final SceneManager sceneManager;
    private final SubtitlesObserver subtitlesObserver;

    private final Group subtitlesGroup;
    private final TextFlow subtitlesTextFlow;

    private final Pane translationPane;
    private final Group translationGroup;
    private final TextFlow translationTextFlow;

    private Text firstSelectedText;
    private Text lastSelectedText;
    private Text leftSelectedText;
    private Text rightSelectedText;

    public SubtitlesControl(SceneManager sceneManager, SubtitlesObserver subtitlesObserver,
                            Group subtitlesGroup, TextFlow subtitlesTextFlow,
                            Pane translationPane, Group translationGroup, TextFlow translationTextFlow) {
        this.sceneManager = sceneManager;
        this.subtitlesObserver = subtitlesObserver;
        this.subtitlesGroup = subtitlesGroup;
        this.subtitlesTextFlow = subtitlesTextFlow;
        this.translationPane = translationPane;
        this.translationGroup = translationGroup;
        this.translationTextFlow = translationTextFlow;

        subtitlesTextFlow.setOnMousePressed(this::onSubtitlesTextFlowMousePressed);
        subtitlesTextFlow.setOnMouseDragged(this::onSubtitlesTextFlowMouseDragged);
        subtitlesTextFlow.setOnMouseReleased(this::onSubtitlesTextFlowOnMouseReleased);
    }

    public void setCurrentSubtitlesMenuItem(RadioMenuItem currentSubtitlesMenuItem) {
        this.currentSubtitlesMenuItem = currentSubtitlesMenuItem;
    }

    public void initSubtitles(String fileName, RadioMenuItem newRadioMenuItem, long newTime) {
        try (FileReader fileReader = new FileReader(fileName)) {
            Spus subtitleUnits = new BOMSrtParser().parse(fileReader);

            subtitlesHandler = new SpuHandler(subtitleUnits);
            subtitlesHandler.addSpuEventListener(subtitleUnit -> {
                if (subtitleUnit != null && !subtitleUnit.value().toString().isEmpty()) {
                    JavaFxSubtitles javaFxSubtitles =
                        new JavaFxSubtitles(subtitleUnit.value().toString());

                    Platform.runLater(() -> {
                        hideTranslationBar();
                        subtitlesTextFlow.getChildren().clear();
                        subtitlesTextFlow.getChildren().addAll(javaFxSubtitles.getTextList());
                        subtitlesTextFlow.setVisible(true);
                    });
                } else {
                    Platform.runLater(() -> {
                        subtitlesTextFlow.setVisible(false);
                        subtitlesTextFlow.getChildren().clear();
                        hideTranslationBar();
                    });
                }
            });

            subtitlesTextFlow.setVisible(false);
            subtitlesTextFlow.getChildren().clear();
            currentSubtitlesMenuItem = newRadioMenuItem;
            subtitlesHandler.setTime(newTime);
        } catch (IOException e) {
            currentSubtitlesMenuItem.setSelected(true);
            sceneManager.showError("File cannot be opened",
                "The next file cannot be opened: " + fileName);
        } catch (Exception e) {
            currentSubtitlesMenuItem.setSelected(true);
            sceneManager.showError("Subtitles cannot be parsed",
                "The next file cannot be parsed: " + fileName);
        }
    }

    public void disposeSubtitles() {
        subtitlesHandler = null;
        subtitlesTextFlow.setVisible(false);
        subtitlesTextFlow.getChildren().clear();
        hideTranslationBar();
    }

    public void setTime(long newTime) {
        if (subtitlesHandler != null) {
            subtitlesHandler.setTime(newTime);
        }
    }

    private void onSubtitlesTextFlowMousePressed(MouseEvent mouseEvent) {
        hideTranslationBar();

        Node intersectedNode = mouseEvent.getPickResult().getIntersectedNode();
        if (intersectedNode != null && intersectedNode.getParent() == subtitlesTextFlow) {
            subtitlesObserver.onSubtitlesTextPressed();

            firstSelectedText = lastSelectedText = (Text) intersectedNode;
            firstSelectedText.setFill(Color.YELLOW);
            for (Node child : subtitlesTextFlow.getChildren()) {
                if (child != firstSelectedText) {
                    ((Text) child).setFill(Color.WHITE);
                }
            }
        }
    }

    private void onSubtitlesTextFlowMouseDragged(MouseEvent mouseEvent) {
        Node intersectedNode = mouseEvent.getPickResult().getIntersectedNode();
        if (intersectedNode != null && intersectedNode.getParent() == subtitlesTextFlow) {
            lastSelectedText = (Text) intersectedNode;

            if (firstSelectedText == lastSelectedText) {
                return;
            }

            boolean filling = false;
            for (Node child : subtitlesTextFlow.getChildren()) {
                Text currentText = (Text) child;
                if (filling) {
                    if (currentText == firstSelectedText || currentText == lastSelectedText) {
                        filling = false;
                    }

                    currentText.setFill(Color.YELLOW);
                } else {
                    if (currentText == firstSelectedText || currentText == lastSelectedText) {
                        currentText.setFill(Color.YELLOW);
                        filling = true;
                        continue;
                    }

                    currentText.setFill(Color.WHITE);
                }
            }
        }
    }

    private void onSubtitlesTextFlowOnMouseReleased(MouseEvent mouseEvent) {
        if (firstSelectedText == null) {
            return;
        }

        for (Node child : subtitlesTextFlow.getChildren()) {
            if (child == firstSelectedText) {
                leftSelectedText = firstSelectedText;
                rightSelectedText = lastSelectedText;
                break;
            } else if (child == lastSelectedText) {
                leftSelectedText = lastSelectedText;
                rightSelectedText = firstSelectedText;
                break;
            }
        }

        boolean containsLineSeparator = false;
        StringBuilder stringBuilder = new StringBuilder();
        if (firstSelectedText == lastSelectedText) {
            stringBuilder.append(firstSelectedText.getText());
        } else {
            boolean adding = false;
            for (Node child : subtitlesTextFlow.getChildren()) {
                Text currentText = (Text) child;
                if (adding) {
                    if (currentText.getText().equals(System.lineSeparator())) { // todo move to constants
                        containsLineSeparator = true;
                        stringBuilder.append(" ");
                    } else {
                        stringBuilder.append(currentText.getText());
                    }

                    if (currentText == rightSelectedText) {
                        break;
                    }
                } else {
                    if (currentText == leftSelectedText) {
                        if (currentText.getText().equals(System.lineSeparator())) { // todo move to constants
                            containsLineSeparator = true;
                            stringBuilder.append(" ");
                        } else {
                            stringBuilder.append(currentText.getText());
                        }

                        adding = true;
                    }
                }
            }
        }

        Text text = new Text(stringBuilder.toString().trim());
        text.setFill(Color.WHITE);

        translationTextFlow.getChildren().clear();
        translationTextFlow.getChildren().add(text);

        if (containsLineSeparator) {
            translationGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
                Bounds bounds = subtitlesGroup.localToScene(subtitlesGroup.getBoundsInLocal());

                double clickedTextCenterX = 0.5d * (bounds.getMinX() + bounds.getMaxX());
                double shiftX = 0.5d * translationGroup.getBoundsInLocal().getWidth();

                return clickedTextCenterX - shiftX;
            }, subtitlesGroup.layoutXProperty(), translationGroup.boundsInLocalProperty()));
        } else {
            translationGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
                Bounds leftBounds = leftSelectedText.localToScene(leftSelectedText.getBoundsInLocal());
                Bounds rightBounds = rightSelectedText.localToScene(rightSelectedText.getBoundsInLocal());

                double clickedTextCenterX = 0.5d * (leftBounds.getMinX() + rightBounds.getMaxX());
                double shiftX = 0.5d * translationGroup.getBoundsInLocal().getWidth();

                return clickedTextCenterX - shiftX;
            }, subtitlesGroup.layoutXProperty(), translationGroup.boundsInLocalProperty()));
        }

        translationGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds bounds = leftSelectedText.localToScene(leftSelectedText.getBoundsInLocal());

            double clickedTextCenterY = 0.5d * (bounds.getMinY() + bounds.getMaxY());
            double shiftY = 1.25d * translationGroup.getBoundsInLocal().getHeight();

            return clickedTextCenterY - shiftY;
        }, subtitlesGroup.layoutYProperty(), translationGroup.boundsInLocalProperty()));

        translationPane.setVisible(true);
    }

    public void hideTranslationBar() {
        for (Node child : subtitlesTextFlow.getChildren()) {
            ((Text) child).setFill(Color.WHITE);
        }

        translationGroup.layoutXProperty().unbind();
        translationGroup.layoutYProperty().unbind();

        firstSelectedText = null;
        lastSelectedText = null;
        leftSelectedText = null;
        rightSelectedText = null;

        translationPane.setVisible(false);
    }

    public boolean isTranslationBarVisible() {
        return translationPane.isVisible();
    }
}
