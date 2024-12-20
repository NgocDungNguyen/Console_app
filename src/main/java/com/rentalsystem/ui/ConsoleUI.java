package com.rentalsystem.ui;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.text.SimpleDateFormat;
import com.rentalsystem.model.Payment;
import com.rentalsystem.util.FileHandler;


import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import com.rentalsystem.manager.HostManager;
import com.rentalsystem.manager.HostManagerImpl;
import com.rentalsystem.manager.OwnerManager;
import com.rentalsystem.manager.OwnerManagerImpl;
import com.rentalsystem.manager.PropertyManager;
import com.rentalsystem.manager.PropertyManagerImpl;
import com.rentalsystem.manager.RentalManager;
import com.rentalsystem.manager.RentalManagerImpl;
import com.rentalsystem.manager.TenantManager;
import com.rentalsystem.manager.TenantManagerImpl;
import com.rentalsystem.model.CommercialProperty;
import com.rentalsystem.model.Host;
import com.rentalsystem.model.Owner;
import com.rentalsystem.model.Property;
import com.rentalsystem.model.RentalAgreement;
import com.rentalsystem.model.ResidentialProperty;
import com.rentalsystem.model.Tenant;
import com.rentalsystem.util.DateUtil;
import com.rentalsystem.util.InputValidator;

public class ConsoleUI {
    private RentalManager rentalManager;
    private TenantManager tenantManager;
    private OwnerManager ownerManager;
    private HostManager hostManager;
    private PropertyManager propertyManager;
    private final LineReader reader;
    private final Terminal terminal;
    private final FileHandler fileHandler;
    private final TableFormatter tableFormatter;

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";


    private static final String[] RENTAL_ASCII = {
            "██████╗ ███████╗███╗   ██╗████████╗ █████╗ ██╗      ",
            "██╔══██╗██╔════╝████╗  ██║╚══██╔══╝██╔══██╗██║      ",
            "██████╔╝█████╗  ██╔██╗ ██║   ██║   ███████║██║      ",
            "██╔══██╗██╔══╝  ██║╚██╗██║   ██║   ██╔══██║██║      ",
            "██║  ██║███████╗██║ ╚████║   ██║   ██║  ██║███████╗ ",
            "╚═╝  ╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝   ╚═╝  ╚═╝╚══════╝ "
    };

    private static final String[] MANAGER_ASCII = {
            "███╗   ███╗ █████╗ ███╗   ██╗ █████╗  ██████╗ ███████╗██████╗  ",
            "████╗ ████║██╔══██╗████╗  ██║██╔══██╗██╔════╝ ██╔════╝██╔══██╗ ",
            "██╔████╔██║███████║██╔██╗ ██║███████║██║  ███╗█████╗  ██████╔╝ ",
            "██║╚██╔╝██║██╔══██║██║╚██╗██║██╔══██║██║   ██║██╔══╝  ██╔══██╗ ",
            "██║ ╚═╝ ██║██║  ██║██║ ╚████║██║  ██║╚██████╔╝███████╗██║  ██║ ",
            "╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝  ╚═╝ "
    };

    private static final String[] SYSTEM_ASCII = {
            "███████╗██╗   ██╗███████╗████████╗███████╗███╗   ███╗ ",
            "██╔════╝╚██╗ ██╔╝██╔════╝╚══██╔══╝██╔════╝████╗ ████║ ",
            "███████╗ ╚████╔╝ ███████╗   ██║   █████╗  ██╔████╔██║ ",
            "╚════██║  ╚██╔╝  ╚════██║   ██║   ██╔══╝  ██║╚██╔╝██║ ",
            "███████║   ██║   ███████║   ██║   ███████╗██║ ╚═╝ ██║ ",
            "╚══════╝   ╚═╝   ╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚═╝ "
    };

    private void displayExitMessage() {
        System.out.println(ANSI_GREEN +
                "╔════════════════════════════════════════════════════════════════════════════╗\n" +
                "║                                                                            ║\n" +
                "║ ████████╗██╗  ██╗ █████╗ ███╗   ██╗██╗  ██╗    ██╗   ██╗ ██████╗ ██╗   ██╗ ║\n" +
                "║ ╚══██╔══╝██║  ██║██╔══██╗████╗  ██║██║ ██╔╝    ╚██╗ ██╔╝██╔═══██╗██║   ██║ ║\n" +
                "║    ██║   ███████║███████║██╔██╗ ██║█████╔╝      ╚████╔╝ ██║   ██║██║   ██║ ║\n" +
                "║    ██║   ██╔══██║██╔══██║██║╚██╗██║██╔═██╗       ╚██╔╝  ██║   ██║██║   ██║ ║\n" +
                "║    ██║   ██║  ██║██║  ██║██║ ╚████║██║  ██╗       ██║   ╚██████╔╝╚██████╔╝ ║\n" +
                "║    ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═╝       ╚═╝    ╚═════╝  ╚═════╝  ║\n" +
                "║                                                                            ║\n" +
                "║                  for using the Rental Management System                    ║\n" +
                "║                                                                            ║\n" +
                "║                      We hope to see you again soon!                        ║\n" +
                "║                                                                            ║\n" +
                "╚════════════════════════════════════════════════════════════════════════════╝" +
                ANSI_RESET);
    }


    public ConsoleUI() throws IOException {
        terminal = TerminalBuilder.builder().system(true).build();
        List<Completer> completers = new ArrayList<>();
        completers.add(new StringsCompleter("1", "2", "3", "4", "5", "6", "7"));
        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(new AggregateCompleter(completers))
                .build();

        tableFormatter = new TableFormatter(terminal);
        fileHandler = new FileHandler();  // Initialize fileHandler here
    }

private void initializeManagers() {
    System.out.print("Initializing system...");
    this.hostManager = new HostManagerImpl(fileHandler);
    this.tenantManager = new TenantManagerImpl(fileHandler);
    this.ownerManager = new OwnerManagerImpl(fileHandler);
    this.propertyManager = new PropertyManagerImpl(fileHandler, hostManager, tenantManager, ownerManager);
    this.rentalManager = new RentalManagerImpl(fileHandler, tenantManager, propertyManager, hostManager, ownerManager);
    System.out.println(" Done!");
}

