package view;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CountryWebViewTest {

    @Test
    public void browseCountriesDisplaysCountryRecords() {
        WebView view = new WebView();

        String actual = view.browseView("countries", "Country ID: 1 Name: United States");

        assertTrue(actual.contains("Browse countries"));
        assertTrue(actual.contains("United States"));
    }

    @Test
    public void readCountryDisplaysCountryDetails() {
        WebView view = new WebView();

        String actual = view.readView("countries", "Country ID: 1 Name: United States");

        assertTrue(actual.contains("Read countries"));
        assertTrue(actual.contains("United States"));
    }

    @Test
    public void browseCountriesWithNoRecordsDisplaysPage() {
        WebView view = new WebView();

        String actual = view.browseView("countries", "");

        assertTrue(actual.contains("Browse countries"));
    }

    @Test
    public void countryPagesContainNavigationToMainMenu() {
        WebView view = new WebView();

        String browsePage = view.browseView("countries", "Country ID: 1 Name: United States");
        String readPage = view.readView("countries", "Country ID: 1 Name: United States");

        assertTrue(browsePage.contains("Back to Main Menu"));
        assertTrue(readPage.contains("Back to Main Menu"));
    }
}
