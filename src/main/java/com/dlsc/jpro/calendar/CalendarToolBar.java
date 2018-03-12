package com.dlsc.jpro.calendar;

import com.calendarfx.view.SearchResultView;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.control.textfield.CustomTextField;

/**
 * Created by lemmi on 29.08.17.
 */
public class CalendarToolBar extends HBox{

    private final CalendarView calendarView;

    public CalendarToolBar(CalendarView calendarView) {
        this.calendarView = calendarView;

        getStyleClass().addAll("module-toolbar", "calendar-toolbar", "primary-color-calendar");

        ToggleGroup group = new ToggleGroup();

        ToggleButton showDay = new ToggleButton("DAY");
        ToggleButton showWeek = new ToggleButton("WEEK");
        ToggleButton showMonth = new ToggleButton("MONTH");
        //ToggleButton showYear = new ToggleButton("YEAR");

        showDay.setOnAction(evt -> showDayPage());
        showWeek.setOnAction(evt -> showWeekPage());
        showMonth.setOnAction(evt -> showMonthPage());
        //showYear.setOnAction(evt -> showYearPage());

        showDay.getStyleClass().add("first");
        showMonth.getStyleClass().add("last");
        //showYear.getStyleClass().add("last");

        showDay.setSelected(true);
        showDay.setMaxHeight(Double.MAX_VALUE);
        showWeek.setMaxHeight(Double.MAX_VALUE);
        showMonth.setMaxHeight(Double.MAX_VALUE);
        //showYear.setMaxHeight(Double.MAX_VALUE);

        group.getToggles().addAll(showDay, showWeek, showMonth);

        HBox switcher = new HBox(showDay, showWeek, showMonth);
        switcher.getStyleClass().add("switcher");
        switcher.setFillHeight(true);
        switcher.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(switcher, Priority.ALWAYS);

        CustomTextField searchField = new CustomTextField();
        Button clearSearchButton = new Button();
        clearSearchButton.getStyleClass().add("clear-search");
        clearSearchButton.setOnAction(evt -> searchField.setText(""));
        searchField.setRight(clearSearchButton);
        searchField.setPromptText("SEARCH");
        searchField.setPrefColumnCount(30);

        SearchResultView searchResultView = calendarView.getSearchResultView();
        searchResultView.searchTextProperty().bind(searchField.textProperty());

        getChildren().addAll(switcher, searchField);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    private void showDayPage() {
        calendarView.showDayPage();
    }

    private void showWeekPage() {
        calendarView.showWeekPage();
    }

    private void showMonthPage() {
        calendarView.showMonthPage();
    }

    private void showYearPage() {
        calendarView.showYearPage();
    }
}
