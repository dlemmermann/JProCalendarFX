/**
 * Copyright (C) 2015, 2016 Dirk Lemmermann Software & Consulting (dlsc.com)
 * <p>
 * This file is part of CalendarFX.
 */

package com.dlsc.jpro.calendar;

import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarFXControl;
import com.calendarfx.view.DeveloperConsole;
import com.calendarfx.view.SearchResultView;
import com.calendarfx.view.SourceView;
import com.calendarfx.view.YearMonthView;
import com.calendarfx.view.page.DayPage;
import com.calendarfx.view.page.PageBase;
import com.calendarfx.view.print.PrintView;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.utils.FontAwesomeIconFactory;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.controlsfx.control.MasterDetailPane;

import static com.calendarfx.view.RequestEvent.REQUEST_DATE;
import static com.calendarfx.view.RequestEvent.REQUEST_DATE_TIME;
import static com.calendarfx.view.RequestEvent.REQUEST_ENTRY;
import static com.calendarfx.view.RequestEvent.REQUEST_WEEK;
import static com.calendarfx.view.RequestEvent.REQUEST_YEAR;
import static com.calendarfx.view.RequestEvent.REQUEST_YEAR_MONTH;
import static com.calendarfx.view.YearMonthView.ClickBehaviour.PERFORM_SELECTION;
import static javafx.geometry.Side.RIGHT;
import static javafx.scene.control.SelectionMode.SINGLE;

public class CalendarViewSkin extends SkinBase<CalendarView> {

    private CustomMasterDetailPane leftMasterDetailPane;
    private SearchResultView searchResultView;
    private StackPane stackPane;

    public CalendarViewSkin(CalendarView view) {
        super(view);

        if (Boolean.getBoolean("calendarfx.developer")) { //$NON-NLS-1$
            view.addEventFilter(KeyEvent.KEY_PRESSED, evt -> {
                if (evt.isMetaDown() && evt.getCode().equals(KeyCode.D)) {
                    view.setShowDeveloperConsole(
                            !view.isShowDeveloperConsole());
                }
            });
        }

        view.addEventHandler(REQUEST_DATE, evt -> view.showDate(evt.getDate()));
        view.addEventHandler(REQUEST_DATE_TIME, evt -> view.showDateTime(evt.getDateTime()));
        view.addEventHandler(REQUEST_WEEK, evt -> view.showWeek(evt.getYear(), evt.getWeekOfYear()));
        view.addEventHandler(REQUEST_YEAR_MONTH, evt -> view.showYearMonth(evt.getYearMonth()));
        view.addEventHandler(REQUEST_YEAR, evt -> view.showYear(evt.getYear()));
        view.addEventHandler(REQUEST_ENTRY, evt -> view.getSelectedPage().editEntry(evt.getEntry()));

        DayPage dayPage = view.getDayPage();

        this.leftMasterDetailPane = new CustomMasterDetailPane(Side.LEFT);
        this.leftMasterDetailPane.setAnimated(false);
        this.leftMasterDetailPane.setShowDetailNode(true);

        TrayPane trayPane = new TrayPane();

//        if (view.isShowSourceTray()) {
//            openTray();
//        } else {
//            closeTray();
//        }

        view.showSourceTrayProperty().addListener(it -> {
            if (view.isShowSourceTray()) {
                openTray();
            } else {
                closeTray();
            }
        });

        // toolbar right

        Text searchIcon = FontAwesomeIconFactory.get().createIcon(FontAwesomeIcon.SEARCH);
        searchIcon.setId("search-icon"); //$NON-NLS-1$

        BorderPane borderPane1 = new BorderPane();

        borderPane1.topProperty().bind(view.headerProperty());
        borderPane1.setCenter(stackPane = new StackPane());
        borderPane1.bottomProperty().bind(view.footerProperty());

        view.selectedPageProperty().addListener(it -> changePage());

        leftMasterDetailPane.setMasterNode(borderPane1);
        leftMasterDetailPane.setDetailNode(trayPane);
        leftMasterDetailPane.setId("tray-pane"); //$NON-NLS-1$
        leftMasterDetailPane.animatedProperty().bindBidirectional(view.traysAnimatedProperty());
        leftMasterDetailPane.getStylesheets().add(CalendarFXControl.class.getResource("calendar.css").toExternalForm()); //$NON-NLS-1$
        leftMasterDetailPane.setDividerPosition(.3);

        MasterDetailPane rightMasterDetailPane = new MasterDetailPane(RIGHT);
        searchResultView = view.getSearchResultView();

        Bindings.bindContentBidirectional(searchResultView.getCalendarSources(), view.getCalendarSources());

        searchResultView.zoneIdProperty().bind(view.zoneIdProperty());
        searchResultView.selectedEntryProperty().addListener(evt -> showSelectedSearchResult());

        view.showSearchResultsTrayProperty().bind(Bindings.not(Bindings.isEmpty(searchResultView.getSearchResults())));

        rightMasterDetailPane.setDetailNode(searchResultView);
        rightMasterDetailPane.setMasterNode(leftMasterDetailPane);
        rightMasterDetailPane.showDetailNodeProperty().bind(view.showSearchResultsTrayProperty());

        BorderPane borderPane = new BorderPane();

        borderPane.setCenter(rightMasterDetailPane);

        if (Boolean.getBoolean("calendarfx.developer")) { //$NON-NLS-1$
            DeveloperConsole developerConsole = view.getDeveloperConsole();
            MasterDetailPane developerConsoleMasterDetailPane = new MasterDetailPane(Side.BOTTOM);
            developerConsoleMasterDetailPane.setDividerPosition(.6);
            developerConsoleMasterDetailPane.animatedProperty().bind(view.traysAnimatedProperty());
            developerConsoleMasterDetailPane.getStyleClass().add("developer-master-detail-pane"); //$NON-NLS-1$
            developerConsoleMasterDetailPane.setDetailSide(Side.BOTTOM);
            developerConsoleMasterDetailPane.setMasterNode(borderPane);
            developerConsoleMasterDetailPane.setDetailNode(developerConsole);
            developerConsoleMasterDetailPane.setShowDetailNode(true);
            developerConsoleMasterDetailPane.showDetailNodeProperty().bind(view.showDeveloperConsoleProperty());
            developerConsoleMasterDetailPane.getStylesheets().add(CalendarFXControl.class.getResource("calendar.css").toExternalForm());
            getChildren().add(developerConsoleMasterDetailPane);
        } else {
            getChildren().add(borderPane);
        }

        stackPane.getChildren().setAll(dayPage);
    }

