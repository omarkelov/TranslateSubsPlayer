package ru.nsu.fit.markelov.controllers.player;

import com.google.common.base.Charsets;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import ru.nsu.fit.markelov.javafxutil.AlertBuilder;
import ru.nsu.fit.markelov.javafxutil.TextBuilder;
import ru.nsu.fit.markelov.managers.SceneManager;
import ru.nsu.fit.markelov.subtitles.AdvancedSrtParser;
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

import static ru.nsu.fit.markelov.Constants.BIG_BOLD_FONT;
import static ru.nsu.fit.markelov.Constants.DISABLED_COLOR;
import static ru.nsu.fit.markelov.Constants.MEDIUM_FONT;
import static ru.nsu.fit.markelov.Constants.NEW_LINE;
import static ru.nsu.fit.markelov.Constants.SELECTED_COLOR;
import static ru.nsu.fit.markelov.Constants.SPACE;
import static ru.nsu.fit.markelov.Constants.STANDARD_COLOR;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.FILE_OPENING_ERROR_CONTENT_PREFIX;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.FILE_OPENING_ERROR_HEADER;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.SUBTITLES_PARSING_ERROR_CONTENT_PREFIX;
import static ru.nsu.fit.markelov.javafxutil.AlertBuilder.SUBTITLES_PARSING_ERROR_HEADER;

public class SubtitlesControl implements AutoCloseable {

    private static final int TRANSLATION_BAR_Y_MARGIN = 15;
    private static final int TOOLTIP_Y_MARGIN = 15;

