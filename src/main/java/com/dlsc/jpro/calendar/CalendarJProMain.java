package com.dlsc.jpro.calendar;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.util.CalendarFX;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class CalendarJProMain extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override

    public void start(Stage stage) {

        if (!CalendarFX.isLicenseKeySet()) {
            CalendarFX.setLicenseKey("LIC=DLSC;VEN=DLSC;VER=1;PRO=STANDARD;RUN=no;CTR=1;SignCode=3F;Signature=302D021500801FD2BF07754E3F3DC2F8858974090B55B0FB7702145059A46432EDD5287F8C08499AE7CB59FE7CBB91");
        }

        CalendarView calendarView = new CalendarView();

        Calendar dirk = new Calendar("Dirk");
        Calendar katja = new Calendar("Katja");
        Calendar philip = new Calendar("Philip");
        Calendar jule = new Calendar("Jule");
        Calendar armin = new Calendar("Armin");

        dirk.setStyle(Style.STYLE1);
        katja.setStyle(Style.STYLE2);
        philip.setStyle(Style.STYLE3);
        jule.setStyle(Style.STYLE4);
        armin.setStyle(Style.STYLE5);

        CalendarSource family = new CalendarSource("Family");
        family.getCalendars().setAll(dirk, katja, philip, jule, armin);

        Calendar n471 = new Calendar("N 470");
        Calendar n788 = new Calendar("N 788");
        Calendar n904 = new Calendar("N 904");

        n471.setStyle(Style.STYLE6);
        n788.setStyle(Style.STYLE7);
        n904.setStyle(Style.STYLE1);

        CalendarSource projects = new CalendarSource("Projects");
        projects.getCalendars().setAll(n471, n788, n904);

        calendarView.getCalendarSources().setAll(family, projects);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(new CalendarToolBar(calendarView));
        borderPane.setCenter(calendarView);

        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(CalendarView.class.getResource("calendar.css").toExternalForm());

        stage.setTitle("Hello jpro!");
        stage.setScene(scene);
        stage.sizeToScene();

        //open JavaFX window
        stage.show();
    }

}