    public void start() {
        clearScreen();
        printWelcomeMessage();
        initializeManagers();

        while (true) {
            String command = showMainMenu();
            switch (command) {
                case "1":
                    handleRentalAgreements();
                    break;
                case "2":
                    handleTenants();
                    break;
                case "3":
                    handleOwners();
                    break;
                case "4":
                    handleHosts();
                    break;
                case "5":
                    handleProperties();
                    break;
                case "6":
                    handleReports();
                    break;
                case "7":
                    displayExitMessage();
                    return;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void printWelcomeMessage() {
        System.out.println(TableFormatter.ANSI_CYAN);
        System.out.println("═".repeat(80));
        printCenteredASCII(RENTAL_ASCII);
        printCenteredASCII(MANAGER_ASCII);
        printCenteredASCII(SYSTEM_ASCII);
        System.out.println();
        System.out.println(centerText("Welcome to the Rental Management System", 80));
        System.out.println("═".repeat(80));
        System.out.println(TableFormatter.ANSI_RESET);
    }

    private String showMainMenu() {
        List<String> options = Arrays.asList(
                "Manage Rental Agreements",
                "Manage Tenants",
                "Manage Owners",
                "Manage Hosts",
                "Manage Properties",
                "Generate Reports",
                "Exit"
        );
        tableFormatter.printTable("MAIN MENU", options, TableFormatter.ANSI_GREEN);
        printStatusBar();
        return readUserInput("Enter your choice: ");
    }

    private void printCenteredASCII(String[] ascii) {
        int maxWidth = Arrays.stream(ascii).mapToInt(String::length).max().orElse(0);
        for (String line : ascii) {
            int padding = (80 - line.length()) / 2;
            System.out.println(" ".repeat(padding) + line);
        }
    }

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
    }

    private void printStatusBar() {
        String currentUser = "Admin";
        String currentDate = LocalDate.now().toString();
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        tableFormatter.printStatusBar(currentUser, currentDate, currentTime);
    }

    private void handleRentalAgreements() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Add Agreement", "Update Agreement", "Delete Agreement",
                    "List Agreements", "Search Agreements", "Back to Main Menu"
            );
            tableFormatter.printTable("RENTAL AGREEMENTS", options, TableFormatter.ANSI_BLUE);
            String choice = readUserInput("Enter your choice: ");

            switch (choice) {
                case "1":
                    addRentalAgreement();
                    break;
                case "2":
                    updateRentalAgreement();
                    break;
                case "3":
                    deleteRentalAgreement();
                    break;
                case "4":
                    listRentalAgreements();
                    break;
                case "5":
                    searchRentalAgreements();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }

    private void handleTenants() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Add Tenant", "Update Tenant", "Delete Tenant",
                    "List Tenants", "Search Tenants", "Back to Main Menu"
            );
            tableFormatter.printTable("TENANTS", options, TableFormatter.ANSI_PURPLE);
            String choice = readUserInput("Enter your choice: ");

            switch (choice) {
                case "1":
                    addTenant();
                    break;
                case "2":
                    updateTenant();
                    break;
                case "3":
                    deleteTenant();
                    break;
                case "4":
                    listTenants();
                    break;
                case "5":
                    searchTenants();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }

