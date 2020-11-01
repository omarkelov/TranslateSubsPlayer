package ru.nsu.fit.markelov.controllers.player;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import ru.nsu.fit.markelov.managers.SceneManager;
import ru.nsu.fit.markelov.subtitles.BOMSrtParser;
import ru.nsu.fit.markelov.subtitles.JavaFxSubtitles;
import ru.nsu.fit.markelov.translation.Translator;
import ru.nsu.fit.markelov.translation.entities.TranslationGroup;
import ru.nsu.fit.markelov.translation.entities.TranslationResult;
import ru.nsu.fit.markelov.translation.entities.TranslationVariant;
import ru.nsu.fit.markelov.translation.googlejson.GoogleJsonTranslator;
import ru.nsu.fit.markelov.translation.googlescripts.GoogleScriptsTranslator;
import uk.co.caprica.vlcj.subs.Spu;
import uk.co.caprica.vlcj.subs.Spus;
import uk.co.caprica.vlcj.subs.TextSpu;
import uk.co.caprica.vlcj.subs.handler.SpuHandler;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class SubtitlesControl {

    private static final Font TOOLTIP_FONT = new Font(20);
    private static final Font BIG_FONT = new Font(30);
    private static final String SPACE = " ";
    private static final String NEW_LINE = System.lineSeparator();

    private static final int TRANSLATION_BAR_Y_MARGIN = 15;
    private static final int TOOLTIP_Y_MARGIN = 15;

    private SpuHandler subtitlesHandler;
    private RadioMenuItem currentSubtitlesMenuItem;

    private final SceneManager sceneManager;
    private final SubtitlesObserver subtitlesObserver;

    private final Group subtitlesGroup;
    private final TextFlow subtitlesTextFlow;

    private final Pane translationPane;
    private final Group translationGroup;
    private final TextFlow translationTextFlow;
    private final ImageView translationSpinnerImageView;

    private final Pane tooltipPane;
    private final Group tooltipGroup;
    private final TextFlow tooltipTextFlow;

    private Text firstSelectedText;
    private Text lastSelectedText;
    private Text leftSelectedText;
    private Text rightSelectedText;

    private Thread translationThread;

    private final Translator googleJsonTranslator = new GoogleJsonTranslator(2);
    private final Translator googleScriptsTranslator = new GoogleScriptsTranslator(1);

    private String sourceLanguageCode;
    private String targetLanguageCode;

    private Map<Integer, CloseSubtitlesInfo> closeSubtitlesInfoMap;
    private int currentSubtitleId = 0;

    public SubtitlesControl(SceneManager sceneManager, SubtitlesObserver subtitlesObserver,
                            Group subtitlesGroup, TextFlow subtitlesTextFlow,
                            Pane translationPane, Group translationGroup,
                            TextFlow translationTextFlow, ImageView translationSpinnerImageView,
                            Pane tooltipPane, Group tooltipGroup, TextFlow tooltipTextFlow)
    {
        this.sceneManager = sceneManager;
        this.subtitlesObserver = subtitlesObserver;
        this.subtitlesGroup = subtitlesGroup;
        this.subtitlesTextFlow = subtitlesTextFlow;
        this.translationPane = translationPane;
        this.translationGroup = translationGroup;
        this.translationTextFlow = translationTextFlow;
        this.translationSpinnerImageView = translationSpinnerImageView;
        this.tooltipPane = tooltipPane;
        this.tooltipGroup = tooltipGroup;
        this.tooltipTextFlow = tooltipTextFlow;

        subtitlesTextFlow.setOnMousePressed(this::onSubtitlesTextFlowMousePressed);
        subtitlesTextFlow.setOnMouseDragged(this::onSubtitlesTextFlowMouseDragged);
        subtitlesTextFlow.setOnMouseReleased(this::onSubtitlesTextFlowOnMouseReleased);
    }

    public void setCurrentSubtitlesMenuItem(RadioMenuItem currentSubtitlesMenuItem) {
        this.currentSubtitlesMenuItem = currentSubtitlesMenuItem;
    }

    public void setSourceLanguageCode(String sourceLanguageCode) {
        this.sourceLanguageCode = sourceLanguageCode;
    }

    public void setTargetLanguageCode(String targetLanguageCode) {
        this.targetLanguageCode = targetLanguageCode;
    }

    public void initSubtitles(String fileName, RadioMenuItem newRadioMenuItem, long newTime) {
        try (FileReader fileReader = new FileReader(fileName)) {
            Spus subtitleUnits = new BOMSrtParser().parse(fileReader);
            List<Spu<?>> spuList = subtitleUnits.asList();
            Map<Integer, CloseSubtitlesInfo> closeSubtitlesInfoMap = null;

            if (!spuList.isEmpty()) {
                subtitleUnits = new Spus();
                closeSubtitlesInfoMap = new HashMap<>();

                Spu<?> first = spuList.get(0);
                if (first.start() > 0) {
                    subtitleUnits.add(new TextSpu(0, 0, first.start() - 1, null));
                    closeSubtitlesInfoMap.put(0, new CloseSubtitlesInfo(
                        null, null, first.start()));
                }

                int i;
                for (i = 0; i < spuList.size() - 1; i++) {
                    Spu<?> current = spuList.get(i);
                    Spu<?> next = spuList.get(i+1);

                    int number = i + 1;
                    subtitleUnits.add(new Spu<>(number, current.start(), current.end(), current.value()));
                    closeSubtitlesInfoMap.put(number, new CloseSubtitlesInfo(
                        i > 0 ? spuList.get(i-1).start() : null, current.start(), next.start()));

                    if (next.start() > current.end() + 1) {
                        subtitleUnits.add(new Spu<>(-number, current.end() + 1, next.start() - 1, null));
                        closeSubtitlesInfoMap.put(-number, new CloseSubtitlesInfo(
                            current.start(), null, next.start()));
                    }
                }

                int number = i + 1;
                Spu<?> last = spuList.get(spuList.size() - 1);
                subtitleUnits.add(new Spu<>(number, last.start(), last.end(), last.value()));
                closeSubtitlesInfoMap.put(number, new CloseSubtitlesInfo(
                    spuList.size() > 1 ? spuList.get(spuList.size() - 2).start() : null, last.start(), null));
                subtitleUnits.add(new Spu<>(-number, last.end() + 1, Long.MAX_VALUE, null));
                closeSubtitlesInfoMap.put(-number, new CloseSubtitlesInfo(
                    last.start(), null, null));
            }

            subtitlesHandler = new SpuHandler(subtitleUnits);
            subtitlesHandler.addSpuEventListener(subtitleUnit -> {
                if (subtitleUnit == null) {
                    System.out.println("Unexpected behavior: subtitleUnit is NULL");

                    Platform.runLater(() -> {
                        hideSubtitlesBar();
                        hideTranslationBar();
                    });

                    return;
                }

                if (subtitleUnit.number() > 0) {
                    JavaFxSubtitles javaFxSubtitles =
                        new JavaFxSubtitles(subtitleUnit.value().toString());

                    Platform.runLater(() -> {
                        hideTranslationBar();
                        subtitlesTextFlow.getChildren().clear();
                        subtitlesTextFlow.getChildren().addAll(javaFxSubtitles.getTextList());
                        subtitlesTextFlow.setVisible(true);
                        currentSubtitleId = subtitleUnit.number();
                    });
                } else {
                    Platform.runLater(() -> {
                        hideTranslationBar();
                        hideSubtitlesBar();
                        currentSubtitleId = subtitleUnit.number();
                    });
                }
            });

            hideSubtitlesBar();
            currentSubtitlesMenuItem = newRadioMenuItem;
            subtitlesHandler.setTime(newTime);
            this.closeSubtitlesInfoMap = closeSubtitlesInfoMap;
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

    private void hideSubtitlesBar() {
        subtitlesTextFlow.setVisible(false);
        subtitlesTextFlow.getChildren().clear();
    }

    public void disposeSubtitles() {
        subtitlesHandler = null;
        closeSubtitlesInfoMap = null;
        currentSubtitleId = 0;
        hideSubtitlesBar();
        hideTranslationBar();
    }

    public void setTime(long newTime) {
        if (subtitlesHandler != null) {
            subtitlesHandler.setTime(newTime);
        }
    }

    public Long getLeftSubtitleTime() {
        if (closeSubtitlesInfoMap == null) {
            return null;
        }

        return closeSubtitlesInfoMap.get(currentSubtitleId).getLeftSubtitleStartTime();
    }

    public Long getCurrentSubtitleTime() {
        if (closeSubtitlesInfoMap == null) {
            return null;
        }

        return closeSubtitlesInfoMap.get(currentSubtitleId).getCurrentSubtitleStartTime();
    }

    public Long getRightSubtitleTime() {
        if (closeSubtitlesInfoMap == null) {
            return null;
        }

        return closeSubtitlesInfoMap.get(currentSubtitleId).getRightSubtitleStartTime();
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
        if (firstSelectedText == null) {
            return;
        }

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
                    if (currentText.getText().equals(NEW_LINE)) {
                        containsLineSeparator = true;
                        stringBuilder.append(SPACE);
                    } else {
                        stringBuilder.append(currentText.getText());
                    }

                    if (currentText == rightSelectedText) {
                        break;
                    }
                } else {
                    if (currentText == leftSelectedText) {
                        if (currentText.getText().equals(NEW_LINE)) {
                            containsLineSeparator = true;
                            stringBuilder.append(SPACE);
                        } else {
                            stringBuilder.append(currentText.getText());
                        }

                        adding = true;
                    }
                }
            }
        }

        translationTextFlow.getChildren().clear();
        translationSpinnerImageView.setVisible(true);
        bindGroups(containsLineSeparator);
        translationPane.setVisible(true);

        boolean finalContainsLineSeparator = containsLineSeparator;
        translationThread = new Thread(() -> {
            try {
                String text = stringBuilder.toString().trim(); // todo!!! trim dots, etc.

                TranslationResult translationResult = googleJsonTranslator.translate(
                    sourceLanguageCode, targetLanguageCode, text);

                if (translationResult.isEmpty()) {
                    translationResult = googleScriptsTranslator.translate(
                        sourceLanguageCode, targetLanguageCode, text);
                }

                // todo!!! refactor
                List<Text> textList = new ArrayList<>();
                if (translationResult.isEmpty()) {
                    textList.add(createText("No translation..."));
                } else {
                    if (translationResult.getTranslation() != null) {
                        textList.add(createText(translationResult.getTranslation()));
                    }

                    if (translationResult.getTranslationGroups() != null) {
                        for (TranslationGroup translationGroup : translationResult.getTranslationGroups()) {
                            textList.add(createText(NEW_LINE));
                            Text bigText = new Text(SPACE);
                            bigText.setFont(BIG_FONT);
                            textList.add(bigText);

                            Text partOfSpeechText = new Text(capitalize(translationGroup.getPartOfSpeech()) + ": ");
                            partOfSpeechText.setFill(Color.GRAY);
                            textList.add(partOfSpeechText);

                            for (TranslationVariant translationVariant : translationGroup.getVariants()) {
                                StringJoiner translationJoiner = new StringJoiner(", ");
                                for (String translation : translationVariant.getTranslations()) {
                                    translationJoiner.add(translation);
                                }

                                Text wordText = new Text(translationVariant.getWord());
                                wordText.setFill(Color.WHITE);
                                wordText.setOnMouseEntered(wordMouseEvent -> {
                                    wordText.setFill(Color.YELLOW);

                                    Text backTranslationText = new Text(translationJoiner.toString());
                                    backTranslationText.setFont(TOOLTIP_FONT);
                                    backTranslationText.setFill(Color.YELLOW);
                                    double backTranslationTextWidth = backTranslationText.getLayoutBounds().getWidth();
                                    double backTranslationTextHeight = backTranslationText.getLayoutBounds().getHeight();

                                    tooltipTextFlow.setMaxWidth(06.d * sceneManager.getStageWidthProperty().doubleValue());
                                    tooltipTextFlow.getChildren().add(backTranslationText);

                                    Bounds bounds = wordText.localToScene(wordText.getBoundsInLocal());
                                    double hoveredTextCenterX = 0.5d * (bounds.getMinX() + bounds.getMaxX());
                                    double shiftX = 0.5d * backTranslationTextWidth;
                                    tooltipGroup.setLayoutX(hoveredTextCenterX - shiftX);

                                    double hoveredTextCenterY = 0.5d * (bounds.getMinY() + bounds.getMaxY());
                                    double shiftY = backTranslationTextHeight + tooltipTextFlow.getPadding().getTop() + tooltipTextFlow.getPadding().getBottom();
                                    tooltipGroup.setLayoutY(hoveredTextCenterY - shiftY - TOOLTIP_Y_MARGIN);

                                    tooltipPane.setVisible(true);
                                });
                                wordText.setOnMouseExited(wordMouseEvent -> {
                                    wordText.setFill(Color.WHITE);
                                    hideTooltipBar();
                                });

                                textList.add(wordText);
                                textList.add(createText(", "));
                            }

                            textList.get(textList.size() - 1).setText(".");
                        }
                    }
                }

                Platform.runLater(() -> {
                    if (translationThread == null || translationThread.isInterrupted()) {
                        return;
                    }

                    translationSpinnerImageView.setVisible(false);
                    translationTextFlow.getChildren().addAll(textList);

                    unbindGroups();
                    bindGroups(finalContainsLineSeparator);
                });
            } catch (InterruptedException e) { // todo!!! handle
                System.out.println("Interrupted: " + e.getMessage()); // todo! print stack trace
            }
        });
        translationThread.start();
    }

    private Text createText(String str) {
        Text text = new Text(str);
        text.setFill(Color.WHITE);

        return text;
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void bindGroups(boolean bindToCenter) {
        translationGroup.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
            double clickedTextCenterX;

            if (bindToCenter) {
                Bounds bounds = subtitlesGroup.localToScene(subtitlesGroup.getBoundsInLocal());

                clickedTextCenterX = 0.5d * (bounds.getMinX() + bounds.getMaxX());
            } else {
                Bounds leftBounds = leftSelectedText.localToScene(leftSelectedText.getBoundsInLocal());
                Bounds rightBounds = rightSelectedText.localToScene(rightSelectedText.getBoundsInLocal());

                clickedTextCenterX = 0.5d * (leftBounds.getMinX() + rightBounds.getMaxX());
            }

            double shiftX = 0.5d * translationGroup.getBoundsInLocal().getWidth();

            return clickedTextCenterX - shiftX;
        }, subtitlesGroup.layoutXProperty(), translationGroup.boundsInLocalProperty()));

        translationGroup.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
            Bounds bounds = leftSelectedText.localToScene(leftSelectedText.getBoundsInLocal());

            double clickedTextCenterY = 0.5d * (bounds.getMinY() + bounds.getMaxY());
            double shiftY = translationGroup.getBoundsInLocal().getHeight();

            return clickedTextCenterY - shiftY - TRANSLATION_BAR_Y_MARGIN;
        }, subtitlesGroup.layoutYProperty(), translationGroup.boundsInLocalProperty()));
    }

    private void unbindGroups() {
        translationGroup.layoutXProperty().unbind();
        translationGroup.layoutYProperty().unbind();
    }

    public void hideTranslationBar() {
        if (translationThread != null) {
            translationThread.interrupt();
            translationThread = null;
        }

        for (Node child : subtitlesTextFlow.getChildren()) {
            ((Text) child).setFill(Color.WHITE);
        }

        unbindGroups();

        firstSelectedText = null;
        lastSelectedText = null;
        leftSelectedText = null;
        rightSelectedText = null;

        hideTooltipBar();
        translationTextFlow.getChildren().clear();
        translationPane.setVisible(false);
    }

    public boolean isTranslationBarVisible() {
        return translationPane.isVisible();
    }

    private void hideTooltipBar() {
        tooltipTextFlow.getChildren().clear();
        tooltipPane.setVisible(false);
    }
}
