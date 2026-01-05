package com.example.myarena.ui.subscription;

import com.example.myarena.domain.SubscriptionPlan;
import com.example.myarena.facade.SessionFacade;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class PlanAdminController {

    private final PlanManagementFrame view;
    private final SessionFacade sessionFacade;

    public PlanAdminController(PlanManagementFrame view) {
        this.view = view;
        this.sessionFacade = SessionFacade.getInstance();
    }

    /**
     * Charge tous les plans d'abonnement depuis la base de données
     */
    public void loadPlans() {
        try {
            System.out.println("PlanAdminController : Chargement des plans...");
            List<SubscriptionPlan> plans = sessionFacade.getSubscriptionPlans();
            view.updateCards(plans);
            view.showMessage(plans.size() + " plan(s) chargé(s)", true);
            System.out.println("Plans chargés : " + plans.size());
        } catch (Exception e) {
            view.showMessage("Erreur lors du chargement des plans", false);
            System.err.println("Erreur : " + e.getMessage());
        }
    }

    /**
     * Ouvre le dialogue de création d'un nouveau plan
     */
    public void handleCreate() {
        System.out.println("PlanAdminController : Création d'un nouveau plan");
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Nouveau plan d'abonnement");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(view.getStage());
        
        VBox root = createPlanFormUI(null); 
        
        TextField nameField = (TextField) root.lookup("#nameField");
        TextField priceField = (TextField) root.lookup("#priceField");
        ComboBox<Integer> durationComboBox = (ComboBox<Integer>) root.lookup("#durationComboBox");
        CheckBox activeCheckBox = (CheckBox) root.lookup("#activeCheckBox");
        Label errorLabel = (Label) root.lookup("#errorLabel");
        
        Button createButton = (Button) root.lookup("#createButton");
        createButton.setOnAction(e -> {
            if (validateAndCreatePlan(nameField, priceField, durationComboBox, activeCheckBox, errorLabel)) {
                dialogStage.close();
                view.showMessage("✅ Plan créé avec succès", true);
                loadPlans();
            }
        });
        
        Button cancelButton = (Button) root.lookup("#cancelButton");
        cancelButton.setOnAction(e -> dialogStage.close());
        
        Scene scene = new Scene(root, 500, 400);
        scene.getStylesheets().add(getClass().getResource("/com/example/myarena/styles/myarena-styles.css").toExternalForm());
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
    
    /**
     * Crée l'interface du formulaire de création/modification de plan (en Java pur)
     */
    private VBox createPlanFormUI(SubscriptionPlan planToEdit) {
        VBox root = new VBox(20);
        root.getStyleClass().add("form-container");
        
        String titleText = (planToEdit == null) ? "Créer un nouveau plan" : "Modifier le plan";
        Label title = new Label(titleText);
        title.getStyleClass().add("dialog-title");
        
        Separator separator1 = new Separator();
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        
        Label nameLabel = new Label("Nom du plan :");
        nameLabel.getStyleClass().add("form-label");
        TextField nameField = new TextField();
        nameField.setId("nameField");
        nameField.setPromptText("Ex: Abonnement Premium");
        nameField.getStyleClass().add("form-field");
        if (planToEdit != null) {
            nameField.setText(planToEdit.getName());
        }
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        
        Label priceLabel = new Label("Prix (€) :");
        priceLabel.getStyleClass().add("form-label");
        TextField priceField = new TextField();
        priceField.setId("priceField");
        priceField.setPromptText("Ex: 29.99");
        priceField.getStyleClass().add("form-field");
        if (planToEdit != null) {
            priceField.setText(String.valueOf(planToEdit.getPrice()));
        }
        grid.add(priceLabel, 0, 1);
        grid.add(priceField, 1, 1);
        
        Label durationLabel = new Label("Durée (mois) :");
        durationLabel.getStyleClass().add("form-label");
        ComboBox<Integer> durationComboBox = new ComboBox<>();
        durationComboBox.setId("durationComboBox");
        durationComboBox.setPromptText("Sélectionner la durée");
        durationComboBox.getItems().addAll(1, 3, 6, 12);
        durationComboBox.getStyleClass().add("form-field");
        if (planToEdit != null) {
            durationComboBox.setValue(planToEdit.getDurationMonths());
        }
        grid.add(durationLabel, 0, 2);
        grid.add(durationComboBox, 1, 2);
        
        Label activeLabel = new Label("Actif :");
        activeLabel.getStyleClass().add("form-label");
        CheckBox activeCheckBox = new CheckBox("Plan actif dès la création");
        activeCheckBox.setId("activeCheckBox");
        activeCheckBox.setSelected(planToEdit != null ? planToEdit.isActive() : true);
        grid.add(activeLabel, 0, 3);
        grid.add(activeCheckBox, 1, 3);
        
        Separator separator2 = new Separator();
        
        Label errorLabel = new Label("");
        errorLabel.setId("errorLabel");
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button cancelButton = new Button("Annuler");
        cancelButton.setId("cancelButton");
        cancelButton.getStyleClass().add("btn-outline");
        
        Button createButton = new Button("Créer le plan");
        createButton.setId("createButton");
        createButton.getStyleClass().add("btn-primary");
        
        buttonsBox.getChildren().addAll(cancelButton, createButton);
        
        root.getChildren().addAll(title, separator1, grid, separator2, errorLabel, buttonsBox);
        return root;
    }
    
    /**
     * Valide les champs et crée le plan
     */
    private boolean validateAndCreatePlan(TextField nameField, TextField priceField, 
                                          ComboBox<Integer> durationComboBox, 
                                          CheckBox activeCheckBox, Label errorLabel) {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        Integer duration = durationComboBox.getValue();
        boolean active = activeCheckBox.isSelected();
        
        if (name.isEmpty()) {
            showError(errorLabel, "Le nom du plan est obligatoire");
            return false;
        }
        
        if (priceText.isEmpty()) {
            showError(errorLabel, "Le prix est obligatoire");
            return false;
        }
        
        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                showError(errorLabel, "Le prix doit être supérieur à 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError(errorLabel, "Le prix doit être un nombre valide");
            return false;
        }
        
        if (duration == null) {
            showError(errorLabel, "Veuillez sélectionner une durée");
            return false;
        }
        
        try {
            SubscriptionPlan newPlan = new SubscriptionPlan(null, name, price, duration, active);
            sessionFacade.createPlan(newPlan);
            return true;
        } catch (Exception e) {
            showError(errorLabel, "Erreur lors de la création : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Valide les champs et met à jour le plan existant (logique métier)
     */
    private boolean validateAndUpdatePlan(SubscriptionPlan existingPlan, TextField nameField, 
                                          TextField priceField, ComboBox<Integer> durationComboBox, 
                                          CheckBox activeCheckBox, Label errorLabel) {
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        Integer duration = durationComboBox.getValue();
        boolean active = activeCheckBox.isSelected();
        
        // MÊME validation que pour la création
        if (name.isEmpty()) {
            showError(errorLabel, "Le nom du plan est obligatoire");
            return false;
        }
        
        if (priceText.isEmpty()) {
            showError(errorLabel, "Le prix est obligatoire");
            return false;
        }
        
        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                showError(errorLabel, "Le prix doit être supérieur à 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError(errorLabel, "Le prix doit être un nombre valide");
            return false;
        }
        
        if (duration == null) {
            showError(errorLabel, "Veuillez sélectionner une durée");
            return false;
        }
        
        try {
            existingPlan.setName(name);
            existingPlan.setPrice(price);
            existingPlan.setDurationMonths(duration);
            existingPlan.setActive(active);
            
            sessionFacade.updatePlan(existingPlan);
            return true;
        } catch (Exception e) {
            showError(errorLabel, "Erreur lors de la modification : " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Affiche un message d'erreur dans le formulaire
     */
    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    /**
     * Ouvre le dialogue de modification d'un plan existant
     */
    public void handleUpdate(SubscriptionPlan plan) {
        System.out.println("PlanAdminController : Modification du plan - " + plan.getName());
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Modifier le plan : " + plan.getName());
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(view.getStage());
        
        VBox root = createPlanFormUI(plan);
        
        // Récupérer les champs du formulaire
        TextField nameField = (TextField) root.lookup("#nameField");
        TextField priceField = (TextField) root.lookup("#priceField");
        ComboBox<Integer> durationComboBox = (ComboBox<Integer>) root.lookup("#durationComboBox");
        CheckBox activeCheckBox = (CheckBox) root.lookup("#activeCheckBox");
        Label errorLabel = (Label) root.lookup("#errorLabel");
        
        Button updateButton = (Button) root.lookup("#createButton");
        updateButton.setText("Modifier le plan");  
        updateButton.setOnAction(e -> {
            if (validateAndUpdatePlan(plan, nameField, priceField, durationComboBox, activeCheckBox, errorLabel)) {
                dialogStage.close();
                view.showMessage("Plan modifié avec succès", true);
                loadPlans();
            }
        });
        
        Button cancelButton = (Button) root.lookup("#cancelButton");
        cancelButton.setOnAction(e -> dialogStage.close());
        
        Scene scene = new Scene(root, 500, 400);
        scene.getStylesheets().add(getClass().getResource("/com/example/myarena/styles/myarena-styles.css").toExternalForm());
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    /**
     * Affiche une confirmation et supprime le plan si l'utilisateur confirme
     */
    public void handleDelete(SubscriptionPlan plan) {
        System.out.println("PlanAdminController : Suppression du plan - " + plan.getName());
        
        Alert confirmationDialog = createDeleteConfirmationDialog(plan);
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deletePlan(plan);
        }
    }

    /**
     * Active ou désactive un plan d'abonnement
     */
    public void handleToggleActive(SubscriptionPlan plan) {
        System.out.println("PlanAdminController : Toggle actif pour - " + plan.getName());
        try {
            plan.setActive(!plan.isActive());
            sessionFacade.updatePlan(plan);
            String status = plan.isActive() ? "activé" : "désactivé";
            view.showMessage("✅ Plan " + status + " : " + plan.getName(), true);
            loadPlans();
        } catch (Exception e) {
            view.showMessage("❌ Erreur : " + e.getMessage(), false);
            System.err.println("Erreur : " + e.getMessage());
        }
    }


    /**
     * Crée la popup de confirmation de suppression avec le style MYARENA
     */
    private Alert createDeleteConfirmationDialog(SubscriptionPlan plan) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.initOwner(view.getStage());
        dialog.setTitle("Confirmation");
        dialog.setHeaderText(null);
        
        VBox content = createDialogContent(plan);
        dialog.getDialogPane().setContent(content);
        
        applyDialogStyles(dialog);
        
        return dialog;
    }

    /**
     * Crée le contenu du dialogue avec les labels
     */
    private VBox createDialogContent(SubscriptionPlan plan) {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10, 0, 0, 0));
        
        Label messageLabel = new Label("Supprimer le plan");
        messageLabel.getStyleClass().add("dialog-message");
        
        Label planNameLabel = new Label(plan.getName());
        planNameLabel.getStyleClass().add("dialog-plan-name");
        
        Label warningLabel = new Label("Cette action est irréversible.");
        warningLabel.getStyleClass().add("dialog-warning");
        
        content.getChildren().addAll(messageLabel, planNameLabel, warningLabel);
        
        return content;
    }

    /**
     * Applique les styles CSS au dialogue
     */
    private void applyDialogStyles(Alert dialog) {
        String cssPath = getClass().getResource("/com/example/myarena/styles/myarena-styles.css").toExternalForm();
        dialog.getDialogPane().getStylesheets().add(cssPath);
        dialog.getDialogPane().getStyleClass().add("confirmation-dialog");
        
        javafx.scene.control.Button deleteBtn = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        deleteBtn.setText("Supprimer");
        deleteBtn.getStyleClass().clear();
        deleteBtn.getStyleClass().add("delete-button");
        
        javafx.scene.control.Button cancelBtn = 
            (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelBtn.setText("Annuler");
        cancelBtn.getStyleClass().clear();
        cancelBtn.getStyleClass().add("cancel-button");
    }

    /**
     * Supprime le plan de la base de données
     */
    private void deletePlan(SubscriptionPlan plan) {
        try {
            sessionFacade.deletePlan(plan.getId());
            view.showMessage("Plan supprimé avec succès", true);
            loadPlans();
        } catch (Exception e) {
            view.showMessage(" Erreur : " + e.getMessage(), false);
            System.err.println("Erreur : " + e.getMessage());
        }
    }
}
