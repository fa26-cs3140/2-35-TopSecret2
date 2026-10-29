package view;

import java.util.List;

public class WebView implements View {

    /// @param message
    /// @return
    @Override
    public String formatMessage(String message) {
        return "";
    }

    /// @param title
    /// @param records
    /// @return
    @Override
    public String formatRecords(String title, List<String> records) {
        return "";
    }

    /// @param title
    /// @param record
    /// @return
    @Override
    public String formatRecord(String title, String record) {
        return "";
    }

    /// @param error
    /// @return
    @Override
    public String formatError(String error) {
        return "";
    }

    public String generateHTML(String title, String content) {
        return "";
    }
}
