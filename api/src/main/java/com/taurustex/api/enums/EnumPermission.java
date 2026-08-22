package com.taurustex.api.enums;

import lombok.Getter;

@Getter
public enum EnumPermission {

    // --- GESTION DES UTILISATEURS & SYSTÈME ---
    PERM_CREATE_USER("Créer de nouveaux comptes utilisateurs"),
    PERM_READ_USER("Consulter la liste et les détails des utilisateurs"),
    PERM_UPDATE_USER("Modifier les informations et l'état (activé/bloqué) des utilisateurs"),
    PERM_DELETE_USER("Supprimer ou archiver un utilisateur"),

    PERM_MANAGE_ROLES("Définir les rôles et assigner les permissions (Configuration RBAC)"),
    PERM_UPDATE_APP_PARAMS("Modifier les paramètres globaux de l'université"),



    // --- GESTION DES CANDIDATS ---
    PERM_REGISTER_CANDIDATE("Inscrire ou enregistrer un nouveau candidat"),
    PERM_VIEW_CANDIDATE("Consulter la fiche ou les détails d'un candidat"),
    PERM_UPDATE_CANDIDATE("Modifier les informations d'un candidat"),
    PERM_DELETE_CANDIDATE("Supprimer ou archiver le dossier d'un candidat"),
    PERM_REGISTER_PAYMENT_CANDIDATE("Enregistrer le payment du frais d'inscription pour un candidat"),
    // --- CONSULTATION ET REPORTING ---
    PERM_VIEW_LISTE("Consulter la liste générale des candidats"),
    PERM_VIEW_DASHBOARD("Accéder aux tableaux de bord et statistiques"),
    PERM_EXPORT("Exporter les données (PDF, Excel, CSV)");

    private final String description;

    EnumPermission(String description) {
        this.description = description;
    }
}