    private void handleOwners() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Add Owner", "Update Owner", "Delete Owner",
                    "List Owners", "Search Owners", "Back to Main Menu"
            );
            tableFormatter.printTable("OWNERS", options, TableFormatter.ANSI_YELLOW);
            String choice = readUserInput("Enter your choice: ");

            switch (choice) {
                case "1":
                    addOwner();
                    break;
                case "2":
                    updateOwner();
                    break;
                case "3":
                    deleteOwner();
                    break;
                case "4":
                    listOwners();
                    break;
                case "5":
                    searchOwners();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }

    private void handleHosts() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Add Host", "Update Host", "Delete Host",
                    "List Hosts", "Search Hosts", "Back to Main Menu"
            );
            tableFormatter.printTable("HOSTS", options, TableFormatter.ANSI_CYAN);
            String choice = readUserInput("Enter your choice: ");

            switch (choice) {
                case "1":
                    addHost();
                    break;
                case "2":
                    updateHost();
                    break;
                case "3":
                    deleteHost();
                    break;
                case "4":
                    listHosts();
                    break;
                case "5":
                    searchHosts();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }

    private void handleProperties() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Add Property", "Update Property", "Delete Property",
                    "List Properties", "Search Properties", "Back to Main Menu"
            );
            tableFormatter.printTable("PROPERTIES", options, TableFormatter.ANSI_YELLOW);
            String choice = readUserInput("Enter your choice: ");

            switch (choice) {
                case "1":
                    addProperty();
                    break;
                case "2":
                    updateProperty();
                    break;
                case "3":
                    deleteProperty();
                    break;
                case "4":
                    listProperties();
                    break;
                case "5":
                    searchProperties();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }

    private void handleReports() {
        while (true) {
            clearScreen();
            List<String> options = Arrays.asList(
                    "Income Report", "Occupancy Report", "Tenant Report",
                    "Property Status Report", "Tenant Payment History", "Host Performance Report",
                    "Back to Main Menu"
            );
            tableFormatter.printTable("REPORTS", options, TableFormatter.ANSI_RED);
            String choice = readUserInput("Enter your choice: ");

            switch (choice) {
                case "1":
                    generateIncomeReport();
                    break;
                case "2":
                    generateOccupancyReport();
                    break;
                case "3":
                    generateTenantReport();
                    break;
                case "4":
                    generatePropertyStatusReport();
                    break;
                case "5":
                    generateTenantPaymentHistoryReport();
                    break;
                case "6":
                    generateHostPerformanceReport();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            readUserInputAllowEmpty("Press Enter to continue...");
        }
    }

    private void generatePropertyStatusReport() {
        List<Property> properties = propertyManager.getAllProperties();
        List<String> headers = Arrays.asList("Property ID", "Type", "Address", "Status", "Owner", "Host");
        List<List<String>> data = new ArrayList<>();
        for (Property property : properties) {
            data.add(Arrays.asList(
                    property.getPropertyId(),
                    property instanceof ResidentialProperty ? "Residential" : "Commercial",
                    property.getAddress(),
                    property.getStatus().toString(),
                    property.getOwner().getFullName(),
                    property.getHost() != null ? property.getHost().getFullName() : "N/A"
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void generateTenantPaymentHistoryReport() {
        String tenantId = readUserInput("Enter tenant ID: ");
        Tenant tenant = tenantManager.getTenant(tenantId);
        if (tenant == null) {
            System.out.println("Tenant not found.");
            return;
        }
        List<Payment> payments = getPaymentsForTenant(tenant);
        if (payments.isEmpty()) {
            System.out.println("No payment history found for this tenant.");
            return;
        }
        List<String> headers = Arrays.asList("Payment ID", "Date", "Amount", "Method", "Agreement ID");
        List<List<String>> data = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Payment payment : payments) {
            data.add(Arrays.asList(
                    payment.getPaymentId(),
                    dateFormat.format(payment.getPaymentDate()),
                    String.format("%.2f", payment.getAmount()),
                    payment.getPaymentMethod(),
                    payment.getRentalAgreement().getAgreementId()
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private String readUserInput(String prompt) {
        System.out.print(prompt);
        return reader.readLine().trim();
    }

    private void generateHostPerformanceReport() {
        List<Host> hosts = hostManager.getAllHosts();
        List<String> headers = Arrays.asList("Host ID", "Name", "Managed Properties", "Active Agreements", "Total Rent");
        List<List<String>> data = new ArrayList<>();
        for (Host host : hosts) {
            int managedProperties = host.getManagedProperties().size();
            List<RentalAgreement> activeAgreements = host.getManagedAgreements().stream()
                    .filter(a -> a.getStatus() == RentalAgreement.Status.ACTIVE)
                    .collect(Collectors.toList());
            int activeAgreementsCount = activeAgreements.size();
            double totalRent = activeAgreements.stream()
                    .mapToDouble(RentalAgreement::getRentAmount)
                    .sum();
            data.add(Arrays.asList(
                    host.getId(),
                    host.getFullName(),
                    String.valueOf(managedProperties),
                    String.valueOf(activeAgreementsCount),
                    String.format("%.2f", totalRent)
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private String readUserInputAllowEmpty(String prompt) {
        System.out.print(prompt);
        return reader.readLine();
    }

    private String readUserInputAllowEsc(String prompt) {
        System.out.print(prompt);
        String input = reader.readLine().trim();
        if (input.equals("\u001B")) {
            return null;
        }
        return input;
    }

    private void addRentalAgreement() {
        String agreementId = null;
        while (agreementId == null) {
            agreementId = readUserInput("Enter agreement ID (or 'back' to return): ");
            if (agreementId.equalsIgnoreCase("back")) {
                return;
            }
            try {
                if (rentalManager.getRentalAgreement(agreementId) != null) {
                    System.out.println(TableFormatter.ANSI_RED + "Agreement with ID " + agreementId + " already exists." + TableFormatter.ANSI_RESET);
                    agreementId = null;
                }
            } catch (IllegalArgumentException e) {
                break;
            }
        }

        String propertyId = readUserInput("Enter property ID: ");
        String tenantId = readUserInput("Enter tenant ID: ");
        String ownerId = readUserInput("Enter owner ID: ");
        String hostId = readUserInput("Enter host ID: ");

        Date startDate = DateUtil.readDate(reader, "Enter start date (yyyy-MM-dd): ");

        Date endDate = null;
        while (endDate == null || endDate.before(startDate)) {
            endDate = DateUtil.readDate(reader, "Enter end date (yyyy-MM-dd): ");
            if (endDate.before(startDate)) {
                System.out.println(TableFormatter.ANSI_RED + "End date must be after start date." + TableFormatter.ANSI_RESET);
            }
        }

        double rentAmount = InputValidator.readDouble(reader, "Enter rent amount: ", 0, Double.MAX_VALUE);
        RentalAgreement.RentalPeriod rentalPeriod = RentalAgreement.RentalPeriod.valueOf(
                readUserInput("Enter rental period (DAILY/WEEKLY/FORTNIGHTLY/MONTHLY): ").toUpperCase()
        );

        Property property = propertyManager.getProperty(propertyId);
        Tenant tenant = tenantManager.getTenant(tenantId);
        Owner owner = ownerManager.getOwner(ownerId);
        Host host = hostManager.getHost(hostId);

        if (property == null || tenant == null || owner == null || host == null) {
            System.out.println(TableFormatter.ANSI_RED + "One or more entities not found. Please check the IDs and try again." + TableFormatter.ANSI_RESET);
            return;
        }

        RentalAgreement agreement = new RentalAgreement(agreementId, property, tenant, owner, host, startDate, endDate, rentAmount, rentalPeriod);
        rentalManager.addRentalAgreement(agreement);
        System.out.println(TableFormatter.ANSI_GREEN + "Rental agreement added successfully." + TableFormatter.ANSI_RESET);

        List<String> headers = Arrays.asList("ID", "Property", "Tenant", "Start Date", "End Date", "Rent Amount", "Status");
        List<List<String>> data = new ArrayList<>();
        data.add(Arrays.asList(
                agreement.getAgreementId(),
                agreement.getProperty().getPropertyId(),
                agreement.getMainTenant().getFullName(),
                agreement.getStartDate().toString(),
                agreement.getEndDate().toString(),
                String.format("%.2f", agreement.getRentAmount()),
                agreement.getStatus().toString()
        ));
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void updateRentalAgreement() {
        while (true) {
            String id = readUserInputAllowEsc("Enter agreement ID to update (press ESC to return): ");
            if (id == null) return;

            try {
                RentalAgreement agreement = rentalManager.getRentalAgreement(id);
                if (agreement == null) {
                    throw new IllegalArgumentException("Rental agreement not found.");
                }

                Date endDate = DateUtil.readOptionalDate(reader, "Enter new end date (yyyy-MM-dd, press enter to keep current): ");
                if (endDate != null) {
                    if (endDate.before(agreement.getStartDate())) {
                        throw new IllegalArgumentException("End date must be after start date.");
                    }
                    agreement.setEndDate(endDate);
                }

                String rentAmountStr = readUserInputAllowEmpty("Enter new rent amount (press enter to keep current): ");
                if (!rentAmountStr.isEmpty()) {
                    try {
                        double rentAmount = Double.parseDouble(rentAmountStr);
                        if (rentAmount < 0) {
                            throw new IllegalArgumentException("Rent amount must be non-negative.");
                        }
                        agreement.setRentAmount(rentAmount);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid rent amount format.");
                    }
                }

                String statusStr = readUserInputAllowEmpty("Enter new status (NEW/ACTIVE/COMPLETED, press enter to keep current): ");
                if (!statusStr.isEmpty()) {
                    try {
                        agreement.setStatus(RentalAgreement.Status.valueOf(statusStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid status.");
                    }
                }

                rentalManager.updateRentalAgreement(agreement);
                System.out.println(TableFormatter.ANSI_GREEN + "Rental agreement updated successfully." + TableFormatter.ANSI_RESET);
                displayRentalAgreementDetails(agreement);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void deleteRentalAgreement() {
        while (true) {
            String agreementId = readUserInput("Enter agreement ID to delete (or 'back' to return): ");
            if (agreementId.equalsIgnoreCase("back")) {
                return;
            }

            try {
                RentalAgreement agreement = rentalManager.getRentalAgreement(agreementId);
                if (agreement == null) {
                    throw new IllegalArgumentException("Rental agreement not found.");
                }

                displayRentalAgreementDetails(agreement);

                String confirm = readUserInput("Are you sure you want to delete this rental agreement? (y/n): ");
                if (confirm.equalsIgnoreCase("y")) {
                    rentalManager.deleteRentalAgreement(agreementId);
                    System.out.println(TableFormatter.ANSI_GREEN + "Rental agreement deleted successfully." + TableFormatter.ANSI_RESET);
                } else {
                    System.out.println("Deletion cancelled.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void listRentalAgreements() {
        String sortBy = readUserInput("Enter sort criteria (id/propertyid/tenantname/ownername/hostname/startdate/enddate/rentamount/status): ");
        try {
            List<RentalAgreement> agreements = rentalManager.getSortedRentalAgreements(sortBy);
            displayRentalAgreements(agreements);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }

    private void searchRentalAgreements() {
        String keyword = readUserInput("Enter search keyword: ");
        List<RentalAgreement> results = rentalManager.searchRentalAgreements(keyword);
        if (results.isEmpty()) {
            System.out.println(TableFormatter.ANSI_YELLOW + "No rental agreements found matching the keyword: " + keyword + TableFormatter.ANSI_RESET);
        } else {
            List<String> headers = Arrays.asList("ID", "Property", "Tenant", "Start Date", "End Date", "Rent Amount", "Status");
            List<List<String>> data = new ArrayList<>();
            for (RentalAgreement agreement : results) {
                data.add(Arrays.asList(
                        agreement.getAgreementId(),
                        agreement.getProperty().getPropertyId(),
                        agreement.getMainTenant().getFullName(),
                        agreement.getStartDate().toString(),
                        agreement.getEndDate().toString(),
                        String.format("%.2f", agreement.getRentAmount()),
                        agreement.getStatus().toString()
                ));
            }
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        }
    }

    private void addTenant() {
        String id = null;
        while (id == null) {
            id = readUserInputAllowEsc("Enter tenant ID (press ESC to return): ");
            if (id == null) return;
            if (tenantManager.getTenant(id) != null) {
                System.out.println("Tenant with ID " + id + " already exists.");
                id = null;
            }
        }

        String fullName = readUserInputAllowEsc("Enter full name (press ESC to return): ");
        if (fullName == null) return;

        Date dateOfBirth = null;
        while (dateOfBirth == null) {
            String dobInput = readUserInputAllowEsc("Enter date of birth (yyyy-MM-dd, press ESC to return): ");
            if (dobInput == null) return;
            dateOfBirth = DateUtil.parseDate(dobInput);
            if (dateOfBirth == null) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        String contactInfo = null;
        while (contactInfo == null) {
            contactInfo = readUserInputAllowEsc("Enter contact information (email, press ESC to return): ");
            if (contactInfo == null) return;
            if (!InputValidator.isValidEmail(contactInfo)) {
                System.out.println("Invalid email format.");
                contactInfo = null;
            } else if (tenantManager.isEmailTaken(contactInfo)) {
                System.out.println("Email is already in use by another tenant.");
                contactInfo = null;
            }
        }

        Tenant tenant = new Tenant(id, fullName, dateOfBirth, contactInfo);
        tenantManager.addTenant(tenant);
        System.out.println("Tenant added successfully.");
    }

    private void updateTenant() {
        while (true) {
            String id = readUserInputAllowEsc("Enter tenant ID to update (press ESC to return): ");
            if (id == null) return;

            try {
                Tenant tenant = tenantManager.getTenant(id);
                if (tenant == null) {
                    throw new IllegalArgumentException("Tenant not found.");
                }

                String fullName = readUserInputAllowEmpty("Enter new full name (press enter to keep current): ");
                if (!fullName.isEmpty()) {
                    tenant.setFullName(fullName);
                }

                Date dateOfBirth = DateUtil.readOptionalDate(reader, "Enter new date of birth (yyyy-MM-dd, press enter to keep current): ");
                if (dateOfBirth != null) {
                    tenant.setDateOfBirth(dateOfBirth);
                }

                String contactInfo = readUserInputAllowEmpty("Enter new contact information (email, press enter to keep current): ");
                if (!contactInfo.isEmpty()) {
                    if (!InputValidator.isValidEmail(contactInfo)) {
                        throw new IllegalArgumentException("Invalid email format.");
                    } else if (!contactInfo.equals(tenant.getContactInformation()) && tenantManager.isEmailTaken(contactInfo)) {
                        throw new IllegalArgumentException("Email is already in use by another tenant.");
                    }
                    tenant.setContactInformation(contactInfo);
                }

                tenantManager.updateTenant(tenant);
                System.out.println(TableFormatter.ANSI_GREEN + "Tenant updated successfully." + TableFormatter.ANSI_RESET);
                List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
                List<List<String>> data = Arrays.asList(Arrays.asList(
                        tenant.getId(),
                        tenant.getFullName(),
                        tenant.getDateOfBirth().toString(),
                        tenant.getContactInformation()
                ));
                tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void deleteTenant() {
        while (true) {
            String id = readUserInput("Enter tenant ID to delete (or 'back' to return): ");
            if (id.equalsIgnoreCase("back")) {
                return;
            }

            try {
                Tenant tenant = tenantManager.getTenant(id);
                if (tenant == null) {
                    throw new IllegalArgumentException("Tenant not found.");
                }

                List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
                List<List<String>> data = new ArrayList<>();
                data.add(Arrays.asList(
                        tenant.getId(),
                        tenant.getFullName(),
                        tenant.getDateOfBirth().toString(),
                        tenant.getContactInformation()
                ));
                tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);

                String confirm = readUserInput("Are you sure you want to delete this tenant? (y/n): ");
                if (confirm.equalsIgnoreCase("y")) {
                    tenantManager.deleteTenant(id);
                    System.out.println(TableFormatter.ANSI_GREEN + "Tenant deleted successfully." + TableFormatter.ANSI_RESET);
                } else {
                    System.out.println("Deletion cancelled.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void listTenants() {
        String sortBy = readUserInput("Enter sort criteria (id/name/dob/email): ");
        try {
            List<Tenant> tenants = tenantManager.getSorted(sortBy);
            displayTenants(tenants);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }

    private void searchTenants() {
        String keyword = readUserInput("Enter search keyword: ");
        List<Tenant> results = tenantManager.searchTenants(keyword);
        if (results.isEmpty()) {
            System.out.println(TableFormatter.ANSI_YELLOW + "No tenants found matching the keyword: " + keyword + TableFormatter.ANSI_RESET);
        } else {
            List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
            List<List<String>> data = new ArrayList<>();
            for (Tenant tenant : results) {
                data.add(Arrays.asList(
                        tenant.getId(),
                        tenant.getFullName(),
                        tenant.getDateOfBirth().toString(),
                        tenant.getContactInformation()
                ));
            }
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        }
    }

    private void addOwner() {
        String id = null;
        while (id == null) {
            id = readUserInputAllowEsc("Enter owner ID (press ESC to return): ");
            if (id == null) return;
            if (ownerManager.getOwner(id) != null) {
                System.out.println(TableFormatter.ANSI_RED + "Owner with ID " + id + " already exists." + TableFormatter.ANSI_RESET);
                id = null;
            }
        }

        String fullName = readUserInputAllowEsc("Enter full name (press ESC to return): ");
        if (fullName == null) return;

        Date dateOfBirth = null;
        while (dateOfBirth == null) {
            String dobInput = readUserInputAllowEsc("Enter date of birth (yyyy-MM-dd, press ESC to return): ");
            if (dobInput == null) return;
            dateOfBirth = DateUtil.parseDate(dobInput);
            if (dateOfBirth == null) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        String contactInfo = null;
        while (contactInfo == null) {
            contactInfo = readUserInputAllowEsc("Enter contact information (email, press ESC to return): ");
            if (contactInfo == null) return;
            if (!InputValidator.isValidEmail(contactInfo)) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid email format." + TableFormatter.ANSI_RESET);
                contactInfo = null;
            } else if (ownerManager.isEmailTaken(contactInfo)) {
                System.out.println(TableFormatter.ANSI_RED + "Email is already in use by another owner." + TableFormatter.ANSI_RESET);
                contactInfo = null;
            }
        }

        try {
            Owner owner = new Owner(id, fullName, dateOfBirth, contactInfo);
            ownerManager.addOwner(owner);
            System.out.println(TableFormatter.ANSI_GREEN + "Owner added successfully." + TableFormatter.ANSI_RESET);
            List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
            List<List<String>> data = Arrays.asList(Arrays.asList(
                    owner.getId(),
                    owner.getFullName(),
                    owner.getDateOfBirth().toString(),
                    owner.getContactInformation()
            ));
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }

    private void updateOwner() {
        while (true) {
            String id = readUserInputAllowEsc("Enter owner ID to update (press ESC to return): ");
            if (id == null) return;

            try {
                Owner owner = ownerManager.getOwner(id);
                if (owner == null) {
                    throw new IllegalArgumentException("Owner not found.");
                }

                String fullName = readUserInputAllowEmpty("Enter new full name (press enter to keep current): ");
                if (!fullName.isEmpty()) {
                    owner.setFullName(fullName);
                }

                Date dateOfBirth = DateUtil.readOptionalDate(reader, "Enter new date of birth (yyyy-MM-dd, press enter to keep current): ");
                if (dateOfBirth != null) {
                    owner.setDateOfBirth(dateOfBirth);
                }

                String contactInfo = readUserInputAllowEmpty("Enter new contact information (email, press enter to keep current): ");
                if (!contactInfo.isEmpty()) {
                    if (!InputValidator.isValidEmail(contactInfo)) {
                        throw new IllegalArgumentException("Invalid email format.");
                    } else if (!contactInfo.equals(owner.getContactInformation()) && ownerManager.isEmailTaken(contactInfo)) {
                        throw new IllegalArgumentException("Email is already in use by another owner.");
                    }
                    owner.setContactInformation(contactInfo);
                }

                ownerManager.updateOwner(owner);
                System.out.println(TableFormatter.ANSI_GREEN + "Owner updated successfully." + TableFormatter.ANSI_RESET);
                List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
                List<List<String>> data = Arrays.asList(Arrays.asList(
                        owner.getId(),
                        owner.getFullName(),
                        owner.getDateOfBirth().toString(),
                        owner.getContactInformation()
                ));
                tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void deleteOwner() {
        while (true) {
            String id = readUserInput("Enter owner ID to delete (or 'back' to return): ");
            if (id.equalsIgnoreCase("back")) {
                return;
            }

            try {
                Owner owner = ownerManager.getOwner(id);
                if (owner == null) {
                    throw new IllegalArgumentException("Owner not found.");
                }

                List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
                List<List<String>> data = Arrays.asList(Arrays.asList(
                        owner.getId(),
                        owner.getFullName(),
                        owner.getDateOfBirth().toString(),
                        owner.getContactInformation()
                ));
                tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);

                String confirm = readUserInput("Are you sure you want to delete this owner? (y/n): ");
                if (confirm.equalsIgnoreCase("y")) {
                    ownerManager.deleteOwner(id);
                    System.out.println(TableFormatter.ANSI_GREEN + "Owner deleted successfully." + TableFormatter.ANSI_RESET);
                } else {
                    System.out.println(TableFormatter.ANSI_YELLOW + "Deletion cancelled." + TableFormatter.ANSI_RESET);
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void listOwners() {
        String sortBy = readUserInput("Enter sort criteria (id/name/dob/email): ");
        try {
            List<Owner> owners = ownerManager.getSorted(sortBy);
            displayOwners(owners);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }

    private void searchOwners() {
        String keyword = readUserInput("Enter search keyword: ");
        List<Owner> results = ownerManager.searchOwners(keyword);
        if (results.isEmpty()) {
            System.out.println(TableFormatter.ANSI_YELLOW + "No owners found matching the keyword: " + keyword + TableFormatter.ANSI_RESET);
        } else {
            List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
            List<List<String>> data = new ArrayList<>();
            for (Owner owner : results) {
                data.add(Arrays.asList(
                        owner.getId(),
                        owner.getFullName(),
                        owner.getDateOfBirth().toString(),
                        owner.getContactInformation()
                ));
            }
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        }
    }

    private void addHost() {
        String id = null;
        while (id == null) {
            id = readUserInputAllowEsc("Enter host ID (press ESC to return): ");
            if (id == null) return;
            if (hostManager.getHost(id) != null) {
                System.out.println(TableFormatter.ANSI_RED + "Host with ID " + id + " already exists." + TableFormatter.ANSI_RESET);
                id = null;
            }
        }

        String fullName = readUserInputAllowEsc("Enter full name (press ESC to return): ");
        if (fullName == null) return;

        Date dateOfBirth = null;
        while (dateOfBirth == null) {
            String dobInput = readUserInputAllowEsc("Enter date of birth (yyyy-MM-dd, press ESC to return): ");
            if (dobInput == null) return;
            dateOfBirth = DateUtil.parseDate(dobInput);
            if (dateOfBirth == null) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }

        String contactInfo = null;
        while (contactInfo == null) {
            contactInfo = readUserInputAllowEsc("Enter contact information (email, press ESC to return): ");
            if (contactInfo == null) return;
            if (!InputValidator.isValidEmail(contactInfo)) {
                System.out.println(TableFormatter.ANSI_RED + "Invalid email format." + TableFormatter.ANSI_RESET);
                contactInfo = null;
            } else if (hostManager.isEmailTaken(contactInfo)) {
                System.out.println(TableFormatter.ANSI_RED + "Email is already in use by another host." + TableFormatter.ANSI_RESET);
                contactInfo = null;
            }
        }

        try {
            Host host = new Host(id, fullName, dateOfBirth, contactInfo);
            hostManager.addHost(host);
            System.out.println(TableFormatter.ANSI_GREEN + "Host added successfully." + TableFormatter.ANSI_RESET);
            List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
            List<List<String>> data = Arrays.asList(Arrays.asList(
                    host.getId(),
                    host.getFullName(),
                    host.getDateOfBirth().toString(),
                    host.getContactInformation()
            ));
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }

    private void updateHost() {
        while (true) {
            String id = readUserInputAllowEsc("Enter host ID to update (press ESC to return): ");
            if (id == null) return;

            try {
                Host host = hostManager.getHost(id);
                if (host == null) {
                    throw new IllegalArgumentException("Host not found.");
                }

                String fullName = readUserInputAllowEmpty("Enter new full name (press enter to keep current): ");
                if (!fullName.isEmpty()) {
                    host.setFullName(fullName);
                }

                Date dateOfBirth = DateUtil.readOptionalDate(reader, "Enter new date of birth (yyyy-MM-dd, press enter to keep current): ");
                if (dateOfBirth != null) {
                    host.setDateOfBirth(dateOfBirth);
                }

                String contactInfo = readUserInputAllowEmpty("Enter new contact information (email, press enter to keep current): ");
                if (!contactInfo.isEmpty()) {
                    if (!InputValidator.isValidEmail(contactInfo)) {
                        throw new IllegalArgumentException("Invalid email format.");
                    } else if (!contactInfo.equals(host.getContactInformation()) && hostManager.isEmailTaken(contactInfo)) {
                        throw new IllegalArgumentException("Email is already in use by another host.");
                    }
                    host.setContactInformation(contactInfo);
                }

                hostManager.updateHost(host);
                System.out.println(TableFormatter.ANSI_GREEN + "Host updated successfully." + TableFormatter.ANSI_RESET);
                List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
                List<List<String>> data = Arrays.asList(Arrays.asList(
                        host.getId(),
                        host.getFullName(),
                        host.getDateOfBirth().toString(),
                        host.getContactInformation()
                ));
                tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void deleteHost() {
        while (true) {
            String id = readUserInput("Enter host ID to delete (or 'back' to return): ");
            if (id.equalsIgnoreCase("back")) {
                return;
            }

            try {
                Host host = hostManager.getHost(id);
                if (host == null) {
                    throw new IllegalArgumentException("Host not found.");
                }

                List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info");
                List<List<String>> data = Arrays.asList(Arrays.asList(
                        host.getId(),
                        host.getFullName(),
                        host.getDateOfBirth().toString(),
                        host.getContactInformation()
                ));
                tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);

                String confirm = readUserInput("Are you sure you want to delete this host? (y/n): ");
                if (confirm.equalsIgnoreCase("y")) {
                    hostManager.deleteHost(id);
                    System.out.println(TableFormatter.ANSI_GREEN + "Host deleted successfully." + TableFormatter.ANSI_RESET);
                } else {
                    System.out.println(TableFormatter.ANSI_YELLOW + "Deletion cancelled." + TableFormatter.ANSI_RESET);
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void listHosts() {
        String sortBy = readUserInput("Enter sort criteria (id/name/dob/email): ");
        try {
            List<Host> hosts = hostManager.getSorted(sortBy);
            displayHosts(hosts);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }

    private void searchHosts() {
        String keyword = readUserInput("Enter search keyword: ");
        List<Host> results = hostManager.searchHosts(keyword);
        if (results.isEmpty()) {
            System.out.println(TableFormatter.ANSI_YELLOW + "No hosts found matching the keyword: " + keyword + TableFormatter.ANSI_RESET);
        } else {
            List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info", "Managed Properties");
            List<List<String>> data = new ArrayList<>();
            for (Host host : results) {
                data.add(Arrays.asList(
                        host.getId(),
                        host.getFullName(),
                        host.getDateOfBirth().toString(),
                        host.getContactInformation(),
                        String.valueOf(host.getManagedProperties().size())
                ));
            }
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        }
    }

    private void addProperty() {
        String propertyId = null;
        while (propertyId == null) {
            propertyId = readUserInput("Enter property ID (or 'back' to return): ");
            if (propertyId.equalsIgnoreCase("back")) {
                return;
            }
            if (propertyManager.getProperty(propertyId) != null) {
                System.out.println(TableFormatter.ANSI_RED + "Property with ID " + propertyId + " already exists." + TableFormatter.ANSI_RESET);
                propertyId = null;
            }
        }

        String propertyType = readUserInput("Enter property type (residential/commercial): ").toLowerCase();
        String address = readUserInput("Enter address: ");
        double price = InputValidator.readDouble(reader, "Enter price: ", 0, Double.MAX_VALUE);
        Property.PropertyStatus status = Property.PropertyStatus.valueOf(readUserInput("Enter status (AVAILABLE/RENTED/UNDER_MAINTENANCE): ").toUpperCase());
        String ownerId = readUserInput("Enter owner ID: ");
        Owner owner = ownerManager.getOwner(ownerId);

        if (owner == null) {
            System.out.println(TableFormatter.ANSI_RED + "Owner not found. Please add the owner first." + TableFormatter.ANSI_RESET);
            return;
        }

        Property property;
        if ("residential".equals(propertyType)) {
            int bedrooms = InputValidator.readInteger(reader, "Enter number of bedrooms: ", 0, Integer.MAX_VALUE);
            boolean hasGarden = InputValidator.readBoolean(reader, "Has garden? (true/false): ");
            boolean isPetFriendly = InputValidator.readBoolean(reader, "Is pet friendly? (true/false): ");
            property = new ResidentialProperty(propertyId, address, price, status, owner, bedrooms, hasGarden, isPetFriendly);
        } else if ("commercial".equals(propertyType)) {
            String businessType = readUserInput("Enter business type: ");
            int parkingSpaces = InputValidator.readInteger(reader, "Enter number of parking spaces: ", 0, Integer.MAX_VALUE);
            double squareFootage = InputValidator.readDouble(reader, "Enter square footage: ", 0, Double.MAX_VALUE);
            property = new CommercialProperty(propertyId, address, price, status, owner, businessType, parkingSpaces, squareFootage);
        } else {
            System.out.println(TableFormatter.ANSI_RED + "Invalid property type." + TableFormatter.ANSI_RESET);
            return;
        }

        propertyManager.addProperty(property);
        System.out.println(TableFormatter.ANSI_GREEN + "Property added successfully." + TableFormatter.ANSI_RESET);
        displayPropertyDetails(property);
    }

    private void updateProperty() {
        while (true) {
            String id = readUserInputAllowEsc("Enter property ID to update (press ESC to return): ");
            if (id == null) return;

            try {
                Property property = propertyManager.getProperty(id);
                if (property == null) {
                    throw new IllegalArgumentException("Property not found.");
                }

                String address = readUserInputAllowEmpty("Enter new address (press enter to keep current): ");
                if (!address.isEmpty()) {
                    property.setAddress(address);
                }

                String priceStr = readUserInputAllowEmpty("Enter new price (press enter to keep current): ");
                if (!priceStr.isEmpty()) {
                    try {
                        double price = Double.parseDouble(priceStr);
                        if (price < 0) {
                            throw new IllegalArgumentException("Price must be non-negative.");
                        }
                        property.setPrice(price);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Invalid price format.");
                    }
                }

                String statusStr = readUserInputAllowEmpty("Enter new status (AVAILABLE/RENTED/UNDER_MAINTENANCE, press enter to keep current): ");
                if (!statusStr.isEmpty()) {
                    try {
                        property.setStatus(Property.PropertyStatus.valueOf(statusStr.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid status.");
                    }
                }

                if (property instanceof ResidentialProperty) {
                    updateResidentialProperty((ResidentialProperty) property);
                } else if (property instanceof CommercialProperty) {
                    updateCommercialProperty((CommercialProperty) property);
                }

                propertyManager.updateProperty(property);
                System.out.println(TableFormatter.ANSI_GREEN + "Property updated successfully." + TableFormatter.ANSI_RESET);
                displayPropertyDetails(property);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + "Error: " + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void updateResidentialProperty(ResidentialProperty property) {
        String bedroomsStr = readUserInputAllowEmpty("Enter new number of bedrooms (press enter to keep current): ");
        if (!bedroomsStr.isEmpty()) {
            try {
                int bedrooms = Integer.parseInt(bedroomsStr);
                if (bedrooms < 0) {
                    throw new IllegalArgumentException("Number of bedrooms must be non-negative.");
                }
                property.setNumberOfBedrooms(bedrooms);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for bedrooms.");
            }
        }

        String hasGardenStr = readUserInputAllowEmpty("Has garden? (true/false, press enter to keep current): ");
        if (!hasGardenStr.isEmpty()) {
            property.setHasGarden(Boolean.parseBoolean(hasGardenStr));
        }

        String isPetFriendlyStr = readUserInputAllowEmpty("Is pet friendly? (true/false, press enter to keep current): ");
        if (!isPetFriendlyStr.isEmpty()) {
            property.setPetFriendly(Boolean.parseBoolean(isPetFriendlyStr));
        }
    }

    private void updateCommercialProperty(CommercialProperty property) {
        String businessType = readUserInputAllowEmpty("Enter new business type (press enter to keep current): ");
        if (!businessType.isEmpty()) {
            property.setBusinessType(businessType);
        }

        String parkingSpacesStr = readUserInputAllowEmpty("Enter new number of parking spaces (press enter to keep current): ");
        if (!parkingSpacesStr.isEmpty()) {
            try {
                int parkingSpaces = Integer.parseInt(parkingSpacesStr);
                if (parkingSpaces < 0) {
                    throw new IllegalArgumentException("Number of parking spaces must be non-negative.");
                }
                property.setParkingSpaces(parkingSpaces);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for parking spaces.");
            }
        }

        String squareFootageStr = readUserInputAllowEmpty("Enter new square footage (press enter to keep current): ");
        if (!squareFootageStr.isEmpty()) {
            try {
                double squareFootage = Double.parseDouble(squareFootageStr);
                if (squareFootage < 0) {
                    throw new IllegalArgumentException("Square footage must be non-negative.");
                }
                property.setSquareFootage(squareFootage);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format for square footage.");
            }
        }
    }

    private void deleteProperty() {
        while (true) {
            String id = readUserInput("Enter property ID to delete (or 'back' to return): ");
            if (id.equalsIgnoreCase("back")) {
                return;
            }

            try {
                Property property = propertyManager.getProperty(id);
                if (property == null) {
                    throw new IllegalArgumentException("Property not found.");
                }

                displayPropertyDetails(property);

                String confirm = readUserInput("Are you sure you want to delete this property? (y/n): ");
                if (confirm.equalsIgnoreCase("y")) {
                    propertyManager.deleteProperty(id);
                    System.out.println(TableFormatter.ANSI_GREEN + "Property deleted successfully." + TableFormatter.ANSI_RESET);
                } else {
                    System.out.println(TableFormatter.ANSI_YELLOW + "Deletion cancelled." + TableFormatter.ANSI_RESET);
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
                System.out.println("Would you like to try again? (y/n)");
                String retry = readUserInput("").toLowerCase();
                if (!retry.equals("y")) {
                    return;
                }
            }
        }
    }

    private void listProperties() {
        String sortBy = readUserInput("Enter sort criteria (id/type/address/price/status/owner): ");
        try {
            List<Property> properties = propertyManager.getSorted(sortBy);
            displayProperties(properties);
        } catch (IllegalArgumentException e) {
            System.out.println(TableFormatter.ANSI_RED + e.getMessage() + TableFormatter.ANSI_RESET);
        }
    }

    private void searchProperties() {
        String keyword = readUserInput("Enter search keyword: ");
        List<Property> results = propertyManager.searchProperties(keyword);
        if (results.isEmpty()) {
            System.out.println(TableFormatter.ANSI_YELLOW + "No properties found matching the keyword: " + keyword + TableFormatter.ANSI_RESET);
        } else {
            List<String> headers = Arrays.asList("ID", "Type", "Address", "Price", "Status", "Owner");
            List<List<String>> data = new ArrayList<>();
            for (Property property : results) {
                data.add(Arrays.asList(
                        property.getPropertyId(),
                        property instanceof ResidentialProperty ? "Residential" : "Commercial",
                        property.getAddress(),
                        String.format("%.2f", property.getPrice()),
                        property.getStatus().toString(),
                        property.getOwner().getFullName()
                ));
            }
            tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
        }
    }

    private void displayPropertyDetails(Property property) {
        List<String> headers = Arrays.asList("ID", "Type", "Address", "Price", "Status", "Owner");
        List<List<String>> data = Arrays.asList(Arrays.asList(
                property.getPropertyId(),
                property instanceof ResidentialProperty ? "Residential" : "Commercial",
                property.getAddress(),
                String.format("%.2f", property.getPrice()),
                property.getStatus().toString(),
                property.getOwner().getFullName()
        ));
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void generateIncomeReport() {
        double totalIncome = rentalManager.getTotalRentalIncome();
        System.out.println(TableFormatter.ANSI_GREEN + "Total Rental Income: $" + String.format("%.2f", totalIncome) + TableFormatter.ANSI_RESET);

        List<RentalAgreement> agreements = rentalManager.getAllRentalAgreements();
        List<String> headers = Arrays.asList("Agreement ID", "Property", "Tenant", "Rent Amount");
        List<List<String>> data = new ArrayList<>();
        for (RentalAgreement agreement : agreements) {
            data.add(Arrays.asList(
                    agreement.getAgreementId(),
                    agreement.getProperty().getPropertyId(),
                    agreement.getMainTenant().getFullName(),
                    String.format("%.2f", agreement.getRentAmount())
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void generateOccupancyReport() {
        int totalProperties = propertyManager.getTotalProperties();
        int occupiedProperties = propertyManager.getOccupiedProperties();
        double occupancyRate = totalProperties > 0 ? (double) occupiedProperties / totalProperties * 100 : 0;
        System.out.println(TableFormatter.ANSI_GREEN + "Occupancy Rate: " + String.format("%.2f%%", occupancyRate) + TableFormatter.ANSI_RESET);

        List<Property> properties = propertyManager.getAllProperties();
        List<String> headers = Arrays.asList("Property ID", "Type", "Status");
        List<List<String>> data = new ArrayList<>();
        for (Property property : properties) {
            data.add(Arrays.asList(
                    property.getPropertyId(),
                    property instanceof ResidentialProperty ? "Residential" : "Commercial",
                    property.getStatus().toString()
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void generateTenantReport() {
        List<Tenant> tenants = tenantManager.getAllTenants();
        List<String> headers = Arrays.asList("ID", "Name", "Date of Birth", "Contact Info", "Active Agreements");
        List<List<String>> data = new ArrayList<>();
        for (Tenant tenant : tenants) {
            int activeAgreements = (int) tenant.getRentalAgreements().stream()
                    .filter(a -> a.getStatus() == RentalAgreement.Status.ACTIVE)
                    .count();
            data.add(Arrays.asList(
                    tenant.getId(),
                    tenant.getFullName(),
                    tenant.getDateOfBirth().toString(),
                    tenant.getContactInformation(),
                    String.valueOf(activeAgreements)
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void displayRentalAgreementDetails(RentalAgreement agreement) {
        List<String> headers = Arrays.asList("ID", "Property", "Tenant", "Start Date", "End Date", "Rent Amount", "Status");
        List<List<String>> data = Arrays.asList(Arrays.asList(
                agreement.getAgreementId(),
                agreement.getProperty().getPropertyId(),
                agreement.getMainTenant().getFullName(),
                agreement.getStartDate().toString(),
                agreement.getEndDate().toString(),
                String.format("%.2f", agreement.getRentAmount()),
                agreement.getStatus().toString()
        ));
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void displayRentalAgreements(List<RentalAgreement> agreements) {
        List<String> headers = Arrays.asList(
                "Agreement ID", "Property ID", "Tenant", "Owner", "Host",
                "Start Date", "End Date", "Rent Amount", "Status"
        );
        List<List<String>> data = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (RentalAgreement agreement : agreements) {
            String tenantInfo = agreement.getMainTenant().getId() + " - " + agreement.getMainTenant().getFullName();
            for (Tenant subTenant : agreement.getSubTenants()) {
                tenantInfo += " / " + subTenant.getId() + " - " + subTenant.getFullName();
            }
            data.add(Arrays.asList(
                    agreement.getAgreementId(),
                    agreement.getProperty().getPropertyId(),
                    tenantInfo,
                    agreement.getOwner().getId() + " - " + agreement.getOwner().getFullName(),
                    agreement.getHost().getId() + " - " + agreement.getHost().getFullName(),
                    dateFormat.format(agreement.getStartDate()),
                    dateFormat.format(agreement.getEndDate()),
                    String.format("%.2f", agreement.getRentAmount()),
                    agreement.getStatus().toString()
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void displayTenants(List<Tenant> tenants) {
        List<String> headers = Arrays.asList(
                "Tenant ID", "Name", "DOB", "Email", "Rented Property",
                "Rental Contract ID", "Payment Amount", "Payment Date", "Payment Method"
        );
        List<List<String>> data = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Tenant tenant : tenants) {
            String rentedProperty = "";
            String rentalContractId = "";
            String paymentAmount = "";
            String paymentDate = "";
            String paymentMethod = "";
            if (!tenant.getRentalAgreements().isEmpty()) {
                RentalAgreement agreement = tenant.getRentalAgreements().get(0);
                rentedProperty = agreement.getProperty().getPropertyId();
                rentalContractId = agreement.getAgreementId();
                List<Payment> payments = getPaymentsForTenant(tenant);
                if (!payments.isEmpty()) {
                    Payment lastPayment = payments.get(payments.size() - 1);
                    paymentAmount = String.format("%.2f", lastPayment.getAmount());
                    paymentDate = dateFormat.format(lastPayment.getPaymentDate());
                    paymentMethod = lastPayment.getPaymentMethod();
                }
            }
            data.add(Arrays.asList(
                    tenant.getId(),
                    tenant.getFullName(),
                    dateFormat.format(tenant.getDateOfBirth()),
                    tenant.getContactInformation(),
                    rentedProperty,
                    rentalContractId,
                    paymentAmount,
                    paymentDate,
                    paymentMethod
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private List<Payment> getPaymentsForTenant(Tenant tenant) {
        return fileHandler.loadPayments().stream()
                .filter(p -> p.getTenant().getId().equals(tenant.getId()))
                .collect(Collectors.toList());
    }


    private void displayOwners(List<Owner> owners) {
        List<String> headers = Arrays.asList(
                "Owner ID", "Name", "DOB", "Email", "Owned Properties", "Managing Hosts"
        );
        List<List<String>> data = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Owner owner : owners) {
            String ownedProperties = owner.getOwnedProperties().stream()
                    .map(Property::getPropertyId)
                    .collect(Collectors.joining(", "));
            String managingHosts = propertyManager.getAllProperties().stream()
                    .filter(p -> p.getOwner().getId().equals(owner.getId()) && p.getHost() != null)
                    .map(p -> p.getHost().getId() + " - " + p.getHost().getFullName())
                    .distinct()
                    .collect(Collectors.joining(", "));
            data.add(Arrays.asList(
                    owner.getId(),
                    owner.getFullName(),
                    dateFormat.format(owner.getDateOfBirth()),
                    owner.getContactInformation(),
                    ownedProperties,
                    managingHosts
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void displayHosts(List<Host> hosts) {
        List<String> headers = Arrays.asList(
                "Host ID", "Name", "DOB", "Email", "Managed Properties", "Property Owners"
        );
        List<List<String>> data = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (Host host : hosts) {
            String managedProperties = host.getManagedProperties().stream()
                    .map(Property::getPropertyId)
                    .collect(Collectors.joining(", "));
            String propertyOwners = propertyManager.getAllProperties().stream()
                    .filter(p -> p.getHost() != null && p.getHost().getId().equals(host.getId()))
                    .map(p -> p.getOwner().getId() + " - " + p.getOwner().getFullName())
                    .distinct()
                    .collect(Collectors.joining(", "));
            data.add(Arrays.asList(
                    host.getId(),
                    host.getFullName(),
                    dateFormat.format(host.getDateOfBirth()),
                    host.getContactInformation(),
                    managedProperties,
                    propertyOwners
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }

    private void displayProperties(List<Property> properties) {
        List<String> headers = Arrays.asList(
                "Property ID", "Type", "Address", "Price", "Status", "Owner", "Host", "Tenants"
        );
        List<List<String>> data = new ArrayList<>();
        for (Property property : properties) {
            String tenantInfo = property.getTenants().stream()
                    .map(t -> t.getId() + " - " + t.getFullName())
                    .collect(Collectors.joining(", "));
            data.add(Arrays.asList(
                    property.getPropertyId(),
                    property instanceof ResidentialProperty ? "Residential" : "Commercial",
                    property.getAddress(),
                    String.format("%.2f", property.getPrice()),
                    property.getStatus().toString(),
                    property.getOwner().getId() + " - " + property.getOwner().getFullName(),
                    property.getHost() != null ? property.getHost().getId() + " - " + property.getHost().getFullName() : "N/A",
                    tenantInfo
            ));
        }
        tableFormatter.printDataTable(headers, data, TableFormatter.ANSI_CYAN);
    }


    public static void main(String[] args) {
        try {
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.start();
        } catch (IOException e) {
            System.err.println("Error initializing the application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
