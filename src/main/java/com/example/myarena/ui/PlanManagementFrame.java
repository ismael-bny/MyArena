package com.example.myarena.ui;

import com.example.myarena.domain.SubscriptionPlan;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class PlanManagementFrame {

    @FXML
    private TilePane cardsContainer;

    @FXML
    private Label statusLabel;

    @FXML
    private Label countLabel;
    
    private PlanAdminController controller;
    private static Stage currentStage;

    public PlanManagementFrame() {
        this.controller = new PlanAdminController(this);
    }

    public Stage getStage() {
        return currentStage;
    }

    @FXML
    public void initialize() {
        System.out.println("PlanManagementFrame : initialize() appelé");
        controller.loadPlans();
    }

    @FXML
    private void handleCreate() {
        controller.handleCreate();
    }

    @FXML
    private void handleBackToMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/myarena/main-menu.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 800, 600);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Met à jour l'affichage des cards avec la liste des plans
     */
    public void updateCards(List<SubscriptionPlan> plans) {
        cardsContainer.getChildren().clear();

        if (plans == null || plans.isEmpty()) {
            showMessage("Aucun plan disponible", false);
            countLabel.setText("0 plan(s)");
            return;
        }

        for (SubscriptionPlan plan : plans) {
            VBox card = buildPlanCard(plan);
            cardsContainer.getChildren().add(card);
        }

        showMessage("✅ " + plans.size() + " plan(s) chargé(s)", true);
        countLabel.setText(plans.size() + " plan(s)");
    }

    /**
     * Affiche un message de statut (succès ou erreur)
     */
    public void showMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-success", "status-error");
        statusLabel.getStyleClass().add(isSuccess ? "status-success" : "status-error");
    }

    /**
     * Convertit la durée en mois en texte lisible
     */
    private String getDurationText(int months) {
        switch (months) {
            case 1:
                return "Mensuel";
            case 3:
                return "Trimestriel";
            case 6:
                return "Semestriel";
            case 12:
                return "Annuel";
            default:
                return months + " mois";
        }
    }

    /**
     * Construit une card pour un plan d'abonnement
     */
    private VBox buildPlanCard(SubscriptionPlan plan) {
        VBox card = createCardContainer();
        addCardShadowEffect(card);
        
        // Créer les éléments de la card
        Label nameLabel = createNameLabel(plan.getName());
        Label priceLabel = createPriceLabel(plan.getPrice());
        Label durationLabel = createDurationLabel(plan.getDurationMonths());
        HBox statusBox = createStatusBox(plan.isActive());
        javafx.scene.control.Separator separator = createSeparator();
        VBox buttonsBox = createCRUDButtons(plan);

        card.getChildren().addAll(nameLabel, priceLabel, durationLabel, statusBox, separator, buttonsBox);
        
        return card;
    }

    /**
     * Crée le conteneur principal de la card
     */
    private VBox createCardContainer() {
        VBox card = new VBox(15);
        card.setPrefSize(250, 360);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.getStyleClass().add("plan-card");
        return card;
    }

    /**
     * Ajoute l'effet d'ombre et l'animation hover à la card
     */
    private void addCardShadowEffect(VBox card) {
        DropShadow normalShadow = new DropShadow(10, 0, 4, Color.rgb(0, 0, 0, 0.15));
        DropShadow hoverShadow = new DropShadow(15, 0, 6, Color.rgb(0, 0, 0, 0.25));
        
        card.setEffect(normalShadow);
        
        card.setOnMouseEntered(e -> {
            card.setEffect(hoverShadow);
            card.setTranslateY(-3);
        });
        
        card.setOnMouseExited(e -> {
            card.setEffect(normalShadow);
            card.setTranslateY(0);
        });
    }

    /**
     * Crée le label du nom du plan
     */
    private Label createNameLabel(String name) {
        Label label = new Label(name);
        label.getStyleClass().add("plan-name");
        label.setMaxWidth(210);
        return label;
    }

    /**
     * Crée le label du prix
     */
    private Label createPriceLabel(double price) {
        DecimalFormat df = new DecimalFormat("0.00");
        Label label = new Label(df.format(price) + " €");
        label.getStyleClass().add("plan-price");
        return label;
    }

    /**
     * Crée le label de la durée
     */
    private Label createDurationLabel(int months) {
        Label label = new Label(getDurationText(months));
        label.getStyleClass().add("plan-duration");
        return label;
    }

    /**
     * Crée la box de statut (Actif/Inactif)
     */
    private HBox createStatusBox(boolean isActive) {
        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER);
        
        Label bullet = new Label("●");
        bullet.getStyleClass().add(isActive ? "status-bullet-active" : "status-bullet-inactive");
        
        Label statusText = new Label(isActive ? "Actif" : "Inactif");
        statusText.getStyleClass().add(isActive ? "status-text-active" : "status-text-inactive");
        
        statusBox.getChildren().addAll(bullet, statusText);
        return statusBox;
    }

    /**
     * Crée le séparateur horizontal
     */
    private javafx.scene.control.Separator createSeparator() {
        javafx.scene.control.Separator separator = new javafx.scene.control.Separator();
        separator.setMaxWidth(200);
        return separator;
    }

    /**
     * Crée les boutons (Modifier, Supprimer, Activer/Désactiver)
     * Facile a modifier ou a ajouter un nouveau boutton
     */
    private VBox createCRUDButtons(SubscriptionPlan plan) {
        VBox buttonsBox = new VBox(8);
        buttonsBox.setAlignment(Pos.CENTER);

        Button editButton = createEditButton(plan);
        Button deleteButton = createDeleteButton(plan);
        Button toggleButton = createToggleButton(plan);

        buttonsBox.getChildren().addAll(editButton, deleteButton, toggleButton);
        return buttonsBox;
    }

    /**
     * Crée le bouton Modifier
     */
    private Button createEditButton(SubscriptionPlan plan) {
        Button button = new Button("Modifier");
        button.setPrefWidth(180);
        button.getStyleClass().addAll("crud-button", "edit-button");
        button.setOnAction(e -> controller.handleUpdate(plan));
        return button;
    }

    /**
     * Crée le bouton Supprimer
     */
    private Button createDeleteButton(SubscriptionPlan plan) {
        Button button = new Button("Supprimer");
        button.setPrefWidth(180);
        button.getStyleClass().addAll("crud-button", "delete-button");
        button.setOnAction(e -> controller.handleDelete(plan));
        return button;
    }

    /**
     * Crée le bouton Activer/Désactiver
     */
    private Button createToggleButton(SubscriptionPlan plan) {
        Button button = new Button(plan.isActive() ? "Désactiver" : "Activer");
        button.setPrefWidth(180);
        button.getStyleClass().add("crud-button");
        button.getStyleClass().add(plan.isActive() ? "toggle-button-active" : "toggle-button-inactive");
        button.setOnAction(e -> controller.handleToggleActive(plan));
        return button;
    }

    /**
     * Ouvre la fenêtre de gestion des plans
     */
    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    PlanManagementFrame.class.getResource("/com/example/myarena/plan-management.fxml")
            );
            Parent root = loader.load();
            
            // Charger le CSS
            String cssPath = PlanManagementFrame.class.getResource("/com/example/myarena/styles/myarena-styles.css").toExternalForm();
            root.getStylesheets().add(cssPath);

            currentStage = new Stage();
            currentStage.setScene(new Scene(root, 1200, 700));
            currentStage.setTitle("Gestion des Plans - MYARENA");
            currentStage.show();

            System.out.println("PlanManagementFrame : Fenêtre ouverte");

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de plan-management.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
