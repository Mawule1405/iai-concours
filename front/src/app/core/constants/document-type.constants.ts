export interface DocumentTypeOption {
  code: string;
  label: string;
}

export const DOCUMENT_TYPES = {
  // 1. Documents administratifs uniques à vie
  STATIC: [
    { code: 'ACTE_NAISSANCE', label: "Extrait d'Acte de Naissance" },
    { code: 'NATIONALITE', label: 'Certificat de Nationalité' }
  ] as DocumentTypeOption[],

  // 2. Diplômes d'accès (évolutifs selon le cycle de l'étudiant)

  DIPLOMAS: [
    { code: 'BACCALAUREAT', label: 'Diplôme du Baccalauréat / Équivalent' },
    { code: 'BTS', label: 'Diplôme de Brevet de Technicien Supérieur (BTS)' },
    { code: 'DUT', label: 'Diplôme Universitaire de Technologie (DUT)' },
    { code: 'DEUG', label: 'Diplôme d’Études Universitaires Générales (DEUG)' },
    { code: 'LICENCE', label: 'Diplôme de Licence' },
    { code: 'AUTRES', label: 'Autre Diplôme Académique' }
  ] as DocumentTypeOption[],

  // 3. Justificatifs de financement (évolutifs / annuels)
  SPONSORSHIPS: [
    { code: 'ATTESTATION_BOURSE', label: 'Attestation de bourse' },
    { code: 'LETTRE_GOUVERNEMENTALE', label: 'Lettre de prise en charge gouvernementale' },
    { code: 'ENGAGEMENT_PARENTAL', label: "Lettre d'engagement parentale" }
  ] as DocumentTypeOption[],

  // 4. Pièces d'identité et documents financiers de scolarité
  EVOLUTIVE: [
    { code: 'CNI', label: "Carte Nationale d'Identité / Passeport" },
    { code: 'PAYMENT_SCOLARITE', label: 'Bordereau de paiement de scolarité' },
    { code: 'PAYMENT_UNIFORME', label: "Bordereau de paiement d'uniforme" },
    { code: 'AUTRE', label: 'Autre document justificatif' }
  ] as DocumentTypeOption[],

  // Getter global pour alimenter des listes de sélection complètes
  all(): DocumentTypeOption[] {
    return [
      ...this.STATIC,
      ...this.DIPLOMAS,
      ...this.SPONSORSHIPS,
      ...this.EVOLUTIVE
    ];
  }
};
