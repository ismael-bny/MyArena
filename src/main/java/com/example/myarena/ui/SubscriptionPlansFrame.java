package com.example.myarena.ui;

import com.example.myarena.domain.Subscription;
import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.domain.UserRole;
import com.example.myarena.facade.SessionFacade;
import com.example.myarena.facade.SubscriptionFacade; // Import the new Facade
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class SubscriptionPlansFrame {

    @FXML
    private TilePane cardsContainer;

    @FXML
    private Label statusLabel;

    private SubscriptionPlansController controller;

    public SubscriptionPlansFrame() {
        this.controller = new SubscriptionPlansController(this);
    }

    @FXML
    public void initialize() {
        System.out.println("SubscriptionPlansFrame : initialize() appelé");
        controller.loadPlans();
    }

    public void updateCards(List<SubscriptionPlan> plans) {
        cardsContainer.getChildren().clear();

        if (plans == null || plans.isEmpty()) {
            showMessage("Aucun plan disponible", false);
            return;
        }

        for (SubscriptionPlan plan : plans) {
            VBox card = buildPlanCard(plan);
            cardsContainer.getChildren().add(card);
        }

        showMessage(plans.size() + " plan(s) chargé(s)", true);
    }

    public void showMessage(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-success", "status-error");
        statusLabel.getStyleClass().add(isSuccess ? "status-success" : "status-error");
    }

    private VBox buildPlanCard(SubscriptionPlan plan) {
        VBox card = createCardContainer();
        addCardShadowEffect(card);

        Label nameLabel = createNameLabel(plan.getName());
        Label priceLabel = createPriceLabel(plan.getPrice());
        Label durationLabel = createDurationLabel(plan.getDurationMonths());
        HBox statusBox = createStatusBox(plan.isActive());
        Separator separator = createSeparator();
        HBox buttonsBox = createCardButtons(plan);

        card.getChildren().addAll(nameLabel, priceLabel, durationLabel, statusBox, separator, buttonsBox);
        return card;
    }

    private VBox createCardContainer() {
        VBox card = new VBox(15);
        card.setPrefSize(250, 320);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.getStyleClass().add("plan-card");
        return card;
    }

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

    private Label createNameLabel(String name) {
        Label label = new Label(name);
        label.getStyleClass().add("plan-name");
        label.setMaxWidth(210);
        return label;
    }

    private Label createPriceLabel(double price) {
        DecimalFormat df = new DecimalFormat("0.00");
        Label label = new Label(df.format(price) + " €");
        label.getStyleClass().add("plan-price");
        return label;
    }

    private Label createDurationLabel(int months) {
        Label label = new Label(getDurationText(months));
        label.getStyleClass().add("plan-duration");
        return label;
    }

    private String getDurationText(int months) {
        switch (months) {
            case 1: return "Mensuel";
            case 3: return "Trimestriel";
            case 6: return "Semestriel";
            case 12: return "Annuel";
            default: return months + " mois";
        }
    }

    private HBox createStatusBox(boolean isActive) {
        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER);

        Label bullet = new Label("●");
        bullet.getStyleClass().add(isActive ? "status-bullet-active" : "status-bullet-inactive");

        Label text = new Label(isActive ? "Actif" : "Inactif");
        text.getStyleClass().add(isActive ? "status-text-active" : "status-text-inactive");

        box.getChildren().addAll(bullet, text);
        return box;
    }

    private Separator createSeparator() {
        Separator separator = new Separator();
        separator.setMaxWidth(200);
        return separator;
    }

    private HBox createCardButtons(SubscriptionPlan plan) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER);

        // SessionFacade for role/user check, SubscriptionFacade for logic
        SessionFacade sessionFacade = SessionFacade.getInstance();
        SubscriptionFacade subFacade = SubscriptionFacade.getInstance();

        UserRole role = (sessionFacade.getCurrentUser() != null) ? sessionFacade.getCurrentUser().getRole() : null;

        if (role == UserRole.CLIENT || role == UserRole.ORGANIZER) {
            Button button = createSubscribeOrCancelButton(plan, subFacade);
            box.getChildren().add(button);
        }

        return box;
    }

    private Button createSubscribeOrCancelButton(SubscriptionPlan plan, SubscriptionFacade subFacade) {
        boolean isSubscribed = isUserSubscribedToThisPlan(plan, subFacade);

        if (isSubscribed) {
            return createCancelButton(plan);
        } else {
            return createSubscribeButton(plan);
        }
    }

    /**
     * Updated: Calls the no-argument version of getActiveSubscription()
     */
    private boolean isUserSubscribedToThisPlan(SubscriptionPlan plan, SubscriptionFacade subFacade) {
        Subscription activeSubscription = subFacade.getActiveSubscription();
        return (activeSubscription != null &&
                activeSubscription.getPlan() != null &&
                activeSubscription.getPlan().getId().equals(plan.getId()));
    }

    private Button createSubscribeButton(SubscriptionPlan plan) {
        Button button = new Button("S'inscrire");
        button.getStyleClass().addAll("crud-button", "btn-subscribe");
        button.setDisable(!plan.isActive());
        button.setOnAction(e -> controller.handleSubscribe(plan));
        return button;
    }

    private Button createCancelButton(SubscriptionPlan plan) {
        Button button = new Button("Annuler");
        button.getStyleClass().addAll("crud-button", "btn-cancel-subscription");
        button.setOnAction(e -> controller.handleCancel(plan));
        return button;
    }

    public static void show() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SubscriptionPlansFrame.class.getResource("/com/example/myarena/subscription-plans.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 1200, 700));
            stage.setTitle("Plans d'Abonnement - MYARENA");
            stage.show();

            System.out.println("SubscriptionPlansFrame : Fenêtre ouverte");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de subscription-plans.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }
}