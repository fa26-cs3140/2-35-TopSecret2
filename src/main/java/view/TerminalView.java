package view;

import control.ControllerInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TerminalView {

    private final ControllerInterface controller;
    private final Scanner scnr;
    private String currentUser;

    public TerminalView(ControllerInterface controller) {
        this.controller = controller;
        this.scnr = new Scanner(System.in);
    }

    // ---------------- GENERAL INPUT ----------------

    private void display(String message) {
    }

    private String getInput(String message) {
        return "";
    }

    private String getRequiredInput(String message) {
        return "";
    }

    private String getOptionalInput(String message) {
        return "";
    }

    private int getInputInt(int min, int max, String message) {
        return 0;
    }

    private int getPositiveInt(String message) {
        return 0;
    }

    private boolean isAdmin() {
        return false;
    }

    private String capitalize(String value) {
        return "";
    }

    // ---------------- APPLICATION ENTRY ----------------

    public int run() {
        return 0;
    }

    // ---------------- LOGIN / ACCOUNT MANAGEMENT ----------------

    private boolean loginStep() {
        return false;
    }

    private String[] promptSignup() {
        return new String[0];
    }

    private String[] promptChangePassword() {
        return new String[0];
    }

    private void changePassword() {
    }

    // ---------------- MAIN MENU ----------------

    private void showMainMenu() {
    }

    // ---------------- BROWSE MENUS ----------------

    private void browseMenu(String itemType) {
    }

    // ---------------- RECORD OPERATIONS ----------------

    private void listEntries(String itemType) {
    }

    private void readEntry(String itemType) {
    }

    private void addEntry(String itemType) {
    }

    private void editEntry(String itemType) {
    }

    private List<String> promptRecordFields(String itemType) {
        return new ArrayList<>();
    }

    private boolean addRequiredField(
            List<String> fields, String message) {
        return false;
    }

    // ---------------- AGENT / MISSION ASSIGNMENT ----------------

    private void assignAgentToMission() {
    }

    // ---------------- USER MANAGEMENT ----------------

    private void createUser() {
    }

    private void manageAccess() {
    }

    // ---------------- AUDIT LOG ----------------

    private void reviewAuditLog() {
    }

    // ---------------- MESSAGES ----------------

    private void browseMessages() {
    }

    private void sendMessage() {
    }
}