    private static final TextBuilder TRANSLATION_TB = new TextBuilder();
    private static final TextBuilder LINE_EXPANDER_TB = new TextBuilder() {{
        setFont(new Font(BIG_BOLD_FONT.getSize()));
    }};
    private static final TextBuilder PART_OF_SPEECH_TB = new TextBuilder() {{
        setColor(DISABLED_COLOR);
    }};
    private static final TextBuilder BACK_TRANSLATION_TB = new TextBuilder() {{
        setFont(MEDIUM_FONT);
        setColor(SELECTED_COLOR);
    }};

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
        try (FileReader fileReader = new FileReader(fileName, Charsets.UTF_8)) {
            Spus subtitleUnits = new AdvancedSrtParser.Builder()
                .enableBomSupport()
                .build().parse(fileReader);
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

            new AlertBuilder()
                .setHeaderText(FILE_OPENING_ERROR_HEADER)
                .setContentText(FILE_OPENING_ERROR_CONTENT_PREFIX + fileName)
                .setException(e)
                .setOwner(sceneManager.getWindowOwner())
                .build()
                .showAndWait();
        } catch (Exception e) {
            currentSubtitlesMenuItem.setSelected(true);

            new AlertBuilder()
                .setHeaderText(SUBTITLES_PARSING_ERROR_HEADER)
                .setContentText(SUBTITLES_PARSING_ERROR_CONTENT_PREFIX + fileName)
                .setException(e)
                .setOwner(sceneManager.getWindowOwner())
                .build()
                .showAndWait();
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
            firstSelectedText.setFill(SELECTED_COLOR);
            for (Node child : subtitlesTextFlow.getChildren()) {
                if (child != firstSelectedText) {
                    ((Text) child).setFill(STANDARD_COLOR);
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

                    currentText.setFill(SELECTED_COLOR);
                } else {
                    if (currentText == firstSelectedText || currentText == lastSelectedText) {
                        currentText.setFill(SELECTED_COLOR);
                        filling = true;
                        continue;
                    }

                    currentText.setFill(STANDARD_COLOR);
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
            if (firstSelectedText.getText().isBlank()) {
                return;
            }

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

                List<Text> textList = buildTranslationTextList(translationResult);

                Platform.runLater(() -> {
                    if (translationThread == null || translationThread.isInterrupted()) {
                        return;
                    }

                    translationSpinnerImageView.setVisible(false);
                    translationTextFlow.getChildren().addAll(textList);

                    unbindTranslationNodes();
                    bindGroups(finalContainsLineSeparator);
                });
            } catch (InterruptedException ignored) {}
        });
        translationThread.start();
    }

    private List<Text> buildTranslationTextList(TranslationResult translationResult) {
        List<Text> textList = new ArrayList<>();

        if (translationResult.isEmpty()) {
            textList.add(TRANSLATION_TB.setText("No translation...").build()); // todo suggest link to the site
        } else {
            if (translationResult.getTranslation() != null) {
                textList.add(TRANSLATION_TB.setText(translationResult.getTranslation()).build());
            }

            if (translationResult.getTranslationGroups() != null) {
                for (TranslationGroup translationGroup : translationResult.getTranslationGroups()) {
                    textList.add(TRANSLATION_TB.setText(NEW_LINE).build());
                    textList.add(LINE_EXPANDER_TB.setText(SPACE).build());

                    textList.add(PART_OF_SPEECH_TB.setText(
                        capitalize(translationGroup.getPartOfSpeech()) + ": ").build());

                    for (TranslationVariant translationVariant : translationGroup.getVariants()) {
                        StringJoiner translationJoiner = new StringJoiner(", ");
                        for (String translation : translationVariant.getTranslations()) {
                            translationJoiner.add(translation);
                        }

                        Text wordText = TRANSLATION_TB.setText(translationVariant.getWord()).build();
                        wordText.setOnMouseEntered(wordMouseEvent -> {
                            wordText.setFill(SELECTED_COLOR);

                            Text backTranslationText = BACK_TRANSLATION_TB
                                .setText(translationJoiner.toString()).build();
                            double backTranslationTextWidth = backTranslationText.getLayoutBounds().getWidth();
                            double backTranslationTextHeight = backTranslationText.getLayoutBounds().getHeight();

                            tooltipTextFlow.setMaxWidth(06.d * sceneManager.getStageWidthProperty().doubleValue());
                            tooltipTextFlow.getChildren().add(backTranslationText);

                            Bounds bounds = wordText.localToScene(wordText.getBoundsInLocal());
                            double hoveredTextCenterX = 0.5d * (bounds.getMinX() + bounds.getMaxX());
                            double shiftX = 0.5d * backTranslationTextWidth;
                            tooltipGroup.setLayoutX(hoveredTextCenterX - shiftX);

                            double hoveredTextCenterY = 0.5d * (bounds.getMinY() + bounds.getMaxY());
                            double shiftY = backTranslationTextHeight +
                                tooltipTextFlow.getPadding().getTop() +
                                tooltipTextFlow.getPadding().getBottom();
                            tooltipGroup.setLayoutY(hoveredTextCenterY - shiftY - TOOLTIP_Y_MARGIN);

                            tooltipPane.setVisible(true);
                        });
                        wordText.setOnMouseExited(wordMouseEvent -> {
                            wordText.setFill(STANDARD_COLOR);
                            hideTooltipBar();
                        });

                        textList.add(wordText);
                        textList.add(TRANSLATION_TB.setText(", ").build());
                    }

                    textList.get(textList.size() - 1).setText(".");
                }
            }
        }

        return textList;
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private void bindGroups(boolean bindToCenter) {
        translationTextFlow.maxWidthProperty().bind(Bindings.createDoubleBinding(() -> {
            double clickedTextCenterX;

            if (bindToCenter) {
                Bounds bounds = subtitlesGroup.localToScene(subtitlesGroup.getBoundsInLocal());

                clickedTextCenterX = 0.5d * (bounds.getMinX() + bounds.getMaxX());
            } else {
                Bounds leftBounds = leftSelectedText.localToScene(leftSelectedText.getBoundsInLocal());
                Bounds rightBounds = rightSelectedText.localToScene(rightSelectedText.getBoundsInLocal());

                clickedTextCenterX = 0.5d * (leftBounds.getMinX() + rightBounds.getMaxX());
            }

            double stageWidth = sceneManager.getStageWidthProperty().doubleValue();

            double minMargin = Math.min(clickedTextCenterX, stageWidth - clickedTextCenterX);
            double partOfStageWidth = 0.6d * stageWidth;

            return Math.min(partOfStageWidth, 2 * minMargin) - 50;
        }, sceneManager.getStageWidthProperty()));

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

    private void unbindTranslationNodes() {
        translationTextFlow.maxWidthProperty().unbind();
        translationGroup.layoutXProperty().unbind();
        translationGroup.layoutYProperty().unbind();
    }

    public void hideTranslationBar() {
        interruptTranslationThread();

        for (Node child : subtitlesTextFlow.getChildren()) {
            ((Text) child).setFill(STANDARD_COLOR);
        }

        unbindTranslationNodes();

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

    @Override
    public void close() {
        interruptTranslationThread();
    }

    private void interruptTranslationThread() {
        if (translationThread != null) {
            translationThread.interrupt();
            translationThread = null;
        }
    }
}