    private void openTray() {
        leftMasterDetailPane.resetDividerPosition();
        leftMasterDetailPane.setShowDetailNode(true);
    }

    private void closeTray() {
        leftMasterDetailPane.setShowDetailNode(false);
    }

    private void changePage() {
        CalendarView view = getSkinnable();

        PageBase selectedPage = view.getSelectedPage();

        selectedPage.setManaged(true);
        selectedPage.setVisible(true);

            /*
             * These values might have been changed if transitions were used
             * before.
             */
        selectedPage.setScaleX(1);
        selectedPage.setScaleY(1);
        selectedPage.setOpacity(1);

        stackPane.getChildren().setAll(selectedPage);
    }

    private void showSelectedSearchResult() {
        Entry<?> result = searchResultView.getSelectedEntry();
        if (result != null) {
            getSkinnable().showEntry(result);
        }
    }

    class TrayPane extends BorderPane {
        private SourceView sourceView;
        private YearMonthView yearMonthView;

        public TrayPane() {
            // source view
            sourceView = getSkinnable().getSourceView();
            sourceView.bind(getSkinnable());

            // year month view
            yearMonthView = getSkinnable().getYearMonthView();
            yearMonthView.setShowToday(false);
            yearMonthView.setShowTodayButton(false);
            yearMonthView.setId("date-picker"); //$NON-NLS-1$
            yearMonthView.setSelectionMode(SINGLE);
            yearMonthView.setClickBehaviour(PERFORM_SELECTION);
            yearMonthView.getSelectedDates().add(getSkinnable().getDate());
            yearMonthView.getSelectedDates().addListener((Observable evt) -> {
                if (yearMonthView.getSelectedDates().size() > 0) {
                    yearMonthView.setDate(yearMonthView.getSelectedDates().iterator().next());
                }
            });

            getSkinnable().dateProperty().addListener(it -> {
                yearMonthView.getSelectedDates().clear();
                yearMonthView.getSelectedDates().add(getSkinnable().getDate());
            });

            Bindings.bindBidirectional(yearMonthView.todayProperty(), getSkinnable().todayProperty());
            Bindings.bindBidirectional(yearMonthView.dateProperty(), getSkinnable().dateProperty());
            yearMonthView.weekFieldsProperty().bind(getSkinnable().weekFieldsProperty());

            ScrollPane scrollPane = new ScrollPane(sourceView);

            scrollPane.getStyleClass().add("source-view-scroll-pane"); //$NON-NLS-1$
            setCenter(scrollPane);
            setBottom(yearMonthView);
        }
    }

    private PrintView printView;

    private void print() {
        if (printView == null) {
            printView = getSkinnable().getPrintView();
        }
        printView.setToday(getSkinnable().getToday());
        printView.setWeekFields(getSkinnable().getWeekFields());
        printView.getCalendarSources().setAll(getSkinnable().getCalendarSources());
        printView.setLayout(getSkinnable().getSelectedPage().getLayout());
        printView.setViewType(getSkinnable().getSelectedPage().getPrintViewType());
        printView.requestStartDate(getSkinnable().getDate());
        printView.show(getSkinnable().getScene().getWindow());
    }

    private class CustomMasterDetailPane extends MasterDetailPane {

        public CustomMasterDetailPane(Side side) {
            super(side);
        }

    }
}
