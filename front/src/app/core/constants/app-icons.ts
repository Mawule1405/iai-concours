export const APP_ICONS = {
  // --- CONFIGURATION & ACCÈS ---
  permissions: 'fas fa-key',
  roles: 'fas fa-shield-alt',
  users: 'fas fa-users',
  globalParameter: 'fas fa-sliders-h',
  settings: 'fas fa-cog',

  // --- ACADÉMIQUE & STRUCTURE ---
  salles: 'fas fa-door-open',
  filieres: 'fas fa-stream',
  cycles: 'fas fa-graduation-cap',
  level: 'fas fa-layer-group',
  classes: 'fas fa-school',
  matieres: 'fas fa-book',
  anneeAcademiques: 'fas fa-calendar-alt',

  // --- SCOLARITÉ & SUIVI ---
  dashboard: 'fas fa-chart-pie',
  dashboardTeacher: 'fas fa-th-large',
  gestionEtudiants: 'fas fa-users-cog',
  cartesEtudiant: 'fas fa-id-card',
  documents: 'fas fa-file-alt',

  // --- PÉDAGOGIE ---
  courses: 'fas fa-chalkboard',
  coursesTeacher: 'fas fa-chalkboard-teacher',
  planning: 'fas fa-calendar-alt',
  cahierTextes: 'fas fa-book-open',
  attendance: 'fas fa-user-check',

  // --- EVALUATIONS ---
  notes: 'fas fa-marker',
  notesTeacher: 'fas fa-pen-nib',
  bulletins: 'fas fa-file-invoice',
  deliberations: 'fas fa-balance-scale',

  // --- FINANCES ---
  inscriptions: 'fas fa-cash-register',
  scolarites: 'fas fa-wallet'
} as const; // "as const" rend l'objet immuable en lecture seule

// Type optionnel pour sécuriser l'utilisation dans ton code
export type AppIconKey = keyof typeof APP_ICONS;
